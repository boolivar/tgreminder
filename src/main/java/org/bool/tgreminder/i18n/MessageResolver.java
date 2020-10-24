package org.bool.tgreminder.i18n;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageResolver {

    private final MessageSource messageSource;
    
    public MessageResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    public String getMessage(Messages message, String... args) {
        return messageSource.getMessage(message.name(), args, null);
    }
}
