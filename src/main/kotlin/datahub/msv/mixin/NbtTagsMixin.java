package datahub.msv.mixin;

import datahub.msv.MSVNbtTags;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class NbtTagsMixin {

    private String mutation = "";
    private int freezeCooldown = 0;
    private int sneezeCooldown = 0;
    private int blindnessCooldown = 0;
    private int nauseaCooldown = 0;
    private int slownessCooldown = 0;
    private int miningFatigueCooldown = 0;
    private int creeperSoundCooldown = 0;
    private int stage = 0;
    private int timeForUpStage = 0;

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound msv = new NbtCompound();
        msv.putString(MSVNbtTags.MUTATION, this.mutation);
        msv.putInt(MSVNbtTags.FREEZE_COOLDOWN, this.freezeCooldown);
        msv.putInt(MSVNbtTags.SNEEZE_COOLDOWN, this.sneezeCooldown);
        msv.putInt(MSVNbtTags.BLINDNESS_COOLDOWN, this.blindnessCooldown);
        msv.putInt(MSVNbtTags.NAUSEA_COOLDOWN, this.nauseaCooldown);
        msv.putInt(MSVNbtTags.SLOWNESS_COOLDOWN, this.slownessCooldown);
        msv.putInt(MSVNbtTags.MINING_FATIGUE_COOLDOWN, this.miningFatigueCooldown);
        msv.putInt(MSVNbtTags.CREEPER_SOUND_COOLDOWN, this.creeperSoundCooldown);
        msv.putInt(MSVNbtTags.STAGE, this.stage);
        msv.putInt(MSVNbtTags.TIME_FOR_UP_STAGE, this.timeForUpStage);
        nbt.put(MSVNbtTags.MSV, msv);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(MSVNbtTags.MSV)) {
            NbtCompound msv = nbt.getCompound(MSVNbtTags.MSV);
            this.mutation = msv.getString(MSVNbtTags.MUTATION);
            this.freezeCooldown = msv.getInt(MSVNbtTags.FREEZE_COOLDOWN);
            this.sneezeCooldown = msv.getInt(MSVNbtTags.SNEEZE_COOLDOWN);
            this.blindnessCooldown = msv.getInt(MSVNbtTags.BLINDNESS_COOLDOWN);
            this.nauseaCooldown = msv.getInt(MSVNbtTags.NAUSEA_COOLDOWN);
            this.slownessCooldown = msv.getInt(MSVNbtTags.SLOWNESS_COOLDOWN);
            this.miningFatigueCooldown = msv.getInt(MSVNbtTags.MINING_FATIGUE_COOLDOWN);
            this.creeperSoundCooldown = msv.getInt(MSVNbtTags.CREEPER_SOUND_COOLDOWN);
            this.stage = msv.getInt(MSVNbtTags.STAGE);
            this.timeForUpStage = msv.getInt(MSVNbtTags.TIME_FOR_UP_STAGE);
        }
    }
}
