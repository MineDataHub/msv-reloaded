package datahub.msv

import com.google.gson.Gson
import net.minecraft.server.MinecraftServer
import net.minecraft.util.WorldSavePath
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object MSVFiles {

    private lateinit var worldDir: Path
    private val msvDir: Path
        get() = worldDir.resolve("msv") // Создание директории при обращении
    private val mutationsFile: File
        get() = msvDir.resolve("mutations.json").toFile() // Файл создается только после инициализации worldDir

    fun register(server: MinecraftServer) {
        // Инициализация worldDir на основе пути сохранения мира
        worldDir = server.getSavePath(WorldSavePath.ROOT)

        // Проверяем, существует ли папка, если нет — создаем
        if (!Files.exists(msvDir)) {
            Files.createDirectories(msvDir)
        }

        // Проверяем, существует ли файл, если нет — создаем и записываем массив строк
        if (!mutationsFile.exists()) {
            val initialMutations = listOf("hydrophobic", "ghoul", "fallen", "vampire")
            mutationsFile.writeText(Gson().toJson(initialMutations))
        }
    }

    private fun readMutationsData(): List<String> {
        if (!mutationsFile.exists()) {
            return emptyList()
        }
        return Gson().fromJson(mutationsFile.readText(), Array<String>::class.java).toList()
    }

    val mutationsData: List<String>
        get() = readMutationsData()

    fun writeMutation(mutation: String) {
        if (!mutationsData.contains(mutation)) {
            val newData = mutationsData + mutation
            mutationsFile.writeText(Gson().toJson(newData))
        }
    }

    fun removeMutation(mutation: String) {
        if (mutationsData.contains(mutation)) {
            val newData = mutationsData - mutation
            mutationsFile.writeText(Gson().toJson(newData))
        }
    }
}
