package net.datahub.msv

import net.datahub.msv.MSVFiles.mutationsData
import net.datahub.msv.constants.Gifts
import net.datahub.msv.constants.Mutations
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
            (it as Access).mutation == Mutations.FALLEN
        }
    }

    private fun isZombie(entity: Entity): Boolean {return entity is ZombieEntity || entity is ZombieHorseEntity}
    private fun zombieEating() {
        UseEntityCallback.EVENT.register { player, world, hand, entity, _ ->
            player as Access
            if (world is ServerWorld && player.gift == Gifts.ZOMBIE_EATER && player.hungerManager.isNotFull && isZombie(entity) && player.zombieEatingCD <= 0) {
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

                player.zombieEatingCD = 25
                return@register ActionResult.SUCCESS
            }
            return@register ActionResult.PASS
        }
    }

    fun dropItem(player: PlayerEntity): ActionResult {
        if ((player as Access).itemDroppingCD > 0)
            return ActionResult.PASS
        
        val stackToDrop: ItemStack =
            if (Random().nextBoolean())
                player.offHandStack
            else
                player.mainHandStack

        if (!stackToDrop.isEmpty) {
            player.dropItem(stackToDrop.split(1), false)
            (player as Access).itemDroppingCD = 20
        }
        return ActionResult.SUCCESS
    }
}
