package ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.ApiConfig;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.Token;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.URIParams;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.security.SecureRandom;
import java.util.Objects;

public class ApiClient {
    private static final Logger logger =
            LoggerFactory.getLogger(ApiClient.class);
    private static final String AUTH_HEADER = "Authorization";
    private SSLContext sslContext;
    private ApiConfig apiConfig;
    private Token token;

    public ApiClient(ApiConfig apiConfig) {
        setApiConfig(apiConfig);
    }

    public ApiConfig getApiConfig() {
        return apiConfig;
    }

    public void setApiConfig(ApiConfig apiConfig) {
        this.apiConfig = apiConfig;
        sslContext = createSslContext();
    }

    public URI getUri(String mapping) throws URISyntaxException {
        return getUri(mapping, null);
    }

    public URI getUri(String mapping, URIParams params) throws URISyntaxException {
        return new URI(
                apiConfig.getScheme(),
                null,
                apiConfig.getHost(),
                apiConfig.getPort(),
                mapping,
                params == null ? null : params.toString(),
                null);
    }

    public HttpClient getClient() {
        HttpClient.Builder builder = HttpClient.newBuilder();
        if(Objects.equals(apiConfig.getScheme(), "https")) {
            if(sslContext != null) {
                builder.sslContext(sslContext);
            }
        }
        builder.connectTimeout(Duration.ofSeconds(
                apiConfig.getSecondsTimeout()));
        if(apiConfig.isAllowRedirects()) {
            builder.followRedirects(HttpClient.Redirect.NORMAL);
        } else {
            builder.followRedirects(HttpClient.Redirect.NEVER);
        }
        return builder.build();
    }

    public HttpRequest getRequest(
            String method,
            URI uri,
            String body,
            boolean useAuth) throws URISyntaxException {
        HttpRequest.Builder builder =
                HttpRequest.newBuilder(uri).
                header("Content-Type", "application/json");
        if(body != null) {
            builder.method(method, HttpRequest.
                    BodyPublishers.ofString(body));
        } else {
            builder.method(method, HttpRequest.
                    BodyPublishers.noBody());
        }
        if(useAuth) {
            builder.header(AUTH_HEADER, getAuthValue(getTokenWithError()));
        }
        return builder.build();
    }

    public HttpRequest getRequest(String method,
                                  URI uri,
                                  String body) throws URISyntaxException {
        return getRequest(method, uri, body, true);
    }

    public String getAuthValue(Token token) {
        return token.getType() + " " + token.getJwtToken();
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public Token getTokenWithError() {
        if(token == null) {
            throw new IllegalStateException("Token not found");
        }
        return token;
    }

    public SSLContext createSslContext() {
        SSLContext sslContext = null;
        try {
            if(apiConfig.isAllowSelfSigned()) {
                X509TrustManager allowAllTrustManager =
                        new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                        X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(
                        X509Certificate[] certs, String authType) {}
                };
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(
                        null,
                        new TrustManager[] {allowAllTrustManager},
                        new SecureRandom()
                );
            } else {
                sslContext = SSLContext.getDefault();
            }
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.error("SSL configuration creating error");
        }
        return sslContext;
    }
}
