package ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo;

import java.util.Objects;

public class ApiConfig {
    private String scheme;
    private String host;
    private int port;
    private int secondsTimeout;
    private boolean allowRedirects;

    private boolean allowSelfSigned;

    public ApiConfig() {
        setScheme("https");
        setHost("localhost");
        setPort(8443);
        setSecondsTimeout(5);
        setAllowRedirects(false);
        setAllowSelfSigned(true);
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSecondsTimeout() {
        return secondsTimeout;
    }

    public void setSecondsTimeout(int secondsTimeout) {
        this.secondsTimeout = secondsTimeout;
    }

    public boolean isAllowRedirects() {
        return allowRedirects;
    }

    public void setAllowRedirects(boolean allowRedirects) {
        this.allowRedirects = allowRedirects;
    }

    public boolean isAllowSelfSigned() {
        return allowSelfSigned;
    }

    public void setAllowSelfSigned(boolean allowSelfSigned) {
        this.allowSelfSigned = allowSelfSigned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApiConfig)) {
            return false;
        }
        ApiConfig config = (ApiConfig) o;
        return port == config.port &&
                secondsTimeout == config.secondsTimeout
                && allowRedirects == config.allowRedirects
                && scheme.equals(config.scheme)
                && host.equals(config.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheme, host, port,
                secondsTimeout, allowRedirects);
    }

    @Override
    public String toString() {
        return "ApiConfig{" +
                "scheme='" + scheme + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", secondsTimeout=" + secondsTimeout +
                ", allowRedirects=" + allowRedirects +
                '}';
    }
}
