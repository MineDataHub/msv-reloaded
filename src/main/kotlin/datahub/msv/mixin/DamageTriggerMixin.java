package datahub.msv.mixin;

import datahub.msv.Features;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class DamageTriggerMixin {

    @Inject(method = "damage", at = @At("HEAD"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof PlayerEntity player) {
            if (player.getCommandTags().contains("infected")) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 1, true, false));
                World world = player.getWorld();
                world.playSound(
                        player,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ENTITY_WARDEN_HEARTBEAT,
                        SoundCategory.PLAYERS,
                        1.0f,
                        world.getRandom().nextFloat() * 0.1f + 0.9f
                );
                Features.INSTANCE.dropItem(player);
            }
        }
    }
}
