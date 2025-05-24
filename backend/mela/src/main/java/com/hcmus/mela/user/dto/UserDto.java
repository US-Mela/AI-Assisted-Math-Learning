package com.hcmus.mela.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private UUID userId;

    private String fullname;

    private String username;

    private Date birthday;

    private String imageUrl;

    private String levelTitle;
}
