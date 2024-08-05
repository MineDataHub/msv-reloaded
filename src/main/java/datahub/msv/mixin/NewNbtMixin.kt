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
abstract class NewNbtMixin {
    @Unique
    var freeze_cooldown: Int = 0
    var sneeze_cooldown: Int = 0
    var blindness_cooldown: Int = 0
    var nausea_cooldown: Int = 0
    var slowness_cooldown: Int = 0
    var mining_fatigue_cooldown: Int = 0
    var creeper_sound_cooldown: Int = 0

    @Inject(method = ["writeCustomDataToNbt"], at = [At("TAIL")])
    fun writeCustomDataToNbt(nbt: NbtCompound, ci: CallbackInfo?) {
        nbt.putInt(MSVNbtTags.FREEZE_COOLDOWN, this.freeze_cooldown)
        nbt.putInt(MSVNbtTags.SNEEZE_COOLDOWN, this.sneeze_cooldown)
        nbt.putInt(MSVNbtTags.BLINDNESS_COOLDOWN, this.blindness_cooldown)
        nbt.putInt(MSVNbtTags.NAUSEA_COOLDOWN, this.nausea_cooldown)
        nbt.putInt(MSVNbtTags.SLOWNESS_COOLDOWN, this.slowness_cooldown)
        nbt.putInt(MSVNbtTags.MINING_FATIGUE_COOLDOWN, this.mining_fatigue_cooldown)
        nbt.putInt(MSVNbtTags.CREEPER_SOUND_COOLDOWN, this.creeper_sound_cooldown)
    }

    @Inject(method = ["readCustomDataFromNbt"], at = [At("TAIL")])
    fun readCustomDataFromNbt(nbt: NbtCompound, ci: CallbackInfo?) {
        this.freeze_cooldown = nbt.getInt(MSVNbtTags.FREEZE_COOLDOWN)
        this.sneeze_cooldown = nbt.getInt(MSVNbtTags.SNEEZE_COOLDOWN)
        this.blindness_cooldown = nbt.getInt(MSVNbtTags.BLINDNESS_COOLDOWN)
        this.nausea_cooldown = nbt.getInt(MSVNbtTags.NAUSEA_COOLDOWN)
        this.slowness_cooldown = nbt.getInt(MSVNbtTags.SLOWNESS_COOLDOWN)
        this.mining_fatigue_cooldown = nbt.getInt(MSVNbtTags.MINING_FATIGUE_COOLDOWN)
        this.creeper_sound_cooldown = nbt.getInt(MSVNbtTags.CREEPER_SOUND_COOLDOWN)


    }
}