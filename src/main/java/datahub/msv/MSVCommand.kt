package datahub.msv

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import datahub.msv.sneeze.BlackSneeze
import datahub.msv.sneeze.NormalSneeze
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.EntitySelector
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object MSVCommand {
    fun register() {
        MSVReloaded.LOGGER.info("Initializing commands...")
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>?, _: CommandRegistryAccess?, _: CommandManager.RegistrationEnvironment? ->
            dispatcher?.register(
                LiteralArgumentBuilder.literal<ServerCommandSource>("msvcontrol")
                    .requires {
                        it.hasPermissionLevel(2)
                    }
                    .then(
                        LiteralArgumentBuilder.literal<ServerCommandSource>("sneeze")
                            .then(
                                LiteralArgumentBuilder.literal<ServerCommandSource>("black")
                                    .executes {
                                        BlackSneeze.spawn(it.source.player!!)
                                        Command.SINGLE_SUCCESS
                                    }
                                    .then(
                                        RequiredArgumentBuilder.argument<ServerCommandSource, EntitySelector>("target", EntityArgumentType.player())
                                            .executes {
                                                val player = EntityArgumentType.getPlayer(it, "target")
                                                BlackSneeze.spawn(player)
                                                Command.SINGLE_SUCCESS
                                            }
                                    )
                            )
                            .then(
                                LiteralArgumentBuilder.literal<ServerCommandSource>("normal")
                                    .executes {
                                        NormalSneeze.spawn(it.source.player!!)
                                        Command.SINGLE_SUCCESS
                                    }
                                    .then(
                                        RequiredArgumentBuilder.argument<ServerCommandSource, EntitySelector>("target", EntityArgumentType.player())
                                            .executes {
                                                val player = EntityArgumentType.getPlayer(it, "target")
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
                                    .executes {
                                        InfectedZombie.spawn(it.source.player!!)
                                        it.source.sendMessage(Text.literal("Attempted to summon new Infected zombie"))
                                        Command.SINGLE_SUCCESS
                                    }
                                    .then(
                                        RequiredArgumentBuilder.argument<ServerCommandSource, EntitySelector>("target", EntityArgumentType.player())
                                            .executes {
                                                val player = EntityArgumentType.getPlayer(it, "target")
                                                InfectedZombie.spawn(player)
                                                it.source.sendMessage(Text.literal("Attempted to summon new Infected zombie for ${player.name.string}"))
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
                                                            .executes {
                                                                val player = EntityArgumentType.getPlayer(it, "player")
                                                                val stage = IntegerArgumentType.getInteger(it, "stage")
                                                                if (MSVNBTData.getStage(player) == stage) {
                                                                    it.source.sendMessage(Text.literal("${player.name.string} is already at that stage!").withColor(16733525))
                                                                    Command.SINGLE_SUCCESS
                                                                } else {
                                                                    it.source.sendMessage(Text.literal("${player.name.string}`s stage is now set to $stage"))
                                                                    MSVNBTData.setStage(player, stage)
                                                                    Command.SINGLE_SUCCESS
                                                                }
                                                            }
                                                    )
                                            )
                                            .then(
                                                LiteralArgumentBuilder.literal<ServerCommandSource>("mutation")
                                                    .then(
                                                        RequiredArgumentBuilder.argument<ServerCommandSource, String>("mutation", StringArgumentType.word())
                                                            .suggests { _, builder ->
                                                                MSVFiles.mutationsList.plus("none").plus("random").forEach { builder.suggest(it) }
                                                                builder.buildFuture()
                                                            }
                                                            .executes {
                                                                val player = EntityArgumentType.getPlayer(it, "player")
                                                                val mutation = it.getArgument("mutation", String::class.java)
                                                                if (mutation == "random") {
                                                                    val randomMutation = MSVNBTData.getRandomMutation()
                                                                    it.source.sendMessage(Text.literal("${player.name.string}`s mutation is now set to $randomMutation"))
                                                                    MSVNBTData.setMutation(player, randomMutation)
                                                                    Command.SINGLE_SUCCESS
                                                                } else if (!MSVFiles.mutationsData.contains(mutation) && !mutation.equals("none")) {
                                                                    it.source.sendMessage(Text.literal("This mutation does not exist!").withColor(16733525))
                                                                    Command.SINGLE_SUCCESS
                                                                } else if (MSVNBTData.getMutation(player) == mutation) {
                                                                    it.source.sendMessage(Text.literal("${player.name.string} already has this mutation!").withColor(16733525))
                                                                    Command.SINGLE_SUCCESS
                                                                } else {
                                                                    it.source.sendMessage(Text.literal("${player.name.string}`s mutation is now set to $mutation"))
                                                                    MSVNBTData.setMutation(player, mutation)
                                                                    Command.SINGLE_SUCCESS
                                                                }
                                                            }
                                                    )
                                            )
                                            .then(
                                                LiteralArgumentBuilder.literal<ServerCommandSource>("gift")
                                                    .then(
                                                        RequiredArgumentBuilder.argument<ServerCommandSource, String>("gift", StringArgumentType.word())
                                                            .suggests { source, builder ->
                                                                MSVFiles.mutationsData[MSVNBTData.getMutation(source.source.player as PlayerEntity)]?.gifts?.plus("none")?.plus("random")?.forEach {builder.suggest(it)}
                                                                builder.buildFuture()
                                                            }
                                                            .executes {
                                                                val player = EntityArgumentType.getPlayer(it, "player")
                                                                val gift = it.getArgument("gift", String::class.java)
                                                                if (gift == "random") {
                                                                    val randomGift = MSVFiles.mutationsData[MSVNBTData.getMutation(player)]?.gifts?.random()
                                                                    if (randomGift == null) {
                                                                        it.source.sendMessage(Text.literal("There is no gifts for this mutation!").withColor(16733525))
                                                                        Command.SINGLE_SUCCESS
                                                                    } else {
                                                                        it.source.sendMessage(Text.literal("${player.name.string}`s gift is now set to $randomGift"))
                                                                        MSVNBTData.setGift(player, randomGift)
                                                                        Command.SINGLE_SUCCESS
                                                                    }
                                                                } else if (!MSVFiles.mutationsData[MSVNBTData.getMutation(player)]?.gifts?.contains(gift)!! && !gift.equals("none")) {
                                                                    it.source.sendMessage(Text.literal("This gift does not exist!").withColor(16733525))
                                                                    Command.SINGLE_SUCCESS
                                                                } else if (MSVNBTData.getGift(player) == gift) {
                                                                    it.source.sendMessage(Text.literal("${player.name.string} already has this gift!").withColor(16733525))
                                                                    Command.SINGLE_SUCCESS
                                                                } else {
                                                                    it.source.sendMessage(Text.literal("${player.name.string}`s gift is now set to $gift"))
                                                                    MSVNBTData.setGift(player, gift)
                                                                    Command.SINGLE_SUCCESS
                                                                }
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
                                                    .executes {
                                                        val mutation = StringArgumentType.getString(it, "mutation")
                                                        if (MSVFiles.mutationsList.contains(mutation)) {
                                                            it.source.sendMessage(Text.literal("This mutation already exists!").withColor(16733525))
                                                            Command.SINGLE_SUCCESS
                                                        } else {
                                                            MSVFiles.writeMutation(mutation, listOf(), 50)
                                                            it.source.sendMessage(Text.literal("Mutation added: $mutation"))
                                                            Command.SINGLE_SUCCESS
                                                        }
                                                    }
                                                    .then(
                                                        RequiredArgumentBuilder.argument<ServerCommandSource, Int>("weight", IntegerArgumentType.integer())
                                                            .executes {
                                                                val mutation = StringArgumentType.getString(it, "mutation")
                                                                val weight = IntegerArgumentType.getInteger(it, "weight")
                                                                if (MSVFiles.mutationsList.contains(mutation)) {
                                                                    it.source.sendMessage(Text.literal("This mutation already exists!").withColor(16733525))
                                                                    Command.SINGLE_SUCCESS
                                                                } else {
                                                                    MSVFiles.writeMutation(mutation, listOf(), weight)
                                                                    it.source.sendMessage(Text.literal("Mutation added: $mutation"))
                                                                    Command.SINGLE_SUCCESS
                                                                }
                                                            }
                                                    )
                                            )
                                    )
                                    .then(
                                        LiteralArgumentBuilder.literal<ServerCommandSource>("remove")
                                            .then(
                                                RequiredArgumentBuilder.argument<ServerCommandSource, String>("mutation", StringArgumentType.string())
                                                    .suggests { _, builder ->
                                                        MSVFiles.mutationsList.forEach { builder.suggest(it) }
                                                        builder.buildFuture()
                                                    }
                                                    .executes {
                                                        val mutation = StringArgumentType.getString(it, "mutation")
                                                        if (!MSVFiles.mutationsData.contains(mutation)) {
                                                            it.source.sendMessage(Text.literal("This mutation does not exists!").withColor(16733525))
                                                            Command.SINGLE_SUCCESS
                                                        } else {
                                                            MSVFiles.removeMutation(mutation)
                                                            it.source.sendMessage(Text.literal("Mutation removed: $mutation"))
                                                            Command.SINGLE_SUCCESS
                                                        }
                                                    }
                                            )
                                    )
                                    .then(LiteralArgumentBuilder.literal<ServerCommandSource>("default")
                                        .executes {
                                            if (MSVFiles.mutationsData == MSVFiles.initialMutations) {
                                                it.source.sendMessage(Text.literal("Mutations are already on default!").withColor(16733525))
                                                Command.SINGLE_SUCCESS
                                            } else {
                                                MSVFiles.writeOnlyDefault()
                                                it.source.sendMessage(Text.literal("Mutations has been set to default"))
                                                Command.SINGLE_SUCCESS
                                            }
                                        }
                                    )
                            )
                            .then(
                                LiteralArgumentBuilder.literal<ServerCommandSource>("gifts")
                                    .then(
                                        LiteralArgumentBuilder.literal<ServerCommandSource>("add")
                                            .then(
                                                RequiredArgumentBuilder.argument<ServerCommandSource, String>("mutation", StringArgumentType.string())
                                                    .suggests { _, builder ->
                                                        MSVFiles.mutationsList.forEach { builder.suggest(it) }
                                                        builder.buildFuture()
                                                    }
                                                    .then(
                                                        RequiredArgumentBuilder.argument<ServerCommandSource, String>("gift", StringArgumentType.string())
                                                            .executes {
                                                                val mutation = StringArgumentType.getString(it, "mutation")
                                                                val gift = StringArgumentType.getString(it, "gift")
                                                                if (!MSVFiles.mutationsData.contains(mutation)) {
                                                                    it.source.sendMessage(Text.literal("This mutation does not exists!").withColor(16733525))
                                                                    Command.SINGLE_SUCCESS
                                                                } else {
                                                                    val currentGifts = MSVFiles.mutationsData[mutation]?.gifts ?: listOf()
                                                                    if (currentGifts.contains(gift)) {
                                                                        it.source.sendMessage(Text.literal("This gift is already exist`s!").withColor(16733525))
                                                                        Command.SINGLE_SUCCESS
                                                                    } else {
                                                                        MSVFiles.writeMutation(mutation, currentGifts + gift, MSVFiles.mutationsData[mutation]?.weight!!)
                                                                        it.source.sendMessage(Text.literal("Gift $gift added to mutation: $mutation"))
                                                                        Command.SINGLE_SUCCESS
                                                                    }
                                                                }
                                                            }
                                                    )
                                            )
                                    )
                                    .then(
                                        LiteralArgumentBuilder.literal<ServerCommandSource>("remove")
                                            .then(
                                                RequiredArgumentBuilder.argument<ServerCommandSource, String>("mutation", StringArgumentType.string())
                                                    .suggests { _, builder ->
                                                        MSVFiles.mutationsList.forEach { builder.suggest(it) }
                                                        builder.buildFuture()
                                                    }
                                                    .then(
                                                        RequiredArgumentBuilder.argument<ServerCommandSource, String>("gift", StringArgumentType.string())
                                                            .suggests { source, builder ->
                                                                MSVFiles.mutationsData[StringArgumentType.getString(source, "mutation")]?.gifts?.forEach { builder.suggest(it) }
                                                                builder.buildFuture()
                                                            }
                                                            .executes {
                                                                val mutation = StringArgumentType.getString(it, "mutation")
                                                                val gift = StringArgumentType.getString(it, "gift")
                                                                if (!MSVFiles.mutationsData.contains(mutation)) {
                                                                    it.source.sendMessage(Text.literal("This mutation does not exists!").withColor(16733525))
                                                                    Command.SINGLE_SUCCESS
                                                                } else {
                                                                    val currentGifts = MSVFiles.mutationsData[mutation]?.gifts ?: listOf()
                                                                    if (!currentGifts.contains(gift)) {
                                                                        it.source.sendMessage(Text.literal("This gift is not added to this mutation!").withColor(16733525))
                                                                        Command.SINGLE_SUCCESS
                                                                    } else {
                                                                        MSVFiles.writeMutation(mutation, currentGifts - gift, MSVFiles.mutationsData[mutation]?.weight ?: 50)
                                                                        it.source.sendMessage(Text.literal("Gift removed from mutation: $mutation"))
                                                                        Command.SINGLE_SUCCESS
                                                                    }
                                                                }
                                                            }
                                                    )
                                            )
                                    )
                    ))
            )
        }
    )}
}