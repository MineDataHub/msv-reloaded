package datahub.msv

import com.mojang.brigadier.context.CommandContext
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.*

object SpawnInfZombie {
    fun spawnZombie(ctx: CommandContext<ServerCommandSource>): Int {
        val player = checkNotNull(ctx.source.player)

        // Попробуем найти подходящее место для спавна
        val targetPos = findDarkSpot(player.world, player.blockPos, player.pos, player.getRotationVec(1.0f), Random())

        if (targetPos != null) {
            val zombie: Entity = checkNotNull(EntityType.ZOMBIE.create(player.world))

            zombie.commandTags.add("infected")

            // Убедитесь, что зомби не заспавнится внутри блоков
            zombie.refreshPositionAndAngles(targetPos, 0.0f, 0.0f)
            player.world.spawnEntity(zombie)
        }
        return 1
    }

    private fun findDarkSpot(
        world: World,
        startPos: BlockPos,
        playerPos: Vec3d,
        playerDirection: Vec3d,
        random: Random
    ): BlockPos? {
        for (attempt in 0..9) { // Пытаемся несколько раз найти подходящее место
            val xOffset = random.nextInt(0, 20) - 10
            val yOffset = random.nextInt(0, 8) - 4 // В пределах ±4 блока по высоте
            val zOffset = random.nextInt(0, 20) - 10
            val pos = startPos.add(xOffset, yOffset, zOffset)

            // Проверяем, что позиция не находится прямо перед игроком и на достаточном расстоянии
            val posVec = Vec3d(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
            val dotProduct = posVec.subtract(playerPos).normalize().dotProduct(playerDirection.normalize())

            if (world.getLightLevel(pos) < 8 && !world.getBlockState(pos)
                    .isSolidBlock(world, pos) && dotProduct < -0.5 && posVec.distanceTo(playerPos) >= 2.0
            ) {
                return pos
            }
        }
        return null
    }
}