package userservice.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Выполняет миграции базы данных при запуске приложения.
 */
public final class DatabaseMigrator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseMigrator.class);
    private static final String MIGRATIONS_LOCATION = "classpath:db/migration";

    private DatabaseMigrator() {
    }

    /**
     * Применяет миграции Flyway к целевой базе данных.
     */
    public static void migrate() {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(AppProperties.dbUrl(), AppProperties.dbUsername(), AppProperties.dbPassword())
                    .locations(MIGRATIONS_LOCATION)
                    .load();

            LOGGER.info("Запуск миграций базы данных");
            flyway.migrate();
            LOGGER.info("Миграции базы данных успешно завершены");
        } catch (Exception e) {
            LOGGER.error("Не удалось выполнить миграции базы данных", e);
            throw e;
        }
    }
}