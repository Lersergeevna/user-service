package userservice.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * Служебный класс для создания {@link SessionFactory} и безопасного выполнения операций Hibernate.
 */
public final class HibernateUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUtil.class);
    private static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";

    private static SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    /**
     * Выполняет действие внутри транзакции.
     *
     * @param action действие, которое нужно выполнить
     * @param <T> тип результата
     * @return результат выполнения действия
     */
    public static <T> T executeInTransaction(Function<Session, T> action) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            T result = action.apply(session);
            transaction.commit();
            return result;
        } catch (RuntimeException e) {
            rollbackQuietly(transaction);
            throw e;
        }
    }

    /**
     * Выполняет действие без открытия транзакции.
     *
     * @param action действие, которое нужно выполнить
     * @param <T> тип результата
     * @return результат выполнения действия
     */
    public static <T> T executeWithoutTransaction(Function<Session, T> action) {
        try (Session session = getSessionFactory().openSession()) {
            return action.apply(session);
        }
    }

    /**
     * Возвращает единственный экземпляр {@link SessionFactory}.
     *
     * @return экземпляр фабрики сессий
     */
    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }

    /**
     * Закрывает фабрику сессий, если она была инициализирована.
     */
    public static synchronized void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            LOGGER.info("Закрытие Hibernate SessionFactory");
            sessionFactory.close();
        }
    }

    /**
     * Создаёт фабрику сессий Hibernate на основе конфигурации приложения.
     *
     * @return инициализированная фабрика сессий
     */
    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
            configuration.setProperty("hibernate.connection.url", AppProperties.dbUrl());
            configuration.setProperty("hibernate.connection.username", AppProperties.dbUsername());
            configuration.setProperty("hibernate.connection.password", AppProperties.dbPassword());
            configuration.setProperty("hibernate.connection.driver_class", POSTGRESQL_DRIVER);
            return configuration.buildSessionFactory();
        } catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Пытается откатить транзакцию без выброса новой ошибки наружу.
     *
     * @param transaction транзакция для отката
     */
    private static void rollbackQuietly(Transaction transaction) {
        if (transaction == null) {
            return;
        }
        try {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        } catch (Exception e) {
            LOGGER.error("Не удалось откатить транзакцию", e);
        }
    }
}