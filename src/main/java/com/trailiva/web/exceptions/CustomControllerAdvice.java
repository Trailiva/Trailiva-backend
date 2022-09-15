package com.trailiva.web.exceptions;

import com.trailiva.web.payload.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
                    (false, "Error occurred from request data", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        log.info("Error message ==> {}", e.getMessage());
        return new ResponseEntity<>(new ApiResponse
                (false, " Unknown error occurred", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
