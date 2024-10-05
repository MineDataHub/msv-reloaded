package datahub.msv.mixin;

import datahub.msv.Features;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static datahub.msv.MSVPlayerData.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
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
    private Integer creeperSoundCooldown = 0;
    @Unique
    private Integer timeForUpStage = 0;

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    protected void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound msv = new NbtCompound();

        nbt.put(MSV, msv);
        msv.putString(MUTATION, mutation);
        msv.putString(GIFT, gift);
        msv.putInt(STAGE, stage);
        msv.putInt(SNEEZE_COOLDOWN, sneezeCooldown);
        msv.putInt(CREEPER_SOUND_COOLDOWN, creeperSoundCooldown);
        msv.putInt(TIME_FOR_UP_STAGE, timeForUpStage);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    protected void readNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound msv = nbt.getCompound(MSV);

        mutation = msv.getString(MUTATION);
        gift = msv.getString(GIFT);
        stage = msv.getInt(STAGE);
        sneezeCooldown = msv.getInt(SNEEZE_COOLDOWN);
        creeperSoundCooldown = msv.getInt(CREEPER_SOUND_COOLDOWN);
        timeForUpStage = msv.getInt(TIME_FOR_UP_STAGE);
    }

    @Inject(method = "eatFood", at = @At("TAIL"))
    private void modifyGhoulsFoodEffect(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        if (Objects.equals(INSTANCE.getMutation(player),"ghoul")) {
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
        if (INSTANCE.getStage(player) > 1) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 1, false, false));
            Features.INSTANCE.dropItem(player);
        }
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void noFireDamage(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (INSTANCE.getMutation(player).equals("hydrophobic") && damageSource.getType().effects().equals(DamageEffects.BURNING)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "setFireTicks", at = @At("HEAD"), cancellable = true)
    private void noFireTicks(int ticks, CallbackInfo ci) {
        if (INSTANCE.getMutation(player).equals("hydrophobic")) {
            ci.cancel();
        }
    }

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void noFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (INSTANCE.getMutation(player).equals("fallen")) {
            cir.setReturnValue(false);
        }
    }
}