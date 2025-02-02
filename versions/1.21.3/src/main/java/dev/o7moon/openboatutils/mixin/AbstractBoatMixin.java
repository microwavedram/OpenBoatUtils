package dev.o7moon.openboatutils.mixin;

import dev.o7moon.openboatutils.CollisionMode;
import dev.o7moon.openboatutils.GetStepHeight;
import dev.o7moon.openboatutils.OpenBoatUtils;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBoatEntity.class)
public abstract class AbstractBoatMixin implements GetStepHeight {
    @Shadow
    abstract boolean checkBoatInWater();

    @Shadow
    @Nullable
    abstract AbstractBoatEntity.Location getUnderWaterLocation();

    @Shadow
    abstract AbstractBoatEntity.Location checkLocation();
    @Shadow
    AbstractBoatEntity.Location location;
    @Shadow
    float velocityDecay;
    @Shadow
    float nearbySlipperiness;
    @Shadow
    double waterLevel;

    @Shadow
    float yawVelocity;
    @Shadow
    boolean pressingForward;
    @Shadow
    boolean pressingBack;

    float openboatutils_step_height;
    public float getStepHeight() {
        return openboatutils_step_height;
    }

    @Unique
    public void set_step_height(float f) {
        openboatutils_step_height = f;
    }


    void oncePerTick(AbstractBoatEntity instance, AbstractBoatEntity.Location loc, MinecraftClient minecraft) {
        if ((loc == AbstractBoatEntity.Location.UNDER_FLOWING_WATER || loc == AbstractBoatEntity.Location.UNDER_WATER) && minecraft.options.jumpKey.isPressed() && OpenBoatUtils.swimForce != 0.0f) {
            Vec3d velocity = instance.getVelocity();
            instance.setVelocity(velocity.x, velocity.y + OpenBoatUtils.swimForce, velocity.z);
        }

        if (loc == AbstractBoatEntity.Location.ON_LAND || (OpenBoatUtils.waterJumping && loc == AbstractBoatEntity.Location.IN_WATER)) {
            OpenBoatUtils.coyoteTimer = OpenBoatUtils.coyoteTime;
        } else {
            OpenBoatUtils.coyoteTimer--;
        }

        float jumpForce = OpenBoatUtils.GetJumpForce((AbstractBoatEntity)(Object)this);

        if (OpenBoatUtils.coyoteTimer >= 0 && jumpForce > 0f && minecraft.options.jumpKey.isPressed()) {
            Vec3d velocity = instance.getVelocity();
            instance.setVelocity(velocity.x, jumpForce, velocity.z);
            OpenBoatUtils.coyoteTimer = -1;// cant jump again until grounded
        }
    }

    @Redirect(method = {"getPaddleSound"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractBoatEntity;checkLocation()Lnet/minecraft/entity/vehicle/AbstractBoatEntity$Location;"))
    AbstractBoatEntity.Location paddleHook(AbstractBoatEntity instance) {
        return hookCheckLocation(instance, false);
    }
    @Redirect(method = {"tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractBoatEntity;checkLocation()Lnet/minecraft/entity/vehicle/AbstractBoatEntity$Location;"))
    AbstractBoatEntity.Location tickHook(AbstractBoatEntity instance) {
        return hookCheckLocation(instance, true);
    }

    AbstractBoatEntity.Location hookCheckLocation(AbstractBoatEntity instance, boolean is_tick) {
        AbstractBoatMixin mixedInstance = (AbstractBoatMixin) (Object) instance;
        mixedInstance.set_step_height(0f);

        AbstractBoatEntity.Location loc = this.checkLocation();
        AbstractBoatEntity.Location original_loc = loc;
        if (!OpenBoatUtils.enabled) return loc;
        MinecraftClient minecraft = MinecraftClient.getInstance();
        if (minecraft == null) return loc;
        PlayerEntity player = minecraft.player;
        if (player == null) return loc;
        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof AbstractBoatEntity)) return loc;
        AbstractBoatEntity boat = (AbstractBoatEntity)vehicle;

        if (!boat.equals(instance)) return loc;
        if (is_tick) oncePerTick(instance, loc, minecraft);

        mixedInstance.set_step_height(OpenBoatUtils.getStepSize());

        if (loc == AbstractBoatEntity.Location.UNDER_WATER || loc == AbstractBoatEntity.Location.UNDER_FLOWING_WATER) {
            if (OpenBoatUtils.waterElevation) {
                instance.setPosition(instance.getX(), this.waterLevel += 1.0, instance.getZ());
                Vec3d velocity = instance.getVelocity();
                instance.setVelocity(velocity.x, 0f, velocity.z);// parity with old boatutils, but maybe in the future
                // there should be an implementation with different y velocities here.
                return AbstractBoatEntity.Location.IN_WATER;
            }
            return loc;
        }

        if (this.checkBoatInWater()) {
            if (OpenBoatUtils.waterElevation) {
                Vec3d velocity = instance.getVelocity();
                instance.setVelocity(velocity.x, 0.0, velocity.z);
            }
            loc = AbstractBoatEntity.Location.IN_WATER;
        }

        if (original_loc == AbstractBoatEntity.Location.IN_AIR && OpenBoatUtils.airControl) {
            this.nearbySlipperiness = OpenBoatUtils.getBlockSlipperiness("minecraft:air");
            loc = AbstractBoatEntity.Location.ON_LAND;
        }

        return loc;
    }

