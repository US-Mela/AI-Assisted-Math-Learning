package com.hcmus.mela.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProfileRequest {

    private String fullname;

    private String imageUrl;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date birthday;

    private String levelTitle;
}
