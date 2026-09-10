package dev.o7moon.openboatutils.mixin;

import dev.o7moon.openboatutils.*;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
//? >= 1.21.5 {
/*import org.spongepowered.asm.mixin.Final;
*///? }
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? >= 1.21.5 {
/*import java.util.function.Supplier;
*///? }
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
//? >= 1.21.5 {
/*import net.minecraft.world.entity.InterpolationHandler;
*///? }
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
//? >= 1.21.11 {
/*import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
*///? } else {
import net.minecraft.world.entity.vehicle.AbstractBoat;
//? }
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(AbstractBoat.class)
public abstract class BoatMixin implements GetStepHeight, GetNearbySetting {

    //? >= 1.21.5 {
    /*@Shadow
    @Final
    private InterpolationHandler interpolation;
    *///? }

    @Shadow
    protected abstract AbstractBoat.Status getStatus();

    @Shadow
    private double waterLevel;

    @Shadow
    protected abstract boolean checkInWater();

    @Shadow private float landFriction;
    @Shadow private float deltaRotation;

    @Shadow private boolean inputUp;
    @Shadow private boolean inputDown;

    //? >= 1.21.9 {
    //? } else {
    @Shadow private float invFriction;
    //? }

    @Unique private int openboatutils$defaultInterpolation;

    @Unique private int openboatutils$coyoteTimer;
    @Unique private int openboatutils$remaining_jumps;
    @Unique private boolean openboatutils$debounce = false;

    @Unique private float openBoatUtils$lastScale;

    @Unique float openboatutils$stepHeight;
    @Unique public float openboatutils$getStepHeight() {
        return openboatutils$stepHeight;
    }
    @Unique public void openboatutils$setStepHeight(float f) {
        openboatutils$stepHeight = f;
    }

    @Unique public float openboatutils$getAverageNearbySetting(ISettingContext context, AbstractBoat instance, PerBlockSettingType setting) {
        AABB box = instance.getBoundingBox();
        AABB box2 = new AABB(box.minX, box.minY - 0.001, box.minZ, box.maxX, box.minY, box.maxZ);
        int i = Mth.floor(box2.minX) - 1;
        int j = Mth.ceil(box2.maxX) + 1;
        int k = Mth.floor(box2.minY) - 1;
        int l = Mth.ceil(box2.maxY) + 1;
        int m = Mth.floor(box2.minZ) - 1;
        int n = Mth.ceil(box2.maxZ) + 1;
        VoxelShape voxelShape = Shapes.create(box2);
        float f = 0.0f;
        int o = 0;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int p = i; p < j; ++p) {
            for (int q = m; q < n; ++q) {
                int r = (p == i || p == j - 1 ? 1 : 0) + (q == m || q == n - 1 ? 1 : 0);
                if (r == 2) continue;
                for (int s = k; s < l; ++s) {
                    if (r > 0 && (s == k || s == l - 1)) continue;
                    mutable.set(p, s, q);

                    Level world = instance.level();

                    BlockState blockState = world.getBlockState(mutable);

                    if (blockState.getBlock() instanceof WaterlilyBlock || !Shapes.joinIsNotEmpty(blockState.getCollisionShape(world, mutable).move(p, s, q), voxelShape, BooleanOp.AND)) continue;

                    Float value = context.getBlockSetting(BuiltInRegistries.BLOCK.getKey(blockState.getBlock()), setting);

                    if (value == null) value = setting.fromContext(context);

                    f += value;
                    ++o;
                }
            }
        }


        Float air = context.getBlockSetting(BuiltInRegistries.BLOCK.getKey(Blocks.AIR), setting);

        if (air == null) air = setting.fromContext(context);

        if (o == 0) return air;

        return f / (float) o;
    }

    @Unique public float openboatutils$getMaxNearbySetting(ISettingContext context, AbstractBoat instance, PerBlockSettingType setting) {
        AABB box = instance.getBoundingBox();
        AABB box2 = new AABB(box.minX, box.minY - 0.001, box.minZ, box.maxX, box.minY, box.maxZ);
        int i = Mth.floor(box2.minX) - 1;
        int j = Mth.ceil(box2.maxX) + 1;
        int k = Mth.floor(box2.minY) - 1;
        int l = Mth.ceil(box2.maxY) + 1;
        int m = Mth.floor(box2.minZ) - 1;
        int n = Mth.ceil(box2.maxZ) + 1;
        VoxelShape voxelShape = Shapes.create(box2);
        float f = 0.0f;
        int o = 0;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int p = i; p < j; ++p) {
            for (int q = m; q < n; ++q) {
                int r = (p == i || p == j - 1 ? 1 : 0) + (q == m || q == n - 1 ? 1 : 0);
                if (r == 2) continue;
                for (int s = k; s < l; ++s) {
                    if (r > 0 && (s == k || s == l - 1)) continue;
                    mutable.set(p, s, q);

                    Level world = instance.level();

                    BlockState blockState = world.getBlockState(mutable);

                    if (blockState.getBlock() instanceof WaterlilyBlock || !Shapes.joinIsNotEmpty(blockState.getCollisionShape(world, mutable).move(p, s, q), voxelShape, BooleanOp.AND)) continue;

                    Float value = context.getBlockSetting(BuiltInRegistries.BLOCK.getKey(blockState.getBlock()), setting);

                    if (value == null) value = setting.fromContext(context);

                    f = Math.max(f, value);
                    ++o;
                }
            }
        }

        Float air = context.getBlockSetting(BuiltInRegistries.BLOCK.getKey(Blocks.AIR), setting);

        if (air == null) air = setting.fromContext(context);

        if (o == 0) return air;

        return f;
    }

    @Unique
    void oncePerTick(AbstractBoat instance, AbstractBoat.Status loc, Minecraft minecraft) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context == null) return;

        if ((loc ==  AbstractBoat.Status.UNDER_FLOWING_WATER || loc == AbstractBoat.Status.UNDER_WATER) && minecraft.options.keyJump.isDown() && context.getSwimForce() != 0.0f) {
            Vec3 velocity = instance.getDeltaMovement();
            instance.setDeltaMovement(velocity.x, velocity.y + context.getSwimForce(), velocity.z);
        }

        if (loc == AbstractBoat.Status.ON_LAND || (context.hasWaterJumping() && loc == AbstractBoat.Status.IN_WATER)) {
            openboatutils$coyoteTimer = context.getCoyoteTime();
            openboatutils$remaining_jumps = context.getJumps();
            openboatutils$debounce = false;

            if (context.hasAnyBlocksWithSetting(PerBlockSettingType.COYOTE_TIME)) {
                openboatutils$coyoteTimer = Math.round(openboatutils$getMaxNearbySetting(context, instance, PerBlockSettingType.COYOTE_TIME));
            }

            if (context.hasAnyBlocksWithSetting(PerBlockSettingType.JUMPS)) {
                openboatutils$remaining_jumps = Math.round(openboatutils$getMaxNearbySetting(context, instance, PerBlockSettingType.JUMPS));
            }
        } else {
            openboatutils$coyoteTimer--;

            if (openboatutils$coyoteTimer == -1) {
                openboatutils$remaining_jumps--;
            }
        }

        float jumpForce = openboatutils$getAverageNearbySetting(context, instance, PerBlockSettingType.JUMP_FORCE);

        boolean jumping = minecraft.options.keyJump.isDown();

        if (!jumping) openboatutils$debounce = false;

        if (openboatutils$remaining_jumps > 0 && jumpForce != 0f && jumping && !openboatutils$debounce) {
            Vec3 velocity = instance.getDeltaMovement();
            instance.setDeltaMovement(velocity.x, jumpForce, velocity.z);

            openboatutils$coyoteTimer = -1;
            openboatutils$remaining_jumps--;
            openboatutils$debounce = true;
        }
    }

    //? >= 1.21.5 {
    /*@Inject(method = "<init>", at = @At("RETURN"))
    private void hookConstructor(EntityType<?> type, Level world, Supplier<?> itemSupplier, CallbackInfo ci) {
        openboatutils$defaultInterpolation = ((PositionInterpolatorAccessor) interpolation).getLerpDuration();
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void hookPositionInterpolator(CallbackInfo ci) {
        if (OpenBoatUtils.instance.getInterpolationCompatibility()) {
            interpolation.setInterpolationLength(10);
            return;
        }

        interpolation.setInterpolationLength(openboatutils$defaultInterpolation);
    }
    *///? } else {
    @ModifyVariable(method = "lerpTo", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    int interpolationStepsHook(int interpolationSteps) {
        if (!OpenBoatUtils.instance.getInterpolationCompatibility()) return interpolationSteps;
        return 10;
    }
    //? }

    //? >= 1.21.11 {
    /*@Redirect(method = "getPaddleSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;getStatus()Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat$Status;"))
    *///? } else {
    @Redirect(method = "getPaddleSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractBoat;getStatus()Lnet/minecraft/world/entity/vehicle/AbstractBoat$Status;"))
    //? }
    AbstractBoat.Status paddleHook(AbstractBoat instance) {
        return hookgetStatus(instance, false);
    }

    //? >= 1.21.11 {
    /*@Redirect(method = {"tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;getStatus()Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat$Status;"))
    *///? } else {
    @Redirect(method = {"tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractBoat;getStatus()Lnet/minecraft/world/entity/vehicle/AbstractBoat$Status;"))
    //? }
    AbstractBoat.Status tickHook(AbstractBoat instance) {
        return hookgetStatus(instance, true);
    }

    @Unique
    AbstractBoat.Status hookgetStatus(AbstractBoat instance, boolean is_tick) {
        BoatMixin mixedInstance = (BoatMixin) (Object) instance;

        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        assert mixedInstance != null;

        mixedInstance.openboatutils$setStepHeight(0f);

        AbstractBoat.Status loc = this.getStatus();
        AbstractBoat.Status original_loc = loc;

        if (context == null) return loc;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) return loc;
        Player player = minecraft.player;
        if (player == null) return loc;
        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof AbstractBoat boat)) return loc;

        if (!boat.equals(instance)) return loc;
        if (is_tick) oncePerTick(instance, loc, minecraft);

        mixedInstance.openboatutils$setStepHeight(context.getStepSize());

        if (loc == AbstractBoat.Status.UNDER_WATER || loc == AbstractBoat.Status.UNDER_FLOWING_WATER) {
            if (context.hasWaterElevation() && (is_tick || !context.getFixDoubleWaterElevation())) {
                instance.setPos(instance.getX(), this.waterLevel += 1.0, instance.getZ());
                Vec3 velocity = instance.getDeltaMovement();
                instance.setDeltaMovement(velocity.x, 0f, velocity.z);// parity with old boatutils, but maybe in the future
                // there should be an implementation with different y velocities here.
                return AbstractBoat.Status.IN_WATER;
            }
            return loc;
        }

        if (this.checkInWater()) {
            if (context.hasWaterElevation() && (is_tick || !context.getFixDoubleWaterElevation())) {
                Vec3 velocity = instance.getDeltaMovement();
                instance.setDeltaMovement(velocity.x, 0.0, velocity.z);
            }
            loc = AbstractBoat.Status.IN_WATER;
        }

        if (original_loc == AbstractBoat.Status.IN_AIR && context.hasAirControl()) {
            Float slipperiness = context.getBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.AIR));

            if (slipperiness == null) slipperiness = context.getDefaultSlipperiness();

            this.landFriction = slipperiness;
            loc = AbstractBoat.Status.ON_LAND;
        }

        return loc;
    }

    @Redirect(method = "getGroundFriction", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/block/Block;getFriction()F"))
    float getFriction(Block block) {

        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context == null) return block.getFriction();

        Float slipperiness = context.getBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(block));

        if (slipperiness != null) return slipperiness;

        return context.getDefaultSlipperiness();
    }

    @Inject(method = "canCollideWith", at = @At("HEAD"), cancellable = true)
    void canCollideHook(Entity other, CallbackInfoReturnable<Boolean> ci) {

        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context == null) return;

        CollisionMode mode = context.getCollisionMode();

        // TODO: investigate this logic

        if (mode == CollisionMode.VANILLA) return;

        if ((mode == CollisionMode.NO_BOATS_OR_PLAYERS || mode == CollisionMode.NO_BOATS_OR_PLAYERS_PLUS_FILTER) &&
                (other instanceof AbstractBoat || other instanceof Player)) {
            ci.setReturnValue(false);
            ci.cancel();
            return;
        }
        if (mode == CollisionMode.NO_ENTITIES) {
            ci.setReturnValue(false);
            ci.cancel();
            return;
        }
        if ((mode == CollisionMode.ENTITYTYPE_FILTER || mode == CollisionMode.NO_BOATS_OR_PLAYERS_PLUS_FILTER) &&
                (context.isEntityTypeFiltered(other.getType()))) {
            ci.setReturnValue(false);
            ci.cancel();
            return;
        }
    }

    @Inject(method = "checkFallDamage", at = @At("HEAD"), cancellable = true)
    void fallHook(CallbackInfo ci) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context == null) return;

        if (!context.hasFallDamage()) ci.cancel();
    }


    @Inject(method = "getDefaultGravity", at = @At("HEAD"), cancellable = true)
    public void onGetGravity(CallbackInfoReturnable<Double> cir) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context == null) return;

        cir.setReturnValue(-context.getGravityForce());
        cir.cancel();
    }

    //? >= 1.21.11 {
    /*@Redirect(method = "controlBoat", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;deltaRotation:F", opcode = Opcodes.PUTFIELD))
    *///? } else {
    @Redirect(method = "controlBoat", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/vehicle/AbstractBoat;deltaRotation:F", opcode = Opcodes.PUTFIELD))
     //? }
    private void redirectDeltaRotationIncrement(AbstractBoat instance, float deltaRotation) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context == null) {
            this.deltaRotation = deltaRotation;
            return;
        }
        float original_delta = deltaRotation - this.deltaRotation;
        // sign isn't needed here because the vanilla acceleration is exactly 1,
        // but I suppose this helps if mojang ever decides to change that value for some reason

        float yaw_accel = openboatutils$getAverageNearbySetting(context, instance, PerBlockSettingType.YAW_ACCEL);

        this.deltaRotation += Mth.sign(original_delta) * yaw_accel;
    }

    // a whole lotta modifyconstants because mojang put the acceleration values in literals
    @ModifyConstant(method = "controlBoat", constant = @Constant(floatValue = 0.04f, ordinal = 0))
    private float forwardsAccel(float original) {
        ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context == null) return original;

        return openboatutils$getAverageNearbySetting(context, (AbstractBoat) (Object) this, PerBlockSettingType.FORWARDS_ACCEL);
    }

    @ModifyConstant(method = "controlBoat", constant = @Constant(floatValue = 0.005f, ordinal = 0))
    private float turnAccel(float original) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context == null) return original;

        return openboatutils$getAverageNearbySetting(context, (AbstractBoat) (Object) this, PerBlockSettingType.TURN_FORWARDS_ACCEL);
    }

    @ModifyConstant(method = "controlBoat", constant = @Constant(floatValue = 0.005f, ordinal = 1))
    private float backwardsAccel(float original) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context == null) return original;

        return openboatutils$getAverageNearbySetting(context, (AbstractBoat) (Object) this, PerBlockSettingType.BACKWARDS_ACCEL);
    }

    //? >= 1.21.11 {
    /*@Redirect(method = "controlBoat", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;inputUp:Z", opcode = Opcodes.GETFIELD, ordinal = 0))
    *///? } else {
    @Redirect(method = "controlBoat", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/vehicle/AbstractBoat;inputUp:Z", opcode = Opcodes.GETFIELD, ordinal = 0))
     //? }
    private boolean pressingForwardHook(AbstractBoat instance) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context == null || !context.hasAllowAccelStacking()) return this.inputUp;

        return false;
    }

    //? >= 1.21.11 {
    /*@Redirect(method = "controlBoat", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;inputDown:Z", opcode = Opcodes.GETFIELD, ordinal = 0))
    *///? } else {
    @Redirect(method = "controlBoat", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/vehicle/AbstractBoat;inputDown:Z", opcode = Opcodes.GETFIELD, ordinal = 0))
     //? }
    private boolean pressingBackHook(AbstractBoat instance) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context != null) {
            float brakeSlipperiness = openboatutils$getAverageNearbySetting(context, instance, PerBlockSettingType.BRAKE_SLIPPERINESS);
            if (brakeSlipperiness != 1f) {
                return false;
            }
        }

        if (context == null || !context.hasAllowAccelStacking()) return ((BoatAccessor) instance).getPressingBack();

        return false;
    }

    //? >= 1.21.11 {
    /*@Inject(
            method = "floatBoat",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;setDeltaMovement(DDD)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    *///? } else {
    @Inject(
            method = "floatBoat",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/vehicle/AbstractBoat;setDeltaMovement(DDD)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    //? }

    private void hookSlipperiness(CallbackInfo ci) {
        AbstractBoat boat = (AbstractBoat) (Object) this;
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();
        if (context != null) {
            float lateralSlipperiness = openboatutils$getAverageNearbySetting(context, boat, PerBlockSettingType.LATERAL_SLIPPERINESS);
            if (lateralSlipperiness != 1f) openboatutils$applyLateralSlipperiness(boat, lateralSlipperiness);

            float maxSpeed = openboatutils$getAverageNearbySetting(context, boat, PerBlockSettingType.MAX_SPEED);
            float maxSpeedResistance = openboatutils$getAverageNearbySetting(context, boat, PerBlockSettingType.MAX_SPEED_RESISTANCE);

            if (maxSpeed >= 0) openboatutils$applyMaxSpeedResistance(boat, maxSpeed, maxSpeedResistance);

            if (inputDown) {
                float brakeSlipperiness = openboatutils$getAverageNearbySetting(context, boat, PerBlockSettingType.BRAKE_SLIPPERINESS);
                if (brakeSlipperiness != 1f) openboatutils$applyBrakeSlipperiness(boat, brakeSlipperiness);
            }
        }
    }

    @Unique
    private void openboatutils$applyLateralSlipperiness(AbstractBoat boat, float slipperiness) {
        Vec3 velocity = boat.getDeltaMovement();

        float grip = 1 - slipperiness;
        double yaw = Math.toRadians(boat.getYRot());

        Vec3 forward = new Vec3(-Math.sin(yaw), 0, Math.cos(yaw));
        Vec3 velocityXZ = new Vec3(velocity.x, 0, velocity.z);
        Vec3 alongForward = forward.scale(velocityXZ.dot(forward));
        Vec3 lateralSlip = velocityXZ.subtract(alongForward);

        Vec3 gripForce = lateralSlip.scale(grip);

        boat.setDeltaMovement(
                velocity.x - gripForce.x,
                velocity.y,
                velocity.z - gripForce.z
        );
    }

    @Unique
    private void openboatutils$applyBrakeSlipperiness(AbstractBoat boat, float slipperiness) {
        Vec3 velocity = boat.getDeltaMovement();

        boat.setDeltaMovement(
                velocity.x * slipperiness,
                velocity.y,
                velocity.z * slipperiness
        );
    }

    @Unique
    private void openboatutils$applyMaxSpeedResistance(AbstractBoat boat, float maxSpeed, float maxSpeedResistance) {
        Vec3 velocity = boat.getDeltaMovement();

        double horizonalSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);

        // it's a little goofy because all per block settings are floats, but it doesn't matter
        float extraSpeed = (float) Math.max(0, horizonalSpeed - maxSpeed);

        if (extraSpeed > 0) {
            float multiplier = 1f - (extraSpeed * maxSpeedResistance);

            boat.setDeltaMovement(
                    velocity.x * multiplier,
                    velocity.y,
                    velocity.z * multiplier
            );
        }
    }

    //? >= 1.21.5 {
    /*// UNDER_FLOWING_WATER
    @ModifyConstant(method="floatBoat", constant = @Constant(floatValue = 0.9F, ordinal = 1))
    private float velocityDecayHook1(float constant) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context != null && context.hasUnderwaterControl()) {
            Float slipperiness = context.getBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.WATER));;

            if (slipperiness != null) return slipperiness;

            return context.getDefaultSlipperiness();
        }

        return constant;
    }

    // UNDER_WATER
    @ModifyConstant(method="floatBoat", constant = @Constant(floatValue = 0.45F))
    private float velocityDecayHook2(float constant) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context != null && context.hasUnderwaterControl()) {
            Float slipperiness = context.getBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.WATER));;

            if (slipperiness != null) return slipperiness;

            return context.getDefaultSlipperiness();
        }

        return constant;
    }

    // IN_WATER
    @ModifyConstant(method="floatBoat", constant = @Constant(floatValue = 0.9F, ordinal = 0))
    private float velocityDecayHook3(float constant) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context != null && context.hasSurfaceWaterControl()) {
            Float slipperiness = context.getBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.WATER));;

            if (slipperiness != null) return slipperiness;

            return context.getDefaultSlipperiness();
        }

        return constant;
    }
    *///? } else {
    // UNDER_FLOWING_WATER velocity decay
    @Redirect(method="floatBoat", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/vehicle/AbstractBoat;invFriction:F", opcode = Opcodes.PUTFIELD, ordinal = 2))
    private void velocityDecayHook1(AbstractBoat boat, float orig) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context == null || !context.hasUnderwaterControl()) {
            invFriction = orig;
            return;
        }

        Float slipperiness = context.getBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.WATER));

        invFriction = slipperiness != null
                ? slipperiness
                : context.getDefaultSlipperiness();
    }


    // UNDER_WATER velocity decay
    @Redirect(method="floatBoat", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/vehicle/AbstractBoat;invFriction:F", opcode = Opcodes.PUTFIELD, ordinal = 3))
    private void velocityDecayHook2(AbstractBoat boat, float orig) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context == null || !context.hasUnderwaterControl()) {
            invFriction = orig;
            return;
        }

        Float slipperiness = context.getBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.WATER));

        invFriction = slipperiness != null
                ? slipperiness
                : context.getDefaultSlipperiness();
    }


    // IN_WATER velocity decay
    @Redirect(method="floatBoat", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/vehicle/AbstractBoat;invFriction:F", opcode = Opcodes.PUTFIELD, ordinal = 1))
    private void velocityDecayHook3(AbstractBoat boat, float orig) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context == null || !context.hasSurfaceWaterControl()) {
            invFriction = orig;
            return;
        }

        Float slipperiness = context.getBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.WATER));

        invFriction = slipperiness != null
                ? slipperiness
                : context.getDefaultSlipperiness();
    }
    //? }


    // Increase resolution for wall priority by running move() multiple times in smaller increments
    //? >= 1.21.11 {
    /*@Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"))
    *///? } else {
    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractBoat;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"))
    //? }
    private void moveHook(AbstractBoat instance, MoverType movementType, Vec3 vec3d) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context == null) {
            instance.move(movementType, vec3d);
            return;
        }

        int resolution = context.getCollisionResolution();

        if (resolution < 1 || resolution > 50) {
            instance.move(movementType, vec3d);
            return;
        }

        Vec3 subMoveVel = instance.getDeltaMovement().scale(1d / resolution);
        for(int i = 0; i < resolution; i++) {
            instance.move(movementType, subMoveVel);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        float currentScale = 1f;

        if (context != null) {
            currentScale = context.getScale();
        }

        if (currentScale != openBoatUtils$lastScale) {
            openBoatUtils$lastScale = currentScale;
            ((AbstractBoat) (Object) this).refreshDimensions();
        }
    }

    @Inject(method = "setBubbleTime", at = @At("HEAD"), cancellable = true)
    private void hookBubbleTime(int wobbleTicks, CallbackInfo ci) {
        ci.cancel();
    }
}
