package com.hcmus.mela.ai.chatbot.service;

import com.hcmus.mela.ai.chatbot.dto.request.CreateConversationRequestDto;
import com.hcmus.mela.ai.chatbot.dto.request.MessageRequestDto;
import com.hcmus.mela.ai.chatbot.dto.response.ChatResponseDto;
import com.hcmus.mela.ai.chatbot.model.Message;

import java.util.List;
import java.util.UUID;

public interface ConversationService {
    Object identifyProblem(Message message);

    Object resolveConfusion(List<Message> messageList, String context);

    Object reviewSubmission(List<Message> messageList, String context);

    Object provideSolution(List<Message> messageList, String context);

    ChatResponseDto getSolutionResponse(MessageRequestDto messageRequestDto, UUID conversationId, UUID userId);

    ChatResponseDto getReviewSubmissionResponse(MessageRequestDto messageRequestDto, UUID conversationId, UUID userId);

    ChatResponseDto sendMessage(MessageRequestDto messageRequestDto, UUID conversationId, UUID userId);

    ChatResponseDto createConversation(UUID userId, CreateConversationRequestDto createConversationRequestDto);
}
