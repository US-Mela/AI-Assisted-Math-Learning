package com.hcmus.mela.review.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ExerciseReference {
    @Field("item_id")
    private UUID exerciseId;

    @Field("ordinal_number")
    private Integer ordinalNumber;

    @Field("is_done")
    private Boolean isDone;
}
