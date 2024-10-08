package datahub.msv.mixin;

import datahub.msv.MSVDamage;
import datahub.msv.nbt.Access;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEntity.class)
public abstract class PotionDamageMixin {
    @Inject(method = "onCollision", at = @At("HEAD"))
    private void onCollision(HitResult hitResult, CallbackInfo ci) {
        PotionEntity potion = (PotionEntity) (Object) this;

        potion.getWorld().getEntitiesByClass(LivingEntity.class, potion.getBoundingBox().expand(4.0), entity -> true)
                .forEach(entity -> {
                    if (entity instanceof PlayerEntity player
                            && ((Access)player).getMutation().equals("hydrophobic")) {
                        player.damage(MSVDamage.INSTANCE.createDamageSource(player.getWorld(), MSVDamage.INSTANCE.getPOTION()), 1.0F);
                    }
                });
    }
}
