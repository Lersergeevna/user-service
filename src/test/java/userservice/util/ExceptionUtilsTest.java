package userservice.util;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class ExceptionUtilsTest {

    @Test
    void isConstraintViolation_shouldReturnFalseForNull() {
        Assertions.assertFalse(ExceptionUtils.isConstraintViolation(null));
    }

    @Test
    void isConstraintViolation_shouldReturnTrueForDirectConstraintViolation() {
        ConstraintViolationException exception =
                new ConstraintViolationException("constraint", new SQLException("sql"), "insert");

        Assertions.assertTrue(ExceptionUtils.isConstraintViolation(exception));
    }

    @Test
    void isConstraintViolation_shouldReturnTrueWhenCauseChainContainsConstraintViolation() {
        ConstraintViolationException root =
                new ConstraintViolationException("constraint", new SQLException("sql"), "insert");
        RuntimeException wrapped = new RuntimeException(new IllegalStateException(root));

        Assertions.assertTrue(ExceptionUtils.isConstraintViolation(wrapped));
    }

    @Test
    void isConstraintViolation_shouldReturnFalseWhenCauseChainDoesNotContainConstraintViolation() {
        RuntimeException exception = new RuntimeException(
                new IllegalStateException(
                        new IllegalArgumentException("no constraint violation")
                )
        );

        Assertions.assertFalse(ExceptionUtils.isConstraintViolation(exception));
    }
}