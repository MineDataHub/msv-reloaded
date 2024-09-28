package datahub.msv

import datahub.msv.MSVFiles.mutationsData
import datahub.msv.sneeze.BlackSneeze
import datahub.msv.sneeze.NormalSneeze
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import kotlin.math.pow
import kotlin.random.Random

object MSVPlayerData {
    const val MSV: String = "MSV"
    const val MUTATION: String = "Mutation"
    const val FREEZE_COOLDOWN: String = "FreezeCooldown"
    const val SNEEZE_COOLDOWN: String = "SneezeCooldown"
    const val CREEPER_SOUND_COOLDOWN: String = "CreeperSoundCooldown"
    const val STAGE: String = "Stage"
    const val TIME_FOR_UP_STAGE: String = "TimeForUpStage"
    const val INFECTED: String = "Infected"

    fun getRandomMutation(): String? {
        val randomNum = Random.nextInt(0, mutationsData.values.sum())
        var currentSum = 0

        for ((key, chance) in mutationsData) {
            currentSum += chance
            if (randomNum < currentSum) {
                return key
            }
        }
        return null
    }

    fun getMutation(entity: LivingEntity): String {
        return entity.writeNbt(NbtCompound()).getCompound(MSV).getString(MUTATION)
    }

    fun setMutation(entity: LivingEntity, value: String?) {
        val nbt = entity.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putString(MUTATION, value)
        nbt.put(MSV, msv)
        entity.readNbt(nbt)
    }

    // Методы для работы с другими данными
    fun isInfected(entity: LivingEntity): Boolean {
        return entity.writeNbt(NbtCompound()).getBoolean(INFECTED)
    }

    fun setInfected(entity: LivingEntity, value: Boolean) {
        val nbt = entity.writeNbt(NbtCompound())
        nbt.putBoolean(INFECTED, value)
        entity.readNbt(nbt)
    }

    // Аналогично для остальных данных
    fun getFreezeCooldown(entity: LivingEntity): Int {
        return entity.writeNbt(NbtCompound()).getCompound(MSV).getInt(FREEZE_COOLDOWN)
    }

    fun setFreezeCooldown(entity: LivingEntity, value: Int) {
        val nbt = entity.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putInt(FREEZE_COOLDOWN, value)
        nbt.put(MSV, msv)
        entity.readNbt(nbt)
    }

    fun getSneezeCooldown(entity: LivingEntity): Int {
        return entity.writeNbt(NbtCompound()).getCompound(MSV).getInt(SNEEZE_COOLDOWN)
    }

    fun setSneezeCooldown(entity: LivingEntity, value: Int) {
        val nbt = entity.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putInt(SNEEZE_COOLDOWN, value)
        nbt.put(MSV, msv)
        entity.readNbt(nbt)
    }

    fun getCreeperSoundCooldown(entity: LivingEntity): Int {
        return entity.writeNbt(NbtCompound()).getCompound(MSV).getInt(CREEPER_SOUND_COOLDOWN)
    }

    fun setCreeperSoundCooldown(entity: LivingEntity, value: Int) {
        val nbt = entity.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putInt(CREEPER_SOUND_COOLDOWN, value)
        nbt.put(MSV, msv)
        entity.readNbt(nbt)
    }

    fun getStage(entity: LivingEntity): Int {
        return entity.writeNbt(NbtCompound()).getCompound(MSV).getInt(STAGE)
    }

    fun setStage(entity: LivingEntity, value: Int) {
        val nbt = entity.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putInt(STAGE, value)
        nbt.put(MSV, msv)
        entity.readNbt(nbt)
    }

    fun getTimeForUpStage(entity: LivingEntity): Int {
        return entity.writeNbt(NbtCompound()).getCompound(MSV).getInt(TIME_FOR_UP_STAGE)
    }

    fun setTimeForUpStage(entity: LivingEntity, value: Int) {
        val nbt = entity.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putInt(TIME_FOR_UP_STAGE, value)
        nbt.put(MSV, msv)
        entity.readNbt(nbt)
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
