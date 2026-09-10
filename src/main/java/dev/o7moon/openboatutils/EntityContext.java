package dev.o7moon.openboatutils;

import java.util.UUID;

public class EntityContext extends MutableContext {

    private final UUID identifier;

    public EntityContext(UUID identifier) {
        this.identifier = identifier;
        this.applyFrom(ISettingContext.VANILLA);
    }

    public UUID getIdentifier() {
        return identifier;
    }

    @Override
    public void switchTo() {}
}
