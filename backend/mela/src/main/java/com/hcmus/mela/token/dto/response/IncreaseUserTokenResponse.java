package com.hcmus.mela.token.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncreaseUserTokenResponse {
    private String message;

    private Integer token;
}
