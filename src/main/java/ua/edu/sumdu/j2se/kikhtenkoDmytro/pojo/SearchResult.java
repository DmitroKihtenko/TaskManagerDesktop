package ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResult<T> {
    private int total;
    private int current;
    private int from;
    private ArrayList<T> result;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public ArrayList<T> getResult() {
        return result;
    }

    public void setResult(ArrayList<T> result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SearchResult)) {
            return false;
        }
        SearchResult<?> that = (SearchResult<?>) o;
        return total == that.total &&
                current == that.current
                && from == that.from
                && Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, current, from, result);
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "total=" + total +
                ", current=" + current +
                ", from=" + from +
                ", result=" + result +
                '}';
    }
}
