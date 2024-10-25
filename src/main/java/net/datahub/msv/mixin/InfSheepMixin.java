package net.datahub.msv.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepEntity.class)
public abstract class InfSheepMixin extends MobEntity {
    protected InfSheepMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onEatingGrass", at = @At("HEAD"), cancellable = true)
    private void forbidEating(CallbackInfo ci) {
        if (this.isInfected())
            ci.cancel();
    }
}
