package com.hcmus.mela.ai.question.hint.model;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "prompt.exercise.hint")
public class QuestionHint {
    private Map<String, String> term;

    private Map<String, String> guide;
}
