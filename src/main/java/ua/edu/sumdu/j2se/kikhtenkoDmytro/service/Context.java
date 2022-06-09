package ua.edu.sumdu.j2se.kikhtenkoDmytro.service;

import java.util.HashMap;

public class Context {
    private static final HashMap<String, Object> values =
            new HashMap<>();

    public static <T> void set(String key, T value) {
        values.put(key, value);
    }

    public static <T> T get(String key) {
        return (T) values.get(key);
    }
}
