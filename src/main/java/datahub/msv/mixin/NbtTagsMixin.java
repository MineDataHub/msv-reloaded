package datahub.msv.mixin;

import datahub.msv.MSVPlayerData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class NbtTagsMixin {

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound msv = new NbtCompound();
        nbt.put(MSVPlayerData.MSV, msv);
        MSVPlayerData.INSTANCE.writeToNbt(msv);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(MSVPlayerData.MSV)) {
            NbtCompound msv = nbt.getCompound(MSVPlayerData.MSV);
            MSVPlayerData.INSTANCE.readFromNbt(msv);
        }
    }
}