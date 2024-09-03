package datahub.msv.mixin;

import datahub.msv.MSVPlayerData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class IgnoringVampiresMixin {
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void ignoreVampires(LivingEntity target, CallbackInfo ci) {
        MobEntity mobEntity = (MobEntity) (Object) this;
        if (target != null && MSVPlayerData.INSTANCE.readStr(target, MSVPlayerData.MUTATION).equals("vampire") && isUndead(mobEntity) ) {
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