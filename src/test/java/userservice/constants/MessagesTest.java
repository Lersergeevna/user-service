package userservice.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessagesTest {

    @Test
    void formatError_shouldAddErrorPrefix() {
        assertEquals("[ОШИБКА] ошибка", Messages.formatError("ошибка"));
    }

    @Test
    void formatSuccess_shouldAddSuccessPrefix() {
        assertEquals("[УСПЕХ] готово", Messages.formatSuccess("готово"));
    }

    @Test
    void formatInfo_shouldAddInfoPrefix() {
        assertEquals("[ИНФО] информация", Messages.formatInfo("информация"));
    }
}