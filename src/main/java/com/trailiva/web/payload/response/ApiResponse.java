package com.trailiva.web.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ApiResponse {
    private boolean isSuccessful;
    private String message;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;
    private Object data;
    private HttpStatus status;

    public ApiResponse(boolean isSuccessful, String message, HttpStatus status) {
        this.isSuccessful = isSuccessful;
        this.message = message;
        localDateTime = LocalDateTime.now();
        this.status = status;
    }

    public ApiResponse(boolean isSuccessful, String message){
        this.isSuccessful = isSuccessful;
        this.message = message;
    }

    public ApiResponse(boolean isSuccessful, String message, Object data, HttpStatus status) {
        this(isSuccessful, message, status);
        this.data = data;
    }
}
