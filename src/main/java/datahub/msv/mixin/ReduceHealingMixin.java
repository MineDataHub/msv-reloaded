package datahub.msv.mixin;

import datahub.msv.MSVPlayerData;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class ReduceHealingMixin {
    @Unique
    int foodLevel;
    @Unique
    float saturationLevel;
    @Unique
    float exhaustion;
    @Unique
    int foodTickTimer;
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void update(PlayerEntity player, CallbackInfo ci) {
        foodLevel = player.getHungerManager().getFoodLevel();
        saturationLevel = player.getHungerManager().getSaturationLevel();
        exhaustion = player.getHungerManager().getExhaustion();
        foodTickTimer = player.writeNbt(new NbtCompound()).getInt("foodTickTimer");
        Difficulty difficulty = player.getWorld().getDifficulty();
        if (this.exhaustion > 4.0F) {
            this.exhaustion -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        boolean bl = player.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
        if (bl && this.saturationLevel > 0.0F && player.canFoodHeal() && this.foodLevel >= 20) {
            this.foodTickTimer++;
            if (MSVPlayerData.INSTANCE.getStage(player) == 0 && this.foodTickTimer >= 10 || MSVPlayerData.INSTANCE.getStage(player) >= 1 && this.foodTickTimer >= 40) {
                float f = Math.min(this.saturationLevel, 6.0F);
                player.heal(f / 6.0F);
                player.addExhaustion(f);
                this.foodTickTimer = 0;
            }
        } else if (bl && this.foodLevel >= 18 && player.canFoodHeal()) {
            this.foodTickTimer++;
            if (MSVPlayerData.INSTANCE.getStage(player) == 0 && this.foodTickTimer >= 80 || MSVPlayerData.INSTANCE.getStage(player) >= 1 && this.foodTickTimer >= 160) {
                player.heal(1.0F);
                player.addExhaustion(6.0F);
                this.foodTickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            this.foodTickTimer++;
            if (MSVPlayerData.INSTANCE.getStage(player) == 0 && this.foodTickTimer >= 80 || MSVPlayerData.INSTANCE.getStage(player) >= 1 && this.foodTickTimer >= 160) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.damage(player.getDamageSources().starve(), 1.0F);
                }

                this.foodTickTimer = 0;
            }
        } else {
            this.foodTickTimer = 0;
        }
        NbtCompound nbt = player.writeNbt(new NbtCompound());
        nbt.putInt("foodLevel", this.foodLevel);
        nbt.putInt("foodTickTimer", this.foodTickTimer);
        nbt.putFloat("foodSaturationLevel", this.saturationLevel);
        nbt.putFloat("foodExhaustionLevel", this.exhaustion);
        player.readNbt(nbt);
        ci.cancel();
    }
}
