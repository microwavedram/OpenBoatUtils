package dev.o7moon.openboatutils;

import net.minecraft.resources.ResourceLocation;

public class StoredContext extends MutableContext {

    private final ResourceLocation identifier;

    public StoredContext(ResourceLocation identifier) {
        this.identifier = identifier;
        this.applyFrom(ISettingContext.VANILLA);
    }

    public ResourceLocation getIdentifier() {
        return identifier;
    }

    @Override
    public void switchTo() {}
}
