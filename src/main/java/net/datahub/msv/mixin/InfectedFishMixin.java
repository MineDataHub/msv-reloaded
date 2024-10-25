package net.datahub.msv.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FishEntity.class)
public abstract class InfectedFishMixin extends WaterCreatureEntity {
    protected InfectedFishMixin(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    int biteCD = random.nextBetween(200, 300);
    @Unique
    int moveCD = random.nextBetween(800, 1200);
    @Unique
    int infectCD = random.nextBetween(1400, 1800);

    @Inject(method = "createFishAttributes", at = @At("HEAD"), cancellable = true)
    private static void createFishAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.setReturnValue(MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 1.5));
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (this.isInfected()) {
            if (moveCD > 0) {
                moveCD--;
            } else {
                moveCD = random.nextBetween(800, 1200);
            }

            FishEntity fish = findRandomFishNearby(this.getWorld(), this.getPos());
            if (fish != null) {
                if (infectCD > 0) {
                    infectCD--;
                } else {
                    fish.setInfected(true);
                    infectCD = random.nextBetween(1400, 1800);
                }
            }

            PlayerEntity player = this.getWorld().getClosestPlayer(this.getX(), this.getY(), this.getZ(), 10.0, p -> this.canSee(p) && ((PlayerEntity) p).getStage() == 0 && !((PlayerEntity) p).isCreative());
            if (player != null) {
                if (biteCD > 0) biteCD--;
                else if (this.distanceTo(player) < 1.5) {
                    biteCD = random.nextBetween(200, 300);
                    if (random.nextBoolean())
                        player.damage(new DamageSource(this.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.MOB_ATTACK), this), 0.5F);
                }

                if (moveCD == 1) this.getNavigation().startMovingTo(player, 1.6);
            }
        }
    }

    @Unique
    private FishEntity findRandomFishNearby(World world, Vec3d pos) {
        List<FishEntity> nearbyFish = world.getEntitiesByClass(
                FishEntity.class,
                Box.of(pos, 10, 10, 10),
                fish -> !fish.isInfected()
        );

        if (!nearbyFish.isEmpty()) {
            int randomIndex = world.random.nextInt(nearbyFish.size());
            return nearbyFish.get(randomIndex);
        }

        return null;
    }
}