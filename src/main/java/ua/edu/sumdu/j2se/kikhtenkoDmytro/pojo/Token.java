package ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Token {
    private String type;
    private String jwtToken;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Token)) {
            return false;
        }
        Token token = (Token) o;
        return Objects.equals(type, token.type) &&
                Objects.equals(jwtToken, token.jwtToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, jwtToken);
    }

    @Override
    public String toString() {
        return "Token{" +
                "type='" + type + '\'' +
                ", jwtToken='" + jwtToken + '\'' +
                '}';
    }
}
