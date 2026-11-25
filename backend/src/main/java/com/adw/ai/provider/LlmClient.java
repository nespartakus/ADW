package com.adw.ai.provider;

/**
 * Domain-agnostic LLM client. Implementations wrap a concrete provider SDK
 * (LangChain4j) so the application layer never depends on it.
 */
public interface LlmClient {

    boolean isConfigured();

    String generate(String systemPrompt, String userPrompt, boolean jsonMode);

    String describe();
}
