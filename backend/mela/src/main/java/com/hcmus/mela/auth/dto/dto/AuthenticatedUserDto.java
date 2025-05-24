package com.hcmus.mela.auth.dto.dto;


import com.hcmus.mela.auth.model.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
public class AuthenticatedUserDto {

    private UUID userId;

    private String fullname;

    private String username;

    private String password;

    private UserRole userRole;

    private String levelTitle;
}
