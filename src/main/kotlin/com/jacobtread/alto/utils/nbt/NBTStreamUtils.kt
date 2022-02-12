package com.jacobtread.alto.utils.nbt

import com.jacobtread.alto.utils.ZERO_BYTE
import com.jacobtread.alto.utils.crash.Report
import com.jacobtread.alto.utils.nbt.types.NBTCompound
import com.jacobtread.alto.utils.nbt.types.NBTEnd
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object NBTStreamUtils {

    @Throws(IOException::class)
    fun read(path: Path): NBTCompound = Files.newInputStream(path).use { read(it) }


    @Throws(IOException::class)
    fun readCompressed(path: Path): NBTCompound = read(GZIPInputStream(Files.newInputStream(path)))


    fun read(inputStream: InputStream): NBTCompound {
        val dataStream = DataInputStream(BufferedInputStream(inputStream))
        return read(dataStream, NBTSizeTracker.INFINITE)
    }

    @Throws(IOException::class)
    fun write(compound: NBTCompound, path: Path) =
        write(compound, Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE))

    @Throws(IOException::class)
    fun writeCompressed(compound: NBTCompound, path: Path) =
        write(
            compound,
            GZIPOutputStream(Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE))
        )

    @Throws(IOException::class)
    fun write(compound: NBTCompound, outputStream: OutputStream) {
        val dataStream = DataOutputStream(BufferedOutputStream(outputStream))
        dataStream.use { writeTag(compound, it) }
    }

    @Throws(IOException::class)
    fun safeWrite(compound: NBTCompound, path: Path) {
        val tmpPath = path.resolveSibling(path.fileName.toString() + "_tmp")
        if (Files.exists(tmpPath)) Files.delete(tmpPath)
        write(compound, tmpPath)
        Files.move(tmpPath, path, StandardCopyOption.REPLACE_EXISTING)
    }

    fun read(dataInput: DataInput, sizeTracker: NBTSizeTracker): NBTCompound {
        val base = readBase(dataInput, sizeTracker)
        if (base is NBTCompound) return base
        throw IOException("Root tag must be named compound tag got: ${base.javaClass.name} $base")
    }

    fun writeTag(nbtCompound: NBTCompound, dataOutput: DataOutput) {
        val id = nbtCompound.id.toInt()
        dataOutput.writeByte(id)
        if (id != 0) {
            dataOutput.writeUTF("")
            nbtCompound.write(dataOutput)
        }
    }

    fun readBase(dataInput: DataInput, nbtSizeTracker: NBTSizeTracker): NBTBase {
        var type: Byte = 0
        try {
            type = dataInput.readByte()
            if (type == ZERO_BYTE) return NBTEnd()
            dataInput.readUTF()
            val base = NBTBase.createByType(type)
            base.read(dataInput, 0, nbtSizeTracker)
            return base
        } catch (e: Throwable) {
            val report = Report("Loading NBT data", e)
            val category = report.section("NBT Tag")
            category.add("Tag name", "[UNNAMED TAG]")
            category.add("Tag type", type)
            throw report
        }
    }

}