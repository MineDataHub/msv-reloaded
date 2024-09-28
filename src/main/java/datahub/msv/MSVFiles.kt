package datahub.msv

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minecraft.server.MinecraftServer
import net.minecraft.util.WorldSavePath
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object MSVFiles {

    private lateinit var worldDir: Path
    private val msvDir: Path
        get() = worldDir.resolve("MSV") // Создание директории при обращении
    private val mutationsFile: File
        get() = msvDir.resolve("mutations.json").toFile() // Файл создается только после инициализации worldDir

    fun register(server: MinecraftServer) {
        // Инициализация worldDir на основе пути сохранения мира
        worldDir = server.getSavePath(WorldSavePath.ROOT)

        // Проверяем, существует ли папка, если нет — создаем
        if (!Files.exists(msvDir)) {
            Files.createDirectories(msvDir)
        }

        // Проверяем, существует ли файл, если нет — создаем и записываем начальные данные
        if (!mutationsFile.exists()) {
            val initialMutations = mapOf(
                "hydrophobic" to 50,
                "ghoul" to 20,
                "fallen" to 30,
                "vampire" to 40
            )
            mutationsFile.writeText(Gson().toJson(initialMutations))
        }
    }

    val mutationsData: Map<String, Int>
        get() = if (mutationsFile.exists()) {
            val type = object : TypeToken<Map<String, Int>>() {}.type
            Gson().fromJson(mutationsFile.readText(), type)
        } else {
            emptyMap()
        }

    val mutationsList: List<String>
        get() = mutationsData.keys.toList()

    fun writeMutation(mutation: String, value: Int) {
        if (!mutationsData.containsKey(mutation)) {
            val newData = mutationsData + (mutation to value)
            mutationsFile.writeText(Gson().toJson(newData))
        }
    }

    fun removeMutation(mutation: String) {
        if (mutationsData.containsKey(mutation)) {
            val newData = mutationsData - mutation
            mutationsFile.writeText(Gson().toJson(newData))
        }
    }
}