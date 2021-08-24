package edu.pure.server.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse {
    private final Boolean success;
    private final String message;
}
