package datahub.msv.mixin

import datahub.msv.Features
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable


@Mixin(LivingEntity::class)
abstract class DamageTriggerMixin {
    @Inject(method = ["damage"], at = [At("HEAD")])
    private fun onDamage(source: DamageSource, amount: Float, cir: CallbackInfoReturnable<Boolean>) {
        if (this is PlayerEntity) {
            if (this.commandTags.contains("damagetrigger")) {
                this.addStatusEffect(StatusEffectInstance(StatusEffects.BLINDNESS, 60, 1, true, false))
                val world: World = this.getWorld()
                world.playSound(
                    this,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    SoundEvents.ENTITY_WARDEN_HEARTBEAT,
                    SoundCategory.PLAYERS,
                    1.0f,
                    world.random.nextFloat() * 0.1f + 0.9f
                )
                Features.dropItem(this)
            }
        }
    }
}