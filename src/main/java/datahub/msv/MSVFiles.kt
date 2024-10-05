package datahub.msv

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.server.MinecraftServer
import net.minecraft.util.WorldSavePath
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object MSVFiles {
    data class Mutation(val gifts: List<String>, val weight: Int)

    private lateinit var worldDir: Path
    private val msvDir: Path
        get() = worldDir.resolve("MSV") // Создание директории при обращении
    private val mutationsFile: File
        get() = msvDir.resolve("mutations.json").toFile() // Файл создается только после инициализации worldDir

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    val initialMutations = mapOf(
        "hydrophobic" to Mutation(listOf("hydrophobic1", "hydrophobic2"), 50),
        "ghoul" to Mutation(listOf("ghoul1", "ghoul2"), 50),
        "fallen" to Mutation(listOf("fallen1", "fallen2"), 50),
        "vampire" to Mutation(listOf("vampire1", "vampire2"), 50)
    )

    fun register(server: MinecraftServer) {
        // Инициализация worldDir на основе пути сохранения мира
        worldDir = server.getSavePath(WorldSavePath.ROOT)

        // Проверяем, существует ли папка, если нет — создаем
        if (!Files.exists(msvDir)) {
            Files.createDirectories(msvDir)
        }

        // Проверяем, существует ли файл, если нет — создаем и записываем начальные данные
        if (!mutationsFile.exists()) {
            mutationsFile.writeText(gson.toJson(initialMutations))
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
        if (!mutationsData.containsKey(mutation)) {
            val newData = mutationsData + (mutation to Mutation(gifts, weight))
            mutationsFile.writeText(gson.toJson(newData))
        }
    }

    fun removeMutation(mutation: String) {
        if (mutationsData.containsKey(mutation)) {
            val newData = mutationsData - mutation
            mutationsFile.writeText(gson.toJson(newData))
        }
    }

    fun writeOnlyDefault() {
        if (!Files.exists(msvDir)) {
            Files.createDirectories(msvDir)
        }
        mutationsFile.writeText(gson.toJson(initialMutations))
    }
}