package ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Calendar extends DatetimeRange {
    private Map<LocalDateTime, Set<Task>> tasks;
    private int amount;

    public Map<LocalDateTime, Set<Task>> getTasks() {
        return tasks;
    }

    public void setTasks(Map<LocalDateTime, Set<Task>> tasks) {
        this.tasks = tasks;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Calendar) || !super.equals(o)) {
            return false;
        }
        Calendar calendar = (Calendar) o;
        return tasks.equals(calendar.tasks)
                && amount == calendar.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tasks, amount);
    }

    @Override
    public String toString() {
        return "Calendar{" +
                super.toString() +
                ", tasks=" + tasks +
                ", amount=" + amount +
                '}';
    }

    @Override
    public Calendar replicate() {
        Calendar value = new Calendar();
        value.update(this);
        return value;
    }

    @Override
    public void update(Object value) {
        super.update(value);
        if(value instanceof Calendar) {
            Calendar calendar = (Calendar) value;
            setAmount(calendar.getAmount());
            setTasks(calendar.getTasks());
        }
    }
}
