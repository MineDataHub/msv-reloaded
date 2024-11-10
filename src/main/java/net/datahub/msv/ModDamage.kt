package net.datahub.msv

import net.datahub.msv.MSVReloaded.Companion.id
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageType
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.MinecraftServer

object ModDamage {
    private lateinit var waterDamage: DamageSource
    private lateinit var rainDamage: DamageSource
    private lateinit var potionDamage: DamageSource

    private val WATER: RegistryKey<DamageType> = register("water")
    private val RAIN: RegistryKey<DamageType> = register("rain")
    private val POTION: RegistryKey<DamageType> = register("potion")

    fun registryDamage(server: MinecraftServer) {
        waterDamage = DamageSource(server.registryManager.getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(WATER))
        rainDamage = DamageSource(server.registryManager.getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(RAIN))
        potionDamage = DamageSource(server.registryManager.getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(POTION))
    }

    private fun register(id: String): RegistryKey<DamageType> {
        MSVReloaded.LOGGER.info("Registering ${id}-damage...")
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id(id))
    }
    fun getWaterDamage(): DamageSource {
        return waterDamage
    }
    fun getRainDamage(): DamageSource {
        return rainDamage
    }
    fun getPotionDamage(): DamageSource {
        return potionDamage
    }
}
