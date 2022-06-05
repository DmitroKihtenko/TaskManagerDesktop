package ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdHolder implements Replicable {
    private int id;

    public IdHolder() {
        id = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IdHolder)) {
            return false;
        }
        IdHolder idHolder = (IdHolder) o;
        return id == idHolder.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "IdHolder{" +
                "id=" + id +
                '}';
    }

    @Override
    public IdHolder replicate() {
        IdHolder value = new IdHolder();
        value.update(this);
        return value;
    }

    @Override
    public void update(Object value) {
        if(value instanceof IdHolder) {
            IdHolder idHolder = (IdHolder) value;
            setId(idHolder.getId());
        }
    }
}
