package org.jlleitschuh.sandbox

import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files

class KotlinTempDirectoryPermissionCheck {
    @Test
    fun `kotlin check default directory permissions`() {
        val dir = createTempDir()
        runLS(dir.parentFile, dir) // Prints drwxr-xr-x
    }

    @Test
    fun `Files check default directory permissions`() {
        val dir = Files.createTempDirectory("random-directory")
        runLS(dir.toFile().parentFile, dir.toFile()) // Prints drwx------
    }

    @Test
    fun `kotlin check default file permissions`() {
        val file = createTempFile()
        runLS(file.parentFile, file) // Prints -rw-r--r--
    }

    @Test
    fun `Files check default file permissions`() {
        val file = Files.createTempFile("random-file", ".txt")
        runLS(file.toFile().parentFile, file.toFile()) // Prints -rw-------
    }

    private fun runLS(file: File, lookingFor: File) {
        val processBuilder = ProcessBuilder()
        processBuilder.command("ls", "-l", file.absolutePath)
        try {
            val process = processBuilder.start()
            val output = StringBuilder()
            val reader = BufferedReader(
                InputStreamReader(process.inputStream)
            )
            reader.lines().forEach { line ->
                if (line.contains("total")) {
                    output.append(line).append('\n')
                }
                if (line.contains(lookingFor.name)) {
                    output.append(line).append('\n')
                }
            }
            val exitVal = process.waitFor()
            if (exitVal == 0) {
                println("Success!")
                println(output)
            } else {
                //abnormal...
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}
