package com.chatapp.chat.auth.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    @DisplayName("Should pass validation when username and password are valid")
    void shouldPassValidationWithValidUsernameAndPassword() {
        // given
        LoginRequest loginRequest = new LoginRequest("username", "password");

        // when
        Set<ConstraintViolation<LoginRequest>> constraintViolations = validator.validate(loginRequest);

        // then
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is blank")
    void shouldFailValidationWithBlankUsername() {
        // given
        LoginRequest request = new LoginRequest(" ", "password");

        // when
        Set<ConstraintViolation<LoginRequest>> constraintViolations = validator.validate(request);

        // then
        assertThat(constraintViolations)
                .extracting(ConstraintViolation::getPropertyPath)
                .anySatisfy(path -> assertThat(path).hasToString("username"));
    }

    @Test
    @DisplayName("Should fail validation when password is blank")
    void shouldFailValidationWithBlankPassword() {
        // given
        LoginRequest request = new LoginRequest("username", " ");

        // when
        Set<ConstraintViolation<LoginRequest>> constraintViolations = validator.validate(request);

        // then
        assertThat(constraintViolations)
                .extracting(ConstraintViolation::getPropertyPath)
                .anySatisfy(path -> assertThat(path).hasToString("password"));
    }
}