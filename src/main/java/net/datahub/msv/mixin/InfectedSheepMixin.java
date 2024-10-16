package net.datahub.msv.mixin;

import net.datahub.msv.nbt.Access;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepEntity.class)
public abstract class InfectedSheepMixin extends LivingEntity {
    protected InfectedSheepMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onEatingGrass", at = @At("HEAD"), cancellable = true)
    private void forbidEating(CallbackInfo ci) {
        if (((Access)this).isInfected())
            ci.cancel();
    }
}
