package com.trailiva.web.exceptions;

import com.trailiva.web.payload.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;


@ControllerAdvice
@Slf4j
public class CustomControllerAdvice {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ApiResponse(false, "File too large!"));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handle(Exception e) {
        if (e instanceof NullPointerException) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        else if (e.getMessage().equalsIgnoreCase("User is disabled")){
            return new ResponseEntity<>(new ApiResponse(false,  "Your account is not verified!"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ApiResponse
                (false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
