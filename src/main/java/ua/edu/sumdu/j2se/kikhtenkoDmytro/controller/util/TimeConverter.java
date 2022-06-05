package ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util;

import javafx.util.StringConverter;
import org.joda.time.format.DateTimeFormatter;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.NotificationsController;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.TimeConverters;
import java.time.LocalTime;

public class TimeConverter extends StringConverter<LocalTime> {
    private NotificationsController notificationsController;
    private DateTimeFormatter formatter;
    private Notification notification;

    public TimeConverter(NotificationsController controller,
                         DateTimeFormatter formatter) {
        setNotificationsController(controller);
        setFormatter(formatter);
        setNotification(new Notification("Invalid time " +
                "format. Please use format '" +
                getFormatter().print(0) + "'",
                Notification.Type.ERROR));
    }

    public NotificationsController getNotificationsController() {
        return notificationsController;
    }

    public void setNotificationsController(
            NotificationsController notificationsController) {
        this.notificationsController = notificationsController;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public void setFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    @Override
    public String toString(java.time.LocalTime time) {
        if(time == null) {
            return "";
        }
        return TimeConverters.toJoda(time).
                toString(getFormatter());
    }

    @Override
    public LocalTime fromString(String s) {
        LocalTime value = null;
        if (s != null && !s.equals("")) {
            try {
                org.joda.time.LocalTime date =
                        org.joda.time.LocalTime.
                                parse(s, getFormatter());
                value = TimeConverters.fromJoda(date);
                notificationsController.hide(notification);
            } catch (IllegalArgumentException e) {
                notificationsController.show(
                        notification);
            }
        } else {
            notificationsController.hide(notification);
        }
        return value;
    }
}
