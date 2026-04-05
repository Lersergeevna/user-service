package userservice.util;

import org.junit.jupiter.api.Test;
import userservice.constants.Messages;
import userservice.exception.InvalidInputException;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorsTest {

    @Test
    void requireValidIdShouldReturnId() {
        assertEquals(1L, Validators.requireValidId(1L));
    }

    @Test
    void requireValidIdShouldThrowForNonPositiveValue() {
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> Validators.requireValidId(0));
        assertEquals(Messages.INVALID_ENTITY_ID, exception.getMessage());
    }

    @Test
    void requireValidNameShouldTrimAndReturnName() {
        assertEquals("Alice", Validators.requireValidName("  Alice  "));
    }

    @Test
    void requireValidNameShouldThrowForBlankValue() {
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> Validators.requireValidName("   "));
        assertEquals(Messages.EMPTY_VALUE, exception.getMessage());
    }

    @Test
    void requireValidEmailShouldNormalizeToLowerCase() {
        assertEquals("test@example.com", Validators.requireValidEmail("  TEST@EXAMPLE.COM  "));
    }

    @Test
    void requireValidEmailShouldThrowForInvalidFormat() {
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> Validators.requireValidEmail("wrong-email"));
        assertEquals(Messages.INVALID_EMAIL, exception.getMessage());
    }

    @Test
    void requireValidAgeShouldReturnAge() {
        assertEquals(18, Validators.requireValidAge(18));
    }

    @Test
    void requireValidAgeShouldThrowOutsideAllowedRange() {
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> Validators.requireValidAge(131));
        assertEquals(Messages.INVALID_AGE, exception.getMessage());
    }
}
