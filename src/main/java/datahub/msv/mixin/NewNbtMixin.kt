package datahub.msv.mixin

import datahub.msv.MSVNbtTags
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
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
        nbt.putInt(MSVNbtTags.FREEZE_COOLDOWN, this.freeze_cooldown)
    }

    @Inject(method = ["readCustomDataFromNbt"], at = [At("TAIL")])
    fun readCustomDataFromNbt(nbt: NbtCompound, ci: CallbackInfo?) {
        this.freeze_cooldown = nbt.getInt(MSVNbtTags.FREEZE_COOLDOWN)
    }
}