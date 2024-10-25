package net.datahub.msv.mixin;

import net.datahub.msv.MSVDamage;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEntity.class)
public abstract class PotionDamageMixin extends ThrownItemEntity {
    public PotionDamageMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onCollision", at = @At("HEAD"))
    private void onCollision(HitResult hitResult, CallbackInfo ci) {
        this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(4.0), entity -> true)
                .forEach(entity -> {
                    if (entity instanceof PlayerEntity player
                            && player.getMutation().equals("hydrophobic")) {
                        player.damage(MSVDamage.INSTANCE.getPotionDamage(), 1.0F);
                    }
                });
    }
}
