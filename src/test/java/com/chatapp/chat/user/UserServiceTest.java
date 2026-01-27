package com.chatapp.chat.user;

import com.chatapp.chat.user.exception.EmailAlreadyUsedException;
import com.chatapp.chat.user.exception.UsernameAlreadyUsedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("registerUser() should throw EmailAlreadyUsedException when email exists")
    void shouldThrowEmailAlreadyUsedExceptionWhenEmailExists() {

        // given
        User user = createTestUser();
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // when + then
        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(EmailAlreadyUsedException.class)
                .hasMessage("Email already in use: test@example.com");

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("registerUser() should throw UsernameAlreadyUsedException when username exists")
    void shouldThrowUsernameAlreadyUsedExceptionWhenUsernameExists() {

        // given
        User user = createTestUser();
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // when + then
        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(UsernameAlreadyUsedException.class)
                .hasMessage("Username already in use: " + user.getUsername());

        verify(userRepository).existsByEmail(anyString());
        verify(userRepository).existsByUsername(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("registerUser() should save user when email and username are free")
    void shouldSaveUserSuccessfully() {

        // given
        User user = createTestUser();
        User saved = User.builder()
                .id(UUID.randomUUID())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(saved);

        // when
        User result = userService.registerUser(user);

        // then
        assertThat(result.getId()).isEqualTo(saved.getId());
        assertThat(result.getEmail()).isEqualTo(saved.getEmail());

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).existsByUsername(user.getUsername());
        verify(userRepository).save(user);
    }

    private User createTestUser() {
        return User.builder()
                .email("test@example.com")
                .username("raf")
                .build();
    }
}