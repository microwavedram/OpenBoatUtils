package dev.o7moon.openboatutils.mixin;

import dev.o7moon.openboatutils.OpenBoatUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientWorldMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void postWorldLoad(CallbackInfo ci) {
        if (OpenBoatUtils.instance.getResetOnWorldLoad()) {
            OpenBoatUtils.instance.setActiveContext(null);

            OpenBoatUtils.instance.dropAllContextStores();
        }
    }
}
