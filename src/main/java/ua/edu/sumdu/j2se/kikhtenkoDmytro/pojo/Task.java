package ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task extends IdHolder {
    private String title;
    private boolean isActive;
    private LocalDateTime start;
    private LocalDateTime end;
    private Duration interval;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

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

    public Duration getInterval() {
        return interval;
    }

    public void setInterval(Duration interval) {
        this.interval = interval;
    }

    public boolean isRepeated() {
        return interval != null && end != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task) || !super.equals(o)) {
            return false;
        }
        Task task = (Task) o;
        return isActive == task.isActive &&
                Objects.equals(title, task.title)
                && Objects.equals(start, task.start)
                && Objects.equals(end, task.end)
                && Objects.equals(interval, task.interval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, isActive, start, end, interval);
    }

    @Override
    public String toString() {
        return "Task{" +
                super.toString() +
                ", title=" + title +
                ", isActive=" + isActive +
                ", start=" + start +
                ", end=" + end +
                ", interval=" + interval +
                '}';
    }

    @Override
    public Task replicate() {
        Task task = new Task();
        task.update(this);
        return task;
    }

    @Override
    public void update(Object value) {
        super.update(value);
        if(value instanceof Task) {
            Task task = (Task) value;
            setTitle(task.getTitle());
            setStart(task.getStart());
            setEnd(task.getEnd());
            setInterval(task.getInterval());
            setActive(task.isActive());
        }
    }
}
