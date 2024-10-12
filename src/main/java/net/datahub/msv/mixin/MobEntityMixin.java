package net.datahub.msv.mixin;

import net.datahub.msv.nbt.Access;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.datahub.msv.MSVReloaded.*;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin implements Access {
    @Unique
    private Boolean infected = false;
    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean(INFECTED, infected);
    }
    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    public void readNbt(NbtCompound nbt, CallbackInfo ci) {
        infected = nbt.getBoolean(INFECTED);
    }

    @Override
    public Boolean isInfected() {
        return infected;
    }
    @Override
    public void setInfected(Boolean infected) {
        this.infected = infected;
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void ignoreVampires(LivingEntity target, CallbackInfo ci) {
        if (isUndead((MobEntity) (Object) this) && ((Access) target).getGift().equals("unDead")) {
            ci.cancel();
        }
    }
    @Unique
    private boolean isUndead(MobEntity mob) {
        return mob instanceof ZombieEntity
                || mob instanceof AbstractSkeletonEntity
                || mob instanceof PhantomEntity;
    }
}
