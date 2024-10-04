package datahub.msv.mixin;

import datahub.msv.MSVPlayerData;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void update(PlayerEntity player, CallbackInfo ci) {
        Difficulty difficulty = player.getWorld().getDifficulty();
        if (exhaustion > 4.0F) {
            exhaustion -= 4.0F;
            if (saturationLevel > 0.0F) {
                saturationLevel = Math.max(saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                foodLevel = Math.max(foodLevel - 1, 0);
            }
        }

        boolean bl = player.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
        if (bl && saturationLevel > 0.0F && player.canFoodHeal() && foodLevel >= 20) {
            foodTickTimer++;
            if (foodTickTimer >= 10) {
                float f = Math.min(saturationLevel, 6.0F);
                player.heal(f / 6 * (1 - ((float) MSVPlayerData.INSTANCE.getStage(player) % 7) / 12));
                player.addExhaustion(f);
                foodTickTimer = 0;
            }
        } else if (bl && foodLevel >= 18 && player.canFoodHeal()) {
            foodTickTimer++;
            if (foodTickTimer >= 80) {
                player.heal(1 - ((float) MSVPlayerData.INSTANCE.getStage(player) % 7) / 12);
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
        ci.cancel();
    }
}