package edu.pure.server.payload;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class ChangePasswordRequest {
    private String oldPassword;
    private
    @NotBlank(message = "密码需要至少包含一个非空字符！")
    @Size(max = 20, message = "密码至多20个字符！")
    String newPassword;
}
