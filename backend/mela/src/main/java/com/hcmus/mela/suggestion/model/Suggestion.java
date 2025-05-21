package com.hcmus.mela.suggestion.model;

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
@Document(collection = "suggestions")
public class Suggestion {
    @Id
    @Field(name = "_id")
    private UUID suggestionId;

    @Field(name = "user_id")
    private UUID userId;

    @Field(name = "created_at")
    private Date createdAt;

    @Field(name = "suggestion_list")
    private List<SectionReference> sectionList;
}
