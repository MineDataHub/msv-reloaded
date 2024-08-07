package datahub.msv

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

object Features {

    fun registerModFeatures() {
        Main.LOGGER.info("Registering Features for" + Main.MOD_ID)
        waterDmgAndBurning()
        elytraClosing()
        zombieEating()
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>?, dedicated: CommandRegistryAccess?, environment: RegistrationEnvironment? ->
            zombieSpawnCommand(
                dispatcher
            )
        })
    }

    private fun elytraClosing() {
        EntityElytraEvents.ALLOW.register { entity ->
            readMSV(entity, MSVNbtTags.MUTATION) != "fallen"
        }
    }

    private var tickCounter = 0
    private fun waterDmgAndBurning() {
        ServerTickEvents.END_SERVER_TICK.register { server ->
            if (++tickCounter >= 10) {
                tickCounter = 0

                server.playerManager.playerList.forEach { player ->
                    val blockPos = BlockPos.ofFloored(player.x, player.eyeY, player.z)

                    if (readMSV(player, MSVNbtTags.MUTATION) == "hydrophobic") {
                        player.takeIf {it.isTouchingWater}?.apply {
                            damage(MSVDamage.createDamageSource(player.world, MSVDamage.WATER), 1.5f)}

                        player.takeIf { it.world.isRaining && it.world.isSkyVisibleAllowingSea(blockPos) }?.apply {
                            damage(MSVDamage.createDamageSource(player.world, MSVDamage.RAIN), 1.5f)}
                    }

                    player.takeIf { it.world.isDay && it.world.isSkyVisibleAllowingSea(blockPos) && it.commandTags.contains("vampire") }?.apply {
                        fireTicks = 20}
                }
            }
        }
    }

    private val lastUseTimes = mutableMapOf<PlayerEntity, Long>()
    private const val COOLDOWN_PERIOD = 1000L
    private fun zombieEating() {
        UseEntityCallback.EVENT.register { player, world, _, entity, _ ->
            if (entity is ZombieEntity && readMSV(player, MSVNbtTags.MUTATION) == "ghoul") {
                val lastUseTime = lastUseTimes[player] ?: 0L
                val currentTime = System.currentTimeMillis()

                if (currentTime - lastUseTime < COOLDOWN_PERIOD) {
                    return@register ActionResult.PASS
                }

                if (player.hungerManager.isNotFull) {
                    if (world is ServerWorld) {
                        world.spawnParticles(ParticleTypes.CRIMSON_SPORE, entity.x, entity.y, entity.z, 5, 0.25, 0.5, 0.25, 0.001)
                    }
                    world.playSound(null, player.x, player.y, player.z, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5f, world.random.nextFloat() * 0.1f + 0.9f)
                    player.hungerManager.add(3, 0.5f)
                    entity.kill()

                    lastUseTimes[player] = currentTime

                    return@register ActionResult.SUCCESS
                }
            }
            ActionResult.PASS
        }
    }

    private fun zombieSpawnCommand(dispatcher: CommandDispatcher<ServerCommandSource?>?) {
        dispatcher?.register(
            literal<ServerCommandSource>("spawnZombie")
                .executes { SpawnInfZombie.spawnZombie(it) }
        )
    }

    fun checkHazmat(player: PlayerEntity): Boolean {
        for (i in 3 downTo 0) {
            val nbt = player.inventory.armor[i].components
            return !Objects.requireNonNull(nbt.get(DataComponentTypes.CUSTOM_DATA))?.contains("hazmat")!!
        }
        return true
    }

    fun dropItem(player: PlayerEntity) {
        val stackToDrop: ItemStack = if (Random().nextBoolean()) {
            player.offHandStack
        } else {
            player.mainHandStack
        }
        if (!stackToDrop.isEmpty) {
            player.dropItem(stackToDrop.split(1), false)
        }
    }

    fun readMSV(entity: Entity, tagPath: String): String {
        return entity.writeNbt(NbtCompound()).getCompound(MSVNbtTags.MSV).getString(tagPath)
    }
}