package edu.pure.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
public class UnimplementedException extends RuntimeException {
    public UnimplementedException() {
        super("Sorry, I'm working on it.🕊");
    }
}
