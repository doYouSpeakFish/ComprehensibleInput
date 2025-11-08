package input.comprehensible.storygen.util

import java.io.File
import java.io.FileInputStream
import java.util.Properties

object LocalProperties {
    fun readProperty(name: String): String {
        val file = File("local.properties")
        if (!file.exists()) {
            error("local.properties file is required to run the story generator")
        }
        val properties = Properties()
        FileInputStream(file).use { stream ->
            properties.load(stream)
        }
        return properties.getProperty(name)
            ?.takeIf { it.isNotBlank() }
            ?: error("Property '$name' was not found in local.properties")
    }
}
