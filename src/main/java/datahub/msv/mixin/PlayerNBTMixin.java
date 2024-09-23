package datahub.msv.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static datahub.msv.MSVPlayerData.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerNBTMixin extends LivingEntity {
    @Unique
    private String mutation = "none";
    @Unique
    private Integer stage = 0;
    @Unique
    private Integer sneezeCooldown = 0;
    @Unique
    private Integer creeperSoundCooldown = 0;
    @Unique
    private Integer timeForUpStage = 0;

    protected PlayerNBTMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    protected void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound msv = new NbtCompound();

        nbt.put(MSV, msv);
        msv.putString(MUTATION, mutation);
        msv.putInt(STAGE, stage);
        msv.putInt(SNEEZE_COOLDOWN, sneezeCooldown);
        msv.putInt(CREEPER_SOUND_COOLDOWN, creeperSoundCooldown);
        msv.putInt(TIME_FOR_UP_STAGE, timeForUpStage);

    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    protected void readNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound msv = nbt.getCompound(MSV);

        mutation = msv.getString(MUTATION);
        stage = msv.getInt(STAGE);
        sneezeCooldown = msv.getInt(SNEEZE_COOLDOWN);
        creeperSoundCooldown = msv.getInt(CREEPER_SOUND_COOLDOWN);
        timeForUpStage = msv.getInt(TIME_FOR_UP_STAGE);
    }
}