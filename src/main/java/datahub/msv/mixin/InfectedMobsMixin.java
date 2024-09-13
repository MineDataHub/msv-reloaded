package datahub.msv.mixin;

import datahub.msv.MSVPlayerData;
import net.minecraft.entity.passive.AnimalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalEntity.class)
public abstract class InfectedMobsMixin {
    @Inject(method = "canEat", at = @At("HEAD"), cancellable = true)
    private void test(CallbackInfoReturnable<Boolean> cir) {
        AnimalEntity entity = (AnimalEntity) (Object) this;
        if (MSVPlayerData.INSTANCE.readBool(entity, MSVPlayerData.INFECTED)) {
            cir.setReturnValue(false);
        }
    }
}
