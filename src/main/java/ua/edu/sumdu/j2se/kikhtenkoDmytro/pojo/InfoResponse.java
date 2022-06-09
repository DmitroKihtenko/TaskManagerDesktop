package ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InfoResponse {
    private int httpStatusCode;
    private String message;
    private Object content;

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int code) {
        httpStatusCode = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InfoResponse)) {
            return false;
        }
        InfoResponse that = (InfoResponse) o;
        return httpStatusCode == that.httpStatusCode &&
                Objects.equals(message, that.message)
                && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpStatusCode, message, content);
    }

    @Override
    public String toString() {
        return "InfoResponse{" +
                "httpStatus=" + httpStatusCode +
                ", message='" + message + '\'' +
                ", content=" + content +
                '}';
    }
}
