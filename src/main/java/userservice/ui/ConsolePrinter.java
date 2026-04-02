package userservice.ui;

import userservice.constants.Messages;
import userservice.entity.UserEntity;

import java.util.List;

/**
 * Отвечает за вывод сообщений и данных в консоль.
 */
public final class ConsolePrinter {
    /**
     * Печатает заголовок приложения.
     */
    public void printHeader() {
        line();
        System.out.println(Messages.APP_TITLE);
        line();
    }

    /**
     * Печатает меню команд.
     */
    public void printMenu() {
        System.out.println();
        System.out.println(Messages.CHOOSE_ACTION);
        for (MenuAction action : MenuAction.values()) {
            System.out.printf("%s - %s%n", action.code(), action.description());
        }
        System.out.println();
    }

    /**
     * Печатает сведения об одном пользователе.
     *
     * @param userEntity пользователь
     */
    public void printUser(UserEntity userEntity) {
        line();
        System.out.println("Пользователь");
        line();
        System.out.println("ID         : " + userEntity.getId());
        System.out.println("Имя        : " + userEntity.getName());
        System.out.println("E-mail     : " + userEntity.getEmail());
        System.out.println("Возраст    : " + userEntity.getAge());
        System.out.println("Создан     : " + userEntity.getCreatedAt());
        line();
    }

    /**
     * Печатает список пользователей в табличном виде.
     *
     * @param usersEntities список пользователей
     */
    public void printUsers(List<UserEntity> usersEntities) {
        if (usersEntities.isEmpty()) {
            printInfo(Messages.USER_LIST_EMPTY);
            return;
        }
        line();
        System.out.printf("%-5s | %-20s | %-30s | %-8s | %-20s%n",
                "ID", "Имя", "E-mail", "Возраст", "Создан");
        line();
        for (UserEntity userEntity : usersEntities) {
            System.out.printf("%-5d | %-20s | %-30s | %-8d | %-20s%n",
                    userEntity.getId(),
                    shorten(userEntity.getName(), 20),
                    shorten(userEntity.getEmail(), 30),
                    userEntity.getAge(),
                    String.valueOf(userEntity.getCreatedAt()));
        }
        line();
    }

    /**
     * Печатает сообщение об успешном выполнении.
     *
     * @param message текст сообщения
     */
    public void printSuccess(String message) {
        System.out.println(Messages.formatSuccess(message));
    }

    /**
     * Печатает сообщение об ошибке.
     *
     * @param message текст сообщения
     */
    public void printError(String message) {
        System.out.println(Messages.formatError(message));
    }

    /**
     * Печатает информационное сообщение.
     *
     * @param message текст сообщения
     */
    public void printInfo(String message) {
        System.out.println(Messages.formatInfo(message));
    }

    /**
     * Печатает разделительную линию.
     */
    private void line() {
        System.out.println("----------------------------------------------------------------------");
    }

    /**
     * Укорачивает строку до заданной длины.
     *
     * @param value строка
     * @param maxLength максимальная длина
     * @return укороченная строка
     */
    private String shorten(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }
}