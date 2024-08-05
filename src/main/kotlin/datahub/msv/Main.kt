package datahub.msv

import datahub.msv.Features.registerModFeatures
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Main : ModInitializer {
    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        registerModFeatures()
        LOGGER.info("Hello Fabric world!")
    }

    companion object {
        // This logger is used to write text to the console and the log file.
        // It is considered best practice to use your mod id as the logger's name.
        const val MOD_ID: String = "msv"
        val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

        @JvmStatic
		fun id(id: String?): Identifier {
            return Identifier.of(MOD_ID, id)
        }
    }
}