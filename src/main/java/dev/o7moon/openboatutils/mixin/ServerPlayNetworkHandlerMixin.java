package dev.o7moon.openboatutils.mixin;

import dev.o7moon.openboatutils.OpenBoatUtils;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {
    @Inject(method = "containsInvalidValues", at = @At("HEAD"), cancellable = true)
    private static void isMovementInvalid(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);// !!! this disables the movement anti-cheat, but is necessary for stepping to work in singleplayer
    }

    // Force the flag that triggers moved wrongly to false

    //? <1.21.6 {
    @ModifyConstant(method = "handleMoveVehicle", constant = @Constant(doubleValue = 0.0625, ordinal = 0))
    private double onVehicleMove_WronglyFlag(double constant) {
        return Float.MAX_VALUE;
    }
    //?}

    //? >=1.21.6 {
    /*@ModifyConstant(method = "handleMoveVehicle", constant = @Constant(doubleValue = 0.0625))
    private double preventMovedWrongly(double constant) {
        return OpenBoatUtils.instance.getActiveContext() != null ? Double.MAX_VALUE : constant;
    }
    *///?}
}
