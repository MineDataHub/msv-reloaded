package datahub.msv.mixin;

import datahub.msv.MSVPlayerData;
import kotlin.random.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
public abstract class InfectedMobsMixin extends MobEntity {
    protected InfectedMobsMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    int tickCounter = 0;
    @Unique
    int CDPlayer = 0;
    @Unique
    int CDAnimal = 0;
    @Inject(method = "mobTick", at = @At("TAIL"))
    public void looking(CallbackInfo ci) {
        if (MSVPlayerData.INSTANCE.isInfected(this)) {
            if (tickCounter > 0) tickCounter--;
            if (CDPlayer > 0) CDPlayer--;
            if (CDAnimal > 0) CDAnimal--;

            PlayerEntity player = this.getWorld().getClosestPlayer(this.getX(), this.getY(), this.getZ(), 15.0, true);
            if (player != null) {

                if (tickCounter == 400 && Random.Default.nextBoolean()) {
                    if (MSVPlayerData.INSTANCE.getStage(this) == 0) {
                        this.lookAtEntity(player, 180, 180);
                    }
                }

                if (this.distanceTo(player) <= 2.0F && CDPlayer == 0) {
                    this.lookAtEntity(player, 180, 180);
                    if (Random.Default.nextBoolean()) {
                        player.damage(new DamageSource(this.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.MOB_ATTACK), this), 2.0F);
                        CDPlayer = Random.Default.nextInt(500, 800);
                    }
                }

                if (tickCounter == 0 && Random.Default.nextBoolean()) {
                    this.getNavigation().startMovingTo(player.getX(), player.getY(), player.getZ(), 1, 1.4);
                    this.lookAtEntity(player, 180, 180);
                    tickCounter = Random.Default.nextInt(700, 900);
                }
            }

            if (CDAnimal == 0) {
                for (AnimalEntity animalEntity : this.getWorld().getEntitiesByClass(AnimalEntity.class, this.getBoundingBox().expand(2.0), animalEntity -> true)) {
                    if (!MSVPlayerData.INSTANCE.isInfected(animalEntity)) {
                        if (Random.Default.nextBoolean()) {
                            animalEntity.damage(new DamageSource(this.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.MOB_ATTACK), this), 2.0F);
                            MSVPlayerData.INSTANCE.setInfected(animalEntity, true);
                        }
                        CDAnimal = Random.Default.nextInt(2200, 2600);
                        break;
                    }
                }
            }
        }
    }
}