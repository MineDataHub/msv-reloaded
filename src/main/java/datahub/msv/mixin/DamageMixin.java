package datahub.msv.mixin;

import datahub.msv.Features;
import datahub.msv.MSVPlayerData;
import net.minecraft.entity.damage.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class DamageMixin {
    @Unique
    PlayerEntity player = (PlayerEntity) (Object) this;

    @Inject(method = "damage", at = @At("TAIL"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (MSVPlayerData.INSTANCE.getStage(player) > 1) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 1, false, false));
            player.getWorld().playSound(player,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.ENTITY_WARDEN_HEARTBEAT,
                    SoundCategory.PLAYERS,
                    1.0F, 1.0F);
            Features.INSTANCE.dropItem(player);
        }
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void noFireDamage(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (Objects.equals(MSVPlayerData.INSTANCE.getMutation(player), "hydrophobic")) {
            if (damageSource.getType().effects().equals(DamageEffects.BURNING)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "setFireTicks", at = @At("HEAD"), cancellable = true)
    private void noFireTicks(int ticks, CallbackInfo ci) {
        if (Objects.equals(MSVPlayerData.INSTANCE.getMutation(player), "hydrophobic")) {
            ci.cancel();
        }
    }

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void noFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (Objects.equals(MSVPlayerData.INSTANCE.getMutation(player), "fallen")) {
            cir.setReturnValue(false);
        }
    }
}