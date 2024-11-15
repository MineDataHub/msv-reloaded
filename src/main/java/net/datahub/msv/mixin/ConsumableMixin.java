package net.datahub.msv.mixin;

import net.datahub.msv.constant.Mutations;
import net.datahub.msv.mutations.Ghoul;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
            Ghoul.INSTANCE.foodEffects(user, stack);
        }
    }
}
