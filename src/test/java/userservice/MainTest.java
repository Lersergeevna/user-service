package userservice;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import userservice.config.DatabaseMigrator;
import userservice.config.HibernateUtil;
import userservice.constants.Messages;
import userservice.ui.ConsoleApp;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class MainTest {

    private final PrintStream originalErr = System.err;

    @AfterEach
    void tearDown() {
        System.setErr(originalErr);
    }

    @Test
    void main_shouldRunApplicationSuccessfully() {
        try (MockedStatic<DatabaseMigrator> migratorMock = mockStatic(DatabaseMigrator.class);
             MockedStatic<HibernateUtil> hibernateMock = mockStatic(HibernateUtil.class);
             MockedConstruction<ConsoleApp> appConstruction = mockConstruction(ConsoleApp.class)) {

            SessionFactory sessionFactory = mock(SessionFactory.class);
            hibernateMock.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);

            Main.main(new String[0]);

            migratorMock.verify(DatabaseMigrator::migrate);
            hibernateMock.verify(HibernateUtil::getSessionFactory);
            verify(appConstruction.constructed().get(0)).run();
            hibernateMock.verify(HibernateUtil::shutdown);
        }
    }

    @Test
    void main_shouldPrintCriticalErrorWhenStartupFails() throws Exception {
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        System.setErr(new PrintStream(err, true, StandardCharsets.UTF_8));

        try (MockedStatic<DatabaseMigrator> migratorMock = mockStatic(DatabaseMigrator.class);
             MockedStatic<HibernateUtil> hibernateMock = mockStatic(HibernateUtil.class)) {

            migratorMock.when(DatabaseMigrator::migrate)
                    .thenThrow(new RuntimeException("startup failed"));

            Main.main(new String[0]);

            String errorOutput = err.toString(StandardCharsets.UTF_8);
            assertTrue(errorOutput.contains(Messages.formatError(Messages.CRITICAL_STARTUP_ERROR)));

            migratorMock.verify(DatabaseMigrator::migrate);
            hibernateMock.verify(HibernateUtil::shutdown);
        }
    }
}