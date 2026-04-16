package userservice.service;

public interface MessageService {
    String getMessage(String code, Object... args);
}