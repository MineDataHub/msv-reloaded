package net.datahub.msv.mixin;

import net.datahub.msv.constant.Mutations;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.LivingEntity;
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

@Mixin(ConsumableComponent.class)
public class ConsumableMixin {
    @Inject(method = "finishConsumption", at = @At("TAIL"))
    private void modifyGhoulsFoodEffect(World world, LivingEntity user, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (user instanceof PlayerEntity && ((PlayerEntity)user).getMutation().equals(Mutations.GHOUL)) {
            if (stack.getItem() == Items.ROTTEN_FLESH) {
                user.removeStatusEffect(StatusEffects.HUNGER);
            } else {
                StatusEffectInstance currentEffect = user.getStatusEffect(StatusEffects.HUNGER);
                int newDuration = (currentEffect != null) ? currentEffect.getDuration() + 300 : 300;
                int newAmplifier = (currentEffect != null) ? currentEffect.getAmplifier() + 1 : 0;
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, newDuration, newAmplifier));

                if (currentEffect != null && currentEffect.getAmplifier() >= 2)
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 0));
            }
        }
    }
}
