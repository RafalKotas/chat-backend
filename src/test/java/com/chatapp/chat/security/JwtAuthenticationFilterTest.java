package com.chatapp.chat.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.Principal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    JwtUtils jwtUtils;

    @Mock
    CustomUserDetailsService customUserDetailsService;

    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    HttpServletResponse httpServletResponse;

    @Mock
    FilterChain filterChain;

    JwtAuthenticationFilter subject;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        subject = new JwtAuthenticationFilter(jwtUtils, customUserDetailsService);
    }

    @Test
    @DisplayName("Should skip filter when 'Authorization' header is missing")
    void shouldIgnoreWhenHeaderMissing() throws Exception {
        // given
        when(httpServletRequest.getHeader("Authorization")).thenReturn(null);

        // when
        subject.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should skip filter when 'Authorization' header does not start with 'Bearer '")
    void shouldIgnoreWhenHeaderNotBearer() throws Exception {
        // given
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Basic 123");

        // when
        subject.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should skip authentication when JWT token is invalid")
    void shouldIgnoreWhenTokenInvalid() throws Exception {
        // given
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer abc");
        when(jwtUtils.isValid("abc")).thenReturn(false);

        // when
        subject.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should skip authentication when extractUserName returns null")
    void shouldIgnoreWhenUsernameNull() throws Exception {
        // given
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer abc");
        when(jwtUtils.isValid("abc")).thenReturn(true);
        when(jwtUtils.extractUserName("abc")).thenReturn(null);

        // when
        subject.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should skip loading user when SecurityContext already contains authentication")
    void shouldSkipWhenAuthenticationAlreadyExists() throws Exception {
        // given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("already", null, Collections.emptyList())
        );

        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer abc");
        when(jwtUtils.isValid("abc")).thenReturn(true);
        when(jwtUtils.extractUserName("abc")).thenReturn("john");

        // when
        subject.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        // then
        verifyNoInteractions(customUserDetailsService);
    }

    @Test
    @DisplayName("Should skip authentication when userDetailsService throws usernameNotFoundException")
    void shouldIgnoreWhenUserNotFound() throws Exception {
        // given
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer abc");
        when(jwtUtils.isValid("abc")).thenReturn(true);
        when(jwtUtils.extractUserName("abc")).thenReturn("john");
        when(customUserDetailsService.loadUserByUsername("john"))
                .thenThrow(new UsernameNotFoundException("not found"));

        // when
        subject.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(filterChain, atLeastOnce()).doFilter(httpServletRequest, httpServletResponse);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should set authentication in SecurityContext when token and user are valid")
    void shouldAuthenticateWhenTokenValid() throws Exception {
        // given
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtUtils.isValid("token123")).thenReturn(true);
        when(jwtUtils.extractUserName("token123")).thenReturn("john");

        UserDetails details = org.springframework.security.core.userdetails.User
                .withUsername("john")
                .password("pass")
                .authorities(Collections.emptyList())
                .build();

        when(customUserDetailsService.loadUserByUsername("john")).thenReturn(details);

        // when
        subject.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNotNull()
                .extracting(Principal::getName).isEqualTo("john");

        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    @DisplayName("Should continue filter chain and return immediately when userDetailsService throws UsernameNotFoundException")
    void shouldContinueChainWhenUserNotFoundAndReturnEarly() throws Exception {
        // given
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer abc");
        when(jwtUtils.isValid("abc")).thenReturn(true);
        when(jwtUtils.extractUserName("abc")).thenReturn("john");

        when(customUserDetailsService.loadUserByUsername("john"))
                .thenThrow(new UsernameNotFoundException("not found"));

        // when
        subject.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should call filterChain.doFilter and skip authentication when authentication already exists")
    void shouldSkipAuthenticationWhenContextHasAuth() throws Exception {
        // given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("existing", null, Collections.emptyList())
        );

        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtUtils.isValid("token123")).thenReturn(true);
        when(jwtUtils.extractUserName("token123")).thenReturn("john");

        // when
        subject.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        // then
        verifyNoInteractions(customUserDetailsService);
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
    }
}