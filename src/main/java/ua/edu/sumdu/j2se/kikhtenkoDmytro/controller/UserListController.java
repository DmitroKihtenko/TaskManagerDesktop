package ua.edu.sumdu.j2se.kikhtenkoDmytro.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.ApiClient;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.RequestConsumer;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.SearchParams;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.SearchResult;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.User;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.Context;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.FxmlManager;

import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UserListController {
    private static final Logger logger =
            LoggerFactory.getLogger(TaskListController.class);
    private final ObjectMapper mapper;
    private final ObjectProperty<SearchParams> searchParamsProperty;

    private final ObjectProperty<User> selectedUserProperty;
    private final ApiClient apiClient;
    @FXML
    private GridPane tableContainer;
    @FXML
    private TableColumn<User, String> userNameColumn;
    @FXML
    private TextField searchUserInput;
    @FXML
    private TableView<User> userTable;
    @FXML
    private Button previousPageButton;
    @FXML
    private Label previousPageLine;
    @FXML
    private Label totalResultsLine;
    @FXML
    private Label nextPageLine;
    @FXML
    private Button nextPageButton;
    @FXML
    private HeaderController headerController;
    @FXML
    private NotificationsController notificationsController;
    @FXML
    private UserController userController;

    public UserListController() {
        mapper = Context.get("mapper");
        apiClient = Context.get("apiClient");
        selectedUserProperty = new SimpleObjectProperty<>();
        searchParamsProperty = new SimpleObjectProperty<>();
    }

    @FXML
    public void initialize() {
        SearchParams searchParams =
                Context.get("startUserSearchParams");
        searchUserInput.setText(searchParams.getSearchRegex());

        userTable.getColumns().get(0).setMinWidth(
                tableContainer.getPrefWidth());

        userTable.getSelectionModel().selectedItemProperty().
                addListener((observableValue, t1, t2) -> {
                    selectedUserProperty.setValue(t2);
                });

        selectedUserProperty.addListener((observableValue, task, t1) -> {
            userController.setUser(t1);
        });

        userNameColumn.setCellValueFactory(
                features -> new SimpleObjectProperty<>(
                        features.getValue().getName()));

        userController.createdUserProperty().addListener(
                (observableValue, task, t1) -> {
                    searchUsers();
                });

        userController.editedUserProperty().addListener(
                (observableValue, task, t1) -> {
                    searchUsers();
                });

        userController.deletedUserProperty().addListener(
                (observableValue, task, t1) -> {
                    if(userTable.getItems().size() <= 1) {
                        searchParamsProperty.
                                getValue().toPrevious();
                    }
                    searchUsers();
                });

        searchUserInput.textProperty().addListener(
                (observableValue, s, t1) -> {
                    getSearchParams().setSearchRegex(t1);
                    searchUsers();
                });

        searchParamsProperty.addListener(
                (observableValue, params, t1) -> {
                    searchUsers();
                });

        setSearchParams(searchParams);

        userController.setNotificationsController(
                notificationsController);
        headerController.setNotificationsController(
                notificationsController);
        headerController.updateAuthorization();
    }

    public void setSearchParams(SearchParams params) {
        this.searchParamsProperty.setValue(params);
    }

    public SearchParams getSearchParams() {
        return searchParamsProperty.getValue();
    }

    public ObjectProperty<User> getSelectedUserProperty() {
        return selectedUserProperty;
    }

    public void unselectUser() {
        userTable.getSelectionModel().clearSelection();
    }

    public void back() {
        FxmlManager manager = Context.get("fxmlManager");
        manager.loadFxml("calendar.fxml");
    }

    public void searchPreviousPage() {
        getSearchParams().toPrevious();
        searchUsers();
    }

    public void searchNextPage() {
        getSearchParams().toNext();
        searchUsers();
    }

    public void searchUsers() {
        HttpClient client = apiClient.getClient();
        HttpRequest request;
        try {
            request = apiClient.getRequest(
                    "GET",
                    apiClient.getUri("/users",
                            getSearchParams()),
                    null
            );
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
            return;
        }

        RequestConsumer consumer = new RequestConsumer(notificationsController) {
            @Override
            public void onSuccess(
                    HttpResponse<String> response)
                    throws Exception {
                super.onSuccess(response);
                SearchResult<User> result = mapper.readValue(
                        response.body(),
                        new TypeReference<>() {}
                );
                Platform.runLater(() -> {
                    userTable.getItems().clear();
                    userTable.getItems().addAll(
                            result.getResult());
                    totalResultsLine.setText(
                            "Total " + result.getTotal());
                    previousPageLine.setText(
                            previousRangeFrom(result,
                                    getSearchParams()));
                    previousPageButton.setDisable(
                            previousPageLine.getText().
                                    equals(""));
                    nextPageLine.setText(
                            nextRangeFrom(result,
                                    getSearchParams()));
                    nextPageButton.setDisable(
                            nextPageLine.getText().
                                    equals(""));
                    if(userTable.getItems().size() > 0) {
                        userTable.getSelectionModel().
                                selectFirst();
                    }
                });
            }
        };

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                thenAccept(consumer).exceptionally(consumer);
    }

    private String previousRangeFrom(
            SearchResult<?> result,
            SearchParams params
    ) {
        int begin = result.getFrom() - params.getAmount();
        if(begin < 0) {
            begin = 0;
        }
        begin++;
        int end = result.getFrom();
        if(begin > end) {
            return "";
        }
        return begin + "-" + end;
    }

    private String nextRangeFrom(
            SearchResult<?> result,
            SearchParams params
    ) {
        int begin = result.getFrom() + result.getCurrent() + 1;
        int end = begin + params.getAmount() - 1;
        if(end > result.getTotal()) {
            end = result.getTotal();
        }
        if(begin > end) {
            return "";
        }
        return begin + "-" + end;
    }
}
