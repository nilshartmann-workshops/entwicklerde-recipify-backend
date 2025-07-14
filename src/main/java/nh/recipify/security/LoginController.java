package nh.recipify.security;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Validated
@Hidden
@Profile("auth")
class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    LoginController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    record LoginRequest(
        @NotNull String username,
        @NotNull String password
    ) {
    }

    record LoginResponse(
        String message
    ) {
    }

    @PostMapping("/api/login")
    @Valid
    public ResponseEntity<@Valid LoginResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response) {
        log.info("LOGIN CONTROLLER INVOKED {}", request);

        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
            )
        );
        String token = jwtUtil.generateToken(request.username());

        // Set the JWT token as HTTP-only cookie
        jwtUtil.createCookie(response, token);

        return ResponseEntity.ok(
            new LoginResponse("Login successful")
        );
    }

    @PostMapping("/api/logout")
    public ResponseEntity<LoginResponse> logout(HttpServletResponse response) {
        // Clear the JWT cookie
        jwtUtil.clearCookie(response);
        return ResponseEntity.ok(new LoginResponse("Logout successful"));
    }
}