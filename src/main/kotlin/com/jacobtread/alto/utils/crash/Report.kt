package com.jacobtread.alto.utils.crash

import com.jacobtread.alto.utils.*
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists
import kotlin.io.path.writeText

class Report(val description: String, override val cause: Throwable) : RuntimeException(description, cause) {

    companion object {
        @JvmStatic
        fun getOrMake(name: String, e: Throwable): Report {
            return if (e is ReportedException) e.report else Report(name, e)
        }
    }

    // The previously saved file
    var file: Path? = null

    // The sections of this crash report
    private val sections = ArrayList<ReportSection>()

    // The stack trace for the exception
    private var stackTraceIn = emptyArray<StackTraceElement>()

    // If the next section will be the first section
    private var head = true

    val system = ReportSection("System Details").apply {
        add("Minecraft Version", "1.8.9 MCK")
        add("Operating System", getSystemName())
        add("Java Version", getJavaName())
        add("Java VM Version", getJavaVMName())
        add("Memory", getMemoryString())
        add("JVM Flags", getUserJVMFlags())
    }

    /**
     * Adds the system section
     */
    init {
        sections.add(system)
    }

    /**
     * build Builds all the components of this crash report into
     * a string ready to be printed to console and saved
     *
     * @return The built string
     */
    fun build(): String {
        val builder = StringBuilder()
        builder.apply {
            append("====   MCK Crash Report   ====\n")
            append("Blame Wowkster or Atlas it was probably their fault\n")
            append("Time: ")
            val time = SimpleDateFormat().format(Date())
            append(time).append('\n')
            append("Description: ").append(description)
            append("\n\n")
            append(cause())
            append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n")
            append("-".repeat(86))
            append("\n\n")
            if (stackTraceIn.isEmpty() && sections.isNotEmpty()) {
                stackTraceIn = sections[0].stackTrace.copyOf()
            }
            if (stackTraceIn.isNotEmpty()) {
                append("-- Head --\n")
                append("Stacktrace:\n")
                for (stackTraceElement in stackTraceIn) {
                    append("\tat")
                    append(stackTraceElement.toString())
                    append('\n')
                }
                append('\n')
            }
            for (section in sections) {
                section.append(builder)
                append('\n')
            }
        }
        return builder.toString()
    }

    /**
     * cause Converts the cause exception to a string. If
     * the exception is missing its message it's assigned to
     * [description]
     *
     * @return The exception string
     */
    private fun cause(): String {
        var cause = this.cause
        if (cause.message == null) {
            cause = when (cause) {
                is NullPointerException -> NullPointerException(description)
                is StackOverflowError -> StackOverflowError(description)
                is OutOfMemoryError -> OutOfMemoryError(description)
                else -> cause
            }
            cause.stackTrace = this.cause.stackTrace
        }
        return cause.stackTraceToString()
    }

    /**
     * save Saves this crash report to the provided file
     *
     * @param path The path of the file to save at
     * @return Whether the crash report was saved
     */
    fun save(path: Path): Boolean {
        if (file != null) return false
        val parent = path.parent
        try {
            if (parent.notExists()) parent.createDirectories()
        } catch (e: IOException) {
            println("Failed to save crash report. Couldn't create director $parent")
            e.printStackTrace()
            return false
        }
        return try {
            path.writeText(build(), StandardCharsets.UTF_8)
            file = path
            true
        } catch (e: IOException) {
            println("Failed to save crash report. Couldn't write to file $path")
            e.printStackTrace()
            false
        }
    }

    /**
     * section Adds a new crash report section, also updates stack
     * traces so that they don't overlap if there are multiple sections
     *
     * @param name The name of this section
     * @return The crash report section
     */
    fun section(name: String): ReportSection {
        val section = ReportSection(name)
        if (head) {
            val addedSize = section.addStackTrace()
            val stackTrace = cause.stackTrace
            val stackSize = stackTrace.size
            val size = stackSize - addedSize
            if (size < 0) println("Negative index in crash report ($stackSize/$addedSize)")
            head = if (size in stackTrace.indices && (stackSize + 1 - addedSize < stackSize)) {
                section.isHeadMatching(stackTrace[size], stackTrace[stackSize + 1 - addedSize])
            } else {
                false
            }
            if (addedSize > 0 && sections.size > 1) {
                sections[sections.size - 1].popStackTrace(addedSize)
            } else if (stackSize >= addedSize && size in stackTrace.indices) {
                this.stackTraceIn = stackTrace.copyOf()
            } else {
                head = false
            }
        }
        sections.add(section)
        return section
    }

}