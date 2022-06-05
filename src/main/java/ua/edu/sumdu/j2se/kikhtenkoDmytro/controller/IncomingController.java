package ua.edu.sumdu.j2se.kikhtenkoDmytro.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.ApiClient;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.RequestConsumer;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.Calendar;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.DatetimeRange;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.Task;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.TaskTime;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.SearchParams;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.*;

import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Set;

public class IncomingController {
    private final static Logger logger =
            LoggerFactory.getLogger(TaskController.class);
    private final ObjectMapper mapper;
    private final ApiClient apiClient;
    private final ObjectProperty<Task> selectedTask;
    private final DatetimeRange datetimeRange;
    @FXML
    private Label incomingTitle;
    @FXML
    private TableView<TaskTime> taskTable;
    @FXML
    private GridPane tableContainer;
    @FXML
    private TableColumn<TaskTime, String> taskNameColumn;
    @FXML
    private TableColumn<TaskTime, String> taskTimeColumn;
    @FXML
    private Label totalResultsLine;
    private NotificationsController notificationsController;
    private static final Duration incomingUpdateInterval =
            Duration.ofSeconds(60);
    private static final Duration incomingPeriod =
            Duration.ofMinutes(60);

    public IncomingController() {
        apiClient = Context.get("apiClient");
        mapper = Context.get("mapper");
        selectedTask = new SimpleObjectProperty<>();
        datetimeRange = new DatetimeRange();
    }

    @FXML
    public void initialize() {
        taskTable.getColumns().get(0).setMinWidth(
                tableContainer.getPrefWidth() / 2);
        taskTable.getColumns().get(1).setMinWidth(
                tableContainer.getPrefWidth() / 2);

        org.joda.time.format.DateTimeFormatter timeFormatter =
                Context.get("timeFormatter");

        taskTable.getSelectionModel().selectedItemProperty().
                addListener((observableValue, t1, t2) -> {
                    if(t2 == null) {
                        selectedTask.setValue(null);
                    } else {
                        selectedTask.setValue(t2.getTask());
                    }
                });

        taskNameColumn.setCellValueFactory(features -> {
            String value = null;
            if (features.getValue() != null &&
                    features.getValue().getTask() != null) {
                value = features.getValue().getTask().getTitle();
            }
            return new SimpleObjectProperty<>(value);
        });

        taskTimeColumn.setCellValueFactory(features -> {
            String value = null;
            if (features.getValue() != null &&
                    features.getValue().getTime() != null) {
                value = TimeConverters.toJoda(features.getValue().
                        getTime()).toString(timeFormatter);
            }
            return new SimpleObjectProperty<>(value);
        });

        incomingTitle.setText("To do in next " +
                incomingPeriod.toMinutes() + " minutes");
    }

    public NotificationsController getNotificationsController() {
        return notificationsController;
    }

    public void setNotificationsController(
            NotificationsController notificationsController) {
        this.notificationsController = notificationsController;
    }

    public void startIncomingThread() {
        Thread incomingThread = new Thread() {
            private final Logger logger =
                    LoggerFactory.getLogger(Thread.class);

            @Override
            public void run() {
                logger.debug(getName() + " thread started");

                LocalDateTime lastSearchTime = LocalDateTime.MIN;
                while(true) {
                    if(isInterrupted()) {
                        logger.debug(getName() + " thread stopped");
                        return;
                    }
                    if(LocalDateTime.now().isAfter(
                            lastSearchTime.plus(
                                    incomingUpdateInterval))) {
                        datetimeRange.setStart(LocalDateTime.now());
                        datetimeRange.setEnd(LocalDateTime.now().
                                plus(incomingPeriod));
                        searchCalendar();
                        lastSearchTime = LocalDateTime.now();
                    }
                }
            }
        };
        incomingThread.setName("Incoming tasks");
        incomingThread.setDaemon(true);
        ThreadManager threadManager = Context.get("threadManager");
        threadManager.addThread(incomingThread);
    }

    public void searchCalendar() {
        HttpClient client = apiClient.getClient();
        HttpRequest request;
        try {
            request = apiClient.getRequest(
                    "GET",
                    apiClient.getUri("/calendar"),
                    mapper.writeValueAsString(datetimeRange)
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
                        Calendar result = mapper.readValue(
                                response.body(),
                                Calendar.class
                        );
                        taskTable.getItems().clear();
                        taskTable.getItems().addAll(
                                calendarToList(result));
                        Platform.runLater(() -> {
                            if(taskTable.getItems().isEmpty()) {
                                unselectTask();
                            }
                            totalResultsLine.setText(
                                    "Total " + result.getAmount());
                        });
                    }
                };

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                thenAccept(consumer).exceptionally(consumer);
    }

    public void editTask() {
        SearchParams params = new SearchParams();
        String regex = "";
        if(selectedTask.getValue() != null) {
            regex = "^" + selectedTask.getValue().getTitle() + "$";
        }
        params.setSearchRegex(regex);
        Context.set("startTaskSearchParams", params);
        FxmlManager manager = Context.get("fxmlManager");
        manager.loadFxml("task-list.fxml");
    }

    public void unselectTask() {
        taskTable.getSelectionModel().clearSelection();
    }

    protected LinkedList<TaskTime> calendarToList(Calendar calendar) {
        LinkedList<TaskTime> taskTimeList = new LinkedList<>();
        for(LocalDateTime time : calendar.getTasks().keySet()) {
            Set<Task> tasks = calendar.getTasks().get(time);
            for(Task task : tasks) {
                TaskTime taskTime = new TaskTime();
                taskTime.setTime(time);
                taskTime.setTask(task);
                taskTimeList.add(taskTime);
            }
        }
        return taskTimeList;
    }
}
