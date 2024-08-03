package datahub.msv.mixin;

import net.minecraft.component.ComponentMap;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.component.DataComponentTypes.FOOD;

@Mixin(PlayerEntity.class)
public abstract class GhoulFoodMixin {

    @Unique

    @Inject(method = "eatFood", at = @At("RETURN"))
    private void modifyRottenFleshEffect(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ComponentMap nbt = stack.getComponents();
        if (entity.getCommandTags().contains("ghoul") && nbt.get(FOOD) != null) {
            if (entity.hasStatusEffect(StatusEffects.HUNGER) && stack.getItem() == Items.ROTTEN_FLESH) {
                entity.removeStatusEffect(StatusEffects.HUNGER);
            } else if (stack.getItem() != Items.ROTTEN_FLESH){
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 300, 0));
            }
        }
    }
}