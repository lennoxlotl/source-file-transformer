package de.lennox.srcvt

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * The main application class
 *
 * @since 1.0.0
 * @author Lennox
 */
class SourceVersionTransformerApplication(
    private val inputPath: String,
    private val outputPath: String,
    private val sourceVersion: Int
) {
    private val classes = mutableMapOf<String, ClassNode>()
    private val resources = mutableMapOf<String, ByteArray>()

    /**
     * Starts the source version transformer
     *
     * @since 1.0.0
     */
    fun run() {
        println("[srcvt] Loading input file...")
        val inputFile = File(inputPath)

        // The input file must exist
        if (!inputFile.exists()) {
            error("[srcvt] The input file you provided does not exist")
        }

        loadInputFile(inputFile)
        println("[srcvt] Transforming file...")
        transformSourceVersions()
        println("[srcvt] Saving result...")
        saveOutputFile()
        println("[srcvt] Done")
    }

    /**
     * Transforms the source versions of all class files
     *
     * @since 1.0.0
     */
    private fun transformSourceVersions() {
        classes.values.forEach { node ->
            node.version = sourceVersion
        }
    }

    /**
     * Loads all class and resource files inside the jar file
     *
     * @param inputFile The input file to load
     * @since 1.0.0
     */
    private fun loadInputFile(inputFile: File) {
        val zipFile = ZipFile(inputFile)

        // Load all file contents
        zipFile.stream().forEach { entry ->
            // Skip directories
            if (entry.isDirectory) {
                return@forEach
            }
            val stream = zipFile.getInputStream(entry)
            val entryBytes = stream.readAllBytes()

            // Check if the entry is a class file
            if (entry.name.endsWith(".class")) {
                val reader = ClassReader(entryBytes)
                val classNode = ClassNode()
                reader.accept(classNode, ClassReader.EXPAND_FRAMES)
                classes[classNode.name] = classNode
            } else {
                resources[entry.name] = entryBytes
            }
        }
    }

    /**
     * Saves the processed output file
     *
     * @since 1.0.0
     */
    private fun saveOutputFile() {
        val outputFile = File(outputPath)

        // Delete old output files
        if (outputFile.exists()) {
            outputFile.delete()
        }
        outputFile.createNewFile()

        val outputStream = ZipOutputStream(FileOutputStream(outputFile))
        // Save all class files
        classes.forEach { (_, node) ->
            val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
            node.accept(classWriter)
            val classBytes = classWriter.toByteArray()
            outputStream.putNextEntry(ZipEntry("${node.name}.class"))
            outputStream.write(classBytes)
            outputStream.closeEntry()
        }
        // Save all resource files
        resources.forEach { (name, bytes) ->
            outputStream.putNextEntry(ZipEntry(name))
            outputStream.write(bytes)
            outputStream.closeEntry()
        }
        outputStream.close()
    }
}