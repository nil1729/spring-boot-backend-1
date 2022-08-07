package tech.nilanjan.spring.backend.main.io.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomIdUtils {
    private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final Random RANDOM = new Random();

    public String generateUserId(int length) {
        return generateRandomId(length);
    }

    public String generateAddressId(int length) {
        return generateRandomId(length);
    }

    private String generateRandomId(int length) {
        StringBuilder returnValue = new StringBuilder();

        for (int i = 0; i < length; i++) {
            returnValue.append(CHARACTERS.charAt(RANDOM.nextInt(0, CHARACTERS.length())));
        }

        return new String(returnValue);
    }
}
