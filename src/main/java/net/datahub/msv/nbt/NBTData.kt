package net.datahub.msv.nbt

import net.datahub.msv.MSVFiles.mutationsData
import net.datahub.msv.sneeze.BlackSneeze
import net.datahub.msv.sneeze.NormalSneeze
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import kotlin.math.pow
import kotlin.random.Random

object NBTData {
    /* Пример использования в таймере игрока
    fun playerTimer() {
        var stage = msv.getInt(STAGE)

        if (stage in 1..6) {
            var timeForUpStage = getTimeForUpStage(player)
            if (timeForUpStage <= 0) {
                msv.putInt(STAGE, ++stage)
                timeForUpStage = ((257 + Random.nextInt(27)) * (2).toDouble().pow((stage - 1))).toInt()
                if (stage == 6) {
                    setMutation(player, getRandomMutation())
                }
                if (stage == 7) {
                    mutationsData[getMutation(player)]?.gifts?.let { setGift(player, it.random()) }
                }
            }
            setTimeForUpStage(player, --timeForUpStage)
        }

        if (stage in 2..7) {
            var sneezeCooldown = getSneezeCooldown(player)
            if (sneezeCooldown <= 0) {
                sneezeCooldown = 15 + Random.nextInt(42) - stage
                if (stage in 2..6) {
                    NormalSneeze.spawn(player)
                } else {
                    BlackSneeze.spawn(player)
                }
            }
            setSneezeCooldown(player, --sneezeCooldown)
        }

        if (stage in 3..7) {
            var creeperSoundCooldown = getCreeperSoundCooldown(player)
            if (creeperSoundCooldown <= 0) {
                creeperSoundCooldown = 15 + Random.nextInt(42) - stage
                player.world.playSound(player, player.x, player.y, player.z, SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.PLAYERS)
            }
            setCreeperSoundCooldown(player, --creeperSoundCooldown)
        }
    }

     */
}
