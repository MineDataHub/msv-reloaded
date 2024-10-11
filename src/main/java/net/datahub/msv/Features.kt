package net.datahub.msv

import net.datahub.msv.MSVFiles.mutationsData
import net.datahub.msv.nbt.Access
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.entity.mob.ZombieHorseEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import java.util.*


object Features {
    fun register() {
        MSVReloaded.LOGGER.info("Initializing features...")
        elytraFlapping()
        zombieEating()
    }

    fun getRandomMutation(): String {
        val randomNum = kotlin.random.Random.nextInt(0, mutationsData.values.sumOf {it.weight})
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

    private fun elytraFlapping() {
        EntityElytraEvents.ALLOW.register {
            (it as Access).mutation == "fallen"
        }
    }

    private val zombieEatingCD = mutableMapOf<PlayerEntity, Long>()
    private fun isZombie(entity: Entity): Boolean {return entity is ZombieEntity || entity is ZombieHorseEntity}
    private fun zombieEating() {
        UseEntityCallback.EVENT.register { player, world, hand, entity, _ ->
            player as Access
            if (player.gift == "zombieEater" && world is ServerWorld && player.hungerManager.isNotFull && isZombie(entity)) {
                val lastUseTime = zombieEatingCD[player] ?: 0L
                val currentTime = System.currentTimeMillis()

                if (currentTime - lastUseTime < 1250L)
                    return@register ActionResult.PASS

                player.swingHand(hand, true)
                world.spawnParticles(
                    ParticleTypes.CRIMSON_SPORE,
                    entity.x,
                    entity.y,
                    entity.z,
                    5,
                    0.25,
                    0.5,
                    0.25,
                    0.001
                )
                world.playSound(
                    null,
                    player.x,
                    player.y,
                    player.z,
                    SoundEvents.ENTITY_PLAYER_BURP,
                    SoundCategory.PLAYERS,
                    0.5f,
                    world.random.nextFloat() * 0.1f + 0.9f
                )
                player.hungerManager.add(3, 0.5f)
                entity.kill()

                zombieEatingCD[player] = currentTime

                return@register ActionResult.SUCCESS
            }
            return@register ActionResult.PASS
        }
    }

    private val itemDroppingCD = mutableMapOf<PlayerEntity, Long>()
    fun dropItem(player: PlayerEntity): ActionResult {
        val lastUseTime = itemDroppingCD[player] ?: 0L
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastUseTime < 500L)
            return ActionResult.PASS
        
        val stackToDrop: ItemStack = if (Random().nextBoolean()) {
            player.offHandStack
        } else {
            player.mainHandStack
        }
        if (!stackToDrop.isEmpty) {
            player.dropItem(stackToDrop.split(1), false)
            itemDroppingCD[player] = currentTime
        }
        return ActionResult.SUCCESS
    }
}
