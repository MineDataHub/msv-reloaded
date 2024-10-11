package net.datahub.msv

import net.datahub.msv.MSVFiles.mutationsData
import net.datahub.msv.nbt.Access
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.entity.Entity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
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
import java.util.*

object Features {
    fun register() {
        MSVReloaded.LOGGER.info("Initializing features...")
        elytraFlapping()
        playerEffects()
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
    private var tickCounter = 0
    private fun playerEffects() {
        ServerTickEvents.END_SERVER_TICK.register { server ->
            ++tickCounter
            server.playerManager.playerList.forEach { player ->
                if (tickCounter % 10 == 0) {
                    if (tickCounter >= 200) {
                        tickCounter = 0
                    }
                    player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.updateModifier(
                        EntityAttributeModifier(
                            MSVReloaded.id("health"),
                            when ((player as Access).stage) {
                                in 5..6 -> -4.0
                                in 2..7 -> -2.0
                                else -> 0.0 },
                            EntityAttributeModifier.Operation.ADD_VALUE
                        )
                    )
                }

                val blockPos = BlockPos.ofFloored(player.x, player.eyeY, player.z)

                if ((player as Access).mutation == "hydrophobic") {
                    player.takeIf { it.isTouchingWater }?.apply {
                        damage(MSVDamage.createDamageSource(player.world, MSVDamage.WATER), 1.5f)
                    }

                    player.takeIf { it.world.isRaining && it.world.isSkyVisibleAllowingSea(blockPos) && !MSVItems.UmbrellaItem.check(
                        player
                    )
                    }?.apply {
                        damage(MSVDamage.createDamageSource(player.world, MSVDamage.RAIN), 1.5f)
                    }
                }

                player.takeIf {
                    it.world.isDay && it.world.isSkyVisibleAllowingSea(blockPos) && (player as Access).mutation == "vampire" && !MSVItems.UmbrellaItem.check(
                        player
                    )
                }?.apply {
                    fireTicks = 80
                }

                if ((player as Access).freezeCoolDown < 0) {
                    player.isFrozen
                    player.frozenTicks += 3
                    if (player.frozenTicks >= 160)
                        (player as Access).freezeCoolDown = 30 + Random().nextInt(12) - (player as Access).stage
                }
            }

        }
    }

    private val zombieEatingCD = mutableMapOf<PlayerEntity, Long>()
    private fun isZombie(entity: Entity): Boolean {return entity is ZombieEntity || entity is ZombieHorseEntity}
    private fun zombieEating() {
        UseEntityCallback.EVENT.register { player, world, hand, entity, _ ->
            if ((player as Access).gift == "zombieEater" && world is ServerWorld && player.hungerManager.isNotFull && isZombie(entity)) {
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