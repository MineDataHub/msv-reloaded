package datahub.msv

import datahub.msv.Features.registerModFeatures
import eu.pb4.polymer.core.api.other.PolymerStatusEffect
import eu.pb4.polymer.core.api.utils.PolymerRegistry
import eu.pb4.polymer.core.impl.PolymerMod
import net.fabricmc.api.ModInitializer
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class Main : ModInitializer {
    override fun onInitialize() {
        registerModFeatures()
        Registry.register(Registries.STATUS_EFFECT, id("infected"), InfectedStatusEffect())
        Registry.register(Registries.STATUS_EFFECT, id("cured"), CuredStatusEffect())
        LOGGER.info("Hello Fabric world!")
    }

    companion object {
        const val MOD_ID: String = "msv"
        val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

        @JvmStatic
		fun id(id: String?): Identifier {
            return Identifier.of(MOD_ID, id)
        }
    }
}