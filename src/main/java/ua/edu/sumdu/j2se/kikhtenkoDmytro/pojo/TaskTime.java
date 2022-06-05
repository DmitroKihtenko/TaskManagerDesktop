package ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo;

import java.time.LocalDateTime;
import java.util.Objects;

public class TaskTime {
    private Task task;
    private LocalDateTime time;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskTime)) {
            return false;
        }
        TaskTime taskTime = (TaskTime) o;
        return Objects.equals(task, taskTime.task) &&
                Objects.equals(time, taskTime.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, time);
    }

    @Override
    public String toString() {
        return "TaskTime{" +
                "task=" + task +
                ", time=" + time +
                '}';
    }
}
