package net.datahub.msv.mixin;

import net.datahub.msv.nbt.Access;
import kotlin.random.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalEntity.class)
public abstract class InfectedAnimalsMixin extends MobEntity implements Access {
    protected InfectedAnimalsMixin(EntityType<? extends MobEntity> entityType, World world, PlayerEntity targetPlayer) {
        super(entityType, world);
        this.targetPlayer = targetPlayer;
    }

    @Inject(method = "canEat", at = @At("HEAD"), cancellable = true)
    private void forbidEating(CallbackInfoReturnable<Boolean> cir) {
        if (this.isInfected()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    int CDTarget = 0;
    @Unique
    int CDHitPlayer = 0;
    @Unique
    int CDHitAnimal = 0;
    @Unique
    private PlayerEntity targetPlayer;

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("CDTarget", CDTarget);
        nbt.putInt("CDHitPlayer", CDHitPlayer);
        nbt.putInt("CDHitAnimal", CDHitAnimal);
    }
    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    public void readNbt(NbtCompound nbt, CallbackInfo ci) {
        CDTarget = nbt.getInt("CDTarget");
        CDHitPlayer = nbt.getInt("CDHitPlayer");
        CDHitAnimal = nbt.getInt("CDHitAnimal");
    }

    @Inject(method = "mobTick", at = @At("TAIL"))
    public void enhancedAI(CallbackInfo ci) {
        if (this.isInfected()) {
            if (CDTarget > 0) CDTarget--;
            if (CDHitPlayer > 0) CDHitPlayer--;
            if (CDHitAnimal > 0) CDHitAnimal--;

            PlayerEntity player = this.getWorld().getClosestPlayer(this.getX(), this.getY(), this.getZ(), 15.0, p -> this.canSee(p) && ((Access)p).getStage() == 0 && !((PlayerEntity) p).isCreative());
            if (player != null) {
                if (this.distanceTo(player) <= 1.5F && CDHitPlayer == 0) {
                    this.lookControl.lookAt(player, 180, 180);
                    if (Random.Default.nextBoolean())
                        player.damage(new DamageSource(this.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.MOB_ATTACK), this), 2.0F);
                    CDHitPlayer = Random.Default.nextInt(500, 800);
                }

                if (this.distanceTo(player) < 10.0 && CDTarget == 0) {
                    if (Random.Default.nextBoolean())
                        this.targetPlayer = player;
                    CDTarget = Random.Default.nextInt(700, 900);
                }
                if (CDTarget == 400) {
                    if (Random.Default.nextBoolean())
                        this.lookControl.lookAt(player, 180, 180);
                }

                if (this.targetPlayer != null && this.targetPlayer.isAlive()) {
                    this.getLookControl().lookAt(this.targetPlayer, 30.0F, 30.0F);
                    this.getNavigation().startMovingTo(this.targetPlayer, 1.4);

                    if (this.distanceTo(this.targetPlayer) < 1.5 || this.distanceTo(this.targetPlayer) > 10 || this.getAttacker() != null || !this.canSee(this.targetPlayer)) {
                        this.targetPlayer = null;
                        this.navigation.stop();
                    }
                }
            }

            if (CDHitAnimal == 0) {
                for (AnimalEntity animalEntity : this.getWorld().getEntitiesByClass(AnimalEntity.class, this.getBoundingBox().expand(2.0), animalEntity -> true)) {
                    if (!((Access)animalEntity).isInfected()) {
                        if (Random.Default.nextBoolean()) {
                            animalEntity.damage(new DamageSource(this.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.MOB_ATTACK), this), 2.0F);
                            ((Access)animalEntity).setInfected(true);
                        }
                        CDHitAnimal = Random.Default.nextInt(2200, 2600);
                        break;
                    }
                }
            }
        }
    }
}