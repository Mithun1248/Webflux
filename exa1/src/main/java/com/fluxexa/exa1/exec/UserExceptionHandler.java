package com.fluxexa.exa1.exec;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Mono<String>> userAlreadyExists(UserAlreadyExistsException ex){
        return ResponseEntity.status(HttpStatusCode.valueOf(409))
                .body(Mono.just(ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Mono<String>> userNotFound(UserNotFoundException ex){
        return ResponseEntity.status(HttpStatusCode.valueOf(404))
                .body(Mono.just(ex.getMessage()));
    }
}
