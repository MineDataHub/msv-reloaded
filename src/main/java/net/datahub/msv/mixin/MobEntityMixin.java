package net.datahub.msv.mixin;

import net.datahub.msv.access.MobAccess;
import net.datahub.msv.constant.Gifts;
import net.datahub.msv.access.PlayerAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.datahub.msv.constant.NBTTags.*;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements MobAccess {
    @Unique
    private Boolean infected = false;

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

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
        if (target != null && isUndead(this) && ((PlayerAccess) target).getGift().equals(Gifts.UNDEAD)) {
            ci.cancel();
        }
    }
    @Unique
    private boolean isUndead(LivingEntity mob) {
        return mob instanceof ZombieEntity
                || mob instanceof AbstractSkeletonEntity
                || mob instanceof PhantomEntity;
    }
}
