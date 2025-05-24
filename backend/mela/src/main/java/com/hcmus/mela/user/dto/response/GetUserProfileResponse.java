package com.hcmus.mela.user.dto.response;

import com.hcmus.mela.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserProfileResponse {
    private UserDto user;

    private String message;
}
