package datahub.msv.mixin;

import datahub.msv.MSVPlayerData;
import kotlin.random.Random;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalEntity.class)
public abstract class InfectedMobsMixin {
    @Inject(method = "canEat", at = @At("HEAD"), cancellable = true)
    private void forbidEating(CallbackInfoReturnable<Boolean> cir) {
        AnimalEntity entity = (AnimalEntity) (Object) this;
        if (MSVPlayerData.INSTANCE.readBool(entity, MSVPlayerData.INFECTED)) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    int tickCounter = 0;
    @Unique
    int CDPlayer = 0;
    @Unique
    int CDAnimal = 0;
    @Inject(method = "mobTick", at = @At("TAIL"))
    public void looking(CallbackInfo ci) {
        MobEntity entity = (MobEntity) (Object) this;
        if (MSVPlayerData.INSTANCE.readBool(entity, MSVPlayerData.INFECTED)) {
            if (tickCounter > 0) tickCounter--;
            if (CDPlayer > 0) CDPlayer--;
            if (CDAnimal > 0) CDAnimal--;

            PlayerEntity player = entity.getWorld().getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), 15.0, true);
            if (player != null) {

                if (tickCounter == 400 && Random.Default.nextBoolean()) {
                    if (MSVPlayerData.INSTANCE.readInt(entity, MSVPlayerData.STAGE) == 0) {
                        entity.lookAtEntity(player, 180, 180);
                    }
                }

                if (entity.distanceTo(player) <= 2.0F && CDPlayer == 0) {
                    entity.lookAtEntity(player, 180, 180);
                    if (Random.Default.nextBoolean()) {
                        player.damage(new DamageSource(entity.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.MOB_ATTACK), entity), 2.0F);
                        CDPlayer = Random.Default.nextInt(500, 800);
                    }
                }

                if (CDAnimal == 0) {
                    for (AnimalEntity animalEntity : entity.getWorld().getEntitiesByClass(AnimalEntity.class, entity.getBoundingBox().expand(2.0), animalEntity -> !MSVPlayerData.INSTANCE.readBool(animalEntity, MSVPlayerData.INFECTED))) {
                        if (Random.Default.nextBoolean()) {
                            animalEntity.damage(new DamageSource(entity.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.MOB_ATTACK), entity), 2.0F);
                            MSVPlayerData.INSTANCE.setInfected(animalEntity, true);
                            CDAnimal = Random.Default.nextInt(2200, 2600);
                        }
                        break;
                    }
                }

                if (tickCounter == 0 && Random.Default.nextBoolean()) {
                    entity.getNavigation().startMovingTo(player.getX(), player.getY(), player.getZ(), 1, 1.4);
                    entity.lookAtEntity(player, 180, 180);
                    tickCounter = Random.Default.nextInt(700, 900);
                }
            }
        }
    }
}