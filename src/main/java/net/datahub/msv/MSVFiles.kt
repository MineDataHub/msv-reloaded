package net.datahub.msv

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.datahub.msv.constant.Gifts
import net.datahub.msv.constant.Mutations
import net.minecraft.server.MinecraftServer
import net.minecraft.util.WorldSavePath
import net.minecraft.world.World
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object MSVFiles {
    data class Mutation(val gifts: List<String>, val weight: Int)

    private lateinit var worldDir: Path
    private val msvDir: Path
        get() = worldDir.resolve("MSV")
    private val mutationsFile: File
        get() = msvDir.resolve("mutations.json").toFile()

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    val initialMutations = mapOf(
        Mutations.HYDROPHOBIC to Mutation(listOf(Gifts.NO_FIRE_DAMAGE, Gifts.PHOENIX), 50),
        Mutations.GHOUL to Mutation(listOf(Gifts.ZOMBIE_EATER, "ghoul2"), 50),
        Mutations.FALLEN to Mutation(listOf(Gifts.NO_FALL_DAMAGE, "fallen2"), 50),
        Mutations.VAMPIRE to Mutation(listOf(Gifts.UNDEAD, "vampire2"), 50)
    )

    fun initFiles() {
        worldDir = Path.of(WorldSavePath.ROOT.relativePath)

        if (!Files.exists(msvDir)) {
            Files.createDirectories(msvDir)
            MSVReloaded.LOGGER.info("Created MSV directory for the world!")
        }

        if (!mutationsFile.exists()) {
            mutationsFile.writeText(gson.toJson(initialMutations))
            MSVReloaded.LOGGER.info("Created mutations.json for the world!")
        }
    }

    val mutationsData: Map<String, Mutation>
        get() = if (mutationsFile.exists()) {
            val type = object : TypeToken<Map<String, Mutation>>() {}.type
            Gson().fromJson(mutationsFile.readText(), type)
        } else {
            emptyMap()
        }

    val mutationsList: List<String>
        get() = mutationsData.keys.toList()

    fun writeMutation(mutation: String, gifts: List<String>, weight: Int) {
        if (!Files.exists(msvDir)) {
            Files.createDirectories(msvDir)
        }
        if (mutationsData.containsKey(mutation)) {
            updateMutation(mutation, gifts, weight)
        } else {
            val newData = mutationsData + (mutation to Mutation(gifts, weight))
            mutationsFile.writeText(gson.toJson(newData))
        }
    }

    private fun updateMutation(mutation: String, gifts: List<String>, weight: Int) {
        if (mutationsData.containsKey(mutation)) {
            val newData = (mutationsData - mutation) + (mutation to Mutation(gifts, weight))
            mutationsFile.writeText(gson.toJson(newData))
        }
    }

    fun removeMutation(mutation: String) {
        if (mutationsData.containsKey(mutation)) {
            val newData = mutationsData - mutation
            mutationsFile.writeText(gson.toJson(newData))
        }
    }

    fun setDefault() {
        if (!Files.exists(msvDir)) {
            Files.createDirectories(msvDir)
        }
        mutationsFile.writeText(gson.toJson(initialMutations))
    }
}