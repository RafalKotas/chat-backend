package com.chatapp.chat.security;

import com.chatapp.chat.user.User;
import com.chatapp.chat.user.UserRepository;
import com.chatapp.chat.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    UserRepository userRepository;

    CustomUserDetailsService subject;

    @BeforeEach
    void setUp() {
        subject = new CustomUserDetailsService(userRepository);
    }

    @Test
    @DisplayName("Should return UserDetails when user exists - enabled true")
    void shouldReturnUserDetailsEnabledTrueWhenUserExists() {
        // given
        User user = User.builder()
                .username("alice")
                .password("encodedPass")
                .role(UserRole.USER)
                .enabled(true)
                .build();

        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(user));

        // when
        UserDetails result = subject.loadUserByUsername("alice");

        // then
        assertThat(result.getUsername()).isEqualTo("alice");
        assertThat(result.getPassword()).isEqualTo("encodedPass");
        assertThat(result.getAuthorities()).extracting("authority")
                .containsExactly("ROLE_" + user.getRole().name());
        assertThat(result.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should return UserDetails when user exists - enabled false")
    void shouldReturnUserDetailsEnabledFalseWhenUserExists() {
        // given
        User user = User.builder()
                .username("alice")
                .password("encodedPass")
                .role(UserRole.USER)
                .enabled(false)
                .build();

        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(user));

        // when
        UserDetails result = subject.loadUserByUsername("alice");

        // then
        assertThat(result.getUsername()).isEqualTo("alice");
        assertThat(result.getPassword()).isEqualTo("encodedPass");
        assertThat(result.getAuthorities()).extracting("authority")
                .containsExactly("ROLE_" + user.getRole().name());
        assertThat(result.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void shouldThrowWhenUserNotFound() {
        // given
        when(userRepository.findByUsername("ghost"))
                .thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> subject.loadUserByUsername("ghost"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}