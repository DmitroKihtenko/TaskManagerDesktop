package ua.edu.sumdu.j2se.kikhtenkoDmytro.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.Notification;

public class NotificationsController {
    private static final String ERROR_STYLE = "bg-error";
    private static final String SUCCESS_STYLE = "bg-success";
    private Integer notificationHash;
    @FXML
    public FlowPane parent;
    @FXML
    public HBox notificationContainer;
    @FXML
    public Label notificationText;

    @FXML
    public void initialize() {
        hide();
    }

    protected void resolveClass(
            ObservableList<String> classes,
            Notification notification
    ) {
        if(notification.getType() == Notification.Type.ERROR) {
            notificationContainer.getStyleClass().remove(SUCCESS_STYLE);
            notificationContainer.getStyleClass().add(ERROR_STYLE);
        } else {
            notificationContainer.getStyleClass().remove(ERROR_STYLE);
            notificationContainer.getStyleClass().add(SUCCESS_STYLE);
        }
    }

    public void show(Notification notification) {
        notificationHash = notification.hashCode();
        resolveClass(notificationContainer.getStyleClass(), notification);
        notificationText.setText(" " + notification.getMessage() + " ");
        parent.setVisible(true);
    }

    public void hide(Notification notification) {
        if(notificationHash != null
                && notification.hashCode() == notificationHash) {
            hide();
        }
    }

    public void hide() {
        parent.setVisible(false);
    }
}
