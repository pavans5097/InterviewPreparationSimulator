package com.ips.util;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {
    private static final int SALT_BYTES = 16;
    private static final int HASH_BITS = 256;
    private static final int ITERATIONS = 210_000;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public record PasswordData(String hash, String salt, int iterations) {
    }

    public static PasswordData hash(char[] password) {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] hash = derive(password, salt, ITERATIONS);

        return new PasswordData(
                Base64.getEncoder().encodeToString(hash),
                Base64.getEncoder().encodeToString(salt),
                ITERATIONS
        );
    }

    public static boolean verify(char[] password, String expectedHash,
                                 String saltBase64, int iterations) {
        byte[] salt = Base64.getDecoder().decode(saltBase64);
        byte[] actualHash = derive(password, salt, iterations);
        byte[] expectedBytes = Base64.getDecoder().decode(expectedHash);
        return MessageDigest.isEqual(actualHash, expectedBytes);
    }

    private static byte[] derive(char[] password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, HASH_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Password hashing is unavailable on this JDK.", ex);
        } finally {
            spec.clearPassword();
        }
    }
}
