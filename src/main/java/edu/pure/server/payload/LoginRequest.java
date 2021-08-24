package edu.pure.server.payload;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class LoginRequest {
    private @NotBlank String username;
    private @NotBlank String password;
}
