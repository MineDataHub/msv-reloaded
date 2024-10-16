package net.datahub.msv

import net.datahub.msv.sneeze.BlackSneeze.Companion.BLACK_SNEEZE
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MSVReloaded : ModInitializer {

    override fun onInitialize() {
        Features.register()
        MSVFiles.register()
        MSVCommand.register()
        PolymerEntityUtils.registerType(BLACK_SNEEZE)
        MSVStatusEffects.register()
        MSVItems.register()
        LOGGER.info("Successfully infected your Minecraft!")
    }

    companion object {
        private const val MOD_ID: String = "msv"
        val LOGGER: Logger = LoggerFactory.getLogger("MSV:Reloaded")

        @JvmStatic
		fun id(id: String?): Identifier? {
            return Identifier.of(MOD_ID, id)
        }
    }
}