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
        ServerCommandSource source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player != null) {
            World world = player.getWorld(); // Используем getWorld()
            Vec3d playerPos = player.getPos();
            Random random = new Random();
            BlockPos playerBlockPos = player.getBlockPos();

            // Попробуем найти подходящее место для спавна
            BlockPos targetPos = findDarkSpot(world, playerBlockPos, playerPos, player.getRotationVec(1.0F), random);

            if (targetPos != null) {
                Entity zombie = EntityType.ZOMBIE.create(world); // Используем getWorld()
                if (zombie != null) {
                    // Добавление командного тега "infected"
                    zombie.getCommandTags().add("infected");

                    // Убедитесь, что зомби не заспавнится внутри блоков
                    zombie.refreshPositionAndAngles(targetPos, 0.0F, 0.0F);
                    world.spawnEntity(zombie); // Используем getWorld()
                }
            }
        }

        return 1;
    }

    private static BlockPos findDarkSpot(World world, BlockPos startPos, Vec3d playerPos, Vec3d playerDirection, Random random) {
        // Ищем подходящую позицию в радиусе 10 блоков от начальной позиции
        for (int attempt = 0; attempt < 100; attempt++) { // Пытаемся несколько раз найти подходящее место
            int xOffset = random.nextInt(21) - 10;
            int yOffset = random.nextInt(9) - 4; // В пределах ±4 блока по высоте
            int zOffset = random.nextInt(21) - 10;
            BlockPos pos = startPos.add(xOffset, yOffset, zOffset);

            // Проверяем, что позиция не находится прямо перед игроком и на достаточном расстоянии
            Vec3d posVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
            Vec3d directionToPos = posVec.subtract(playerPos).normalize();
            Vec3d normalizedPlayerDirection = playerDirection.normalize();
            double dotProduct = directionToPos.dotProduct(normalizedPlayerDirection);

            double distance = posVec.distanceTo(playerPos);

            if (isDark(world, pos) && !world.getBlockState(pos).isSolidBlock(world, pos) && dotProduct < -0.5 && distance >= 2.0) {
                return pos;
            }
        }
        return null;
    }

    private static boolean isDark(World world, BlockPos pos) {
        // Проверка, что место тёмное
        return world.getLightLevel(pos) < 8;
    }
}










