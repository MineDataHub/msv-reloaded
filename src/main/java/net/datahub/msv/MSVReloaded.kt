package net.datahub.msv

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils
import net.datahub.msv.MSVFiles.initFiles
import net.datahub.msv.sneeze.BlackSneeze.Companion.BLACK_SNEEZE
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MSVReloaded : ModInitializer {

    override fun onInitialize() {
        LOGGER.info("Booting...")
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted { server: MinecraftServer ->
            initFiles()
            ModDamage.registryDamage(server)
        })
        MSVCommand.register()
        PolymerEntityUtils.registerType(BLACK_SNEEZE)
        ModItems.register()
        LOGGER.info("Successfully infected your Minecraft!")
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger("MSV:Reloaded")

        @JvmStatic
		fun id(id: String?): Identifier? {
            return Identifier.of("msv", id)
        }
    }
}