package dev.o7moon.openboatutils.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.o7moon.openboatutils.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
//? >= 1.21.11 {
/*import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
*///? } else {
import net.minecraft.world.entity.vehicle.AbstractBoat;
//? }
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract UUID getUUID();

    @Shadow
    private static float[] collectCandidateStepUpHeights(AABB aABB, List<VoxelShape> list, float f, float g) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    private static Vec3 collideWithShapes(Vec3 vec3, AABB aABB, List<VoxelShape> list) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    //? < 26.2 {
    @Shadow
    private static List<VoxelShape> collectColliders(@Nullable Entity entity, Level level, List<VoxelShape> list, AABB aABB) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }
    //? }

    @Shadow
    private Level level;

    @Shadow
    private static List<VoxelShape> collectCollidersIgnoringWorldBorder(Entity par1, Level par2, List<VoxelShape> par3, AABB par4) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Inject(method = "maxUpStep", at = @At("HEAD"), cancellable = true)
    public void getStepHeight(CallbackInfoReturnable<Float> cir) {
        if (this instanceof GetStepHeight step) {
            cir.setReturnValue(step.openboatutils$getStepHeight());
            cir.cancel();
        }
    }

    //? < 26.2 {
    @Redirect(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V"
            )
    )
    private void hookWalltap(Entity instance, double x, double y, double z) {
    //? } else {
    /*@Redirect(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
            )
    )
    private void hookWalltap(Entity instance, Vec3 vec3) {
        double x = vec3.x();
        double y = vec3.y();
        double z = vec3.z();
    *///? }

        if ((Object) this instanceof AbstractBoat) {
            ISettingContext context = OpenBoatUtils.instance.getActiveContext();

            if (context != null && (context.getWalltapMultiplier() > 0 ||
                    context.hasAnyBlocksWithSetting(PerBlockSettingType.WALLTAP_MULTIPLIER))) {

                Vec3 before = instance.getDeltaMovement();
                float multiplier = context.getWalltapMultiplier();

                List<BlockPos> blockPositions = new ArrayList<>();

                if (context.hasAnyBlocksWithSetting(PerBlockSettingType.WALLTAP_MULTIPLIER)) {
                    AABB box = instance.getBoundingBox();
                    Vec3 min = box.getMinPosition();
                    Vec3 max = box.getMaxPosition();

                    int minX = (int) Math.floor(min.x + 1e-5);
                    int minY = (int) Math.floor(min.y + 1e-5);
                    int minZ = (int) Math.floor(min.z + 1e-5);
                    int maxX = (int) Math.ceil(max.x - 1e-5);
                    int maxY = (int) Math.ceil(max.y - 1e-5);
                    int maxZ = (int) Math.ceil(max.z - 1e-5);

                    if (max.x % 1 == 0) {
                        for (int y1 = minY; y1 <= maxY; y1++) {
                            for (int z1 = minZ; z1 < maxZ; z1++) {
                                blockPositions.add(new BlockPos(maxX, y1, z1));
                            }
                        }
                    }

                    if (min.x % 1 == 0) {
                        for (int y1 = minY; y1 <= maxY; y1++) {
                            for (int z1 = minZ; z1 < maxZ; z1++) {
                                blockPositions.add(new BlockPos(minX - 1, y1, z1));
                            }
                        }
                    }

                    if (max.z % 1 == 0) {
                        for (int y1 = minY; y1 <= maxY; y1++) {
                            for (int x1 = minX; x1 < maxX; x1++) {
                                blockPositions.add(new BlockPos(x1, y1, maxZ));
                            }
                        }
                    }

                    if (min.z % 1 == 0) {
                        for (int y1 = minY; y1 <= maxY; y1++) {
                            for (int x1 = minX; x1 < maxX; x1++) {
                                blockPositions.add(new BlockPos(x1, y1, minZ - 1));
                            }
                        }
                    }
                }

                if (!blockPositions.isEmpty()) {
                    Level world = instance.level();

                    int n = 0;
                    float multipliers = 0;

                    for (BlockPos pos : blockPositions) {
                        BlockState state = world.getBlockState(pos);

                        Float v = context.getBlockSetting(
                                BuiltInRegistries.BLOCK.getKey(state.getBlock()),
                                PerBlockSettingType.WALLTAP_MULTIPLIER
                        );

                        if (v != null) {
                            n++;
                            multipliers += v;
                        }
                    }

                    if (n > 0) {
                        multiplier = multipliers / n;
                    }
                }

                if (multiplier > 0) {
                    if (x == 0) x = before.x * -multiplier;
                    if (z == 0) z = before.z * -multiplier;
                }
            }
        }

        instance.setDeltaMovement(x, y, z);
    }

    @ModifyVariable(method = "collide", at = @At("STORE"), ordinal = 3)
    private boolean hookStepHeightOnGroundCheck(boolean original) {

        if ((Object) this instanceof AbstractBoat) {
            @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

            if (context == null) return original;

            if (context.hasStepWhileFalling()) {
                return true;
            }
        }

        return original;
    }

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    public void getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if ((Object) this instanceof AbstractBoat) {
            @Nullable ISettingContext boatContext = OpenBoatUtils.instance.getEntityContext(this.getUUID());

            if (boatContext != null) {
                cir.setReturnValue(cir.getReturnValue().scale(Math.abs(boatContext.getScale())));
            }

            @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

            if (context != null) {
                cir.setReturnValue(cir.getReturnValue().scale(Math.abs(context.getScale())));
            }
        }
    }

    @Inject(
            method = "collide",
            at = @At(
                    value = "INVOKE",
                    //? >= 1.21.5 {
                    /*target = "Lnet/minecraft/world/phys/Vec3;subtract(DDD)Lnet/minecraft/world/phys/Vec3;",
                    *///? } else {
                    target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;",
                    //? }
                    shift = At.Shift.BEFORE
            )
    )
    private void hookStepUp(Vec3 movement, CallbackInfoReturnable<Vec3> cir) {
        if ((Object) this instanceof AbstractBoat boat) {
            @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

            if (context != null) {
                float slipperiness = ((GetNearbySetting) boat).openboatutils$getAverageNearbySetting(context, boat, PerBlockSettingType.STEP_UP_SLIPPERINESS);

                if (slipperiness != 1) {
                    boat.setDeltaMovement(boat.getDeltaMovement().scale(slipperiness));
                }
            }
        }
    }

    // Previously based on https://github.com/Moulberry/MC276641Fix/blob/master/src/main/java/com/moulberry/mc276641fix/mixin/MixinEntity.java
    // Modified to re-evaluate the candidates after each step so enable stepping past the step size if it is possible to step multiple times
    @Inject(
            method = "collide",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;collectCandidateStepUpHeights(Lnet/minecraft/world/phys/AABB;Ljava/util/List;FF)[F",
                    shift = At.Shift.BY,
                    by = 2
            ),
            cancellable = true
    )
    private void fixMultipleStep(
            Vec3 velocity,
            CallbackInfoReturnable<Vec3> cir,
            @Local(ordinal = 1) List<VoxelShape> colliders,
            @Local(ordinal = 0) AABB aABB,
            @Local(ordinal = 1) AABB aABB2,
            @Local(ordinal = 1) Vec3 vec3d
    ) {
        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();
        Entity entity = (Entity) (Object) this;
        if (context != null && entity instanceof AbstractBoat boat && context.hasMultiStepping()) {
            Vec3 result = openboatutils$attemptStep(boat, velocity, aABB2, colliders, vec3d, 0, 100);
            double d = aABB.minY - aABB2.minY;

            double correctedY = Math.abs(result.y - vec3d.y) > 1.0E-7
                    ? result.y - d
                    : vec3d.y;

            cir.setReturnValue(new Vec3(result.x, correctedY, result.z));
        }
    }

    @Unique
    private Vec3 openboatutils$attemptStep(AbstractBoat boat, Vec3 velocity, AABB box, List<VoxelShape> colliders, Vec3 fallback, int depth, int maxDepth) {
        if (depth >= maxDepth) return fallback;

        float f = (float) fallback.y;
        float[] heights = collectCandidateStepUpHeights(box, colliders, boat.maxUpStep(), f);

        Vec3 best = fallback;

        for (float height : heights) {
            Vec3 candidate = collideWithShapes(
                    new Vec3(velocity.x, height, velocity.z), box, colliders
            );
            if (candidate.horizontalDistanceSqr() > best.horizontalDistanceSqr()) {
                best = candidate;
            }
        }

        if (best == fallback) return fallback;

        Vec3 remaining = new Vec3(
                velocity.x - best.x,
                velocity.y,
                velocity.z - best.z
        );

        if (remaining.horizontalDistanceSqr() > 1.0E-7) {
            AABB movedBox = box.move(best.x, best.y, best.z);
            AABB stretched = movedBox.expandTowards(remaining);

            //? >= 26.2 {
            /*List<VoxelShape> newColliders = collectCollidersIgnoringWorldBorder(
                    boat,
                    this.level,
                    colliders,
                    stretched
            );
            *///? } else {
            List<VoxelShape> newColliders = collectColliders(
                    boat,
                    this.level,
                    colliders,
                    stretched
            );
            //? }

            Vec3 nextFallback = collideWithShapes(remaining, movedBox, newColliders);

            boolean blockedX = remaining.x != nextFallback.x;
            boolean blockedZ = remaining.z != nextFallback.z;

            if (blockedX || blockedZ) {
                Vec3 next = openboatutils$attemptStep(boat, remaining, movedBox, newColliders, nextFallback, depth + 1, maxDepth);
                best = best.add(next);
            } else {
                best = best.add(nextFallback);
            }
        }

        return best;
    }
}
