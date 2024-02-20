package org.opendatamesh.platform.pp.params.server.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class EncryptionKeyManager {
    private static final String KEY_STORAGE_PROPERTY = "encryption.key";

    private static SecretKey secretKey;

    public static SecretKey getSecretKey(String algorithm, Integer keySize) throws NoSuchAlgorithmException {
        if (secretKey == null) {
            // Load the key from storage if it's not already loaded
            secretKey = loadSecretKeyFromStorage(algorithm);
            if (secretKey == null) {
                // Generate a new key if it's not found in the storage
                secretKey = generateSecretKey(algorithm, keySize);
                // Store the key in the storage for future use
                storeSecretKeyToStorage(secretKey);
            }
        }
        return secretKey;
    }

    private static SecretKey loadSecretKeyFromStorage(String algorithm) throws NoSuchAlgorithmException {
        // Retrieve the key from the system properties
        String encodedKey = System.getProperty(KEY_STORAGE_PROPERTY);
        if (encodedKey != null && !encodedKey.isEmpty()) {
            // Decode the key from Base64
            byte[] keyBytes = Base64.getDecoder().decode(encodedKey);
            return new SecretKeySpec(keyBytes, algorithm);
        }
        return null;
    }

    private static void storeSecretKeyToStorage(SecretKey secretKey) {
        // Encode the key to Base64
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        // Store the key in the system properties
        System.setProperty(KEY_STORAGE_PROPERTY, encodedKey);
    }

    private static SecretKey generateSecretKey(String algorithm, Integer keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(keySize);
        return keyGenerator.generateKey();
    }

}
