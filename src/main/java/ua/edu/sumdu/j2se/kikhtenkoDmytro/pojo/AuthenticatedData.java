package ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticatedData extends IdHolder {
    private String name;
    private ArrayList<Integer> authorities;
    private boolean authenticated;
    private String details;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(ArrayList<Integer> authorities) {
        this.authorities = authorities;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuthenticatedData) || !super.equals(o)) {
            return false;
        }
        AuthenticatedData that = (AuthenticatedData) o;
        return authenticated == that.authenticated &&
                Objects.equals(name, that.name) &&
                Objects.equals(authorities, that.authorities)
                && Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name,
                authorities, authenticated, details);
    }

    @Override
    public String toString() {
        return "Authentication{" +
                super.toString() +
                ", name='" + name + '\'' +
                ", authorities=" + authorities +
                ", authenticated=" + authenticated +
                ", details='" + details + '\'' +
                '}';
    }

    @Override
    public AuthenticatedData replicate() {
        AuthenticatedData authenticatedData = new AuthenticatedData();
        authenticatedData.update(this);
        return authenticatedData;
    }

    @Override
    public void update(Object value) {
        super.update(value);
        if(value instanceof AuthenticatedData) {
            AuthenticatedData authenticatedData = (AuthenticatedData) value;
            setName(authenticatedData.getName());
            setAuthenticated(authenticatedData.isAuthenticated());
            setAuthorities(authenticatedData.getAuthorities());
            setDetails(authenticatedData.getDetails());
        }
    }
}
