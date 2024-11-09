package com.hcmus.mela.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String newPassword;

    @NotEmpty
    private String jwt;
}
