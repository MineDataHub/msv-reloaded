package net.datahub.msv.mixin;

import net.datahub.msv.Features;
import net.datahub.msv.nbt.Access;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.datahub.msv.MSVReloaded.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements Access {
    @Unique
    PlayerEntity player = (PlayerEntity) (Object) this;

    @Unique
    private String mutation = "none";
    @Unique
    private String gift = "none";
    @Unique
    private Integer stage = 0;
    @Unique
    private Integer sneezeCooldown = 0;
    @Unique
    private Integer freezeCooldown = 0;
    @Unique
    private Integer hallucinationCooldown = 0;
    @Unique
    private Integer infection = 0;

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    protected void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound msv = new NbtCompound();

        nbt.put(MSV, msv);
        msv.putString(MUTATION, mutation);
        msv.putString(GIFT, gift);
        msv.putInt(STAGE, stage);
        msv.putInt(SNEEZE_COOLDOWN, sneezeCooldown);
        msv.putInt(FREEZE_COOLDOWN, freezeCooldown);
        msv.putInt(HALLUCINATION_COOLDOWN, hallucinationCooldown);
        msv.putInt(INFECTION, infection);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    protected void readNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound msv = nbt.getCompound(MSV);

        mutation = msv.getString(MUTATION);
        gift = msv.getString(GIFT);
        stage = msv.getInt(STAGE);
        sneezeCooldown = msv.getInt(SNEEZE_COOLDOWN);
        freezeCooldown = msv.getInt(FREEZE_COOLDOWN);
        hallucinationCooldown = msv.getInt(HALLUCINATION_COOLDOWN);
        infection = msv.getInt(INFECTION);
    }

    @Override
    public @NotNull String getMutation() {
        return this.mutation;
    }
    @Override
    public void setMutation(@NotNull String string) {
        mutation = string;
    }
    @Override
    public @NotNull String getGift() {
        return gift;
    }
    @Override
    public void setGift(@NotNull String string) {
        gift = string;
    }
    @Override
    public int getStage() {
        return stage;
    }
    @Override
    public void setStage(int i) {
        stage = i;
    }
    @Override
    public int getSneezeCoolDown() {
        return sneezeCooldown;
    }
    @Override
    public void setSneezeCoolDown(int i) {
        sneezeCooldown = i;
    }
    @Override
    public int getFreezeCoolDown() {
        return freezeCooldown;
    }
    @Override
    public void setFreezeCoolDown(int i) {
        freezeCooldown = i;
    }
    @Override
    public int getHallucinationCoolDown() {
        return hallucinationCooldown;
    }
    @Override
    public void setHallucinationCoolDown(int i) {
        hallucinationCooldown = i;
    }
    @Override
    public int getInfection() {
        return infection;
    }
    @Override
    public void setInfection(int i) {
        infection = i;
    }

    @Inject(method = "eatFood", at = @At("TAIL"))
    private void modifyGhoulsFoodEffect(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        if (mutation.equals("ghoul")) {
            if (stack.getItem() == Items.ROTTEN_FLESH) {
                player.removeStatusEffect(StatusEffects.HUNGER);
            } else {
                StatusEffectInstance currentEffect = player.getStatusEffect(StatusEffects.HUNGER);
                int newDuration = (currentEffect != null) ? currentEffect.getDuration() + 300 : 300;
                int newAmplifier = (currentEffect != null) ? currentEffect.getAmplifier() + 1 : 0;
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, newDuration, newAmplifier));

                if (currentEffect != null && currentEffect.getAmplifier() >= 2)
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 0));
            }
        }
    }

    @Inject(method = "damage", at = @At("TAIL"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (stage > 1) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 1, false, false));
            Features.INSTANCE.dropItem(player);
        }
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void noFireDamage(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (gift.equals("noFireDamage") && damageSource.getType().effects().equals(DamageEffects.BURNING)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "setFireTicks", at = @At("HEAD"), cancellable = true)
    private void noFireTicks(int ticks, CallbackInfo ci) {
        if (gift.equals("noFireDamage")) {
            ci.cancel();
        }
    }

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void noFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (gift.equals("noFallDamage")) {
            cir.setReturnValue(false);
        }
    }
}