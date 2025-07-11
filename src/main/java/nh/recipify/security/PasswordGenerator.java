package nh.recipify.security;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encoded = encoder.encode("lauren");
        System.out.println("Encoded password: " + encoded);
    }
}
