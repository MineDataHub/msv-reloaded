package datahub.msv

import com.mojang.brigadier.Command
import net.minecraft.entity.EntityType
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.*

object InfectedZombie {
    fun spawn(player: PlayerEntity): Int {
        val targetPos = findDarkSpot(player.world, player.blockPos, player.pos, player.getRotationVec(1.0f), Random())

        if (targetPos != null) {
            val zombie = EntityType.ZOMBIE.create(player.world)!!.also { it.commandTags.add("infected") }

            zombie.refreshPositionAndAngles(targetPos, 0.0f, 0.0f)
            player.world.spawnEntity(zombie)
        }
        return Command.SINGLE_SUCCESS
    }

    private fun findDarkSpot(
        world: World,
        startPos: BlockPos,
        playerPos: Vec3d,
        playerDirection: Vec3d,
        random: Random
    ): BlockPos? {
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
}