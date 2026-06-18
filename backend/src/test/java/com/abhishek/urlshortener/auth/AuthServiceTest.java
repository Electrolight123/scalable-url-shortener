package com.abhishek.urlshortener.auth;

import com.abhishek.urlshortener.exception.BadRequestException;
import com.abhishek.urlshortener.user.User;
import com.abhishek.urlshortener.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    void registerShouldCreateUserAndReturnToken() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Abhishek");
        request.setEmail("abhishek@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(jwtService.generateToken(request.getEmail()))
                .thenReturn("test-jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        assertEquals("abhishek@example.com", response.getEmail());
        assertEquals("Abhishek", response.getName());

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
        verify(jwtService).generateToken("abhishek@example.com");
    }

    @Test
    void registerShouldFailWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Abhishek");
        request.setEmail("abhishek@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "Email is already registered",
                exception.getMessage()
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginShouldReturnTokenForValidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("abhishek@example.com");
        request.setPassword("password123");

        User user = new User(
                "Abhishek",
                "abhishek@example.com",
                "encoded-password"
        );

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )).thenReturn(true);

        when(jwtService.generateToken(user.getEmail()))
                .thenReturn("login-jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("login-jwt-token", response.getToken());
        assertEquals("abhishek@example.com", response.getEmail());
        assertEquals("Abhishek", response.getName());
    }

    @Test
    void loginShouldFailWhenPasswordIsIncorrect() {
        LoginRequest request = new LoginRequest();
        request.setEmail("abhishek@example.com");
        request.setPassword("wrong-password");

        User user = new User(
                "Abhishek",
                "abhishek@example.com",
                "encoded-password"
        );

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )).thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authService.login(request)
        );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void loginShouldFailWhenUserDoesNotExist() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authService.login(request)
        );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );
    }
}