package nh.recipify.security;

import nh.recipify.domain.model.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Profile("auth")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository; // Dein JpaRepository<User, ...>

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        nh.recipify.domain.model.User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword()) // Hier steht das {bcrypt} Passwort
            .roles("USER") // oder aus DB rollen holen, falls du sie hast
            .build();
    }
}
