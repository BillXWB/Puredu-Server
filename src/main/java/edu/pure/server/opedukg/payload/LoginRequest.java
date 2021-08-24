package edu.pure.server.opedukg.payload;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class LoginRequest {
    private @NotBlank String phoneNumber;
    private @NotBlank String password;
}
