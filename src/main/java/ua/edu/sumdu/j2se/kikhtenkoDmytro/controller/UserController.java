package ua.edu.sumdu.j2se.kikhtenkoDmytro.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.*;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.User;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.Context;

import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class UserController {
    private final static Logger logger =
            LoggerFactory.getLogger(TaskController.class);
    private final ObjectMapper mapper;
    private final User user;
    private final ApiClient apiClient;
    private final ObjectProperty<User> createdUser;
    private final ObjectProperty<User> editedUser;
    private final ObjectProperty<User> deletedUser;
    @FXML
    private TextField nameInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private CheckBox enabledInput;
    @FXML
    private Button createUserButton;
    @FXML
    private Button editUserButton;
    @FXML
    private Button deleteUserButton;
    private NotificationsController notificationsController;
    @FXML
    private AuthoritiesController authoritiesController;

    public UserController() {
        mapper = Context.get("mapper");
        apiClient = Context.get("apiClient");
        createdUser = new SimpleObjectProperty<>();
        editedUser = new SimpleObjectProperty<>();
        deletedUser = new SimpleObjectProperty<>();
        user = new User();
    }

    @FXML
    public void initialize() {
        nameInput.textProperty().addListener((
                observableValue, s, t1) -> user.setName(t1));

        passwordInput.textProperty().addListener((
                observableValue, localDate, t1) ->
                user.setPassword(t1));

        enabledInput.selectedProperty().addListener((
                observableValue, aBoolean, t1) -> user.setEnabled(t1));

        authoritiesController.selectedAuthoritiesProperty().
                addListener((ListChangeListener<Integer>) change ->
                        user.setAuthorities(
                                authoritiesController.getAuthorities()));

        setUser(null);
    }

    public User getUser() {
        return user.replicate();
    }

    public void setUser(User user) {
        if(user == null) {
            nameInput.setText("");
            passwordInput.setText(null);
            enabledInput.setSelected(false);
            authoritiesController.setAuthorities(new ArrayList<>());

            editUserButton.setDisable(true);
            deleteUserButton.setDisable(true);
        } else {
            this.user.setId(user.getId());
            nameInput.setText(user.getName());
            passwordInput.setText(user.getPassword());
            enabledInput.setSelected(user.isEnabled());
            authoritiesController.setAuthorities(
                    user.getAuthorities());

            editUserButton.setDisable(false);
            deleteUserButton.setDisable(false);
        }
    }

    public ObjectProperty<User> createdUserProperty() {
        return createdUser;
    }

    public ObjectProperty<User> editedUserProperty() {
        return editedUser;
    }

    public ObjectProperty<User> deletedUserProperty() {
        return deletedUser;
    }

    public NotificationsController getNotificationsController() {
        return notificationsController;
    }

    public void setNotificationsController(
            NotificationsController controller) {
        this.notificationsController = controller;

        authoritiesController.setNotificationsController(
                notificationsController);
        authoritiesController.updateAuthoritiesData();
    }

    public void createUser() {
        User user = getUser();
        HttpClient client = apiClient.getClient();
        HttpRequest request;
        try {
            request = apiClient.getRequest(
                    "POST",
                    apiClient.getUri("/users"),
                    mapper.writeValueAsString(user)
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
                        createdUserProperty().setValue(user);
                    }
                };

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                thenAccept(consumer).exceptionally(consumer);
    }

    public void editUser() {
        User user = getUser();
        if(user.getPassword() != null &&
                user.getPassword().equals("")) {
            user.setPassword(null);
        }
        HttpClient client = apiClient.getClient();
        HttpRequest request;
        try {
            request = apiClient.getRequest(
                    "PATCH",
                    apiClient.getUri("/users/" + user.getId()),
                    mapper.writeValueAsString(user)
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
                        editedUserProperty().setValue(user);
                    }
                };

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                thenAccept(consumer).exceptionally(consumer);
    }

    public void deleteUser() {
        User user = getUser();
        HttpClient client = apiClient.getClient();
        HttpRequest request;
        try {
            request = apiClient.getRequest(
                    "DELETE",
                    apiClient.getUri("/users/" + user.getId()),
                    mapper.writeValueAsString(user)
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
                        deletedUserProperty().setValue(user);
                    }
                };

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                thenAccept(consumer).exceptionally(consumer);
    }
}
