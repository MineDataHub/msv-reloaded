package net.datahub.msv.mixin;

import net.datahub.msv.Features;
import net.datahub.msv.ModItems;
import net.datahub.msv.MSVReloaded;
import net.datahub.msv.constant.Gifts;
import net.datahub.msv.constant.Mutations;
import net.datahub.msv.access.PlayerAccess;
import net.datahub.msv.mutations.Hydrophobic;
import net.datahub.msv.sneeze.BlackSneeze;
import net.datahub.msv.sneeze.NormalSneeze;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static kotlin.random.RandomKt.Random;
import static net.datahub.msv.constant.NBTTags.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerAccess {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

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
        if (stage > 0) {
            if (++tickCounter % 10 == 0) {
                if (tickCounter >= 200) {
                    tickCounter = 0;
                    if (stage < 7) {
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
            EntityAttributeInstance attributeInstance = Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.MAX_HEALTH));
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
            if (Objects.equals(mutation, Mutations.HYDROPHOBIC)) {
                Hydrophobic.INSTANCE.waterDamage(this);
            } else if (!ModItems.UmbrellaItem.INSTANCE.check((PlayerEntity) (Object) this) && Objects.equals(mutation, Mutations.VAMPIRE) && world.isSkyVisibleAllowingSea(this.getBlockPos())) {
                this.setFireTicks(80);
            }
            if (sneezePicking > 0) sneezePicking--;
            if (itemDroppingCD > 0) itemDroppingCD--;
            if (zombieEatingCD > 0) zombieEatingCD--;
        }
    }

    @Inject(method = "setFireTicks", at = @At("HEAD"), cancellable = true)
    private void noFireTicks(int fireTicks, CallbackInfo ci) {
        if (gift.equals(Gifts.NO_FIRE_DAMAGE) && this.getFireTicks() < fireTicks) {
            ci.cancel();
        }
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void noFireDamage(ServerWorld world, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (gift.equals(Gifts.NO_FIRE_DAMAGE) && source.getType().effects().equals(DamageEffects.BURNING)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canGlide", at = @At("HEAD"), cancellable = true)
    private void blockElytra(CallbackInfoReturnable<Boolean> cir) {
        if (mutation.equals(Mutations.FALLEN)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void noFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (gift.equals(Gifts.NO_FALL_DAMAGE)) {
            cir.setReturnValue(false);
        }
    }
}