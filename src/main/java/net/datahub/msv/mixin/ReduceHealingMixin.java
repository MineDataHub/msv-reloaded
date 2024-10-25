package net.datahub.msv.mixin;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HungerManager.class)
public class ReduceHealingMixin {
    @Redirect(method = "update",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;heal(F)V"))
    public void redirectHealMethod(PlayerEntity instance, float v) {
        instance.heal(v * (1 - ((float) instance.getStage() % 7) / 12));
    }
}