package com.hcmus.mela.ai.client.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmus.mela.ai.client.exception.ApiException;
import com.hcmus.mela.ai.client.filter.AiResponseFilter;
import com.hcmus.mela.ai.client.config.AiClientProperties;
import com.hcmus.mela.ai.client.config.AiFeatureProperties;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Client for making HTTP requests to AI service providers.
 * This class creates and manages WebClient instances for different AI features
 * and handles communication with external AI APIs.
 */
@Component
@AllArgsConstructor
public class AiWebClient {
    private final AiClientProperties aiClientProperties;
    private final AiResponseFilter aiResponseFilter;

    public WebClient getWebClientForChatBot() {
        return createWebClient(aiClientProperties.getChatBot().getProvider());
    }

    public WebClient getWebClientForQuestionHint() {
        return createWebClient(aiClientProperties.getQuestionHint().getProvider());
    }

    /**
     * Makes an API request to the AI provider and fetches the response.
     *
     * @param aiFeatureProperties The properties for the AI feature
     * @param requestBody The request payload to send to the AI provider
     * @return The response from the AI provider, or null if there was an error
     */
    public Object fetchAiResponse(AiFeatureProperties aiFeatureProperties, Object requestBody) {
        String provider = aiFeatureProperties.getProvider();
        Class<?> responseType = aiResponseFilter.getResponseType(provider);
        ObjectMapper objectMapper = new ObjectMapper();

        // Make the API request
        try {
            return getWebClientForChatBot()
                    .post()
                    .uri(aiFeatureProperties.getPath())
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class).flatMap(body -> {
                                try {
                                    JsonNode jsonNode = objectMapper.readTree(body);
                                    String errorMessage = jsonNode.path("error").path("message").asText("Unknown error");
                                    return Mono.error(new ApiException(clientResponse.statusCode().value(), errorMessage));
                                } catch (Exception e) {
                                    return Mono.error(new ApiException(clientResponse.statusCode().value(), "Error parsing API response"));
                                }
                            })
                    )
                    .bodyToMono(responseType)
                    .block();
        } catch (WebClientResponseException e) {
            throw new ApiException(e.getStatusCode().value(), e.getResponseBodyAsString());
        }


    }

    /**
     * Creates a WebClient configured for a specific AI provider.
     *
     * @param provider The name of the AI provider to configure the WebClient for
     * @return A configured WebClient instance
     * @throws IllegalArgumentException if the provider is not supported
     */
    private WebClient createWebClient(String provider) {
        AiClientProperties.Provider providerConfig = aiClientProperties.getProviders().get(provider);
        if (providerConfig == null) {
            throw new IllegalArgumentException("Unsupported AI provider: " + provider);
        }
        return WebClient.builder()
                .baseUrl(providerConfig.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + providerConfig.getKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
