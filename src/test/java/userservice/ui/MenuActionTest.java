package userservice.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(expected, MenuAction.fromCode(code));
    }

    @Test
    void fromCode_shouldReturnNullForUnknownCode() {
        assertNull(MenuAction.fromCode("999"));
        assertNull(MenuAction.fromCode(""));
        assertNull(MenuAction.fromCode(null));
    }

    @Test
    void actionProperties_shouldBeCorrect() {
        assertAll(
                () -> assertEquals("1", MenuAction.CREATE.code()),
                () -> assertEquals("Создать пользователя", MenuAction.CREATE.description()),
                () -> assertFalse(MenuAction.CREATE.requiresExistingUsers()),

                () -> assertEquals("2", MenuAction.GET_BY_ID.code()),
                () -> assertEquals("Найти пользователя по id", MenuAction.GET_BY_ID.description()),
                () -> assertTrue(MenuAction.GET_BY_ID.requiresExistingUsers()),

                () -> assertEquals("3", MenuAction.GET_ALL.code()),
                () -> assertTrue(MenuAction.GET_ALL.requiresExistingUsers()),

                () -> assertEquals("4", MenuAction.UPDATE.code()),
                () -> assertTrue(MenuAction.UPDATE.requiresExistingUsers()),

                () -> assertEquals("5", MenuAction.DELETE.code()),
                () -> assertTrue(MenuAction.DELETE.requiresExistingUsers()),

                () -> assertEquals("0", MenuAction.EXIT.code()),
                () -> assertEquals("Выход", MenuAction.EXIT.description()),
                () -> assertFalse(MenuAction.EXIT.requiresExistingUsers())
        );
    }
}