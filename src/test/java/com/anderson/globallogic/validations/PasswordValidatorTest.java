package com.anderson.globallogic.validations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordValidatorTest {

    private PasswordValidator passwordValidator;
    private ConstraintValidatorContext mockContext;

    @BeforeEach
    void setUp() {
        passwordValidator = new PasswordValidator();
        mockContext = mock(ConstraintValidatorContext.class);
    }

    @Test
    void testValidPassword() {
        // Valid password: 8-12 characters, at least one uppercase letter, and exactly two digits
        String validPassword = "Ab123456";
        assertTrue(passwordValidator.isValid(validPassword, mockContext));
    }

    @Test
    void testPasswordTooShort() {
        String shortPassword = "Ab12";
        assertFalse(passwordValidator.isValid(shortPassword, mockContext));
    }

    @Test
    void testPasswordTooLong() {
        String longPassword = "Ab123456789012";
        assertFalse(passwordValidator.isValid(longPassword, mockContext));
    }

    @Test
    void testPasswordMissingUppercase() {
        String noUppercasePassword = "ab123456";
        assertFalse(passwordValidator.isValid(noUppercasePassword, mockContext));
    }

    @Test
    void testPasswordLessThanTwoDigits() {
        String lessDigitsPassword = "Abcdefgh1";
        assertFalse(passwordValidator.isValid(lessDigitsPassword, mockContext));
    }

    @Test
    void testPasswordWithMoreThanTwoDigits() {
        String moreDigitsPassword = "Ab12345";
        assertFalse(passwordValidator.isValid(moreDigitsPassword, mockContext));
    }

    @Test
    void testPasswordWithSpecialCharacters() {
        String specialCharPassword = "Ab12@345";
        assertFalse(passwordValidator.isValid(specialCharPassword, mockContext));
    }

    @Test
    void testNullPassword() {
        String nullPassword = null;
        assertFalse(passwordValidator.isValid(nullPassword, mockContext));
    }

    @Test
    void testEmptyPassword() {
        String emptyPassword = "";
        assertFalse(passwordValidator.isValid(emptyPassword, mockContext));
    }
}
