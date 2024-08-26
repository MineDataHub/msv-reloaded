package datahub.msv

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import kotlin.math.pow
import kotlin.random.Random

object MSVPlayerData {
    const val MSV: String = "MSV"
    const val MUTATION: String = "Mutation"
    const val FREEZE_COOLDOWN: String = "FreezeCooldown"
    const val SNEEZE_COOLDOWN: String = "SneezeCooldown"
    const val BLINDNESS_COOLDOWN: String = "BlindnessCooldown"
    const val NAUSEA_COOLDOWN: String = "NauseaCooldown"
    const val SLOWNESS_COOLDOWN: String = "SlownessCooldown"
    const val MINING_FATIGUE_COOLDOWN: String = "MiningFatigueCooldown"
    const val CREEPER_SOUND_COOLDOWN: String = "CreeperSoundCooldown"
    const val STAGE: String = "Stage"
    const val TIME_FOR_UP_STAGE: String = "TimeForUpStage"

    private var mutation: String = ""
    private var freezeCooldown = 0
    private var sneezeCooldown = 0
    private var blindnessCooldown = 0
    private var nauseaCooldown = 0
    private var slownessCooldown = 0
    private var miningFatigueCooldown = 0
    private var creeperSoundCooldown = 0
    private var stage = 0
    private var timeForUpStage = 0

    fun writeToNbt(nbt: NbtCompound) {
        nbt.putString(MUTATION, this.mutation)
        nbt.putInt(FREEZE_COOLDOWN, this.freezeCooldown)
        nbt.putInt(SNEEZE_COOLDOWN, this.sneezeCooldown)
        nbt.putInt(BLINDNESS_COOLDOWN, this.blindnessCooldown)
        nbt.putInt(NAUSEA_COOLDOWN, this.nauseaCooldown)
        nbt.putInt(SLOWNESS_COOLDOWN, this.slownessCooldown)
        nbt.putInt(MINING_FATIGUE_COOLDOWN, this.miningFatigueCooldown)
        nbt.putInt(CREEPER_SOUND_COOLDOWN, this.creeperSoundCooldown)
        nbt.putInt(STAGE, this.stage)
        nbt.putInt(TIME_FOR_UP_STAGE, this.timeForUpStage)
    }

    fun readFromNbt(nbt: NbtCompound) {
        this.mutation = nbt.getString(MUTATION)
        this.freezeCooldown = nbt.getInt(FREEZE_COOLDOWN)
        this.sneezeCooldown = nbt.getInt(SNEEZE_COOLDOWN)
        this.blindnessCooldown = nbt.getInt(BLINDNESS_COOLDOWN)
        this.nauseaCooldown = nbt.getInt(NAUSEA_COOLDOWN)
        this.slownessCooldown = nbt.getInt(SLOWNESS_COOLDOWN)
        this.miningFatigueCooldown = nbt.getInt(MINING_FATIGUE_COOLDOWN)
        this.creeperSoundCooldown = nbt.getInt(CREEPER_SOUND_COOLDOWN)
        this.stage = nbt.getInt(STAGE)
        this.timeForUpStage = nbt.getInt(TIME_FOR_UP_STAGE)
    }

    fun readStr(player: Entity, tagPath: String): String {
        return player.writeNbt(NbtCompound()).getCompound(MSV).getString(tagPath)
    }

    fun readInt(player: Entity, tagPath: String): Int {
        return player.writeNbt(NbtCompound()).getCompound(MSV).getInt(tagPath)
    }

    fun playerTimer(player: PlayerEntity) {
        val nbt = player.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        var stage = msv.getInt(STAGE)

        if (stage in 1..7) {
            var timeForUpStage = msv.getInt(TIME_FOR_UP_STAGE)
            if (timeForUpStage <= 0) {
                msv.putInt(STAGE, ++stage)
                timeForUpStage = ((180 + Random.nextInt(-9, 9)) * (2).toDouble().pow((stage - 1))).toInt()
            }
            msv.putInt(TIME_FOR_UP_STAGE, --timeForUpStage)
        }

        if (stage in 2..7) {
            var sneezeCooldown = msv.getInt(SNEEZE_COOLDOWN)
            if (sneezeCooldown <= 0) {
                sneezeCooldown = 27 + Random.nextInt(-14, 14) - stage
                println("${player.name} чихнул")
            }
            msv.putInt(SNEEZE_COOLDOWN, --sneezeCooldown)
        }

        if (stage in 3..7) {
            var creeperSoundCooldown = msv.getInt(CREEPER_SOUND_COOLDOWN)
            if (creeperSoundCooldown <= 0) {
                creeperSoundCooldown = 24 + Random.nextInt(-14, 14) - stage
                println("${player.name} испугался")
            }
            msv.putInt(CREEPER_SOUND_COOLDOWN, --creeperSoundCooldown)
        }

        if (stage in 5..7) {
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