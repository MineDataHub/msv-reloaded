package datahub.msv

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import datahub.msv.MSVPlayerData.MSV
import datahub.msv.sneeze.BlackSneeze
import datahub.msv.sneeze.NormalSneeze
import net.minecraft.command.CommandSource
import net.minecraft.command.EntitySelector
import net.minecraft.command.argument.ArgumentTypes
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.command.argument.EnumArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.nbt.NbtCompound
import kotlin.enums.enumEntries
import kotlin.reflect.typeOf

object MSVCommand : Command<CommandSource> {
    override fun run(context: CommandContext<CommandSource>): Int {
        throw AssertionError()
    }

    fun command(dispatcher: CommandDispatcher<ServerCommandSource?>) {
        dispatcher.register(
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
                )
                .then(
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
                .then(
                    LiteralArgumentBuilder.literal<ServerCommandSource>("edit")
                        .then(
                            RequiredArgumentBuilder.argument<ServerCommandSource, EntitySelector>("player", EntityArgumentType.player())
                                .then(
                                    LiteralArgumentBuilder.literal<ServerCommandSource>("stage")
                                        .then(
                                            RequiredArgumentBuilder.argument<ServerCommandSource, Int>("value", IntegerArgumentType.integer())
                                                .suggests { _, builder ->
                                                    listOf("0", "1", "2", "3", "4", "5").forEach { builder.suggest(it) }
                                                    builder.buildFuture()
                                                }
                                                .executes { ctx ->
                                                    val player = EntityArgumentType.getPlayer(ctx, "player")
                                                    val value = IntegerArgumentType.getInteger(ctx, "value")
                                                    setStageTag(player, value)
                                                }
                                        )
                                )
                                .then(
                                    LiteralArgumentBuilder.literal<ServerCommandSource>("mutation")
                                        .then(
                                            RequiredArgumentBuilder.argument<ServerCommandSource, String>("mutation", StringArgumentType.word())
                                                .suggests { _, builder ->
                                                    listOf("none", "hydrophobic", "fallen", "vampire", "ghoul").forEach { builder.suggest(it) }
                                                    builder.buildFuture()
                                                }
                                                .executes { ctx ->
                                                    val player = EntityArgumentType.getPlayer(ctx, "player")
                                                    val mutation = ctx.getArgument("mutation", String::class.java)
                                                    setMutationTag(player, mutation)
                                                }
                                        )
                                )
                        )
                )
        )
    }

    private fun setStageTag(player: ServerPlayerEntity, value: Int): Int {
        val nbt = player.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putInt(MSVPlayerData.STAGE, value)
        nbt.put(MSV, msv)
        player.readNbt(nbt)
        return Command.SINGLE_SUCCESS
    }
    private fun setMutationTag(player: ServerPlayerEntity, value: String?): Int {
        val nbt = player.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putString(MSVPlayerData.MUTATION, value)
        nbt.put(MSV, msv)
        player.readNbt(nbt)
        return Command.SINGLE_SUCCESS
    }
}
