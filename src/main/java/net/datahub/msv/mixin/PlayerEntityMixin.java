package net.datahub.msv.mixin;

import net.datahub.msv.Features;
import net.datahub.msv.MSVDamage;
import net.datahub.msv.MSVItems;
import net.datahub.msv.MSVReloaded;
import net.datahub.msv.nbt.Access;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static kotlin.random.RandomKt.Random;
import static net.datahub.msv.MSVReloaded.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements Access {
    @Unique
    private Integer tickCounter = 0;

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

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    
    @Inject(method = "tick", at = @At("TAIL"))
    protected void cdUpdate(CallbackInfo ci) {
        if (tickCounter % 10 == 0) {
            if (tickCounter >= 200) {
                tickCounter = 0;
            }
            Objects.requireNonNull(this.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH)).updateModifier(
                    new EntityAttributeModifier(
                        MSVReloaded.id("health"),
                        switch (stage) {
                            case 2, 3, 4: yield -2.0;
                            case 5, 6: yield -4.0;
                            default: yield 0.0;
                        },
                        EntityAttributeModifier.Operation.ADD_VALUE
                    )
            );
        }
        World world = this.getWorld();
        BlockPos pos = this.getBlockPos();

        if (Objects.equals(mutation, "hydrophobic")) {
            if (this.isTouchingWater()) {
                damage(MSVDamage.INSTANCE.getWaterDamage(), 1.5f);
            }

            if (!MSVItems.UmbrellaItem.INSTANCE.check((PlayerEntity)(Object)this) && world.isRaining() && world.isSkyVisibleAllowingSea(pos)) {
                damage(MSVDamage.INSTANCE.getRainDamage(), 1.5f);
            }
        } else if (!MSVItems.UmbrellaItem.INSTANCE.check((PlayerEntity) (Object) this) && Objects.equals(mutation, "vampire") && world.isSkyVisibleAllowingSea(pos)) {
            this.setFireTicks(80);
        }
        if (freezeCooldown < 0) {
            this.setFrozenTicks(getFreezeCoolDown() + 3);
            if (this.getFrozenTicks() >= 160)
                freezeCooldown = 30 + Random(12).nextInt(12) - stage;
        }
    }

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
    public String getMutation() {
        return this.mutation;
    }
    @Override
    public void setMutation(String string) {
        this.mutation = string;
    }
    @Override
    public String getGift() {
        return this.gift;
    }
    @Override
    public void setGift(String string) {
        this.gift = string;
    }
    @Override
    public int getStage() {
        return this.stage;
    }
    @Override
    public void setStage(int i) {
        this.stage = i;
    }
    @Override
    public int getSneezeCoolDown() {
        return this.sneezeCooldown;
    }
    @Override
    public void setSneezeCoolDown(int i) {
        this.sneezeCooldown = i;
    }
    @Override
    public int getFreezeCoolDown() {
        return this.freezeCooldown;
    }
    @Override
    public void setFreezeCoolDown(int i) {
        this.freezeCooldown = i;
    }
    @Override
    public int getHallucinationCoolDown() {
        return this.hallucinationCooldown;
    }
    @Override
    public void setHallucinationCoolDown(int i) {
        this.hallucinationCooldown = i;
    }
    @Override
    public int getInfection() {
        return this.infection;
    }
    @Override
    public void setInfection(int i) {
        this.infection = i;
    }

    @Inject(method = "eatFood", at = @At("TAIL"))
    private void modifyGhoulsFoodEffect(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        if (mutation.equals("ghoul")) {
            if (stack.getItem() == Items.ROTTEN_FLESH) {
                this.removeStatusEffect(StatusEffects.HUNGER);
            } else {
                StatusEffectInstance currentEffect = this.getStatusEffect(StatusEffects.HUNGER);
                int newDuration = (currentEffect != null) ? currentEffect.getDuration() + 300 : 300;
                int newAmplifier = (currentEffect != null) ? currentEffect.getAmplifier() + 1 : 0;
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, newDuration, newAmplifier));

                if (currentEffect != null && currentEffect.getAmplifier() >= 2)
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 0));
            }
        }
    }

    @Inject(method = "damage", at = @At("TAIL"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (stage > 1) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 1, false, false));
            Features.INSTANCE.dropItem((PlayerEntity) (Object) this);
        }
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void noFireDamage(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (gift.equals("noFireDamage") && damageSource.getType().effects().equals(DamageEffects.BURNING)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "setFireTicks", at = @At("HEAD"), cancellable = true)
    private void noFireTicks(int fireTicks, CallbackInfo ci) {
        if (gift.equals("noFireDamage") && this.getFireTicks() < fireTicks) {
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