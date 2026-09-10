package dev.o7moon.openboatutils.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.o7moon.openboatutils.ISettingContext;
import dev.o7moon.openboatutils.OpenBoatUtils;
import dev.o7moon.openboatutils.ScaledBoatRenderState;
//? >= 1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
//? >= 26.1 {
/^import net.minecraft.client.renderer.state.level.CameraRenderState;
^///? } else {
import net.minecraft.client.renderer.state.CameraRenderState;
//? }
*///? }
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
//? >= 1.21.11 {
/*import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
*///? } else {
import net.minecraft.world.entity.vehicle.AbstractBoat;
//? }
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBoatRenderer.class)

public abstract class BoatRendererMixin {

    @Unique private float openBoatUtils$getScale(AbstractBoat boat) {
        @Nullable ISettingContext boatContext = OpenBoatUtils.instance.getEntityContext(boat.getUUID());

        if (boatContext != null) {
            return boatContext.getScale();
        }

        @Nullable ISettingContext context = OpenBoatUtils.instance.getActiveContext();

        if (context != null) {
            return context.getScale();
        }

        return 1f;
    }

    //? >= 1.21.9 {
    /*//? >= 26.1 {
    /^@Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/BoatRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("HEAD"))
    ^///? } else {
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/BoatRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At("HEAD"))
    //? }
    private void preRender(BoatRenderState state, PoseStack matrices, SubmitNodeCollector renderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        float scale = ((ScaledBoatRenderState) state).openBoatUtils$getScale();
        matrices.pushPose();

        if (scale < 0) {
            matrices.translate(0, 0.5625 * -scale, 0);
        }

        matrices.scale(scale, scale, scale);

        if (scale < 0) {
            matrices.translate(0, 0.5625 * scale, 0);
        }
    }

    //? >= 26.1 {
    /^@Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/BoatRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("RETURN"))
    ^///? } else {
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/BoatRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At("RETURN"))
    //? }
    private void postRender(BoatRenderState state, PoseStack matrices, SubmitNodeCollector renderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        matrices.popPose();
    }
    *///? } else {
    @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/BoatRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void preRender(BoatRenderState state, PoseStack matrices, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
        float scale = ((ScaledBoatRenderState) state).openBoatUtils$getScale();
        matrices.pushPose();

        if (scale < 0) {
            matrices.translate(0, 0.5625 * -scale, 0);
        }

        matrices.scale(scale, scale, scale);
    }

    @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/BoatRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("RETURN"))
    private void postRender(BoatRenderState state, PoseStack matrices, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
        matrices.popPose();
    }
    //? } else {
    /*@Inject(method = "render(Lnet/minecraft/entity/vehicle/BoatEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private void preRender(BoatEntity boatEntity, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        float scale = openBoatUtils$getScale(boatEntity);
        matrices.push();

        if (scale < 0) {
            matrices.translate(0, 0.5625 * -scale, 0);
        }

        matrices.scale(scale, scale, scale);
    }

    @Inject(method = "render(Lnet/minecraft/entity/vehicle/BoatEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("RETURN"))
    private void postRender(BoatEntity boatEntity, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        matrices.pop();
    }
    *///? }

    //? >= 1.21.11 {
    /*@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;Lnet/minecraft/client/renderer/entity/state/BoatRenderState;F)V", at = @At("RETURN"))
    *///? } else {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/AbstractBoat;Lnet/minecraft/client/renderer/entity/state/BoatRenderState;F)V", at = @At("RETURN"))
     //? }
    private void populateScale(AbstractBoat entity, BoatRenderState state, float tickDelta, CallbackInfo ci) {
        ((ScaledBoatRenderState) state).openBoatUtils$setScale(
                openBoatUtils$getScale(entity)
        );
    }
}