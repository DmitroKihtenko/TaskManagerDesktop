package ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util;

import javafx.util.StringConverter;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.NotificationsController;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.TimeConverters;

import java.time.Duration;

public class DurationConverter extends StringConverter<Duration> {
    private NotificationsController notificationsController;
    private PeriodFormatter formatter;
    private Notification notification;

    public DurationConverter(NotificationsController controller,
                             PeriodFormatter formatter) {
        setNotificationsController(controller);
        setFormatter(formatter);
        setNotification(new Notification("Invalid period " +
                "format. Please use format '" +
                getFormatter().print(Period.seconds(0)) + "'",
                Notification.Type.ERROR));
    }

    public NotificationsController getNotificationsController() {
        return notificationsController;
    }

    public void setNotificationsController(
            NotificationsController notificationsController) {
        this.notificationsController = notificationsController;
    }

    public PeriodFormatter getFormatter() {
        return formatter;
    }

    public void setFormatter(PeriodFormatter formatter) {
        this.formatter = formatter;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    @Override
    public String toString(java.time.Duration time) {
        if(time == null) {
            return "";
        }
        return TimeConverters.toJoda(time).
                toString(getFormatter());
    }

    @Override
    public Duration fromString(String s) {
        Duration value = null;
        if (s != null && !s.equals("")) {
            try {
                org.joda.time.Period date =
                        org.joda.time.Period.
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
