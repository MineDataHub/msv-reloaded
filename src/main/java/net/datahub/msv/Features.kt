package net.datahub.msv

import net.datahub.msv.MSVFiles.mutationsData
import net.datahub.msv.access.MobAccess
import net.datahub.msv.access.PlayerAccess
import net.datahub.msv.constant.Gifts
import net.datahub.msv.constant.Mutations
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.entity.mob.ZombieHorseEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.biome.BiomeKeys
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

    fun getRandomGift(mutation: String ): String {
        return mutationsData[mutation]?.gifts?.random() ?: "none"
    }

    @Suppress("unused")
    fun getRandomGift(player: PlayerEntity ): String {
        return mutationsData[(player as PlayerAccess).mutation]?.gifts?.random() ?: "none"
    }

    private fun elytraFlapping() {
        EntityElytraEvents.ALLOW.register {
            (it as PlayerAccess).mutation == Mutations.FALLEN
        }
    }

    private fun isZombie(entity: Entity): Boolean {return entity is ZombieEntity || entity is ZombieHorseEntity}
    private fun zombieEating() {
        UseEntityCallback.EVENT.register { player, world, hand, entity, _ ->
            player as PlayerAccess
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

    fun spawnInfectedZombie(player: PlayerEntity) {
        val zombieType: EntityType<*> = when (player.world.getBiome(player.blockPos)) {
            BiomeKeys.DESERT, BiomeKeys.BADLANDS, BiomeKeys.SAVANNA, BiomeKeys.SAVANNA_PLATEAU -> EntityType.HUSK
            BiomeKeys.OCEAN, BiomeKeys.RIVER, BiomeKeys.FROZEN_OCEAN, BiomeKeys.FROZEN_RIVER -> EntityType.DROWNED
            else -> EntityType.ZOMBIE
        }

        val zombie = zombieType.create(player.world)!!.also {
            (it as MobAccess).isInfected = true
        }
        zombie.refreshPositionAndAngles(findSpot(player), 0.0f, 0.0f)
        player.world.spawnEntity(zombie)
    }

    private fun findSpot(
        player: PlayerEntity
    ): BlockPos? {
        val world: World = player.world
        val startPos: BlockPos = player.blockPos
        val playerPos: Vec3d = player.pos
        val playerDirection: Vec3d = player.getRotationVec(1.0f)
        val random = Random()

        return (0..9).asSequence().map {
            val xOffset = random.nextInt(20) - 10
            val yOffset = random.nextInt(8) - 4
            val zOffset = random.nextInt(20) - 10
            startPos.add(xOffset, yOffset, zOffset)
        }.firstOrNull { pos ->
            world.getLightLevel(pos) < 8 &&
                    !world.getBlockState(pos).isSolidBlock(world, pos) &&
                    Vec3d(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()).let { vec ->
                        vec.subtract(playerPos).normalize().dotProduct(playerDirection.normalize()) < -0.5 &&
                                vec.distanceTo(playerPos) >= 2.0
                    }
        }
    }

    fun dropItem(player: PlayerEntity): ActionResult {
        if ((player as PlayerAccess).itemDroppingCD > 0)
            return ActionResult.PASS
        
        val stackToDrop: ItemStack =
            if (Random().nextBoolean())
                player.offHandStack
            else
                player.mainHandStack

        if (!stackToDrop.isEmpty) {
            player.dropItem(stackToDrop.split(1), false)
            (player as PlayerAccess).itemDroppingCD = 20
        }
        return ActionResult.SUCCESS
    }
}
