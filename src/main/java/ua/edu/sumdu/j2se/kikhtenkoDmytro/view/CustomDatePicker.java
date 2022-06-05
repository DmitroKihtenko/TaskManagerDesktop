package ua.edu.sumdu.j2se.kikhtenkoDmytro.view;

import javafx.scene.control.DatePicker;

import java.util.Objects;

public class CustomDatePicker extends DatePicker {
    public CustomDatePicker() {
        getEditor().textProperty().addListener(
                (observableValue, s, t1) -> {
            if(!Objects.equals(s, t1)) {
                setValue(getConverter().fromString(t1));
            }
        });
    }
}
