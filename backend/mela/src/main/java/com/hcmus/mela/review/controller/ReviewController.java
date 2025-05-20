package com.hcmus.mela.review.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.review.dto.request.UpdateReviewRequest;
import com.hcmus.mela.review.dto.response.GetReviewsResponse;
import com.hcmus.mela.review.dto.response.UpdateReviewResponse;
import com.hcmus.mela.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    private final JwtTokenService jwtTokenService;

    @GetMapping
    @Operation(
            tags = "Review Service",
            summary = "Get reviews by user ID",
            description = "Retrieves all reviews of the user with given user ID."
    )
    public ResponseEntity<GetReviewsResponse> getReviews(@RequestHeader("Authorization") String authorizationHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        log.info("Get review sections and exercises for user {}.", userId);

        GetReviewsResponse response = reviewService.getReviews(userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reviewId}")
    @Operation(
            tags = "Review Service",
            summary = "Update review with review ID",
            description = "Update review that has given ID."
    )
    public ResponseEntity<UpdateReviewResponse> updateReview(@RequestHeader("Authorization") String authorizationHeader,
                                                             @PathVariable("reviewId") String reviewId,
                                                             @RequestBody UpdateReviewRequest request) {
        log.info("Update review with review ID {}.", reviewId);

        UpdateReviewResponse response = reviewService.updateReview(UUID.fromString(reviewId), request);

        return ResponseEntity.ok(response);
    }
}
