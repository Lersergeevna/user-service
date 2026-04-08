package userservice;

import userservice.config.DatabaseMigrator;
import userservice.config.HibernateUtil;
import userservice.constants.Messages;
import userservice.ui.ConsoleApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Точка входа в консольное приложение.
 */
public final class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private Main() {
    }

    /**
     * Запускает приложение, выполняет миграции базы данных и открывает консольное меню.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        try {
            DatabaseMigrator.migrate();
            HibernateUtil.getSessionFactory();
            new ConsoleApp().run();
        } catch (Throwable e) {
            LOGGER.error("Критическая ошибка запуска приложения.", e);
            System.err.println(Messages.formatError(Messages.CRITICAL_STARTUP_ERROR));
        } finally {
            HibernateUtil.shutdown();
        }
    }
}