package net.datahub.msv.mixin;

import net.datahub.msv.Features;
import net.datahub.msv.MSVDamage;
import net.datahub.msv.MSVItems;
import net.datahub.msv.MSVReloaded;
import net.datahub.msv.constants.Gifts;
import net.datahub.msv.constants.Mutations;
import net.datahub.msv.nbt.Access;
import net.datahub.msv.sneeze.BlackSneeze;
import net.datahub.msv.sneeze.NormalSneeze;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static kotlin.random.RandomKt.Random;
import static net.datahub.msv.constants.NBTTags.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements Access {
    @Shadow @Final private PlayerInventory inventory;

    @Shadow public abstract boolean isPlayer();

    @Shadow public abstract PlayerAbilities getAbilities();

    @Unique
    private int tickCounter = 0;
    @Unique
    private int sneezePicking = 0;
    @Unique
    private int zombieEatingCD = 0;
    @Unique
    private int itemDroppingCD = 0;

    @Unique
    private String mutation = "none";
    @Unique
    private String gift = "none";
    @Unique
    private int stage = 0;
    @Unique
    private int sneezeCooldown = 0;
    @Unique
    private int freezeCooldown = 0;
    @Unique
    private int frozenTime = 0;
    @Unique
    private int hallucinationCooldown = 0;
    @Unique
    private int infection = 0;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private void freezing() {
        this.frozenTime--;
        this.setFrozenTicks(getFrozenTicks() + 3);
    }
    
    @Inject(method = "tick", at = @At("TAIL"))
    protected void tick(CallbackInfo ci) {
        if (frozenTime > 0) {
            freezing();
        }
        if (++tickCounter % 10 == 0) {
            if (tickCounter >= 200) {
                tickCounter = 0;
                if (stage > 0 && stage < 7) {
                    if (--infection <= 0) {
                        stage++;
                        infection = (int) ((257 + random.nextInt(27)) * Math.pow(2.0, stage - 2));
                        if (stage == 6) {
                            mutation = Features.INSTANCE.getRandomMutation();
                        }
                        if (stage == 7) {
                            gift = Features.INSTANCE.getRandomGift(mutation);
                        }
                    }
                }
                if (stage > 1 && stage <= 7) {
                    if (--sneezeCooldown <= 0) {
                        sneezeCooldown = 15 + random.nextInt(42) - stage;
                        if (stage < 7) {
                            NormalSneeze.INSTANCE.spawn((PlayerEntity) (Object) this);
                        } else {
                            BlackSneeze.Companion.spawn((PlayerEntity) (Object) this);
                        }
                    }
                }
                if (stage > 2 && stage <= 7) {
                    if (--hallucinationCooldown <= 0) {
                        hallucinationCooldown = 15 + random.nextInt(42) - stage;
                        this.getWorld().playSound((PlayerEntity) (Object) this, getBlockPos(), SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.PLAYERS);
                    }
                }
                if (stage > 3 && stage < 7) {
                    if (--freezeCooldown < 0) {
                        this.frozenTime += 160;
                        this.freezeCooldown = 30 + Random(12).nextInt(12) - stage;
                    }
                }
            }
        }
        double modifier = switch (stage) {
            case 2, 3, 4, 7:
                yield -2.0;
            case 5, 6:
                yield -4.0;
            default:
                yield 0.0;
        };
        EntityAttributeInstance attributeInstance = Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH));
        if (!attributeInstance.hasModifier(MSVReloaded.id("health")) || Objects.requireNonNull(attributeInstance.getModifier(MSVReloaded.id("health"))).value() != modifier) {
            attributeInstance.updateModifier(
                    new EntityAttributeModifier(
                            MSVReloaded.id("health"),
                            modifier,
                            EntityAttributeModifier.Operation.ADD_VALUE
                    )
            );
        }
        World world = this.getWorld();
        BlockPos pos = this.getBlockPos();
        if (Objects.equals(mutation, Mutations.HYDROPHOBIC)) {
            if (this.isTouchingWater()) {
                damage(MSVDamage.INSTANCE.getWaterDamage(), 1.5f);
            }

            if (!MSVItems.UmbrellaItem.INSTANCE.check((PlayerEntity)(Object)this) && world.isRaining() && world.isSkyVisibleAllowingSea(pos)) {
                damage(MSVDamage.INSTANCE.getRainDamage(), 1.5f);
            }
        } else if (!MSVItems.UmbrellaItem.INSTANCE.check((PlayerEntity) (Object) this) && Objects.equals(mutation, Mutations.VAMPIRE) && world.isSkyVisibleAllowingSea(pos)) {
            this.setFireTicks(80);
        }
        if (sneezePicking > 0) sneezePicking--;
        if (itemDroppingCD > 0) itemDroppingCD--;
        if (zombieEatingCD > 0) zombieEatingCD--;
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
        msv.putInt(FROZEN_TIME, frozenTime);
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
        frozenTime = msv.getInt(FROZEN_TIME);
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
    @Override
    public int getSneezePicking() {
        return this.sneezePicking;
    }
    @Override
    public void setSneezePicking(int i) {
        this.sneezePicking = i;
    }
    @Override
    public int getItemDroppingCD() {
        return this.sneezePicking;
    }
    @Override
    public void setItemDroppingCD(int i) {
        this.sneezePicking = i;
    }
    @Override
    public int getZombieEatingCD() {
        return this.zombieEatingCD;
    }
    @Override
    public void setZombieEatingCD(int i) {
        this.zombieEatingCD = i;
    }
    @Override
    public int getFrozenTime() {
        return frozenTime;
    }
    @Override
    public void setFrozenTime(int frozenTime) {
        this.frozenTime = frozenTime;
    }
    @Override
    public void addFrozenTime(int frozenTime) {
        this.frozenTime += frozenTime;
    }

    @Inject(method = "eatFood", at = @At("TAIL"))
    private void modifyGhoulsFoodEffect(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        if (mutation.equals(Mutations.GHOUL)) {
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
        if (gift.equals(Gifts.NO_FIRE_DAMAGE) && damageSource.getType().effects().equals(DamageEffects.BURNING)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "setFireTicks", at = @At("HEAD"), cancellable = true)
    private void noFireTicks(int fireTicks, CallbackInfo ci) {
        if (gift.equals(Gifts.NO_FIRE_DAMAGE) && this.getFireTicks() < fireTicks) {
            ci.cancel();
        }
    }

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void noFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (gift.equals(Gifts.NO_FALL_DAMAGE)) {
            cir.setReturnValue(false);
        }
    }
}