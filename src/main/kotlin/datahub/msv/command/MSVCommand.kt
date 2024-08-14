package datahub.msv.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import datahub.msv.Features.spawnBlackSneeze
import net.minecraft.command.CommandSource
import net.minecraft.server.command.ServerCommandSource

object MSVCommand : Command<CommandSource> {
    override fun run(context: CommandContext<CommandSource>): Int {
        // This method should not be implemented, as we're using the Brigadier command system
        throw AssertionError()
    }
    fun command(dispatcher: CommandDispatcher<ServerCommandSource?>?) {

        dispatcher?.register(
            LiteralArgumentBuilder.literal<ServerCommandSource>("msv")
                .then(
                    LiteralArgumentBuilder.literal<ServerCommandSource>("sneeze")
                        .then(
                            LiteralArgumentBuilder.literal<ServerCommandSource>("black")
                                .executes { ctx ->
                                    spawnBlackSneeze(ctx.source.world, ctx.source.player!!.blockPos)
                                }
                        )
                )
        )
    }
}