package com.hcmus.mela.ai.chatbot.repository;

import com.hcmus.mela.ai.chatbot.model.Conversation;
import com.hcmus.mela.ai.chatbot.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, UUID> {

    List<Conversation> findAllByUserId(UUID userId);

    Optional<Conversation> findByConversationIdAndUserId(UUID id, UUID userId);

    // Define a method to get all key messages from a specific conversation by its ID
    default List<Message> getKeyMessages(UUID conversationId) {
        Optional<Conversation> conversation = findById(conversationId);

        if (conversation.isPresent() && conversation.get().getMessages() != null && conversation.get().getSummary() != null && conversation.get().getSummary().getKeyMessages() != null) {
            List<UUID> keyMessageIds = conversation.get().getSummary().getKeyMessages();
            return conversation.get().getMessages().stream()
                    .filter(message -> keyMessageIds.contains(message.getMessageId()))
                    .collect(Collectors.toList());
        } else {
            return null; // Or return an empty list: return Collections.emptyList();
        }
    }

    /**
     * Get the list of messages in a conversation with pagination.
     */
    default List<Message> getMessages(UUID conversationId, int limit, UUID beforeMessageId, UUID afterMessageId, AtomicBoolean hasMoreHolder) {
        Optional<Conversation> conversation = findById(conversationId);

        if (conversation.isEmpty() || conversation.get().getMessages() == null) {
            return Collections.emptyList();
        }

        List<Message> messages = conversation.get().getMessages();

        List<Message> result;

        if (afterMessageId != null) {
            result = messages.stream()
                    .dropWhile(msg -> !msg.getMessageId().equals(afterMessageId))
                    .skip(1)
                    .limit(limit)
                    .collect(Collectors.toList());
        } else if (beforeMessageId != null) {
            int beforeIndex = IntStream.range(0, messages.size())
                    .filter(i -> messages.get(i).getMessageId().equals(beforeMessageId))
                    .findFirst()
                    .orElse(-1);

            if (beforeIndex > 0) {
                int fromIndex = Math.max(0, beforeIndex - limit);
                result = messages.subList(fromIndex, beforeIndex);
            } else {
                result = Collections.emptyList();
            }
        } else {
            int fromIndex = Math.max(messages.size() - limit, 0);
            result = messages.subList(fromIndex, messages.size());
        }

        hasMoreHolder.set(result.size() == limit); // nếu đủ số lượng yêu cầu, có thể còn nữa

        return result;
    }



    // Custom method to delete a conversation by conversationId
    void deleteByConversationId(UUID conversationId);

    // Method to check if a conversation exists
    boolean existsByConversationId(UUID conversationId);
}
