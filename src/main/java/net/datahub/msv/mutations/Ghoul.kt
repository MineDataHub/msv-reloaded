package net.datahub.msv.mutations

import net.datahub.msv.access.PlayerAccess
import net.datahub.msv.constant.Gifts
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.entity.mob.ZombieHorseEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult

object Ghoul {
    init {
        zombieEating()
    }

    fun foodEffects(player: LivingEntity, stack: ItemStack) {
        if (stack.item === Items.ROTTEN_FLESH) {
            player.removeStatusEffect(StatusEffects.HUNGER)
        } else {
            val currentEffect: StatusEffectInstance? = player.getStatusEffect(StatusEffects.HUNGER)
            val newDuration = if ((currentEffect != null)) currentEffect.duration + 300 else 300
            val newAmplifier = if ((currentEffect != null)) currentEffect.amplifier + 1 else 0
            player.addStatusEffect(StatusEffectInstance(StatusEffects.HUNGER, newDuration, newAmplifier))

            if (currentEffect != null && currentEffect.amplifier >= 2) player.addStatusEffect(
                StatusEffectInstance(
                    StatusEffects.POISON,
                    200,
                    0
                )
            )
        }
    }

    private fun zombieEating() {
        UseEntityCallback.EVENT.register { player, world, hand, entity, _ ->
            player as PlayerAccess
            if (world is ServerWorld
                && player.gift == Gifts.ZOMBIE_EATER
                && player.hungerManager.isNotFull
                && (entity is ZombieEntity || entity is ZombieHorseEntity)
                && player.zombieEatingCD <= 0) {
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
                entity.kill(entity.world as ServerWorld?)

                player.zombieEatingCD = 25
                return@register ActionResult.SUCCESS
            }
            return@register ActionResult.PASS
        }
    }
}