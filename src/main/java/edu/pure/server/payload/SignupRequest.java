package edu.pure.server.payload;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class SignupRequest {
    private @NotBlank @Size(min = 2, max = 20) String name;
    private @NotBlank @Size(max = 20) String username;
    private @NotBlank @Size(max = 50) @Email String email;
    private @NotBlank @Size(max = 20) String password;
}
