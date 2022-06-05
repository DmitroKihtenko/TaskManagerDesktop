package ua.edu.sumdu.j2se.kikhtenkoDmytro.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.DateConverter;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.RequestConsumer;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.TimeConverter;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.*;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.SearchParams;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.ApiClient;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.Context;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.FxmlManager;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.TimeConverters;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.view.TimePicker;

import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Set;

public class CalendarController {
    private final static Logger logger =
            LoggerFactory.getLogger(TaskController.class);
    private final ObjectMapper mapper;
    private final ApiClient apiClient;
    private final ObjectProperty<Task> selectedTask;
    private final DatetimeRange datetimeRange;
    @FXML
    private TableView<TaskTime> taskTable;
    @FXML
    private GridPane tableContainer;
    @FXML
    private TableColumn<TaskTime, String> taskNameColumn;
    @FXML
    private TableColumn<TaskTime, String> taskTimeColumn;
    @FXML
    private DatePicker startDateInput;
    @FXML
    private TimePicker startTimeInput;
    @FXML
    private DatePicker endDateInput;
    @FXML
    private TimePicker endTimeInput;
    @FXML
    private Label totalResultsLine;
    @FXML
    private HeaderController headerController;
    @FXML
    private NotificationsController notificationsController;
    @FXML
    private IncomingController incomingController;

    public CalendarController() {
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

        org.joda.time.format.DateTimeFormatter dateFormatter =
                Context.get("dateFormatter");
        org.joda.time.format.DateTimeFormatter timeFormatter =
                Context.get("timeFormatter");
        org.joda.time.format.DateTimeFormatter dateTimeFormatter =
                Context.get("dateTimeFormatter");

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
                                getTime()).toString(dateTimeFormatter);
            }
            return new SimpleObjectProperty<>(value);
        });

        startDateInput.setConverter(new DateConverter(
                notificationsController,
                dateFormatter
        ));
        endDateInput.setConverter(new DateConverter(
                notificationsController,
                dateFormatter
        ));
        startTimeInput.setConverter(new TimeConverter(
                notificationsController,
                timeFormatter
        ));
        endTimeInput.setConverter(new TimeConverter(
                notificationsController,
                timeFormatter
        ));

        startDateInput.valueProperty().addListener((
                observableValue, localDate, t1) -> {
            if(t1 != null && startTimeInput.getValue() != null) {
                datetimeRange.setStart(LocalDateTime.of(
                        t1, startTimeInput.getValue()));
            } else {
                datetimeRange.setStart(null);
            }
            Context.set("startCalendarParams", getCalendarParams());
        });

        startTimeInput.valueProperty().addListener((
                observableValue, localDate, t1) -> {
            if(t1 != null && startDateInput.getValue() != null) {
                datetimeRange.setStart(LocalDateTime.of(
                        startDateInput.getValue(), t1));
            } else {
                datetimeRange.setStart(null);
            }
            Context.set("startCalendarParams", getCalendarParams());
        });

        endDateInput.valueProperty().addListener((
                observableValue, localDate, t1) -> {
            if(t1 != null && endTimeInput.getValue() != null) {
                datetimeRange.setEnd(LocalDateTime.of(
                        t1, endTimeInput.getValue()));
            } else {
                datetimeRange.setEnd(null);
            }
            Context.set("startCalendarParams", getCalendarParams());
        });

        endTimeInput.valueProperty().addListener((
                observableValue, localDate, t1) -> {
            if(t1 != null && endDateInput.getValue() != null) {
                datetimeRange.setEnd(LocalDateTime.of(
                        endDateInput.getValue(), t1));
            } else {
                datetimeRange.setEnd(null);
            }
            Context.set("startCalendarParams", getCalendarParams());
        });

        headerController.setNotificationsController(
                notificationsController);
        headerController.updateAuthorization();
        incomingController.setNotificationsController(
                notificationsController);
        incomingController.startIncomingThread();

        DatetimeRange params = Context.get("startCalendarParams");
        setCalendarParams(params);
        if(params != null) {
            refresh();
        }
    }

    public void setCalendarParams(DatetimeRange params) {
        if(params == null) {
            startDateInput.setValue(null);
            startTimeInput.setValue(null);
            endDateInput.setValue(null);
            endTimeInput.setValue(null);
        } else {
            if(params.getStart() != null) {
                startDateInput.setValue(params.getStart().toLocalDate());
                startTimeInput.setValue(params.getStart().toLocalTime());
            }
            if(params.getEnd() != null) {
                endDateInput.setValue(params.getEnd().toLocalDate());
                endTimeInput.setValue(params.getEnd().toLocalTime());
            }
        }
    }

    public DatetimeRange getCalendarParams() {
        return datetimeRange.replicate();
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
                Platform.runLater(() -> {
                    taskTable.getItems().clear();
                    taskTable.getItems().addAll(
                            calendarToList(result));
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

    public void refresh() {
        searchCalendar();
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
