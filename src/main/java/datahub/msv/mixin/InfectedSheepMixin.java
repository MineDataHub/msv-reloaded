package datahub.msv.mixin;

import datahub.msv.MSVNBTData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepEntity.class)
public class InfectedSheepMixin extends MobEntity {
    protected InfectedSheepMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onEatingGrass", at = @At("HEAD"), cancellable = true)
    private void forbidEating(CallbackInfo ci) {
        if (MSVNBTData.INSTANCE.isInfected(this))
            ci.cancel();
    }
}
