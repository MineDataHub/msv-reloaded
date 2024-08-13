package datahub.msv.mixin

import datahub.msv.MSVNbtTags
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ServerPlayerEntity::class)
abstract class NbtTagsMixin {

    @Unique
    private var mutation: String = ""
    private var freezeCooldown: Int = 0
    private var sneezeCooldown: Int = 0
    private var blindnessCooldown: Int = 0
    private var nauseaCooldown: Int = 0
    private var slownessCooldown: Int = 0
    private var miningFatigueCooldown: Int = 0
    private var creeperSoundCooldown: Int = 0
    private var stage: Int = 0
    private var timeForUpStage: Int = 0

    @Inject(method = ["writeCustomDataToNbt"], at = [At("TAIL")])
    fun writeCustomDataToNbt(nbt: NbtCompound, ci: CallbackInfo?) {
        val msv = NbtCompound()
        msv.putString(MSVNbtTags.MUTATION, this.mutation)
        msv.putInt(MSVNbtTags.FREEZE_COOLDOWN, this.freezeCooldown)
        msv.putInt(MSVNbtTags.SNEEZE_COOLDOWN, this.sneezeCooldown)
        msv.putInt(MSVNbtTags.BLINDNESS_COOLDOWN, this.blindnessCooldown)
        msv.putInt(MSVNbtTags.NAUSEA_COOLDOWN, this.nauseaCooldown)
        msv.putInt(MSVNbtTags.SLOWNESS_COOLDOWN, this.slownessCooldown)
        msv.putInt(MSVNbtTags.MINING_FATIGUE_COOLDOWN, this.miningFatigueCooldown)
        msv.putInt(MSVNbtTags.CREEPER_SOUND_COOLDOWN, this.creeperSoundCooldown)
        msv.putInt(MSVNbtTags.STAGE, this.stage)
        msv.putInt(MSVNbtTags.TIME_FOR_UP_STAGE, this.timeForUpStage)
        nbt.put(MSVNbtTags.MSV, msv)
    }

    @Inject(method = ["readCustomDataFromNbt"], at = [At("TAIL")])
    fun readCustomDataFromNbt(nbt: NbtCompound, ci: CallbackInfo?) {
        if (nbt.contains(MSVNbtTags.MSV)) {
            val msv = nbt.getCompound(MSVNbtTags.MSV)
            this.mutation = msv.getString(MSVNbtTags.MUTATION)
            this.freezeCooldown = msv.getInt(MSVNbtTags.FREEZE_COOLDOWN)
            this.sneezeCooldown = msv.getInt(MSVNbtTags.SNEEZE_COOLDOWN)
            this.blindnessCooldown = msv.getInt(MSVNbtTags.BLINDNESS_COOLDOWN)
            this.nauseaCooldown = msv.getInt(MSVNbtTags.NAUSEA_COOLDOWN)
            this.slownessCooldown = msv.getInt(MSVNbtTags.SLOWNESS_COOLDOWN)
            this.miningFatigueCooldown = msv.getInt(MSVNbtTags.MINING_FATIGUE_COOLDOWN)
            this.creeperSoundCooldown = msv.getInt(MSVNbtTags.CREEPER_SOUND_COOLDOWN)
            this.stage = msv.getInt(MSVNbtTags.STAGE)
            this.timeForUpStage = msv.getInt(MSVNbtTags.TIME_FOR_UP_STAGE)
        }

    }
}