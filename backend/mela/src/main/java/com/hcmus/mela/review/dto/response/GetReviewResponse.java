package com.hcmus.mela.review.dto.response;

import com.hcmus.mela.review.dto.ReviewDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetReviewResponse {
    private String message;

    private List<ReviewDto> reviews;
}
