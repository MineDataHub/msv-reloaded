package datahub.msv

import datahub.msv.sneeze.BlackSneeze.Companion.BLACK_SNEEZE
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.MinecraftServer
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
        LOGGER.info("Successfully initialised MSV:Reloaded!")
    }

    companion object {
        private const val MOD_ID: String = "msv"
        val LOGGER: Logger = LoggerFactory.getLogger("MSV:Reloaded")

        @JvmStatic
		fun id(id: String?): Identifier? {
            return Identifier.of(MOD_ID, id)
        }

        const val MSV: String = "MSV"
        const val MUTATION: String = "Mutation"
        const val GIFT: String = "Gift"
        const val FREEZE_COOLDOWN: String = "FreezeCooldown"
        const val SNEEZE_COOLDOWN: String = "SneezeCooldown"
        const val HALLUCINATION_COOLDOWN: String = "HallucinationCooldown"
        const val STAGE: String = "Stage"
        const val INFECTION: String = "Infection"

        const val INFECTED: String = "Infected"
    }
}