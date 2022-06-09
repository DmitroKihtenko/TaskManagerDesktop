package ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util;

import javafx.util.StringConverter;
import org.joda.time.format.DateTimeFormatter;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.NotificationsController;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.TimeConverters;

import java.time.LocalDate;

public class DateConverter extends StringConverter<LocalDate> {
    private NotificationsController notificationsController;
    private DateTimeFormatter formatter;
    private Notification notification;

    public DateConverter(NotificationsController controller,
                         DateTimeFormatter formatter) {
        setNotificationsController(controller);
        setFormatter(formatter);
        setNotification(new Notification("Invalid date " +
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
    public String toString(java.time.LocalDate localDate) {
        if(localDate == null) {
            return "";
        }
        return TimeConverters.toJoda(localDate).
                toString(getFormatter());
    }

    @Override
    public LocalDate fromString(String s) {
        LocalDate value = null;
        if(s != null && !s.equals("")) {
            try {
                org.joda.time.LocalDate date =
                        org.joda.time.LocalDate.
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
