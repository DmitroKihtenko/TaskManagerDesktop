package ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo;

import java.util.HashMap;

public class URIParams {
    private final HashMap<String, String> params;

    public URIParams() {
        params = new HashMap<>();
    }

    public void set(String key, Object value) {
        params.put(key, value.toString());
    }

    public Object get(String key) {
        Object value = null;
        if(params.containsKey(key)) {
            value = params.get(key);
        }
        return value;
    }

    public void remove(String key) {
        params.remove(key);
    }

    public void clear() {
        params.clear();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(String key : params.keySet()) {
            if(first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(key).append("=").append(params.get(key));
        }
        return result.toString();
    }
}
