package com.chatapp.chat.security;

import com.chatapp.chat.user.User;
import com.chatapp.chat.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    private User createUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .email("john@example.com")
                .password("secret")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.USER)
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("Should return correct authorities based on User role")
    void shouldReturnCorrectAuthorities() {
        // given
        User user = createUser();
        CustomUserDetails details = new CustomUserDetails(user);

        // when
        List<GrantedAuthority> authorities =
                List.copyOf(details.getAuthorities());

        // then
        assertThat(authorities)
                .containsExactly(new SimpleGrantedAuthority("USER"));
    }

    @Test
    @DisplayName("Should return correct password")
    void shouldReturnPassword() {
        // given
        User user = createUser();
        CustomUserDetails details = new CustomUserDetails(user);

        // when
        String password = details.getPassword();

        // then
        assertThat(password).isEqualTo("secret");
    }

    @Test
    @DisplayName("Should return email as username")
    void shouldReturnUsernameAsEmail() {
        // given
        User user = createUser();
        CustomUserDetails details = new CustomUserDetails(user);

        // when
        String username = details.getUsername();

        // then
        assertThat(username).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Should return enabled flag from domain user")
    void shouldReturnEnabledStatus() {
        // given
        User user = createUser();
        CustomUserDetails details = new CustomUserDetails(user);

        // then
        assertThat(details.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("AccountNonExpired, NonLocked and CredentialsNonExpired should always be true")
    void alwaysTrueFlags() {
        // given
        User user = createUser();
        CustomUserDetails details = new CustomUserDetails(user);

        // then
        assertThat(details.isAccountNonExpired()).isTrue();
        assertThat(details.isAccountNonLocked()).isTrue();
        assertThat(details.isCredentialsNonExpired()).isTrue();
    }

    @Test
    @DisplayName("Should return domain User via getter")
    void shouldReturnDomainUser() {
        // given
        User user = createUser();
        CustomUserDetails details = new CustomUserDetails(user);

        // then
        assertThat(details.getDomainUser()).isEqualTo(user);
    }
}