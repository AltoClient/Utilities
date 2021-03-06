package com.jacobtread.alto.utils

import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.io.IOException
import java.lang.management.ManagementFactory
import java.net.URI
import java.nio.file.Path

/**
 * openFolder Opens the provided path using the system. Tries opening
 * it multiple operating system specific ways otherwise will resort to
 * [Desktop.browse]
 *
 * @param folder The folder to open
 */
fun openFolder(folder: Path) {
    val os = getOperatingSystem()
    val path = folder.toAbsolutePath().toString()
    try {
        if (os == OS.WINDOWS) {
            Runtime.getRuntime().exec("cmd.exe /C start \"Open file\" \"$path\"")
            return
        } else if (os == OS.OSX) {
            Runtime.getRuntime().exec(arrayOf("/usr/bin/open", path))
            return
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    try {
        Desktop.getDesktop().browse(folder.toUri())
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

/**
 * openLink Opens the provided uri using the operating
 * system default. This can be used to open files or
 * websites
 *
 * @param uri The uri to open
 * @return If it was successfully opened or not
 */
fun openLink(uri: URI): Boolean {
    return try {
        Desktop.getDesktop().browse(uri)
        true
    } catch (e: Throwable) {
        println("Failed to open link: $uri : ${e.message}")
        false
    }
}

/**
 * getClipboard Retrieves the contents of the
 * system clipboard and returns them
 *
 * @return The contents of the clipboard or an empty string on failure
 */
fun getClipboard(): String {
    return try {
        val transferable = Toolkit.getDefaultToolkit()
            .systemClipboard
            .getContents(null)
        if (transferable != null
            && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)
        ) {
            transferable.getTransferData(DataFlavor.stringFlavor) as String
        } else ""
    } catch (e: Exception) {
        ""
    }
}

/**
 * setClipboard Sets the system clipboard text to the
 * provided [text]
 *
 * @param text The new text for the clipboard
 */
fun setClipboard(text: String?) {
    if (text.isNullOrEmpty()) return
    try {
        val section = StringSelection(text)
        Toolkit.getDefaultToolkit()
            .systemClipboard
            .setContents(section, null)
    } catch (_: Exception) {
    }
}

/**
 * getOperatingSystem Finds the enum representation ([OS])
 * of the current operating system
 *
 * @return The enum representation [OS]
 */
fun getOperatingSystem(): OS {
    val osName = System.getProperty("os.name").lowercase()
    return when {
        osName.contains("win") -> OS.WINDOWS
        osName.contains("mac") -> OS.OSX
        osName.contains("solaris") || osName.contains("sunos") -> OS.SOLARIS
        osName.contains("linux") || osName.contains("unix") -> OS.LINUX
        else -> OS.UNKNOWN
    }
}

/**
 * getExceptionRoot Finds the root cause of the exception
 * by adding the entire cause list to an array and returning
 * the last element of the array
 *
 * @param e The throwable to find the root of
 * @return The root exception or null if none were found
 */
fun getExceptionRoot(e: Throwable): Throwable? {
    val causes = ArrayList<Throwable>()
    var cause = e.cause
    while (cause != null && !causes.contains(cause)) {
        causes.add(cause)
        cause = e.cause
    }
    return causes.lastOrNull()
}

/**
 * getSystemName Generates a name string for the operating
 * system including the name, arch and version
 *
 * @return The system name
 */
fun getSystemName(): String {
    return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version")
}

/**
 * getJavaName Generates a name string for the java runtime
 * includes version and vendor
 *
 * @return The system name
 */
fun getJavaName(): String {
    return System.getProperty("java.version") + ", " + System.getProperty("java.vendor")
}

/**
 * getJavaVMName Generates a name string for the current
 * JVM includes the name, info, and vendor
 *
 * @return The system name
 */
fun getJavaVMName(): String {
    return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor")
}

/**
 * getMemoryString Used to get a pretty representation
 * of the current memory usage
 *
 * @return The memory usage string
 */
fun getMemoryString(): String {
    val runtime = Runtime.getRuntime()
    val maxMemory = runtime.maxMemory()
    val totalMemory = runtime.totalMemory()
    val freeMemory = runtime.freeMemory()
    val maxMemoryMB = maxMemory.bytesToMb()
    val totalMemoryMB = totalMemory.bytesToMb()
    val freeMemoryMB = freeMemory.bytesToMb()
    return "$freeMemory bytes ($freeMemoryMB MB) / $totalMemory bytes ($totalMemoryMB MB) up to $maxMemory bytes ($maxMemoryMB MB)"
}

/**
 * getJVMFlags Returns a string containing all
 * the flags provided to the JVM by the user
 * (Ones with -X)
 *
 * @return The string of flags
 */
fun getUserJVMFlags(): String {
    val runtimeMxBean = ManagementFactory.getRuntimeMXBean()
    val inputArguments = runtimeMxBean.inputArguments
    return inputArguments.filter { it.startsWith("-X") }.joinToString { " " }
}

/**
 * isJVM64Bit Checks system properties to see if the JVM
 * is a 64 bit jvm or not
 *
 * @return Whether the JVM is 64bit
 */
fun isJVM64Bit(): Boolean {
    arrayOf("sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch").forEach {
        val property = System.getProperty(it)
        if (property != null && property.contains("64")) {
            return true
        }
    }
    return false
}

/**
 * availableProcessors Returns the number of available
 * processors
 *
 * @return The number of available processors
 */
fun availableProcessors(): Int = Runtime.getRuntime().availableProcessors()

/**
 * OS An enum to represent different
 * possible operating systems
 *
 * @constructor Create empty OS
 */
enum class OS { LINUX, SOLARIS, WINDOWS, OSX, UNKNOWN }

/**
 * lazyNone
 *
 * @param T
 * @param initializer
 * @receiver
 * @return
 */
fun <T> lazyNone(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

