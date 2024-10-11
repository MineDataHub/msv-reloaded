package net.datahub.msv.mixin;

import net.datahub.msv.nbt.Access;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HungerManager.class)
public class ReduceHealingMixin {
    @Shadow
    private int foodLevel;
    @Shadow
    private float saturationLevel;
    @Shadow
    private float exhaustion;
    @Shadow
    private int foodTickTimer;

    /**
     * @author
     * NecRoZ
     * @reason
     * to decrease healing for infected players
     */
    @Overwrite
    public void update(PlayerEntity player) {
        Difficulty difficulty = player.getWorld().getDifficulty();
        if (exhaustion > 4.0F) {
            exhaustion -= 4.0F;
            if (saturationLevel > 0.0F) {
                saturationLevel = Math.max(saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL)
                foodLevel = Math.max(foodLevel - 1, 0);
        }

        boolean bl = player.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
        if (bl && saturationLevel > 0.0F && player.canFoodHeal() && foodLevel >= 20) {
            foodTickTimer++;
            if (foodTickTimer >= 10) {
                float f = Math.min(saturationLevel, 6.0F);
                player.heal(f / 6 * (1 - ((float) ((Access)player).getStage() % 7) / 12));
                player.addExhaustion(f);
                foodTickTimer = 0;
            }
        } else if (bl && foodLevel >= 18 && player.canFoodHeal()) {
            foodTickTimer++;
            if (foodTickTimer >= 80) {
                player.heal(1 - ((float) ((Access)player).getStage() % 7) / 12);
                player.addExhaustion(6.0F);
                foodTickTimer = 0;
            }
        } else if (foodLevel <= 0) {
            foodTickTimer++;
            if (foodTickTimer >= 80) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.damage(player.getDamageSources().starve(), 1.0F);
                }
                foodTickTimer = 0;
            }
        } else {
            foodTickTimer = 0;
        }
    }
}