package dev.o7moon.openboatutils.mixin;


import org.spongepowered.asm.mixin.Mixin;
import dev.o7moon.openboatutils.ISettingContext;
import dev.o7moon.openboatutils.OpenBoatUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.HoneyBlock;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoneyBlock.class)
public class HoneyBlockMixin {
    @Shadow
    private static double getOldDeltaY(double d) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    private static double getNewDeltaY(double d) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Redirect(method = "doSlideMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/HoneyBlock;getOldDeltaY(D)D"))
    private double restoreVelocityThreshold(double v) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context != null && context.hasHoneyCompatibility()) {
            return v;
        }

        return getOldDeltaY(v);
    }

    @Redirect(method = "doSlideMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/HoneyBlock;getNewDeltaY(D)D"))
    private double restoreVelocitySet(double v) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context != null && context.hasHoneyCompatibility()) {
            return v;
        }

        return getNewDeltaY(v);
    }

    @Inject(method = "isSlidingDown", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/HoneyBlock;getOldDeltaY(D)D"), cancellable = true)
    private void restoreSlidingThreshold(BlockPos pos, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();
        if (context != null && context.hasHoneyCompatibility()) {
            if (entity.getDeltaMovement().y >= -0.08) {
                cir.setReturnValue(false);
            }
        }
    }

    @Redirect(method = "isSlidingDown", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/HoneyBlock;getOldDeltaY(D)D"))
    private double restoreSlidingVelocityCheck(double v) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();
        if (context != null && context.hasHoneyCompatibility()) {
            return v;
        }
        return getOldDeltaY(v);
    }
}
