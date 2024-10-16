package net.datahub.msv.sneeze

import net.datahub.msv.MSVStatusEffects
import net.datahub.msv.nbt.Access
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.PotionContentsComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Vec3d

object NormalSneeze {
    init {
        UseItemCallback.EVENT.register { player, _, hand ->
            val itemStack = player.getStackInHand(hand)

            if ((player as Access).sneezePicking > 0 && itemStack.item == Items.GLASS_BOTTLE) {
                player.sneezePicking = 0

                itemStack.decrement(1)
                val item = ItemStack(Items.POTION)
                item.set(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent(MSVStatusEffects.INFECTION_POTION))
                player.giveItemStack(item)
                player.world.playSound(null, player.x, player.y, player.z, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS, 0.5f, 1f)
                player.world.playSound(null, player.x, player.y, player.z, SoundEvents.BLOCK_MUD_FALL, SoundCategory.PLAYERS, 0.5f, 1.5f)

                TypedActionResult.success(itemStack, true)
            } else
                TypedActionResult.pass(itemStack)
        }
    }

    fun spawn(player: PlayerEntity) {
        (player as Access).sneezePicking = 100

        val world = player.world as ServerWorld
        val particlePos = Vec3d(player.x, player.eyeY, player.z).add(player.getRotationVec(1.0f).multiply(0.5))
        world.spawnParticles(
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
        world.playSound(
            null,
            player.x,
            player.y,
            player.z,
            SoundEvents.ENTITY_PANDA_SNEEZE,
            SoundCategory.PLAYERS,
            0.7f,
            world.random.nextFloat() * 0.2f + 0.5f
        )
    }
}