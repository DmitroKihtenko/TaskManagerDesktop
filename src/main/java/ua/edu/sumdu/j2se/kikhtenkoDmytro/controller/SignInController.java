package ua.edu.sumdu.j2se.kikhtenkoDmytro.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.http.HttpClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.RequestConsumer;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.Authentication;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.Token;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.ApiClient;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.FxmlManager;

import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SignInController {
    private static final Logger logger =
            LoggerFactory.getLogger(SignInController.class);
    private final ObjectMapper mapper;
    private final ApiClient apiClient;
    @FXML
    public TextField usernameInput;
    @FXML
    public PasswordField passwordInput;
    @FXML
    public NotificationsController notificationsController;

    public SignInController() {
        mapper = Context.get("mapper");
        apiClient = Context.get("apiClient");
    }

    @FXML
    public void signIn() {
        HttpClient client = apiClient.getClient();
        HttpRequest request;
        try {
            request = apiClient.getRequest(
                    "POST",
                    apiClient.getUri("/signIn"),
                    mapper.writeValueAsString(getUserFromInput()),
                    false
                    );
        } catch (URISyntaxException | JsonProcessingException e) {
            logger.error(e.getMessage());
            return;
        }

        RequestConsumer consumer =
                new RequestConsumer(notificationsController) {
            @Override
            public void onSuccess(
                    HttpResponse<String> response)
                    throws Exception {
                super.onSuccess(response);
                apiClient.setToken(mapper.readValue(
                        response.body(),
                        Token.class));
                toCalendarPage();
            }
        };

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                thenAccept(consumer).exceptionally(consumer);
    }

    public Authentication getUserFromInput() {
        Authentication authentication = new Authentication();
        authentication.setName(usernameInput.getText());
        authentication.setPassword(passwordInput.getText());
        return authentication;
    }

    protected void toCalendarPage() {
        FxmlManager manager = Context.get("fxmlManager");
        manager.loadFxml("calendar.fxml");
    }
}
