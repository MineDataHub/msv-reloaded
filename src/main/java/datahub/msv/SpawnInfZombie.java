package datahub.msv;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class SpawnInfZombie {

    static int spawnZombie(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();

        assert player != null;

        // Попробуем найти подходящее место для спавна
        BlockPos targetPos = findDarkSpot(player.getWorld(), player.getBlockPos(), player.getPos(), player.getRotationVec(1.0F), new Random());

        if (targetPos != null) {
            Entity zombie = EntityType.ZOMBIE.create(player.getWorld());

            assert zombie != null;
            zombie.getCommandTags().add("infected");

            // Убедитесь, что зомби не заспавнится внутри блоков
            zombie.refreshPositionAndAngles(targetPos, 0.0F, 0.0F);
            player.getWorld().spawnEntity(zombie);

        }
        return 1;
    }

    private static BlockPos findDarkSpot(World world, BlockPos startPos, Vec3d playerPos, Vec3d playerDirection, Random random) {
        for (int attempt = 0; attempt < 10; attempt++) { // Пытаемся несколько раз найти подходящее место
            int xOffset = random.nextInt(0,20) - 10;
            int yOffset = random.nextInt(0,8) - 4; // В пределах ±4 блока по высоте
            int zOffset = random.nextInt(0,20) - 10;
            BlockPos pos = startPos.add(xOffset, yOffset, zOffset);

            // Проверяем, что позиция не находится прямо перед игроком и на достаточном расстоянии
            Vec3d posVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
            double dotProduct = posVec.subtract(playerPos).normalize().dotProduct(playerDirection.normalize());

            if (world.getLightLevel(pos) < 8 && !world.getBlockState(pos).isSolidBlock(world, pos) && dotProduct < -0.5 && posVec.distanceTo(playerPos) >= 2.0) {
                return pos;
            }
        }
        return null;
    }
}