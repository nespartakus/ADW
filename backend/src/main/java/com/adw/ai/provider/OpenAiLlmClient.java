package com.adw.ai.provider;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;

/**
 * OpenAI (and OpenAI-compatible) adapter backed by LangChain4j. Kept isolated
 * from the domain so the provider SDK never leaks into application code.
 */
public class OpenAiLlmClient implements LlmClient {

    private final ChatModel model;
    private final String modelName;

    public OpenAiLlmClient(String apiKey, String modelName, String baseUrl, Duration timeout) {
        this.modelName = modelName;
        var builder = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .timeout(timeout);
        if (baseUrl != null && !baseUrl.isBlank()) {
            builder = builder.baseUrl(baseUrl);
        }
        this.model = builder.build();
    }

    @Override
    public boolean isConfigured() {
        return true;
    }

    @Override
    public String generate(String systemPrompt, String userPrompt, boolean jsonMode) {
        try {
            var request = ChatRequest.builder()
                    .messages(SystemMessage.from(systemPrompt), UserMessage.from(userPrompt));
            if (jsonMode) {
                request = request.responseFormat(
                        ResponseFormat.builder().type(ResponseFormatType.JSON).build());
            }
            var response = model.chat(request.build());
            return response.aiMessage().text();
        } catch (Exception e) {
            throw mapException(e);
        }
    }

    @Override
    public String describe() {
        return "openai/" + modelName;
    }

    private LlmException mapException(Exception e) {
        StringBuilder sb = new StringBuilder();
        Throwable cause = e;
        while (cause != null) {
            sb.append(' ').append(String.valueOf(cause.getMessage()));
            cause = cause.getCause();
        }
        String msg = sb.toString().toLowerCase();

        if (containsAny(msg, "401", "invalid api key", "incorrect api key", "unauthorized", "authentication")) {
            return new LlmException(LlmException.Kind.INVALID_API_KEY, "API key inválida o sin autorización", e);
        }
        if (containsAny(msg, "429", "quota", "rate limit", "insufficient_quota")) {
            return new LlmException(LlmException.Kind.QUOTA_EXCEEDED, "Cuota excedida o límite de velocidad alcanzado", e);
        }
        if (containsAny(msg, "timeout", "timed out", "read timed out", "connect timed out")) {
            return new LlmException(LlmException.Kind.TIMEOUT, "El proveedor tardó demasiado en responder", e);
        }
        return new LlmException(LlmException.Kind.PROVIDER_ERROR, "Error del proveedor LLM: " + e.getMessage(), e);
    }

    private static boolean containsAny(String haystack, String... needles) {
        for (String needle : needles) {
            if (haystack.contains(needle)) {
                return true;
            }
        }
        return false;
    }
}
