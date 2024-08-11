package datahub.msv

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import kotlin.math.pow
import kotlin.random.Random

object MSVNbtTags{
    const val MSV = "msv"
    const val FREEZE_COOLDOWN = "freeze_cooldown"
    const val SNEEZE_COOLDOWN = "sneeze_cooldown"
    const val BLINDNESS_COOLDOWN = "blindness_cooldown"
    const val NAUSEA_COOLDOWN = "nausea_cooldown"
    const val SLOWNESS_COOLDOWN = "slowness_cooldown"
    const val MINING_FATIGUE_COOLDOWN = "mining_fatigue_cooldown"
    const val CREEPER_SOUND_COOLDOWN = "creeper_sound_cooldown"
    const val MUTATION = "mutation"
    const val STAGE = "stage"
    const val TIME_FOR_UP_STAGE = "time_for_up_stage"

    fun readStringMSV(player: Entity, tagPath: String): String {
        return player.writeNbt(NbtCompound()).getCompound(MSV).getString(tagPath)
    }

    fun readIntMSV(player: Entity, tagPath: String): Int {
        return player.writeNbt(NbtCompound()).getCompound(MSV).getInt(tagPath)
    }

    fun playerTimer(player: PlayerEntity) {
        val nbt = player.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        var stage = msv.getInt(STAGE)

        stage.takeIf { it in 1..7 }.apply {
            var timeForUpStage = msv.getInt(TIME_FOR_UP_STAGE)
            if (timeForUpStage <= 0) {
                msv.putInt(STAGE, ++stage)
                timeForUpStage = ((180 + Random.nextInt(-9, 9) ) * (2).toDouble().pow((stage - 1))).toInt()
            }
            msv.putInt(TIME_FOR_UP_STAGE, --timeForUpStage)
        }

        stage.takeIf { it in 2..7 }.apply {
            var sneezeCooldown = msv.getInt(SNEEZE_COOLDOWN)
            if (sneezeCooldown <= 0) {
                sneezeCooldown = 27 + Random.nextInt(-14, 14) - stage
                println("${player.name} чихнул")
            }
            msv.putInt(SNEEZE_COOLDOWN, --sneezeCooldown)
        }

        stage.takeIf { it in 3..7 }.apply {
            var creeperSoundCooldown = msv.getInt(CREEPER_SOUND_COOLDOWN)
            if (creeperSoundCooldown <= 0) {
                creeperSoundCooldown = 24 + Random.nextInt(-14, 14) - stage
                println("${player.name} испугался")
            }
            msv.putInt(CREEPER_SOUND_COOLDOWN, --creeperSoundCooldown)
        }

        stage.takeIf { it in 5..7 }.apply {
            var freezeCooldown = msv.getInt(FREEZE_COOLDOWN)
            if (freezeCooldown <= 0) {
                freezeCooldown = 24 + Random.nextInt(-4, 4) - stage
                println("${player.name} замёрз")
            }
            msv.putInt(FREEZE_COOLDOWN, --freezeCooldown)

            var blindnessCooldown = msv.getInt(BLINDNESS_COOLDOWN)
            if (blindnessCooldown <= 0) {
                blindnessCooldown = 24 + Random.nextInt(-4, 4) - stage
                println("У ${player.name} потемнело в глазах")
            }
            msv.putInt(BLINDNESS_COOLDOWN, --blindnessCooldown)

            var nauseaCooldown = msv.getInt(NAUSEA_COOLDOWN)
            if (nauseaCooldown <= 0) {
                nauseaCooldown = 24 + Random.nextInt(-4, 4) - stage
                println("У ${player.name} кружится голова")
            }
            msv.putInt(NAUSEA_COOLDOWN, --nauseaCooldown)

            var slownessCooldown = msv.getInt(SLOWNESS_COOLDOWN)
            if (slownessCooldown <= 0) {
                slownessCooldown = 24 + Random.nextInt(-4, 4) - stage
                println("У ${player.name} устали ноги")
            }
            msv.putInt(SLOWNESS_COOLDOWN, --slownessCooldown)

            var miningFatigueCooldown = msv.getInt(MINING_FATIGUE_COOLDOWN)
            if (miningFatigueCooldown <= 0) {
                miningFatigueCooldown = 24 + Random.nextInt(-4, 4) - stage
                println("У ${player.name} устали руки")
            }
            msv.putInt(MINING_FATIGUE_COOLDOWN, --miningFatigueCooldown)
        }

        nbt.put(MSV, msv)
        player.readNbt(nbt)

    }
}