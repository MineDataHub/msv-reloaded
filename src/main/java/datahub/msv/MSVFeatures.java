package datahub.msv;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.minecraft.component.DataComponentTypes.CUSTOM_DATA;

public class MSVFeatures {
    public static void registerModFeatures() {
        Main.LOGGER.info("Registering mod for" + Main.MOD_ID);
        MSVFeatures.waterDamageBurning();
        MSVFeatures.electrolysing();
        MSVFeatures.zombieEating();

    }

    public static void waterDamageBurning() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                BlockPos blockPos = BlockPos.ofFloored(player.getX(), player.getEyeY(), player.getZ());
                if (player.isWet() && player.getCommandTags().contains("hydrofob")) {
                    player.damage(NewDamage.createDamageSource(player.getWorld(), NewDamage.RAIN), 1.5F);
                }
                else if (player.getWorld().isDay() && player.getWorld().isSkyVisibleAllowingSea(blockPos) && player.getCommandTags().contains("vampire")) {
                    player.setFireTicks(20);
                }
            }
        });
    }

    public static void electrolysing() {
        EntityElytraEvents.ALLOW.register(entity -> {
            if (entity instanceof PlayerEntity player) {
                return !player.getCommandTags().contains("fallen");
            }
            return true;
        });
    }

    private static final Map<PlayerEntity, Long> lastUseTimes = new HashMap<>();
    private static final long COOLDOWN_PERIOD = 500; // Кулдаун в миллисекундах (0.5 секунды)

    public static void zombieEating() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof ZombieEntity) {
                long currentTime = System.currentTimeMillis();
                long lastUseTime = lastUseTimes.getOrDefault(player, 0L);

                if (currentTime - lastUseTime < COOLDOWN_PERIOD) {
                    return ActionResult.PASS;
                }

                if (!world.isClient && player.getHungerManager().isNotFull()) {
                    if (world instanceof ServerWorld) {
                        ((ServerWorld) world).spawnParticles(ParticleTypes.CRIMSON_SPORE, entity.getX(), entity.getY(), entity.getZ(), 5, 0.25, 0.5, 0.25, 0);
                    }
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
                    player.getHungerManager().add(3, 0.5f);
                    entity.kill();

                    // Обновляем время последнего использования
                    lastUseTimes.put(player, currentTime);

                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }

    public static boolean checkHazmat(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();

        for (int i = 3; i >= 0; i--) {
            ItemStack armorPiece = inventory.armor.get(i);
            ComponentMap nbt = armorPiece.getComponents();
            if (Objects.requireNonNull(nbt.get(CUSTOM_DATA)).contains("hazmat")) {
                return false;
            }
        }
        return true;
    }

    public static void zombieSpawn(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<ServerCommandSource>literal("spawnZombie")
                        .executes(SpawnZombieCommand::spawnZombie) // Используем метод ссылки
        );
    }
}