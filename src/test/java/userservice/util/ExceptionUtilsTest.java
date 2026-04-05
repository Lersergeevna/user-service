package userservice.util;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionUtilsTest {

    @Test
    void isConstraintViolation_shouldReturnFalseForNull() {
        assertFalse(ExceptionUtils.isConstraintViolation(null));
    }

    @Test
    void isConstraintViolation_shouldReturnTrueForDirectConstraintViolation() {
        ConstraintViolationException exception =
                new ConstraintViolationException("constraint", new SQLException("sql"), "insert");

        assertTrue(ExceptionUtils.isConstraintViolation(exception));
    }

    @Test
    void isConstraintViolation_shouldReturnTrueWhenCauseChainContainsConstraintViolation() {
        ConstraintViolationException root =
                new ConstraintViolationException("constraint", new SQLException("sql"), "insert");
        RuntimeException wrapped = new RuntimeException(new IllegalStateException(root));

        assertTrue(ExceptionUtils.isConstraintViolation(wrapped));
    }

    @Test
    void isConstraintViolation_shouldReturnFalseWhenCauseChainDoesNotContainConstraintViolation() {
        RuntimeException exception = new RuntimeException(
                new IllegalStateException(
                        new IllegalArgumentException("no constraint violation")
                )
        );

        assertFalse(ExceptionUtils.isConstraintViolation(exception));
    }
}