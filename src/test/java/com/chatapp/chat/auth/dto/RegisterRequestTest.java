package com.chatapp.chat.auth.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Should pass validation when all fields are valid")
    void shouldPassValidationWhenAllFieldsAreValid() {
        // given
        RegisterRequest request = new RegisterRequest(
                "alice@example.com",
                "bpass321",
                "aliz523",
                "Alice",
                "Smith"
        );

        // when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void shouldFailValidationWhenEmailIsInvalid() {
        // given
        RegisterRequest request = new RegisterRequest(
                "aliceexample.com",
                "bpass321",
                "aliz523",
                "Alice",
                "Smith"
        );

        // when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail validation when required fields are blank")
    void shouldFailValidationWhenRequiredFieldsAreBlank() {
        // given
        RegisterRequest request = new RegisterRequest(
                "",
                "",
                "",
                "Alice",
                "Smith"
        );

        // when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty().hasSizeGreaterThanOrEqualTo(3);
    }
}