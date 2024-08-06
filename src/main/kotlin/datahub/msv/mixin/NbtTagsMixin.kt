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
    var mutation: String = ""
    var freeze_cooldown: Int = 0
    var sneeze_cooldown: Int = 0
    var blindness_cooldown: Int = 0
    var nausea_cooldown: Int = 0
    var slowness_cooldown: Int = 0
    var mining_fatigue_cooldown: Int = 0
    var creeper_sound_cooldown: Int = 0

    @Inject(method = ["writeCustomDataToNbt"], at = [At("TAIL")])
    fun writeCustomDataToNbt(nbt: NbtCompound, ci: CallbackInfo?) {
        val msv = NbtCompound()
        msv.putString(MSVNbtTags.MUTATION, this.mutation)
        msv.putInt(MSVNbtTags.FREEZE_COOLDOWN, this.freeze_cooldown)
        msv.putInt(MSVNbtTags.SNEEZE_COOLDOWN, this.sneeze_cooldown)
        msv.putInt(MSVNbtTags.BLINDNESS_COOLDOWN, this.blindness_cooldown)
        msv.putInt(MSVNbtTags.NAUSEA_COOLDOWN, this.nausea_cooldown)
        msv.putInt(MSVNbtTags.SLOWNESS_COOLDOWN, this.slowness_cooldown)
        msv.putInt(MSVNbtTags.MINING_FATIGUE_COOLDOWN, this.mining_fatigue_cooldown)
        msv.putInt(MSVNbtTags.CREEPER_SOUND_COOLDOWN, this.creeper_sound_cooldown)
        nbt.put(MSVNbtTags.MSV, msv)
    }

    @Inject(method = ["readCustomDataFromNbt"], at = [At("TAIL")])
    fun readCustomDataFromNbt(nbt: NbtCompound, ci: CallbackInfo?) {
        if (nbt.contains(MSVNbtTags.MSV)) {
            val msv = nbt.getCompound(MSVNbtTags.MSV)
            this.mutation = msv.getString(MSVNbtTags.MUTATION)
            this.freeze_cooldown = msv.getInt(MSVNbtTags.FREEZE_COOLDOWN)
            this.sneeze_cooldown = msv.getInt(MSVNbtTags.SNEEZE_COOLDOWN)
            this.blindness_cooldown = msv.getInt(MSVNbtTags.BLINDNESS_COOLDOWN)
            this.nausea_cooldown = msv.getInt(MSVNbtTags.NAUSEA_COOLDOWN)
            this.slowness_cooldown = msv.getInt(MSVNbtTags.SLOWNESS_COOLDOWN)
            this.mining_fatigue_cooldown = msv.getInt(MSVNbtTags.MINING_FATIGUE_COOLDOWN)
            this.creeper_sound_cooldown = msv.getInt(MSVNbtTags.CREEPER_SOUND_COOLDOWN)
        }

    }
}