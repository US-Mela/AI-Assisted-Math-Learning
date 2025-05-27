package com.hcmus.mela.ai.chatbot.dto.request;

import com.hcmus.mela.shared.validator.AtLeastOneNotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AtLeastOneNotEmpty(fields = {"text", "imageUrl"}, message = "Field must not be empty")
public class MessageRequestDto {
    private String text;

    private String imageUrl;

    public Map<String , Object> getContent() {
        Map<String , Object> content = new HashMap<>();
        if(text != null && !text.isEmpty()) {
            content.put("text", text);
        }
        if(imageUrl != null && !imageUrl.isEmpty()) {
            content.put("imageUrl", imageUrl);
        }
        return content;
    }
}
