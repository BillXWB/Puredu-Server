package edu.pure.server.opedukg.payload;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class LoginRequest {
    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String password;
}
