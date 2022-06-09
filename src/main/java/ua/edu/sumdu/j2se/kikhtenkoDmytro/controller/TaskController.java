package ua.edu.sumdu.j2se.kikhtenkoDmytro.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.*;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.Task;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.Context;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.view.DurationPicker;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.view.TimePicker;

import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Objects;

public class TaskController {
    private final static Logger logger =
            LoggerFactory.getLogger(TaskController.class);
    private final ObjectMapper mapper;
    private final Task task;
    private final ApiClient apiClient;
    private final ObjectProperty<Task> createdTask;
    private final ObjectProperty<Task> editedTask;
    private final ObjectProperty<Task> deletedTask;
    private NotificationsController notificationsController;
    @FXML
    private TextField titleInput;
    @FXML
    private DatePicker startDateInput;
    @FXML
    private TimePicker startTimeInput;
    @FXML
    private DatePicker endDateInput;
    @FXML
    private TimePicker endTimeInput;
    @FXML
    private DurationPicker intervalInput;
    @FXML
    private CheckBox activeInput;
    @FXML
    private Button editTaskButton;
    @FXML
    private Button createTaskButton;
    @FXML
    private Button deleteTaskButton;

    public TaskController() {
        mapper = Context.get("mapper");
        apiClient = Context.get("apiClient");
        createdTask = new SimpleObjectProperty<>();
        editedTask = new SimpleObjectProperty<>();
        deletedTask = new SimpleObjectProperty<>();
        task = new Task();
    }

    @FXML
    public void initialize() {
        titleInput.textProperty().addListener((
                observableValue, s, t1) -> {
            task.setTitle(t1);
        });

        startDateInput.valueProperty().addListener((
                observableValue, localDate, t1) -> {
            if(startTimeInput.getValue() != null && t1 != null) {
                task.setStart(LocalDateTime.of(
                        t1,
                        startTimeInput.getValue()
                ));
            } else {
                task.setStart(null);
            }
        });

        startTimeInput.valueProperty().addListener((
                observableValue, localTime, t1) -> {
            if(t1 != null && startDateInput.getValue() != null) {
                task.setStart(LocalDateTime.of(
                        startDateInput.getValue(),
                        t1
                ));
            } else {
                task.setStart(null);
            }
        });

        endDateInput.valueProperty().addListener((
                observableValue, localDate, t1) -> {
            if(endTimeInput.getValue() != null && t1 != null) {
                task.setEnd(LocalDateTime.of(
                        t1,
                        endTimeInput.getValue()
                ));
            } else {
                task.setEnd(null);
            }
        });

        endTimeInput.valueProperty().addListener((
                observableValue, localTime, t1) -> {
            if(t1 != null && endDateInput.getValue() != null) {
                task.setEnd(LocalDateTime.of(
                        endDateInput.getValue(),
                        t1
                ));
            } else {
                task.setEnd(null);
            }
        });

        intervalInput.valueProperty().addListener((
                observableValue, duration, t1) -> {
            task.setInterval(t1);
        });

        activeInput.selectedProperty().addListener((
                observableValue, aBoolean, t1) -> {
            task.setActive(t1);
        });

        setTask(null);
    }

    public Task getTask() {
        return task.replicate();
    }

    public void setTask(Task task) {
        if(task == null) {
            titleInput.setText("");
            startDateInput.setValue(null);
            startTimeInput.setValue(null);
            endDateInput.setValue(null);
            endTimeInput.setValue(null);
            intervalInput.setValue(null);
            activeInput.setSelected(false);

            editTaskButton.setDisable(true);
            deleteTaskButton.setDisable(true);
        } else {
            this.task.setId(task.getId());
            titleInput.setText(Objects.requireNonNull(
                    task.getTitle(), ""));
            if(task.getStart() != null) {
                startDateInput.setValue(task.getStart().toLocalDate());
                startTimeInput.setValue(task.getStart().toLocalTime());
            } else {
                startDateInput.setValue(null);
                startTimeInput.setValue(null);
            }
            if(task.getEnd() != null) {
                endDateInput.setValue(task.getEnd().toLocalDate());
                endTimeInput.setValue(task.getEnd().toLocalTime());
            } else {
                endDateInput.setValue(null);
                endTimeInput.setValue(null);
            }
            intervalInput.setValue(task.getInterval());
            activeInput.setSelected(task.isActive());

            editTaskButton.setDisable(false);
            deleteTaskButton.setDisable(false);
        }
    }

    public ObjectProperty<Task> createdTaskProperty() {
        return createdTask;
    }

    public ObjectProperty<Task> editedTaskProperty() {
        return editedTask;
    }

    public ObjectProperty<Task> deletedTaskProperty() {
        return deletedTask;
    }

    public NotificationsController getNotificationsController() {
        return notificationsController;
    }

    public void setNotificationsController(
            NotificationsController controller) {
        this.notificationsController = controller;

        org.joda.time.format.DateTimeFormatter dateFormatter =
                Context.get("dateFormatter");
        org.joda.time.format.DateTimeFormatter timeFormatter =
                Context.get("timeFormatter");
        org.joda.time.format.PeriodFormatter periodFormatter =
                Context.get("periodFormatter");

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
        intervalInput.setConverter(new DurationConverter(
                notificationsController,
                periodFormatter
        ));
    }

    public void createTask() {
        Task task = getTask();
        HttpClient client = apiClient.getClient();
        HttpRequest request;
        try {
            request = apiClient.getRequest(
                    "POST",
                    apiClient.getUri("/tasks"),
                    mapper.writeValueAsString(task)
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
                createdTaskProperty().setValue(task);
            }
        };

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                thenAccept(consumer).exceptionally(consumer);
    }

    public void editTask() {
        Task task = getTask();
        HttpClient client = apiClient.getClient();
        HttpRequest request;
        try {
            request = apiClient.getRequest(
                    "PATCH",
                    apiClient.getUri("/tasks/" + task.getId()),
                    mapper.writeValueAsString(task)
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
                editedTaskProperty().setValue(task);
            }
        };

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                thenAccept(consumer).exceptionally(consumer);
    }

    public void deleteTask() {
        Task task = getTask();
        if(task != null) {
            HttpClient client = apiClient.getClient();
            HttpRequest request;
            try {
                request = apiClient.getRequest(
                        "DELETE",
                        apiClient.getUri("/tasks/" + task.getId()),
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
                    deletedTaskProperty().setValue(task);
                }
            };

            client.sendAsync(
                    request,
                    HttpResponse.BodyHandlers.ofString()).
                    thenAccept(consumer).exceptionally(consumer);
        }
    }
}
