package com.hcmus.mela.lecture.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDto {
    private Integer topicId;
    private String topicName;
}