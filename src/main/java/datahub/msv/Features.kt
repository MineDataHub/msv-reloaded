package datahub.msv

import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.mob.ZombieEntity
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

    fun registerModFeatures() {
        Main.LOGGER.info("Registering features for" + Main.MOD_ID)
        playerEffects()
        elytraFlapping()
        zombieEating()
    }

    private fun elytraFlapping() {
        EntityElytraEvents.ALLOW.register { entity ->
            MSVNbtTags.readStr(entity, MSVNbtTags.MUTATION) != "fallen"
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
                        MSVNbtTags.playerTimer(player)
                    }
                    val blockPos = BlockPos.ofFloored(player.x, player.eyeY, player.z)

                    if (MSVNbtTags.readStr(player, MSVNbtTags.MUTATION) == "hydrophobic") {
                        player.takeIf { it.isTouchingWater }?.apply {
                            damage(MSVDamage.createDamageSource(player.world, MSVDamage.WATER), 1.5f)
                        }

                        player.takeIf { it.world.isRaining && it.world.isSkyVisibleAllowingSea(blockPos) && !MSVItems.UmbrellaItem.check(player)}?.apply {
                            damage(MSVDamage.createDamageSource(player.world, MSVDamage.RAIN), 1.5f)
                        }
                    }

                    player.takeIf {
                        it.world.isDay && it.world.isSkyVisibleAllowingSea(blockPos) && MSVNbtTags.readStr(player, MSVNbtTags.MUTATION) == "vampire" && !MSVItems.UmbrellaItem.check(player)
                    }?.apply {
                        fireTicks = 160
                    }
                }
            }

        }
    }

    private val zombieEatingCD = mutableMapOf<PlayerEntity, Long>()
    private fun zombieEating() {
        UseEntityCallback.EVENT.register { player, world, _, entity, _ ->
            if (entity is ZombieEntity && MSVNbtTags.readStr(player, MSVNbtTags.MUTATION) == "ghoul") {
                val lastUseTime = zombieEatingCD[player] ?: 0L
                val currentTime = System.currentTimeMillis()

                if (currentTime - lastUseTime < 1000L) {
                    return@register ActionResult.PASS
                }

                if (player.hungerManager.isNotFull) {
                    if (world is ServerWorld) {
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
                    }
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
            }
            ActionResult.PASS
        }
    }

    fun checkHazmat(player: PlayerEntity): Boolean {
        for (i in 3 downTo 0) {
            val nbt = player.inventory.armor[i].components
            return !Objects.requireNonNull(nbt.get(DataComponentTypes.CUSTOM_DATA))?.contains("hazmat")!!
        }
        return true
    }

    private val itemDroppingCD = mutableMapOf<PlayerEntity, Long>()
    fun dropItem(player: PlayerEntity): ActionResult {
        val lastUseTime = itemDroppingCD[player] ?: 0L
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastUseTime < 500L) {
            return ActionResult.PASS
        }
        
        val stackToDrop: ItemStack = if (Random().nextBoolean()) {
            player.offHandStack
        } else {
            player.mainHandStack
        }
        if (!stackToDrop.isEmpty) {
            player.dropItem(stackToDrop.split(1), false)
        }
        itemDroppingCD[player] = currentTime

        return ActionResult.SUCCESS
    }
}