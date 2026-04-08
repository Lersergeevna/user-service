package userservice.ui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MenuActionTest {

    @ParameterizedTest
    @CsvSource({
            "1, CREATE",
            "2, GET_BY_ID",
            "3, GET_ALL",
            "4, UPDATE",
            "5, DELETE",
            "0, EXIT"
    })
    void fromCode_shouldReturnExpectedAction(String code, MenuAction expected) {
        Assertions.assertEquals(expected, MenuAction.fromCode(code));
    }

    @Test
    void fromCode_shouldReturnNullForUnknownCode() {
        Assertions.assertNull(MenuAction.fromCode("999"));
        Assertions.assertNull(MenuAction.fromCode(""));
        Assertions.assertNull(MenuAction.fromCode(null));
    }

    @Test
    void actionProperties_shouldBeCorrect() {
        Assertions.assertAll(
                () -> Assertions.assertEquals("1", MenuAction.CREATE.code()),
                () -> Assertions.assertEquals("Создать пользователя", MenuAction.CREATE.description()),
                () -> Assertions.assertFalse(MenuAction.CREATE.requiresExistingUsers()),

                () -> Assertions.assertEquals("2", MenuAction.GET_BY_ID.code()),
                () -> Assertions.assertEquals("Найти пользователя по id", MenuAction.GET_BY_ID.description()),
                () -> Assertions.assertTrue(MenuAction.GET_BY_ID.requiresExistingUsers()),

                () -> Assertions.assertEquals("3", MenuAction.GET_ALL.code()),
                () -> Assertions.assertTrue(MenuAction.GET_ALL.requiresExistingUsers()),

                () -> Assertions.assertEquals("4", MenuAction.UPDATE.code()),
                () -> Assertions.assertTrue(MenuAction.UPDATE.requiresExistingUsers()),

                () -> Assertions.assertEquals("5", MenuAction.DELETE.code()),
                () -> Assertions.assertTrue(MenuAction.DELETE.requiresExistingUsers()),

                () -> Assertions.assertEquals("0", MenuAction.EXIT.code()),
                () -> Assertions.assertEquals("Выход", MenuAction.EXIT.description()),
                () -> Assertions.assertFalse(MenuAction.EXIT.requiresExistingUsers())
        );
    }
}