    @Redirect(method = "getNearbySlipperiness", at = @At(value="INVOKE",target="Lnet/minecraft/block/Block;getSlipperiness()F"))
    float getFriction(Block block) {
        if (!OpenBoatUtils.enabled) return block.getSlipperiness();
        return OpenBoatUtils.getBlockSlipperiness(Registries.BLOCK.getId(block).toString());
    }

    @Inject(method = "collidesWith", at = @At("HEAD"), cancellable = true)
    void canCollideHook(Entity other, CallbackInfoReturnable<Boolean> ci) {
        if (!OpenBoatUtils.enabled) return;
        CollisionMode mode = OpenBoatUtils.getCollisionMode();
        if (mode == CollisionMode.VANILLA) return;
        if (mode == CollisionMode.NO_BOATS_OR_PLAYERS && (other instanceof AbstractBoatEntity || other instanceof PlayerEntity)) {
            ci.setReturnValue(false);
            ci.cancel();
            return;
        }
        if (mode == CollisionMode.NO_ENTITIES) {
            ci.setReturnValue(false);
            ci.cancel();
            return;
        }
    }

    @Inject(method = "fall", at = @At("HEAD"), cancellable = true)
    void fallHook(CallbackInfo ci) {
        if (!OpenBoatUtils.fallDamage) ci.cancel();
    }

    @Inject(method = "getGravity", at = @At("HEAD"), cancellable = true)
    public void onGetGravity(CallbackInfoReturnable<Double> cir) {
        if (!OpenBoatUtils.enabled) return;

        cir.setReturnValue(-OpenBoatUtils.gravityForce);
        cir.cancel();
    }

    @Redirect(method = "updatePaddles", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/vehicle/AbstractBoatEntity;yawVelocity:F", opcode = Opcodes.PUTFIELD))
    private void redirectYawVelocityIncrement(AbstractBoatEntity boat, float yawVelocity) {
        if (!OpenBoatUtils.enabled) {
            this.yawVelocity = yawVelocity;
            return;
        }
        float original_delta = yawVelocity - this.yawVelocity;
        // sign isn't needed here because the vanilla acceleration is exactly 1,
        // but I suppose this helps if mojang ever decides to change that value for some reason
        this.yawVelocity += MathHelper.sign(original_delta) * OpenBoatUtils.GetYawAccel((AbstractBoatEntity)(Object)this);
    }

    // a whole lotta modifyconstants because mojang put the acceleration values in literals
    @ModifyConstant(method = "updatePaddles", constant = @Constant(floatValue = 0.04f, ordinal = 0))
    private float forwardsAccel(float original) {
        if (!OpenBoatUtils.enabled) return original;
        return OpenBoatUtils.GetForwardAccel((AbstractBoatEntity)(Object)this);
    }

    @ModifyConstant(method = "updatePaddles", constant = @Constant(floatValue = 0.005f, ordinal = 0))
    private float turnAccel(float original) {
        if (!OpenBoatUtils.enabled) return original;
        return OpenBoatUtils.GetTurnForwardAccel((AbstractBoatEntity)(Object)this);
    }

    @ModifyConstant(method = "updatePaddles", constant = @Constant(floatValue = 0.005f, ordinal = 1))
    private float backwardsAccel(float original) {
        if (!OpenBoatUtils.enabled) return original;
        return OpenBoatUtils.GetBackwardAccel((AbstractBoatEntity)(Object)this);
    }

    @Redirect(method = "updatePaddles", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/vehicle/AbstractBoatEntity;pressingForward:Z", opcode = Opcodes.GETFIELD, ordinal = 0))
    private boolean pressingForwardHook(AbstractBoatEntity instance) {
        if (!OpenBoatUtils.enabled || !OpenBoatUtils.allowAccelStacking) return this.pressingForward;
        return false;
    }

    @Redirect(method = "updatePaddles", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/vehicle/AbstractBoatEntity;pressingBack:Z", opcode = Opcodes.GETFIELD, ordinal = 0))
    private boolean pressingBackHook(AbstractBoatEntity instance) {
        if (!OpenBoatUtils.enabled || !OpenBoatUtils.allowAccelStacking) return this.pressingBack;
        return false;
    }

    // UNDER_FLOWING_WATER velocity decay
    @Redirect(method="updateVelocity", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/vehicle/AbstractBoatEntity;velocityDecay:F", opcode = Opcodes.PUTFIELD, ordinal = 2))
    private void velocityDecayHook1(AbstractBoatEntity boat, float orig) {
        if (!OpenBoatUtils.enabled || !OpenBoatUtils.underwaterControl) velocityDecay = orig;
        else velocityDecay = OpenBoatUtils.getBlockSlipperiness("minecraft:water");
    }

    // UNDER_WATER velocity decay
    @Redirect(method="updateVelocity", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/vehicle/AbstractBoatEntity;velocityDecay:F", opcode = Opcodes.PUTFIELD, ordinal = 3))
    private void velocityDecayHook2(AbstractBoatEntity boat, float orig) {
        if (!OpenBoatUtils.enabled || !OpenBoatUtils.underwaterControl) velocityDecay = orig;
        else velocityDecay = OpenBoatUtils.getBlockSlipperiness("minecraft:water");
    }

    // IN_WATER velocity decay
    @Redirect(method="updateVelocity", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/vehicle/AbstractBoatEntity;velocityDecay:F", opcode = Opcodes.PUTFIELD, ordinal = 1))
    private void velocityDecayHook3(AbstractBoatEntity boat, float orig) {
        if (!OpenBoatUtils.enabled || !OpenBoatUtils.surfaceWaterControl) velocityDecay = orig;
        else velocityDecay = OpenBoatUtils.getBlockSlipperiness("minecraft:water");
    }
}
