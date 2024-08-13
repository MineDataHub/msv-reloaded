package datahub.msv.mixin;

import datahub.msv.MSVNbtTags;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class GhoulMixin {
    @Inject(method = "eatFood", at = @At("RETURN"))
    private void ghoulFood(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (MSVNbtTags.INSTANCE.readStringMSV(player, MSVNbtTags.MUTATION).equals("ghoul") && stack.getComponents().get(DataComponentTypes.FOOD) != null) {
            if (stack.getItem() == Items.ROTTEN_FLESH) {
                player.removeStatusEffect(StatusEffects.HUNGER);
            } else {
                StatusEffectInstance currentEffect = player.getStatusEffect(StatusEffects.HUNGER);
                int newDuration = (currentEffect != null) ? currentEffect.getDuration() + 300 : 300;
                int newAmplifier = (currentEffect != null) ? currentEffect.getAmplifier() + 1 : 0;
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, newDuration, newAmplifier));

                if (currentEffect != null && currentEffect.getDuration() >= 5) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 0));
                }
            }
        }
    }
}
