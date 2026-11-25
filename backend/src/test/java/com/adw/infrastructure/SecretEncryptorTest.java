package com.adw.infrastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SecretEncryptorTest {

    private final SecretEncryptor encryptor = new SecretEncryptor("");

    @Test
    void roundTrip() {
        String ciphertext = encryptor.encrypt("sk-test-1234567890");
        assertNotEquals("sk-test-1234567890", ciphertext);
        assertEquals("sk-test-1234567890", encryptor.decrypt(ciphertext));
    }

    @Test
    void encryptIsNonDeterministic() {
        String a = encryptor.encrypt("same-secret");
        String b = encryptor.encrypt("same-secret");
        assertNotEquals(a, b);
        assertEquals("same-secret", encryptor.decrypt(a));
        assertEquals("same-secret", encryptor.decrypt(b));
    }

    @Test
    void nullValues() {
        assertNull(encryptor.encrypt(null));
        assertNull(encryptor.decrypt(null));
    }
}
