package dev.o7moon.openboatutils;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public interface ISettingContext {

    ISettingContext VANILLA = getVanilla();
    Map<ResourceLocation, Float> VANILLA_SLIPPERINESS = getVanillaSlipperinessMap();

    void switchTo();

    boolean hasFallDamage();
    boolean hasWaterElevation();
    boolean hasAirControl();
    float getDefaultSlipperiness();
    float getJumpForce();
    float getStepSize();
    double getGravityForce();
    float getYawAccel();
    float getForwardAccel();
    float getBackwardAccel();
    float getTurnForwardAccel();
    boolean hasAllowAccelStacking();
    boolean hasUnderwaterControl();
    boolean hasSurfaceWaterControl();
    int getCoyoteTime();
    boolean hasWaterJumping();
    float getSwimForce();
    CollisionMode getCollisionMode();
    boolean hasStepWhileFalling();
    @Nullable Float getBlockSlipperiness(ResourceLocation id);
    boolean isEntityTypeFiltered(EntityType<?> type);
    @Nullable Float getBlockSetting(ResourceLocation id, PerBlockSettingType type);
    int getCollisionResolution();
    float getWalltapMultiplier();
    int getJumps();
    float getScale();
    float getStepUpSlipperiness();
    boolean getFixDoubleWaterElevation();
    float getLateralSlipperiness();
    float getBrakeSlipperiness();
    boolean hasMultiStepping();
    float getMaxSpeed();
    float getMaxSpeedResistance();
    boolean hasHoneyCompatibility();

    Set<ResourceLocation> getBlocksWithSettings();
    boolean hasAnyBlocksWithSetting(PerBlockSettingType type);

    static Map<ResourceLocation, Float> getVanillaSlipperinessMap() {
        Map<ResourceLocation, Float> map = new HashMap<>();

        for (Block b : BuiltInRegistries.BLOCK.stream().toList()) {
            if (b.getFriction() != 0.6f){
                map.put(BuiltInRegistries.BLOCK.getKey(b), b.getFriction());
            }
        }

        return map;
    }

    private static ISettingContext getVanilla() {
        return new ISettingContext() {
            @Override
            public void switchTo() {}

            @Override public boolean hasFallDamage() { return true; }
            @Override public boolean hasWaterElevation() { return false; }
            @Override public boolean hasAirControl() { return false; }
            @Override public float getDefaultSlipperiness() { return 0.6f; }
            @Override public float getJumpForce() { return 0; }
            @Override public float getStepSize() { return 0; }
            @Override public double getGravityForce() { return -0.03999999910593033; }
            @Override public float getYawAccel() { return 1f; }
            @Override public float getForwardAccel() { return 0.04f; }
            @Override public float getBackwardAccel() { return 0.005f; }
            @Override public float getTurnForwardAccel() { return 0.005f; }
            @Override public boolean hasAllowAccelStacking() { return false; }
            @Override public boolean hasUnderwaterControl() { return false; }
            @Override public boolean hasSurfaceWaterControl() { return false; }
            @Override public int getCoyoteTime() { return 0; }
            @Override public boolean hasWaterJumping() { return false; }
            @Override public float getSwimForce() { return 0; }
            @Override public CollisionMode getCollisionMode() { return CollisionMode.VANILLA; }
            @Override public boolean hasStepWhileFalling() { return false; }
            @Override public @Nullable Float getBlockSlipperiness(ResourceLocation id) { return VANILLA_SLIPPERINESS.get(id); }
            @Override public boolean isEntityTypeFiltered(EntityType<?> type) { return false; }
            @Override public @Nullable Float getBlockSetting(ResourceLocation id, PerBlockSettingType type) { return null; }
            @Override public int getCollisionResolution() { return 1; }
            @Override public float getWalltapMultiplier() { return 0; }
            @Override public int getJumps() { return 1; }
            @Override public float getScale() { return 1; }
            @Override public float getStepUpSlipperiness() { return 1; }
            @Override public boolean getFixDoubleWaterElevation() { return false; }
            @Override public float getLateralSlipperiness() { return 1; }
            @Override public float getBrakeSlipperiness() { return 1; }
            @Override public boolean hasMultiStepping() { return false; }
            @Override public float getMaxSpeed() { return -1; }
            @Override public float getMaxSpeedResistance() { return 0; }
            @Override public boolean hasHoneyCompatibility() { return false; }

            @Override public Set<ResourceLocation> getBlocksWithSettings() { return Set.of(); }

            @Override
            public boolean hasAnyBlocksWithSetting(PerBlockSettingType type) { return false; }
        };
    }
}