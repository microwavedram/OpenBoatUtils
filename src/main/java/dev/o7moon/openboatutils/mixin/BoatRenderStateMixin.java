package dev.o7moon.openboatutils.mixin;

import dev.o7moon.openboatutils.ScaledBoatRenderState;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BoatRenderState.class)
public class BoatRenderStateMixin implements ScaledBoatRenderState {
    @Unique public float openBoatUtils$scale = 1.0f;

    @Override
    public float openBoatUtils$getScale() {
        return openBoatUtils$scale;
    }

    @Override
    public void openBoatUtils$setScale(float scale) {
        openBoatUtils$scale = scale;
    }
}
