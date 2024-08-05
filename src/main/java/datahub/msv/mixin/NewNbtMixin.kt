package datahub.msv.mixin

import com.mojang.authlib.GameProfile
import datahub.msv.MsvNewNbtTags
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo


@Mixin(ServerPlayerEntity::class)
abstract class NewNbtMixin {
    @Unique
    var freeze_cooldown: Int = 0

    @Inject(method = ["writeCustomDataToNbt"], at = [At("TAIL")])
    fun writeCustomDataToNbt(nbt: NbtCompound, ci: CallbackInfo?) {
        nbt.putInt(MsvNewNbtTags.FREEZE_COOLDOWN, this.freeze_cooldown)
    }

    @Inject(method = ["readCustomDataFromNbt"], at = [At("TAIL")])
    fun readCustomDataFromNbt(nbt: NbtCompound, ci: CallbackInfo?) {
        this.freeze_cooldown = nbt.getInt(MsvNewNbtTags.FREEZE_COOLDOWN)
    }
}