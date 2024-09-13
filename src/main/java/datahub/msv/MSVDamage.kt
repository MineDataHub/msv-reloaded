package datahub.msv

import datahub.msv.Main.Companion.id
import net.minecraft.entity.damage.DamageEffects
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageType
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.World

object MSVDamage {
    val WATER: RegistryKey<DamageType> = register("water")
    val RAIN: RegistryKey<DamageType> = register("rain")
    val POTION: RegistryKey<DamageType> = register("potion")

    fun createDamageSource(world: World, damageTypeRegistryKey: RegistryKey<DamageType>?): DamageSource {
        return DamageSource(world.registryManager.get(RegistryKeys.DAMAGE_TYPE).entryOf(damageTypeRegistryKey))
    }

    private fun register(id: String): RegistryKey<DamageType> {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id(id))
    }
}
