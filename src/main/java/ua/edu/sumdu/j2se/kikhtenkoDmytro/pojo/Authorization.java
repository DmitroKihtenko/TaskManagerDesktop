package ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Authorization extends Authentication {
    private ArrayList<Integer> authorities;
    private boolean enabled;

    public ArrayList<Integer> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(ArrayList<Integer> authorities) {
        this.authorities = authorities;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Authorization) || !super.equals(o)) {
            return false;
        }
        Authorization that = (Authorization) o;
        return enabled == that.enabled &&
                Objects.equals(authorities, that.authorities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), authorities, enabled);
    }

    @Override
    public String toString() {
        return "Authorization{" +
                super.toString() +
                ", authorities=" + authorities +
                ", enabled=" + enabled +
                '}';
    }

    @Override
    public Authorization replicate() {
        Authorization authorization  = new Authorization();
        authorization.update(this);
        return authorization;
    }

    @Override
    public void update(Object value) {
        super.update(value);
        if(value instanceof Authorization) {
            Authorization authorization = (Authorization) value;
            setAuthorities(authorization.getAuthorities());
            setEnabled(authorization.isEnabled());
        }
    }
}
