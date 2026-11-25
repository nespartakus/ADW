package com.adw.ai.provider;

/**
 * Typed failure surfaced when talking to an LLM provider, with a
 * user-comprehensible classification.
 */
public class LlmException extends RuntimeException {

    public enum Kind {
        NOT_CONFIGURED,
        INVALID_API_KEY,
        QUOTA_EXCEEDED,
        TIMEOUT,
        PROVIDER_ERROR
    }

    private final Kind kind;

    public LlmException(Kind kind, String message) {
        super(message);
        this.kind = kind;
    }

    public LlmException(Kind kind, String message, Throwable cause) {
        super(message, cause);
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }
}
