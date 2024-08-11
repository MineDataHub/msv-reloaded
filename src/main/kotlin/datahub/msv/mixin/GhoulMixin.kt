package datahub.msv.mixin

import datahub.msv.MSVNbtTags
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
        val player = this as PlayerEntity

        if (MSVNbtTags.readStringMSV(player, MSVNbtTags.MUTATION) == "ghoul" && stack.components.get(DataComponentTypes.FOOD) != null) {
            if (stack.item === Items.ROTTEN_FLESH) {
                player.removeStatusEffect(StatusEffects.HUNGER)
            } else {
                val currentEffect = player.getStatusEffect(StatusEffects.HUNGER)
                val newDuration = if ((currentEffect != null)) currentEffect.duration + 300 else 300
                val newAmplifier = if ((currentEffect != null)) currentEffect.amplifier + 1 else 0
                player.addStatusEffect(StatusEffectInstance(StatusEffects.HUNGER, newDuration, newAmplifier))

                if (currentEffect != null && currentEffect.duration >= 5) {
                    player.addStatusEffect(StatusEffectInstance(StatusEffects.POISON, 200, 0))
                }
            }
        }
    }
}