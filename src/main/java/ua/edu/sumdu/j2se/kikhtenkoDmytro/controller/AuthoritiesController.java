package ua.edu.sumdu.j2se.kikhtenkoDmytro.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.ApiClient;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.RequestConsumer;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.Authority;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.Context;

import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class AuthoritiesController {
    private final static Logger logger =
            LoggerFactory.getLogger(TaskController.class);
    private final ObjectMapper mapper;
    private final ApiClient apiClient;
    private final ObservableList<Integer> selectedAuthorities;
    private ArrayList<Authority> authorities;
    private ArrayList<Integer> authoritiesToSet;
    @FXML
    private ScrollPane parent;
    @FXML
    private GridPane authoritiesContainer;
    private NotificationsController notificationsController;

    public AuthoritiesController() {
        apiClient = Context.get("apiClient");
        mapper = Context.get("mapper");
        selectedAuthorities = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
    }

    public NotificationsController getNotificationsController() {
        return notificationsController;
    }

    public void setNotificationsController(
            NotificationsController controller) {
        this.notificationsController = controller;
    }

    public void setMinWidth(double width) {
        parent.setMinWidth(width);
        authoritiesContainer.setMinWidth(width);
    }

    public void setMinHeight(double height) {
        parent.setMinHeight(height);
        authoritiesContainer.setMinHeight(height);
    }

    public void setAuthorities(List<Integer> authorities) {
        if(authorities == null) {
            authorities = new ArrayList<>();
        }
        authoritiesToSet = new ArrayList<>(authorities.size());
        authoritiesToSet.addAll(authorities);
        updateSet();
    }

    public ArrayList<Integer> getAuthorities() {
        ArrayList<Integer> result =
                new ArrayList<>(selectedAuthorities.size());
        result.addAll(selectedAuthorities);
        return result;
    }

    public void updateSet() {
        if(authoritiesToSet != null) {
            if(authorities != null) {
                for(Node node : authoritiesContainer.
                        getChildren()) {
                    if(node instanceof CheckBox) {
                        ((CheckBox) node).setSelected(false);
                    }
                }
                for(Integer id : authoritiesToSet) {
                    for(Node node : authoritiesContainer.
                            getChildren()) {
                        if(node instanceof CheckBox &&
                                node.getStyleClass().contains(
                                String.valueOf(id))) {
                            ((CheckBox) node).setSelected(true);
                        }
                    }
                }
                authoritiesToSet = null;
            } else {
                updateAuthoritiesData();
            }
        }
    }

    protected Label createAuthorityLabel() {
        Label label = new Label();
        label.getStyleClass().add("bg-diluted");
        label.setAlignment(Pos.CENTER_LEFT);
        label.setWrapText(true);
        return label;
    }

    private CheckBox createAuthorityCheckbox() {
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(false);
        checkBox.getStyleClass().add("bg-diluted");
        checkBox.setAlignment(Pos.CENTER_RIGHT);
        return checkBox;
    }

    protected void updateAuthoritiesView() {
        Insets margin = new Insets(5, 5, 5, 5);
        authoritiesContainer.getChildren().clear();
        int rowCounter = 0;
        for(Authority authority : authorities) {
            Label label = createAuthorityLabel();
            label.setText(authority.getDescription());
            CheckBox checkBox = createAuthorityCheckbox();
            checkBox.getStyleClass().add(
                    String.valueOf(authority.getId()));

            int finalRowCounter = rowCounter;
            checkBox.selectedProperty().addListener(
                    (observableValue, aBoolean, t1) -> {
                if(t1) {
                    if(!selectedAuthorities.contains(
                            finalRowCounter)) {
                        selectedAuthorities.add(finalRowCounter);
                    }
                } else {
                    if(selectedAuthorities.contains(
                            finalRowCounter)) {
                        selectedAuthorities.remove(
                                Integer.valueOf(finalRowCounter));
                    }
                }
            });
            authoritiesContainer.add(label, 0, rowCounter);
            authoritiesContainer.add(checkBox, 1, rowCounter);
            GridPane.setMargin(label, margin);
            GridPane.setMargin(checkBox, margin);
            rowCounter += 1;
        }
    }

    public void updateAuthoritiesData() {
        HttpClient client = apiClient.getClient();
        HttpRequest request;
        try {
            request = apiClient.getRequest(
                    "GET",
                    apiClient.getUri("/authorities"),
                    null
            );
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
            return;
        }

        RequestConsumer consumer = new RequestConsumer(
                notificationsController) {
                    @Override
                    public void onSuccess(
                            HttpResponse<String> response)
                            throws Exception {
                        super.onSuccess(response);
                        authorities = mapper.readValue(
                                        response.body(),
                                        new TypeReference<>() {});
                        Platform.runLater(() -> {
                            updateAuthoritiesView();
                            updateSet();
                        });
                    }
                };

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                thenAccept(consumer).exceptionally(consumer);
    }

    public ObservableList<Integer> selectedAuthoritiesProperty() {
        return selectedAuthorities;
    }
}
