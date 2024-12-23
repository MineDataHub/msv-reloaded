package net.datahub.msv

import net.datahub.msv.MSVFiles.mutationsData
import net.datahub.msv.access.MobAccess
import net.datahub.msv.access.PlayerAccess
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.biome.BiomeKeys
import java.util.*

object Features {
    init {
        MSVReloaded.LOGGER.info("Initializing features...")
        onDamage()
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

    fun getRandomGift(mutation: String): String {
        return mutationsData[mutation]?.gifts?.random() ?: "none"
    }

    @Suppress("unused")
    fun getRandomGift(player: PlayerEntity ): String {
        return mutationsData[(player as PlayerAccess).mutation]?.gifts?.random() ?: "none"
    }

    private fun onDamage() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register { entity: LivingEntity, _: DamageSource, _: Float, _: Float, _: Boolean ->
            if (entity is PlayerEntity && (entity as PlayerAccess).stage > 1) {
                entity.addStatusEffect(StatusEffectInstance(StatusEffects.BLINDNESS, 60, 1, false, false))
                if (Random().nextBoolean()) dropItem(entity)
            }
        }
    }

    fun spawnInfectedZombie(player: PlayerEntity) {
        val zombieType: EntityType<*> = when (player.world.getBiome(player.blockPos)) {
            BiomeKeys.DESERT, BiomeKeys.BADLANDS, BiomeKeys.SAVANNA, BiomeKeys.SAVANNA_PLATEAU -> EntityType.HUSK
            BiomeKeys.OCEAN, BiomeKeys.RIVER, BiomeKeys.FROZEN_OCEAN, BiomeKeys.FROZEN_RIVER -> EntityType.DROWNED
            else -> EntityType.ZOMBIE
        }

        val zombie = zombieType.create(player.world, SpawnReason.EVENT)!!.also {
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

    private fun dropItem(player: PlayerEntity): ActionResult {
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
