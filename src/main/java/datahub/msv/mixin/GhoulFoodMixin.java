package datahub.msv.mixin;

import net.minecraft.component.ComponentMap;
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

import static net.minecraft.component.DataComponentTypes.FOOD;

@Mixin(PlayerEntity.class)
public abstract class GhoulFoodMixin {


    @Inject(method = "eatFood", at = @At("RETURN"))
    private void modifyRottenFleshEffect(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        PlayerEntity entity = (PlayerEntity) (Object) this;
        ComponentMap nbt = stack.getComponents();

        if (entity.getCommandTags().contains("ghoul") && nbt.get(FOOD) != null) {
            if (stack.getItem() == Items.ROTTEN_FLESH) {
                entity.removeStatusEffect(StatusEffects.HUNGER);
            } else {
                StatusEffectInstance currentEffect = entity.getStatusEffect(StatusEffects.HUNGER);
                int newDuration = (currentEffect != null) ? currentEffect.getDuration() + 300 : 300;
                int newAmplifier = (currentEffect != null) ? currentEffect.getAmplifier() + 1 : 0;
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, newDuration, newAmplifier));

                if (currentEffect != null && currentEffect.getDuration() >= 5) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 0));
                }
            }
        }
    }
}