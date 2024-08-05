package datahub.msv.mixin

import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.FoodComponent
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.world.World
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(PlayerEntity::class)
abstract class GhoulMixin {
    @Inject(method = ["eatFood"], at = [At("RETURN")])
    private fun ghoulFood(world: World, stack: ItemStack, foodComponent: FoodComponent, cir: CallbackInfoReturnable<ItemStack>) {
        val entity = this as PlayerEntity
        val nbt = stack.components

        if (entity.commandTags.contains("ghoul") && nbt.get(DataComponentTypes.FOOD) != null) {
            if (stack.item === Items.ROTTEN_FLESH) {
                entity.removeStatusEffect(StatusEffects.HUNGER)
            } else {
                val currentEffect = entity.getStatusEffect(StatusEffects.HUNGER)
                val newDuration = if ((currentEffect != null)) currentEffect.duration + 300 else 300
                val newAmplifier = if ((currentEffect != null)) currentEffect.amplifier + 1 else 0
                entity.addStatusEffect(StatusEffectInstance(StatusEffects.HUNGER, newDuration, newAmplifier))

                if (currentEffect != null && currentEffect.duration >= 5) {
                    entity.addStatusEffect(StatusEffectInstance(StatusEffects.POISON, 200, 0))
                }
            }
        }
    }
}