package datahub.msv

import datahub.msv.Features.registerModFeatures
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Main : ModInitializer {
    override fun onInitialize() {
        registerModFeatures()
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