package userservice.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import userservice.constants.Messages;
import userservice.exception.InvalidInputException;

class ValidatorsTest {

    @Test
    void requireValidIdShouldReturnId() {
        Assertions.assertEquals(1L, Validators.requireValidId(1L));
    }

    @Test
    void requireValidIdShouldThrowForNonPositiveValue() {
        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> Validators.requireValidId(0));
        Assertions.assertEquals(Messages.INVALID_ENTITY_ID, exception.getMessage());
    }

    @Test
    void requireValidNameShouldTrimAndReturnName() {
        Assertions.assertEquals("Alice", Validators.requireValidName("  Alice  "));
    }

    @Test
    void requireValidNameShouldThrowForBlankValue() {
        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> Validators.requireValidName("   "));
        Assertions.assertEquals(Messages.EMPTY_VALUE, exception.getMessage());
    }

    @Test
    void requireValidEmailShouldNormalizeToLowerCase() {
        Assertions.assertEquals("test@example.com", Validators.requireValidEmail("  TEST@EXAMPLE.COM  "));
    }

    @Test
    void requireValidEmailShouldThrowForInvalidFormat() {
        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> Validators.requireValidEmail("wrong-email"));
        Assertions.assertEquals(Messages.INVALID_EMAIL, exception.getMessage());
    }

    @Test
    void requireValidAgeShouldReturnAge() {
        Assertions.assertEquals(18, Validators.requireValidAge(18));
    }

    @Test
    void requireValidAgeShouldThrowOutsideAllowedRange() {
        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> Validators.requireValidAge(131));
        Assertions.assertEquals(Messages.INVALID_AGE, exception.getMessage());
    }
}
