package datahub.msv

import com.mojang.brigadier.CommandDispatcher
import datahub.msv.Features.registerModFeatures
import datahub.msv.command.MSVCommand.command
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class Main : ModInitializer {
    override fun onInitialize() {
        registerModFeatures()
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>?, dedicated: CommandRegistryAccess?, environment: CommandManager.RegistrationEnvironment? ->
            command(
                dispatcher
            )
        })
        Registry.register(Registries.STATUS_EFFECT, id("infected"), InfectedStatusEffect())
        Registry.register(Registries.STATUS_EFFECT, id("cured"), CuredStatusEffect())
        BlackSneeze.init()
        LOGGER.info("Hello Fabric world!")
    }

    companion object {
        const val MOD_ID: String = "msv"
        val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

        @JvmStatic
		fun id(id: String?): Identifier? {
            return Identifier.of(MOD_ID, id)
        }
    }
}