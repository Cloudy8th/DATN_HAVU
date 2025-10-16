package com.project.hmartweb.application.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Setter
@Getter
public class RestData<Data> {
    private HttpStatus status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userMessage;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String devMessage;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Data data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> userMessages;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> devMessages;

    public RestData(HttpStatus status, String userMessage, String devMessage, Data data) {
        this.status = status;
        this.userMessage = userMessage;
        this.devMessage = devMessage;
        this.data = data;
    }

    public RestData(HttpStatus status, String userMessage, String devMessage) {
        this.status = status;
        this.userMessage = userMessage;
        this.devMessage = devMessage;
    }

    public RestData(HttpStatus status, String devMessage, Data data) {
        this.status = status;
        this.devMessage = devMessage;
        this.data = data;
    }

    public RestData(HttpStatus status, List<String> userMessages, List<String> devMessages) {
        this.status = status;
        this.userMessages = userMessages;
        this.devMessages = devMessages;
    }

    public RestData(HttpStatus status, String devMessage) {
        this.status = status;
        this.devMessage = devMessage;
    }

    public static RestData<?> error(HttpStatus status, String userMessage, String devMessage) {
        return new RestData<>(status, userMessage, devMessage);
    }

    public static RestData<?> error(HttpStatus status, String devMessage) {
        return new RestData<>(status, devMessage);
    }

    public static RestData<?> errors(HttpStatus status, List<String> userMessages, List<String> devMessages) {
        return new RestData<>(status, userMessages, devMessages);
    }
}
