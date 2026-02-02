package com.chatapp.chat.chat._testconfig;

import com.chatapp.chat.security.AuthenticationEntryPointImpl;
import com.chatapp.chat.security.CustomUserDetailsService;
import com.chatapp.chat.security.JwtAuthenticationFilter;
import com.chatapp.chat.security.JwtUtils;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MockedSecurityConfig {

    @Bean
    @Primary
    public JwtUtils jwtUtils() {
        return Mockito.mock(JwtUtils.class);
    }

    @Bean
    @Primary
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return Mockito.mock(JwtAuthenticationFilter.class);
    }

    @Bean
    @Primary
    public CustomUserDetailsService customUserDetailsService() {
        return Mockito.mock(CustomUserDetailsService.class);
    }

    @Bean
    @Primary
    public AuthenticationEntryPointImpl authenticationEntryPoint() {
        return Mockito.mock(AuthenticationEntryPointImpl.class);
    }
}
