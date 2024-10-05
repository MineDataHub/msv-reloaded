package datahub.msv

import datahub.msv.MSVFiles.mutationsData
import datahub.msv.sneeze.BlackSneeze
import datahub.msv.sneeze.NormalSneeze
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import kotlin.math.pow
import kotlin.random.Random

object MSVPlayerData {
    const val MSV: String = "MSV"
    const val MUTATION: String = "Mutation"
    const val GIFT: String = "Gift"
    const val FREEZE_COOLDOWN: String = "FreezeCooldown"
    const val SNEEZE_COOLDOWN: String = "SneezeCooldown"
    const val CREEPER_SOUND_COOLDOWN: String = "CreeperSoundCooldown"
    const val STAGE: String = "Stage"
    const val TIME_FOR_UP_STAGE: String = "TimeForUpStage"
    const val INFECTED: String = "Infected"

    fun getRandomMutation(): String {
        val randomNum = Random.nextInt(0, mutationsData.values.sumOf {it.weight})
        var currentSum = 0

        for ((key) in mutationsData) {
            val chance = mutationsData[key]?.weight
            currentSum += chance!!
            if (randomNum < currentSum) {
                return key
            }
        }
        return "none"
    }

    fun getMutation(player: PlayerEntity): String {
        return player.writeNbt(NbtCompound()).getCompound(MSV).getString(MUTATION)
    }

    fun setMutation(player: PlayerEntity, value: String?) {
        val nbt = player.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putString(MUTATION, value)
        nbt.put(MSV, msv)
        player.readNbt(nbt)
    }

    fun getGift(player: PlayerEntity): String {
        return player.writeNbt(NbtCompound()).getCompound(MSV).getString(GIFT)
    }

    fun setGift(player: PlayerEntity, value: String) {
        val nbt = player.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putString(GIFT, value)
        nbt.put(MSV, msv)
        player.readNbt(nbt)
    }

    fun isInfected(entity: MobEntity): Boolean {
        return entity.writeNbt(NbtCompound()).getBoolean(INFECTED)
    }

    fun setInfected(entity: MobEntity, value: Boolean) {
        val nbt = entity.writeNbt(NbtCompound())
        nbt.putBoolean(INFECTED, value)
        entity.readNbt(nbt)
    }

    fun getFreezeCooldown(player: PlayerEntity): Int {
        return player.writeNbt(NbtCompound()).getCompound(MSV).getInt(FREEZE_COOLDOWN)
    }

    fun setFreezeCooldown(player: PlayerEntity, value: Int) {
        val nbt = player.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putInt(FREEZE_COOLDOWN, value)
        nbt.put(MSV, msv)
        player.readNbt(nbt)
    }

    fun getSneezeCooldown(player: PlayerEntity): Int {
        return player.writeNbt(NbtCompound()).getCompound(MSV).getInt(SNEEZE_COOLDOWN)
    }

    fun setSneezeCooldown(player: PlayerEntity, value: Int) {
        val nbt = player.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putInt(SNEEZE_COOLDOWN, value)
        nbt.put(MSV, msv)
        player.readNbt(nbt)
    }

    fun getCreeperSoundCooldown(player: PlayerEntity): Int {
        return player.writeNbt(NbtCompound()).getCompound(MSV).getInt(CREEPER_SOUND_COOLDOWN)
    }

    fun setCreeperSoundCooldown(player: PlayerEntity, value: Int) {
        val nbt = player.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putInt(CREEPER_SOUND_COOLDOWN, value)
        nbt.put(MSV, msv)
        player.readNbt(nbt)
    }

    fun getStage(player: PlayerEntity): Int {
        return player.writeNbt(NbtCompound()).getCompound(MSV).getInt(STAGE)
    }

    fun setStage(player: PlayerEntity, value: Int) {
        val nbt = player.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putInt(STAGE, value)
        nbt.put(MSV, msv)
        player.readNbt(nbt)
    }

    fun getTimeForUpStage(player: PlayerEntity): Int {
        return player.writeNbt(NbtCompound()).getCompound(MSV).getInt(TIME_FOR_UP_STAGE)
    }

    fun setTimeForUpStage(player: PlayerEntity, value: Int) {
        val nbt = player.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putInt(TIME_FOR_UP_STAGE, value)
        nbt.put(MSV, msv)
        player.readNbt(nbt)
    }

    // Пример использования в таймере игрока
    fun playerTimer(player: PlayerEntity) {
        val nbt = player.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        var stage = msv.getInt(STAGE)

        if (stage in 1..7) {
            var timeForUpStage = msv.getInt(TIME_FOR_UP_STAGE)
            if (timeForUpStage <= 0) {
                msv.putInt(STAGE, ++stage)
                timeForUpStage = ((257 + Random.nextInt(27)) * (2).toDouble().pow((stage - 1))).toInt()
                if (stage == 5) {
                    setMutation(player, getRandomMutation())
                }
            }
            msv.putInt(TIME_FOR_UP_STAGE, --timeForUpStage)
        }

        if (stage in 2..7) {
            var sneezeCooldown = msv.getInt(SNEEZE_COOLDOWN)
            if (sneezeCooldown <= 0) {
                sneezeCooldown = 15 + Random.nextInt(42) - stage
                if (stage in 2..6) {
                    NormalSneeze.spawn(player)
                } else {
                    BlackSneeze.spawn(player)
                }
            }
            msv.putInt(SNEEZE_COOLDOWN, --sneezeCooldown)
        }

        if (stage in 3..7) {
            var creeperSoundCooldown = msv.getInt(CREEPER_SOUND_COOLDOWN)
            if (creeperSoundCooldown <= 0) {
                creeperSoundCooldown = 15 + Random.nextInt(42) - stage
                player.world.playSound(player, player.x, player.y, player.z, SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.PLAYERS)
            }
            msv.putInt(CREEPER_SOUND_COOLDOWN, --creeperSoundCooldown)
        }

        nbt.put(MSV, msv)
        player.readNbt(nbt)
    }
}
