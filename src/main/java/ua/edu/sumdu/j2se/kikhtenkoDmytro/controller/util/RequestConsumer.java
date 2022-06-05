package ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import org.slf4j.LoggerFactory;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.NotificationsController;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.InfoResponse;

import org.slf4j.Logger;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.Context;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.FxmlManager;

import java.net.http.HttpResponse;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class RequestConsumer implements
        Consumer<HttpResponse<String>>, Function<Throwable, Void> {
    private static final Logger logger =
            LoggerFactory.getLogger(RequestConsumer.class);
    private NotificationsController notificationsController;

    public RequestConsumer(NotificationsController controller) {
        setNotificationsController(controller);
    }

    public NotificationsController getNotificationsController() {
        return notificationsController;
    }

    public void setNotificationsController(NotificationsController controller) {
        this.notificationsController = controller;
    }

    @Override
    public void accept(HttpResponse<String> response) {
        try {
            if(response.statusCode() < 300) {
                onSuccess(response);
            } else if(response.statusCode() >= 400 &&
                    response.statusCode() < 600) {
                onError(response);
            }
            logger.debug(response.uri().toString() + " status " +
                    response.statusCode());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public final Void apply(Throwable throwable) {
        onConnectionError(throwable);
        return null;
    }

    public void onConnectionError(Throwable throwable) {
        logger.error("Connection error. " +
                throwable.getLocalizedMessage());
        Platform.runLater(() ->
                notificationsController.show(
                        new Notification(
                                "Server connection error",
                                Notification.Type.ERROR)));
    }

    public void onError(HttpResponse<String> response)
            throws Exception {
        try {
            InfoResponse infoResponse = new ObjectMapper().
                    readValue(response.body(), InfoResponse.class);
            if(infoResponse.getHttpStatusCode() == 401) {
                FxmlManager manager = Context.get("fxmlManager");
                manager.loadFxml("sign-in.fxml");
            }
            Platform.runLater(() ->
                    notificationsController.show(new Notification(
                            infoResponse.getMessage(),
                            Notification.Type.ERROR)));
        } catch (JsonProcessingException ignored) {}
    }

    public void onSuccess(
            HttpResponse<String> response) throws Exception {
        try {
            InfoResponse infoResponse = new ObjectMapper().
                    readValue(response.body(), InfoResponse.class);
            Platform.runLater(() ->
                    notificationsController.show(new Notification(
                            infoResponse.getMessage(),
                            Notification.Type.SUCCESS)));
        } catch (JsonProcessingException ignored) {}
    }
}
