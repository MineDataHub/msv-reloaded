package datahub.msv

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import datahub.msv.sneeze.BlackSneeze
import datahub.msv.sneeze.NormalSneeze
import net.minecraft.command.CommandSource
import net.minecraft.command.EntitySelector
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.ServerCommandSource

object MSVCommand : Command<CommandSource> {
    override fun run(context: CommandContext<CommandSource>): Int {
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
                                    BlackSneeze.spawn(ctx.source.player!!)
                                }
                                .then(
                                    RequiredArgumentBuilder.argument<ServerCommandSource, EntitySelector>("target", EntityArgumentType.player())
                                        .executes { ctx ->
                                            val player = EntityArgumentType.getPlayer(ctx, "target")
                                            BlackSneeze.spawn(player)
                                        }
                                )
                        )
                        .then(
                            LiteralArgumentBuilder.literal<ServerCommandSource>("normal")
                                .executes { ctx ->
                                    NormalSneeze.spawn(ctx.source.player!!)
                                }
                                .then(
                                    RequiredArgumentBuilder.argument<ServerCommandSource, EntitySelector>("target", EntityArgumentType.player())
                                        .executes { ctx ->
                                            val player = EntityArgumentType.getPlayer(ctx, "target")
                                            NormalSneeze.spawn(player)
                                        }
                                )
                        )
                ).then(
                    LiteralArgumentBuilder.literal<ServerCommandSource>("spawn")
                        .then(
                            LiteralArgumentBuilder.literal<ServerCommandSource>("zombie")
                                .executes { ctx ->
                                    InfectedZombie.spawn(ctx.source.player!!)
                                }
                                .then(
                                    RequiredArgumentBuilder.argument<ServerCommandSource, EntitySelector>("target", EntityArgumentType.player())
                                        .executes { ctx ->
                                            val player = EntityArgumentType.getPlayer(ctx, "target")
                                            InfectedZombie.spawn(player)
                                        }
                                )
                        )
                )
        )
    }
}