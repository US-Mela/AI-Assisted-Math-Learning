package com.hcmus.mela.ai.chatbot.service;

import com.hcmus.mela.ai.chatbot.dto.request.CreateConversationRequestDto;
import com.hcmus.mela.ai.chatbot.dto.request.MessageRequestDto;
import com.hcmus.mela.ai.chatbot.dto.response.AiResponseContent;
import com.hcmus.mela.ai.chatbot.dto.response.ConversationMetadataDto;
import com.hcmus.mela.ai.chatbot.dto.response.ChatResponseDto;
import com.hcmus.mela.ai.chatbot.dto.response.MessageResponseDto;
import com.hcmus.mela.ai.chatbot.model.*;
import com.hcmus.mela.ai.chatbot.repository.ConversationRepository;
import com.hcmus.mela.ai.client.config.AiClientProperties;
import com.hcmus.mela.ai.client.web.AiWebClient;
import com.hcmus.mela.ai.client.builder.AiRequestBodyFactory;
import com.hcmus.mela.ai.client.filter.AiResponseFilter;
import com.hcmus.mela.shared.exception.BadRequestException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final AiWebClient aiWebClient;
    private final AiClientProperties.ChatBot chatBotProperties;
    private final ChatBotPrompt chatBotPrompt;
    private final AiRequestBodyFactory aiRequestBodyFactory;
    private final AiResponseFilter aiResponseFilter;
    private final ConversationRepository conversationRepository;
    private final MongoTemplate mongoTemplate;

    public ConversationServiceImpl(AiWebClient aiWebClient,
                                   AiClientProperties aiClientProperties,
                                   ChatBotPrompt chatBotPrompt,
                                   AiRequestBodyFactory aiRequestBodyFactory,
                                   ConversationRepository conversationRepository,
                                   AiResponseFilter aiResponseFilter,
                                   MongoTemplate mongoTemplate) {
        this.aiWebClient = aiWebClient;
        this.chatBotProperties = aiClientProperties.getChatBot();
        this.chatBotPrompt = chatBotPrompt;
        this.aiRequestBodyFactory = aiRequestBodyFactory;
        this.conversationRepository = conversationRepository;
        this.aiResponseFilter = aiResponseFilter;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Object identifyProblem(Message message) {

        Object requestBody = aiRequestBodyFactory.createRequestBodyForChatBot(
                chatBotPrompt.getIdentifyProblem().getInstruction(),
                List.of(message),
                chatBotProperties);

        return aiWebClient.fetchAiResponse(chatBotProperties, requestBody);
    }

    @Override
    public Object resolveConfusion(List<Message> messageList, String context) {
        Object requestBody = aiRequestBodyFactory.createRequestBodyForChatBot(
                chatBotPrompt.getResolveConfusion().formatInstruction(context),
                messageList,
                chatBotProperties);

        return aiWebClient.fetchAiResponse(chatBotProperties, requestBody);
    }

    @Override
    public Object reviewSubmission(List<Message> messageList, String context) {

        Object requestBody = aiRequestBodyFactory.createRequestBodyForChatBot(
                chatBotPrompt.getReviewSubmission().formatInstruction(context),
                messageList,
                chatBotProperties);

        return aiWebClient.fetchAiResponse(chatBotProperties, requestBody);
    }

    @Override
    public Object provideSolution(List<Message> messageList, String context) {
        Object requestBody = aiRequestBodyFactory.createRequestBodyForChatBot(
                chatBotPrompt.getProvideSolution().formatInstruction(context),
                messageList,
                chatBotProperties);

        return aiWebClient.fetchAiResponse(chatBotProperties, requestBody);
    }

    @Override
    public ChatResponseDto getSolutionResponse(MessageRequestDto messageRequestDto, UUID conversationId, UUID userId) {
        Conversation conversation = conversationRepository.findByConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new BadRequestException("Conversation with id " + conversationId + " not found"));

        // Create userMessage response
        Date userMessageDate = new Date();
        UUID userMessageId = UUID.randomUUID();
        UUID aiMessageId = UUID.randomUUID();
        MessageResponseDto userMessageResponseDto = new MessageResponseDto(
                userMessageId,
                Role.USER.getRole(),
                messageRequestDto.getContent(),
                userMessageDate
        );

        // Create userMessage to save to database
        Message savedUserMessage = Message.builder()
                .messageId(userMessageId)
                .role(Role.USER.getRole())
                .content(messageRequestDto.getContent())
                .timestamp(userMessageDate).build();

        // Get the list of key messages
        List<Message> messageList = conversationRepository.getKeyMessages(conversationId);
        messageList.add(savedUserMessage);

        // Call AI service to resolve confusion
        Object response = provideSolution(messageList, conversation.getSummary().getContext());
        String responseText = aiResponseFilter.getMessage(response);
        AiResponseContent aiResponseContent = AiResponseContent.fromJson(responseText);

        Map<String, Object> aiMessageContent = aiResponseContent.getSolution();

        ConversationStatus conversationStatus = ConversationStatus.SOLUTION_PROVIDED;

        // Create AI message to save to database
        Date aiMessageDate = new Date();
        Message saveAiMessage = Message.builder()
                .messageId(aiMessageId)
                .role(Role.ASSISTANT.getRole())
                .content(aiMessageContent)
                .timestamp(aiMessageDate).build();

        // Update metadata
        int totalTokens = aiResponseFilter.getTotalTokens(response);
        Metadata currentMetadata = conversation.getMetadata();
        currentMetadata.setTotalTokens(currentMetadata.getTotalTokens() + totalTokens);
        currentMetadata.setStatus(conversationStatus);
        currentMetadata.setUpdatedAt(aiMessageDate);

        // Update conversation
        Map<String, Object> updates = new HashMap<>();
        updates.put("metadata", currentMetadata);
        List<Object> messagesToPush = List.of(savedUserMessage, saveAiMessage);
        updateConversation(conversationId, userId, updates, messagesToPush);

        // Add AI message to response
        MessageResponseDto aiMessageResponseDto = new MessageResponseDto(
                aiMessageId,
                "assistant",
                aiMessageContent,
                aiMessageDate
        );

        return new ChatResponseDto(
                conversationId,
                conversation.getTitle(),
                List.of(userMessageResponseDto, aiMessageResponseDto),
                new ConversationMetadataDto(conversationStatus.name(), currentMetadata.getCreatedAt(), currentMetadata.getUpdatedAt())
        );
    }

    @Override
    public ChatResponseDto getReviewSubmissionResponse(MessageRequestDto messageRequestDto, UUID conversationId, UUID userId) {
        Conversation conversation = conversationRepository.findByConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new BadRequestException("Conversation with id " + conversationId + " not found"));

        // Create userMessage response
        Date userMessageDate = new Date();
        UUID userMessageId = UUID.randomUUID();
        UUID aiMessageId = UUID.randomUUID();
        MessageResponseDto userMessageResponseDto = new MessageResponseDto(
                userMessageId,
                Role.USER.getRole(),
                messageRequestDto.getContent(),
                userMessageDate
        );

        // Create userMessage to save to database
        Message savedUserMessage = Message.builder()
                .messageId(userMessageId)
                .role(Role.USER.getRole())
                .content(messageRequestDto.getContent())
                .timestamp(userMessageDate).build();

        // Get the list of key messages
        List<Message> messageList = conversationRepository.getKeyMessages(conversationId);
        messageList.add(savedUserMessage);

        // Call AI service to resolve confusion
        Object response = reviewSubmission(messageList, conversation.getSummary().getContext());
        String responseText = aiResponseFilter.getMessage(response);
        AiResponseContent aiResponseContent = AiResponseContent.fromJson(responseText);

        Map<String, Object> aiMessageContent = aiResponseContent.getCommonText();

        ConversationStatus conversationStatus = conversation.getMetadata().getStatus();
        if (aiMessageContent.isEmpty()) {
            aiMessageContent = aiResponseContent.getReviewSubmissionResponse();
            conversationStatus = ConversationStatus.SUBMISSION_REVIEWED;
        }

        // Create AI message to save to database
        Date aiMessageDate = new Date();
        Message saveAiMessage = Message.builder()
                .messageId(aiMessageId)
                .role(Role.ASSISTANT.getRole())
                .content(aiMessageContent)
                .timestamp(aiMessageDate).build();

        // Update metadata
        int totalTokens = aiResponseFilter.getTotalTokens(response);
        Metadata currentMetadata = conversation.getMetadata();
        currentMetadata.setTotalTokens(currentMetadata.getTotalTokens() + totalTokens);
        currentMetadata.setStatus(conversationStatus);
        currentMetadata.setUpdatedAt(aiMessageDate);

        // Update conversation
        Map<String, Object> updates = new HashMap<>();
        updates.put("metadata", currentMetadata);
        List<Object> messagesToPush = List.of(savedUserMessage, saveAiMessage);
        updateConversation(conversationId, userId, updates, messagesToPush);

        // Add AI message to response
        MessageResponseDto aiMessageResponseDto = new MessageResponseDto(
                aiMessageId,
                "assistant",
                aiMessageContent,
                aiMessageDate
        );

        return new ChatResponseDto(
                conversationId,
                conversation.getTitle(),
                List.of(userMessageResponseDto, aiMessageResponseDto),
                new ConversationMetadataDto(conversationStatus.name(), currentMetadata.getCreatedAt(), currentMetadata.getUpdatedAt())
        );
    }

    private void updateConversation(UUID conversationId, UUID userId, Map<String, Object> updates, List<Object> messagesToPush) {
        Query query = new Query(Criteria.where("conversationId").is(conversationId).and("userId").is(userId));
        Update update = new Update();

        // Add the provided updates
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            update.set(entry.getKey(), entry.getValue());
        }

        // Push the messages if there are any
        if(messagesToPush != null && !messagesToPush.isEmpty()) {
            update.push("messages").each(messagesToPush.toArray()); // Push all messages at once
        }

        mongoTemplate.updateFirst(query, update, Conversation.class);
    }

    @Override
    public ChatResponseDto sendMessage(MessageRequestDto messageRequestDto, UUID conversationId, UUID userId) {
        Conversation conversation = conversationRepository.findByConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new BadRequestException("Conversation with id " + conversationId + " not found"));

        // Create userMessage response
        Date userMessageDate = new Date();
        UUID userMessageId = UUID.randomUUID();
        UUID aiMessageId = UUID.randomUUID();
        MessageResponseDto userMessageResponseDto = new MessageResponseDto(
                userMessageId,
                Role.USER.getRole(),
                messageRequestDto.getContent(),
                userMessageDate
        );

        // Create userMessage to save to database
        Message savedUserMessage = Message.builder()
                .messageId(userMessageId)
                .role(Role.USER.getRole())
                .content(messageRequestDto.getContent())
                .timestamp(userMessageDate).build();

        ConversationStatus conversationStatus = conversation.getMetadata().getStatus();

        // Call AI service to resolve confusion
        Object response = null;
        String responseText;
        AiResponseContent aiResponseContent;
        Map<String, Object> aiMessageContent = new HashMap<>();

        String currentTitle = conversation.getTitle();

        // Update summary
        Summary currentSummary = conversation.getSummary();
        String newContext;
        Date aiMessageDate = null;
        if(conversationStatus == ConversationStatus.PROBLEM_IDENTIFIED
                || conversationStatus == ConversationStatus.SOLUTION_PROVIDED
                || conversationStatus == ConversationStatus.SUBMISSION_REVIEWED)  {

            // Get the list of key messages
            List<Message> messageList = conversationRepository.getKeyMessages(conversationId);
            messageList.add(savedUserMessage);

            // Call AI service to resolve confusion
            response = resolveConfusion(messageList, conversation.getSummary().getContext());
            responseText = aiResponseFilter.getMessage(response);
            aiResponseContent = AiResponseContent.fromJson(responseText);
            aiMessageContent = aiResponseContent.getCommonText();
            aiMessageDate = new Date();
            if (aiMessageContent.isEmpty()) {
                aiMessageContent = aiResponseContent.getResolveConfusionResponse();
                newContext = aiResponseContent.getContextConversation()
                        .getOrDefault("context", "").toString();
                currentSummary.setContext(currentSummary.getContext() + "\n-Thắc mắc đã giải đáp: " + newContext);
                currentSummary.setLatestUpdate(aiMessageDate);
            }
        }

        if(conversationStatus == ConversationStatus.UNIDENTIFIED) {
            // Call AI service to identify problem
            response = identifyProblem(savedUserMessage);
            responseText = aiResponseFilter.getMessage(response);
            aiResponseContent = AiResponseContent.fromJson(responseText);
            aiMessageContent = aiResponseContent.getCommonText();
            aiMessageDate = new Date();
            // If AI message content is empty, use resolve confusion response
            if (aiMessageContent.isEmpty()) {
                aiMessageContent = aiResponseContent.getResolveConfusionResponse();
            }

            // If AI message content is still empty, use identify problem response
            if (aiMessageContent.isEmpty()) {
                aiMessageContent = aiResponseContent.getIdentifyProblemResponse();
                conversationStatus = ConversationStatus.PROBLEM_IDENTIFIED;
                currentTitle = aiResponseContent.getTitleConversation().getOrDefault("title", "").toString();
                newContext = aiResponseContent.getContextConversation()
                        .getOrDefault("context", "").toString();
                currentSummary.setContext("Vấn đề: " + newContext);

                currentSummary.setLatestUpdate(aiMessageDate);
                currentSummary.getKeyMessages().add(aiMessageId);
            }
        }

        // Create AI message to save to database

        Message saveAiMessage = Message.builder()
                .messageId(aiMessageId)
                .role(Role.ASSISTANT.getRole())
                .content(aiMessageContent)
                .timestamp(aiMessageDate).build();

        // Update metadata
        int totalTokens = aiResponseFilter.getTotalTokens(response);
        Metadata currentMetadata = conversation.getMetadata();
        currentMetadata.setTotalTokens(currentMetadata.getTotalTokens() + totalTokens);
        currentMetadata.setStatus(conversationStatus);
        currentMetadata.setUpdatedAt(aiMessageDate);

        // Update conversation
        Map<String, Object> updates = new HashMap<>();
        updates.put("summary", currentSummary);
        updates.put("metadata", currentMetadata);
        updates.put("title", currentTitle);
        List<Object> messagesToPush = List.of(savedUserMessage, saveAiMessage);
        updateConversation(conversationId, userId, updates, messagesToPush);

        // Add AI message to response
        MessageResponseDto aiMessageResponseDto = new MessageResponseDto(
                aiMessageId,
                "assistant",
                aiMessageContent,
                aiMessageDate
        );

        return new ChatResponseDto(
                conversationId,
                currentTitle,
                List.of(userMessageResponseDto, aiMessageResponseDto),
                new ConversationMetadataDto(conversationStatus.name(), currentMetadata.getCreatedAt(), currentMetadata.getUpdatedAt())
        );
    }

    @Override
    public ChatResponseDto createConversation(UUID userId, CreateConversationRequestDto createConversationRequestDto) {

        // Create userMessage response
        Date userMessageDate = new Date();
        UUID userMessageId = UUID.randomUUID();
        MessageResponseDto userMessageResponseDto = new MessageResponseDto(
                userMessageId,
                Role.USER.getRole(),
                createConversationRequestDto.getMessage().getContent(),
                userMessageDate
        );

        // Create userMessage to save to database
        Message savedUserMessage = Message.builder()
                .messageId(userMessageId)
                .role(Role.USER.getRole())
                .content(createConversationRequestDto.getMessage().getContent())
                .timestamp(userMessageDate).build();

        // Call AI service to identify problem
        Object response = identifyProblem(savedUserMessage);

        // Filter AI response
        String responseText = aiResponseFilter.getMessage(response);
        AiResponseContent aiResponseContent = AiResponseContent.fromJson(responseText);

        Date aiMessageDate = new Date();

        // Create AI message to save to database
        Map<String, Object> aiMessageContent = aiResponseContent.getCommonText();

        ConversationStatus conversationStatus = ConversationStatus.UNIDENTIFIED;

        // If AI message content is empty, use resolve confusion response
        if (aiMessageContent.isEmpty()) {
            aiMessageContent = aiResponseContent.getResolveConfusionResponse();
        }

        // Create AI messageId
        UUID aiMessageId = UUID.randomUUID();
        // If AI message content is still empty, use identify problem response
        List<UUID> keyMessageIds = new ArrayList<>();
        if (aiMessageContent.isEmpty()) {
            aiMessageContent = aiResponseContent.getIdentifyProblemResponse();
            conversationStatus = ConversationStatus.PROBLEM_IDENTIFIED;
            keyMessageIds.add(aiMessageId);
        }

        // Create AI message to save to database

        Message saveAiMessage = Message.builder()
                .messageId(aiMessageId)
                .role(Role.ASSISTANT.getRole())
                .content(aiMessageContent)
                .timestamp(aiMessageDate).build();

        // Create summary and metadata
        Summary summary = Summary.builder()
                .latestUpdate(aiMessageDate)
                .context("Vấn đề: " + aiResponseContent.getContextConversation().getOrDefault("context", "").toString())
                .keyMessages(keyMessageIds)
                .build();

        int totalTokens = aiResponseFilter.getTotalTokens(response);
        Metadata metadata = Metadata
                .builder()
                .totalTokens(totalTokens)
                .status(conversationStatus)
                .createdAt(userMessageDate)
                .updatedAt(aiMessageDate)
                .build();

        // Create new conversation
        String title = aiResponseContent.getTitleConversation().getOrDefault("title", "").toString();
        UUID conversationId = UUID.randomUUID();
        Conversation newConversation = Conversation.builder()
                .conversationId(conversationId)
                .userId(userId)
                .title(title)
                .model(chatBotProperties.getModel())
                .messages(List.of(
                        savedUserMessage,
                        saveAiMessage
                ))
                .summary(summary)
                .metadata(metadata)
                .build();

        // Save conversation to database
        conversationRepository.save(newConversation);

        // Add AI message to response
        MessageResponseDto aiMessageResponseDto = new MessageResponseDto(
                aiMessageId,
                "assistant",
                aiMessageContent,
                aiMessageDate
        );

        return new ChatResponseDto(
                conversationId,
                title,
                List.of(userMessageResponseDto, aiMessageResponseDto),
                new ConversationMetadataDto(conversationStatus.name(), metadata.getCreatedAt(), metadata.getUpdatedAt())
        );
    }
}