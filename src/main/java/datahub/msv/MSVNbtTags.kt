package datahub.msv

import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity

object MSVNbtTags{
    val nbt = NbtCompound()
    public final val MSVTAGS = "msv_tags"
    public final val FREEZE_COOLDOWN = "freeze_cooldown"
    public final val SNEEZE_COOLDOWN = "sneeze_cooldown"
    public final val BLINDNESS_COOLDOWN = "blindness_cooldown"
    public final val NAUSEA_COOLDOWN = "nausea_cooldown"
    public final val SLOWNESS_COOLDOWN = "slowness_cooldown"
    public final val MINING_FATIGUE_COOLDOWN = "mining_fatigue_cooldown"
    public final val CREEPER_SOUND_COOLDOWN = "creeper_sound_cooldown"
    fun freeze_cooldown(player: ServerPlayerEntity) {
        player.writeCustomDataToNbt(nbt)
    }
}