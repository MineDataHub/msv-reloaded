package net.datahub.msv.mutations

import net.datahub.msv.ModDamage.getPotionDamage
import net.datahub.msv.ModDamage.getRainDamage
import net.datahub.msv.ModDamage.getWaterDamage
import net.datahub.msv.ModItems.UmbrellaItem.check
import net.datahub.msv.access.PlayerAccess
import net.datahub.msv.constant.Mutations
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.thrown.ThrownItemEntity
import net.minecraft.server.world.ServerWorld
import java.util.function.Consumer

object Hydrophobic {
    fun waterDamage(player: LivingEntity) {
        if (player.isTouchingWater) {
            player.damage(player.world as ServerWorld?, getWaterDamage(), 1.5f)
        }

        if (!check(player) && player.world.isRaining && player.world.isSkyVisibleAllowingSea(
                player.blockPos
            )
        ) {
            player.damage(player.world as ServerWorld?, getRainDamage(), 1.5f)
        }
    }

    fun potionDamage(potion: ThrownItemEntity) {
        potion.world.getEntitiesByClass(
            PlayerEntity::class.java, potion.boundingBox.expand(4.0, 2.0, 4.0)
        ) { player: PlayerAccess -> player.mutation == Mutations.HYDROPHOBIC }
            .forEach(Consumer { entity: PlayerEntity ->
                entity.damage(
                    entity.world as ServerWorld,
                    getPotionDamage(),
                    1.0f
                )
            })
    }
}