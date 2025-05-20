package com.hcmus.mela.review.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
public class Review {
    @Id
    @Field(name = "_id")
    private UUID reviewId;

    @Field(name = "user_id")
    private UUID userId;

    @Field(name = "created_at")
    private Date createdAt;

    @Field(name = "exercise_list")
    private List<ExerciseReference> exerciseList;

    @Field(name = "section_list")
    private List<SectionReference> sectionList;
}
