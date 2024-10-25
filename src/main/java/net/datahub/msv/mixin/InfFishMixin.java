package net.datahub.msv.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishEntity.class)
public abstract class InfFishMixin extends WaterCreatureEntity {
    protected InfFishMixin(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "createFishAttributes", at = @At("HEAD"), cancellable = true)
    private static void createFishAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.setReturnValue(MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 1.5).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.5));
    }

    @Unique
    private Vec3d getRandomPointBehindPlayer(PlayerEntity player) {
        Vec3d playerDirection = player.getRotationVector().normalize().multiply(-1);

        return player.getPos().add(
                playerDirection.x * 3,
                playerDirection.y * 3,
                playerDirection.z * 3
        );
    }
}