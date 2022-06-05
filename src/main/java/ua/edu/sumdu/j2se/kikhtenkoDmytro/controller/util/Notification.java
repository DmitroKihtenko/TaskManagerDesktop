package ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util;

public class Notification {
    public enum Type {
        SUCCESS,
        ERROR
    }

    private Type type;
    private String message;

    public Notification(String message, Type type) {
        setMessage(message);
        setType(type);
    }

    public Notification(Type type) {
        setType(type);
        if(getType() == Type.ERROR) {
            setMessage("Unknown error");
        } else {
            setMessage("Operation successful");
        }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
