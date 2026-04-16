package userservice.service.impl;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import userservice.service.MessageService;

import java.util.Locale;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageSource messageSource;

    public MessageServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }
}