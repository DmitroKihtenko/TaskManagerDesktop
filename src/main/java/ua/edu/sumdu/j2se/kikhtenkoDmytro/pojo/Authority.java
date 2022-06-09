package ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Authority extends IdHolder {
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Authority) || !super.equals(o)) {
            return false;
        }
        Authority authority = (Authority) o;
        return Objects.equals(name, authority.name) &&
                Objects.equals(description, authority.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, description);
    }

    @Override
    public String toString() {
        return "Authority{" +
                super.toString() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public Authority replicate() {
        Authority authority = new Authority();
        authority.update(this);
        return authority;
    }

    @Override
    public void update(Object value) {
        super.update(value);
        if(value instanceof Authority) {
            Authority authority = (Authority) value;
            setName(authority.getName());
            setDescription(authority.getDescription());
        }
    }
}
