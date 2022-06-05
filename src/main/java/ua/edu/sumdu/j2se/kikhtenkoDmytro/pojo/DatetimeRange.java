package ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatetimeRange implements Replicable {
    private LocalDateTime start;
    private LocalDateTime end;

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DatetimeRange)) {
            return false;
        }
        DatetimeRange that = (DatetimeRange) o;
        return Objects.equals(start, that.start)
                && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "CalendarParams{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

    @Override
    public DatetimeRange replicate() {
        DatetimeRange params = new DatetimeRange();
        params.update(this);
        return params;
    }

    @Override
    public void update(Object value) {
        if(value instanceof DatetimeRange) {
            DatetimeRange params = (DatetimeRange) value;
            setStart(params.getStart());
            setEnd(params.getEnd());
        }
    }
}
