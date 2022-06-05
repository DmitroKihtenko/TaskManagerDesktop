package ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo;

import java.util.ArrayList;
import java.util.Objects;

public class User extends Authentication {
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
        if (!(o instanceof User) || !super.equals(o)) {
            return false;
        }
        User that = (User) o;
        return enabled == that.enabled &&
                Objects.equals(authorities, that.authorities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), authorities, enabled);
    }

    @Override
    public String toString() {
        return "User{" +
                super.toString() +
                ", authorities=" + authorities +
                ", enabled=" + enabled +
                '}';
    }

    @Override
    public User replicate() {
        User user  = new User();
        user.update(this);
        return user;
    }

    @Override
    public void update(Object value) {
        super.update(value);
        if(value instanceof User) {
            User user = (User) value;
            setAuthorities(user.getAuthorities());
            setEnabled(user.isEnabled());
        }
    }
}
