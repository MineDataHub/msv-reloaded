package datahub.msv.mixin;

import datahub.msv.MSVPlayerData;
import net.minecraft.entity.passive.SheepEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepEntity.class)
public class InfectedSheepMixin {
    @Inject(method = "onEatingGrass", at = @At("HEAD"), cancellable = true)
    private void forbidEating(CallbackInfo ci) {
        SheepEntity entity = (SheepEntity) (Object) this;
        if (MSVPlayerData.INSTANCE.readBool(entity, MSVPlayerData.INFECTED)) {
            ci.cancel();
        }
    }
}
