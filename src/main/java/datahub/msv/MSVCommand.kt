package datahub.msv

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
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
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

object MSVCommand : Command<CommandSource> {
    override fun run(context: CommandContext<CommandSource>): Int {
        throw AssertionError()
    }

    fun command(dispatcher: CommandDispatcher<ServerCommandSource?>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<ServerCommandSource>("msv")
                .requires {ctx ->
                    ctx.hasPermissionLevel(2)
                }
                .then(
                    LiteralArgumentBuilder.literal<ServerCommandSource>("sneeze")
                        .then(
                            LiteralArgumentBuilder.literal<ServerCommandSource>("black")
                                .executes { ctx ->
                                    BlackSneeze.spawn(ctx.source.player!!)
                                    Command.SINGLE_SUCCESS
                                }
                                .then(
                                    RequiredArgumentBuilder.argument<ServerCommandSource, EntitySelector>("target", EntityArgumentType.player())
                                        .executes { ctx ->
                                            val player = EntityArgumentType.getPlayer(ctx, "target")
                                            BlackSneeze.spawn(player)
                                            Command.SINGLE_SUCCESS
                                        }
                                )
                        )
                        .then(
                            LiteralArgumentBuilder.literal<ServerCommandSource>("normal")
                                .executes { ctx ->
                                    NormalSneeze.spawn(ctx.source.player!!)
                                    Command.SINGLE_SUCCESS
                                }
                                .then(
                                    RequiredArgumentBuilder.argument<ServerCommandSource, EntitySelector>("target", EntityArgumentType.player())
                                        .executes { ctx ->
                                            val player = EntityArgumentType.getPlayer(ctx, "target")
                                            NormalSneeze.spawn(player)
                                            Command.SINGLE_SUCCESS
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
                                    Command.SINGLE_SUCCESS
                                }
                                .then(
                                    RequiredArgumentBuilder.argument<ServerCommandSource, EntitySelector>("target", EntityArgumentType.player())
                                        .executes { ctx ->
                                            val player = EntityArgumentType.getPlayer(ctx, "target")
                                            InfectedZombie.spawn(player)
                                            Command.SINGLE_SUCCESS
                                        }
                                )
                        )
                )
                .then(
                    LiteralArgumentBuilder.literal<ServerCommandSource>("edit")
                        .then(
                            LiteralArgumentBuilder.literal<ServerCommandSource>("players")
                                .then(
                                    RequiredArgumentBuilder.argument<ServerCommandSource, EntitySelector>("player", EntityArgumentType.player())
                                        .then(
                                            LiteralArgumentBuilder.literal<ServerCommandSource>("stage")
                                                .then(
                                                    RequiredArgumentBuilder.argument<ServerCommandSource, Int>("stage", IntegerArgumentType.integer())
                                                        .suggests { _, builder ->
                                                            listOf(0, 1, 2, 3, 4, 5).forEach { builder.suggest(it) }
                                                            builder.buildFuture()
                                                        }
                                                        .executes { ctx ->
                                                            val player = EntityArgumentType.getPlayer(ctx, "player")
                                                            val stage = IntegerArgumentType.getInteger(ctx, "stage")
                                                            if (MSVPlayerData.readInt(player, MSVPlayerData.STAGE) == stage) {
                                                                ctx.source.sendMessage(Text.literal("The player is already at that stage!").withColor(16733525))
                                                                Command.SINGLE_SUCCESS
                                                            }
                                                            ctx.source.sendMessage(Text.literal("Player`s stage is now set to $stage."))
                                                            setStage(player, stage)
                                                            Command.SINGLE_SUCCESS
                                                        }
                                                )
                                        )
                                        .then(
                                            LiteralArgumentBuilder.literal<ServerCommandSource>("mutation")
                                                .then(
                                                    RequiredArgumentBuilder.argument<ServerCommandSource, String>("mutation", StringArgumentType.word())
                                                        .suggests { _, builder ->
                                                            MSVFiles.mutationsData.plus("none").forEach { builder.suggest(it) }
                                                            builder.buildFuture()
                                                        }
                                                        .executes { ctx ->
                                                            val player = EntityArgumentType.getPlayer(ctx, "player")
                                                            val mutation = ctx.getArgument("mutation", String::class.java)
                                                            if (!MSVFiles.mutationsData.contains(mutation)) {
                                                                ctx.source.sendMessage(Text.literal("This mutation does not exist!").withColor(16733525))
                                                                Command.SINGLE_SUCCESS
                                                            }
                                                            if (MSVPlayerData.readStr(player, MSVPlayerData.MUTATION) == mutation) {
                                                                ctx.source.sendMessage(Text.literal("The player already has this mutation!").withColor(16733525))
                                                                Command.SINGLE_SUCCESS
                                                            }
                                                            ctx.source.sendMessage(Text.literal("Player`s mutation is now set to $mutation."))
                                                            setMutation(player, mutation)
                                                            Command.SINGLE_SUCCESS
                                                        }
                                                )
                                        )
                                )
                        )
                        .then(
                            LiteralArgumentBuilder.literal<ServerCommandSource>("mutations")
                                .then(
                                    LiteralArgumentBuilder.literal<ServerCommandSource>("add")
                                        .then(
                                            RequiredArgumentBuilder.argument<ServerCommandSource, String>("mutation", StringArgumentType.string())
                                                .executes { ctx ->
                                                    val mutation = StringArgumentType.getString(ctx, "mutation")
                                                    if (MSVFiles.mutationsData.contains(mutation)) {
                                                        ctx.source.sendMessage(Text.literal("This mutation already exists!").withColor(16733525))
                                                        Command.SINGLE_SUCCESS
                                                    }
                                                    MSVFiles.writeMutation(mutation)
                                                    ctx.source.sendMessage(Text.literal("Mutation added: $mutation"))
                                                    Command.SINGLE_SUCCESS
                                                }
                                        )
                                )
                                .then(
                                    LiteralArgumentBuilder.literal<ServerCommandSource>("remove")
                                        .then(
                                            RequiredArgumentBuilder.argument<ServerCommandSource, String>("mutation", StringArgumentType.string())
                                                .suggests { _, builder ->
                                                    MSVFiles.mutationsData.forEach { builder.suggest(it) }
                                                    builder.buildFuture()
                                                }
                                                .executes { ctx ->
                                                    val mutation = StringArgumentType.getString(ctx, "mutation")
                                                    if (!MSVFiles.mutationsData.contains(mutation)) {
                                                        ctx.source.sendMessage(Text.literal("This mutation does not exists!").withColor(16733525))
                                                        Command.SINGLE_SUCCESS
                                                    }
                                                    MSVFiles.removeMutation(mutation)
                                                    ctx.source.sendMessage(Text.literal("Mutation removed: $mutation"))
                                                    Command.SINGLE_SUCCESS
                                                }
                                        )
                                )
                        )
                )
        )
    }

    private fun setStage(player: ServerPlayerEntity, value: Int) {
        val nbt = player.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putInt(MSVPlayerData.STAGE, value)
        nbt.put(MSV, msv)
        player.readNbt(nbt)
    }
    private fun setMutation(player: ServerPlayerEntity, value: String?) {
        val nbt = player.writeNbt(NbtCompound())
        val msv = nbt.getCompound(MSV)
        msv.putString(MSVPlayerData.MUTATION, value)
        nbt.put(MSV, msv)
        player.readNbt(nbt)
    }
}