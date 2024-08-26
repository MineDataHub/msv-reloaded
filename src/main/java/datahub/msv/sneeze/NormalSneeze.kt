package datahub.msv.sneeze

import com.mojang.brigadier.Command
import datahub.msv.MSVStatusEffects
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.PotionContentsComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Vec3d

object NormalSneeze {
    private val playerSneezing = mutableListOf<PlayerTimer>()

    data class PlayerTimer(val player: PlayerEntity, val timer: Long)

    private fun collect(player: ServerPlayerEntity, items: ItemStack) {
        val playerTimer = playerSneezing.find { it.player == player }
        if (playerTimer != null) {
            playerSneezing.remove(playerTimer)

            items.decrement(1)
            val item = ItemStack(Items.POTION)
            item.set(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent(MSVStatusEffects.INFECTION_POTION))
            player.giveItemStack(item)
            player.world.playSound(null, player.x, player.y, player.z, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS, 0.5f, 1f)
            player.world.playSound(null, player.x, player.y, player.z, SoundEvents.BLOCK_MUD_FALL, SoundCategory.PLAYERS, 0.5f, 1.5f)
        }
    }

    fun spawn(player: ServerPlayerEntity): Int {
        val timer = System.currentTimeMillis() + 5000L
        val playerTimer = PlayerTimer(player, timer)
        playerSneezing.add(playerTimer)

        val world = player.world as? ServerWorld ?

        val particlePos = Vec3d(player.x, player.eyeY, player.z).add(player.getRotationVec(1.0f).multiply(0.5))
        world?.spawnParticles(
            ParticleTypes.SNEEZE,
            particlePos.x,
            particlePos.y - 0.5,
            particlePos.z,
            3,
            0.25,
            0.25,
            0.25,
            0.0
        )
        world?.playSound(null, player.x, player.y, player.z, SoundEvents.ENTITY_PANDA_SNEEZE, SoundCategory.PLAYERS, 0.7f, world.random.nextFloat() * 0.2f + 0.5f)

        return Command.SINGLE_SUCCESS
    }

    init {
        ServerTickEvents.END_SERVER_TICK.register {
            checkSneezedPlayers()
        }
        UseItemCallback.EVENT.register { player, world, hand ->
            if (world.isClient) {
                return@register TypedActionResult.pass(player.getStackInHand(hand))
            }

            val serverPlayer = player as ServerPlayerEntity
            val itemStack = serverPlayer.getStackInHand(hand)

            if (itemStack.item == Items.GLASS_BOTTLE) {
                collect(serverPlayer, itemStack)
                TypedActionResult.success(itemStack, world.isClient)
            } else {
                TypedActionResult.pass(itemStack)
            }
        }
    }

    private fun checkSneezedPlayers(): List<Int> {
        val results = mutableListOf<Int>()
        playerSneezing.removeIf { playerTimer ->
            if (System.currentTimeMillis() > playerTimer.timer) {
                results.add(0)
                true
            } else {
                false
            }
        }
        return results
    }
}