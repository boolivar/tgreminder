package org.bool.tgreminder.core;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpdateToken {

    private final String value;

    public UpdateToken() {
        this(UUID.randomUUID());
    }
    
    public UpdateToken(UUID uuid) {
        this(uuid.toString());
    }
    
    public UpdateToken(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
