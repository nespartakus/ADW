package com.adw.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Authenticated encryption (AES-256-GCM) for secrets at rest.
 *
 * The master key comes from the {@code APP_SECRET_ENCRYPTION_KEY} environment
 * variable (base64, 32 bytes). When unset, a clearly-labeled development key
 * is used so the app runs out of the box; production MUST inject a real key.
 */
@Component
public class SecretEncryptor {

    private static final int GCM_TAG_BITS = 128;
    private static final int IV_BYTES = 12;
    private static final String DEV_KEY_CONSTANT = "adw-dev-only-master-key-00000000";

    private final SecretKeySpec key;
    private final SecureRandom random = new SecureRandom();

    public SecretEncryptor(@Value("${app.secret-encryption-key:}") String configuredKey) {
        this.key = new SecretKeySpec(resolveKey(configuredKey), "AES");
    }

    public String encrypt(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        try {
            byte[] iv = new byte[IV_BYTES];
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] payload = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(ciphertext, 0, payload, iv.length, ciphertext.length);
            return Base64.getEncoder().encodeToString(payload);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encrypt secret", e);
        }
    }

    public String decrypt(String ciphertext) {
        if (ciphertext == null) {
            return null;
        }
        try {
            byte[] payload = Base64.getDecoder().decode(ciphertext);
            byte[] iv = new byte[IV_BYTES];
            System.arraycopy(payload, 0, iv, 0, IV_BYTES);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] plaintext = cipher.doFinal(payload, IV_BYTES, payload.length - IV_BYTES);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrypt secret", e);
        }
    }

    private static byte[] resolveKey(String configuredKey) {
        if (configuredKey != null && !configuredKey.isBlank()) {
            return Base64.getDecoder().decode(configuredKey);
        }
        return sha256(DEV_KEY_CONSTANT);
    }

    private static byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
