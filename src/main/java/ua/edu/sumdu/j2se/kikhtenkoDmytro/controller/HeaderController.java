package ua.edu.sumdu.j2se.kikhtenkoDmytro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.ApiClient;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.RequestConsumer;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.AuthenticatedData;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.Context;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.FxmlManager;

import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;

public class HeaderController {
    private final static Logger logger =
            LoggerFactory.getLogger(TaskController.class);
    private final ObjectMapper mapper;
    private final ApiClient apiClient;
    private final AuthenticatedData authenticatedData;
    @FXML
    private Button tasksButton;
    @FXML
    private Button usersButton;
    @FXML
    private Label userNameText;
    private NotificationsController notificationsController;

    public HeaderController() {
        mapper = Context.get("mapper");
        apiClient = Context.get("apiClient");
        authenticatedData = new AuthenticatedData();
    }

    public NotificationsController getNotificationsController() {
        return notificationsController;
    }

    public void setNotificationsController(
            NotificationsController controller) {
        this.notificationsController = controller;
    }

    public void setAuthentication(
            AuthenticatedData authenticatedData) {
        this.authenticatedData.update(authenticatedData);
        userNameText.setText(authenticatedData.getName());
    }

    public AuthenticatedData getAuthentication() {
        return authenticatedData.replicate();
    }

    public void updateAuthorization() {
        HttpClient client = apiClient.getClient();
        HttpRequest request;
        try {
            request = apiClient.getRequest(
                    "GET",
                    apiClient.getUri("/auth"),
                    null
            );
        } catch (URISyntaxException e) {
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
                        AuthenticatedData result = mapper.readValue(
                                response.body(),
                                AuthenticatedData.class
                        );
                        Platform.runLater(() -> {
                            setAuthentication(result);
                            updateAccessElements();
                        });
                    }
                };

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                thenAccept(consumer).exceptionally(consumer);
    }

    public void updateAccessElements() {
        if(authenticatedData != null) {
            HashSet<Integer> authorities = new HashSet<>(
                    authenticatedData.getAuthorities().size());
            authorities.addAll(authenticatedData.getAuthorities());
            usersButton.setVisible(authorities.contains(0) ||
                    authorities.contains(1));
            tasksButton.setVisible(authorities.contains(2));
        }
    }

    public void loadCalendarPage() {
        FxmlManager manager = Context.get("fxmlManager");
        manager.loadFxml("calendar.fxml");
    }

    public void loadUsersPage() {
        FxmlManager manager = Context.get("fxmlManager");
        manager.loadFxml("user-list.fxml");
    }

    public void loadTasksPage() {
        FxmlManager manager = Context.get("fxmlManager");
        manager.loadFxml("task-list.fxml");
    }

    public void signOut() {
        apiClient.setToken(null);
        FxmlManager manager = Context.get("fxmlManager");
        manager.loadFxml("sign-in.fxml");
    }
}
