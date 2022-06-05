package ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Authentication extends IdHolder {
    private String name;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Authentication)) {
            return false;
        }
        Authentication authentication = (Authentication) o;
        return Objects.equals(name, authentication.name) &&
                Objects.equals(password, authentication.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, password);
    }

    @Override
    public String toString() {
        return "User{" +
                super.toString() +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public Authentication replicate() {
        Authentication authentication = new Authentication();
        authentication.update(this);
        return authentication;
    }

    @Override
    public void update(Object value) {
        super.update(value);
        if(value instanceof Authentication) {
            Authentication authentication = (Authentication) value;
            setName(authentication.getName());
            setPassword(authentication.getPassword());
        }
    }
}
