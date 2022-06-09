package ua.edu.sumdu.j2se.kikhtenkoDmytro.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.time.Duration;

public class DurationPicker extends TextField {
    private final ObjectProperty<Duration> value;
    private StringConverter<Duration> converter;

    public DurationPicker() {
        value = new SimpleObjectProperty<>();
        setConverter(new StringConverter<>() {
            @Override
            public String toString(Duration localTime) {
                return "";
            }

            @Override
            public Duration fromString(String s) {
                return null;
            }
        });
       focusedProperty().addListener(
               (observableValue, actionEventEventHandler, t1) -> {
           if(!t1) {
               this.value.setValue(getConverter().
                       fromString(getText()));
           }
        });
        valueProperty().addListener(
                (observableValue, duration, t1) -> {
            setText(getConverter().toString(t1));
        });
    }

    public Duration getValue() {
        return value.getValue();
    }

    public void setValue(Duration value) {
        this.value.setValue(value);
    }

    public ObjectProperty<Duration> valueProperty() {
        return value;
    }

    public StringConverter<Duration> getConverter() {
        return converter;
    }

    public void setConverter(StringConverter<Duration> converter) {
        this.converter = converter;
    }
}
