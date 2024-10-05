package datahub.msv.mixin;

import datahub.msv.MSVPlayerData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @Unique
    private Boolean infected = false;
    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean(MSVPlayerData.INFECTED, infected);
    }
    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    public void readNbt(NbtCompound nbt, CallbackInfo ci) {
        infected = nbt.getBoolean(MSVPlayerData.INFECTED);
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void ignoreVampires(LivingEntity target, CallbackInfo ci) {
        if (target instanceof PlayerEntity && MSVPlayerData.INSTANCE.getMutation((PlayerEntity) target).equals("vampire") && isUndead)
            ci.cancel();
    }

    @Unique
    MobEntity mob = (MobEntity) (Object) this;
    @Unique
    private boolean isUndead = mob instanceof ZombieEntity
            || mob instanceof AbstractSkeletonEntity
            || mob instanceof PhantomEntity;
}
