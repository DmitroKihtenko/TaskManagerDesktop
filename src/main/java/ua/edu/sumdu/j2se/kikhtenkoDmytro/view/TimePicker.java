package ua.edu.sumdu.j2se.kikhtenkoDmytro.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.time.LocalTime;

public class TimePicker extends TextField {
    private final ObjectProperty<LocalTime> value;
    private StringConverter<LocalTime> converter;

    public TimePicker() {
        value = new SimpleObjectProperty<>();
        setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalTime localTime) {
                return "";
            }

            @Override
            public LocalTime fromString(String s) {
                return null;
            }
        });
        focusedProperty().addListener((observableValue,
                                        actionEventEventHandler, t1) -> {
            if(!t1) {
                value.setValue(getConverter().
                        fromString(getText()));
            }
        });
        valueProperty().addListener(
                (observableValue, localTime, t1) -> {
            setText(getConverter().toString(t1));
        });
    }

    public LocalTime getValue() {
        return value.getValue();
    }

    public void setValue(LocalTime value) {
        this.value.setValue(value);
    }

    public ObjectProperty<LocalTime> valueProperty() {
        return value;
    }

    public StringConverter<LocalTime> getConverter() {
        return converter;
    }

    public void setConverter(StringConverter<LocalTime> converter) {
        this.converter = converter;
    }
}
