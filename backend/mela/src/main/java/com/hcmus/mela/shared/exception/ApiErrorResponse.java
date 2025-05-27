package com.hcmus.mela.shared.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ApiErrorResponse {
    private String requestId;
    private int status;
    private String message;
    private String path;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public String toJson() {
        return "{"
                + "\"requestId\":\"" + requestId + "\","
                + "\"status\":" + status + ","
                + "\"message\":\"" + message + "\","
                + "\"path\":\"" + path + "\","
                + "\"timestamp\":\"" + timestamp + "\""
                + "}";
    }
}
