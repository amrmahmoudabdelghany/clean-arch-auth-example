package com.gray.auth.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class GlobalWebUIExceptionHandler {

    @ExceptionHandler({NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoResourceFoundException ex) {
        HttpStatus statusCode = HttpStatus.NOT_FOUND ;
        String message  = "Oops! The resource you're looking for cannot be found." +
                " Please check the URL and try again." ;
        return ResponseEntity.status(statusCode)
                .body(new ErrorResponse(statusCode.name() , message)) ;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public class ErrorResponse {
        private String error ;
        private String message ;
        private final String timestamp = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE) ;

    }
}
