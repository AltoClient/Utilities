package com.jacobtread.alto.utils.crash

import java.util.concurrent.Callable

/**
 * ReportSection Represents a section of a [Report]
 *
 * @property name
 * @constructor Create empty ReportSection
 */
class ReportSection(val name: String) {
    val entries = ArrayList<Entry>()
    var stackTrace = emptyArray<StackTraceElement>()

    /**
     * add Adds a new entry to this crash report category
     * with the provided value which can be anything,
     * if its null it will be replaced with "~~NULL~~"
     * and if it's an exception it will be replaced with:
     * ~~ERROR~~ ClassName: Message
     *
     * @param name The name of this subsection
     * @param value The vale of this subsection
     */
    fun add(name: String, value: Any?) {
        val error = if (value is Throwable) {
            "~~ERROR~~ ${value.javaClass.simpleName}: ${value.message}"
        } else value?.toString() ?: "~~NULL~~"
        entries.add(Entry(name, error))
    }

    fun addSafe(name: String, value: Callable<String>) {
        try {
            add(name, value.call())
        } catch (e: Throwable) {
            add(name, e)
        }
    }

    /**
     * addStackTrace Adds the current program stack trace to this
     * error taking away. Skips the 4 deepest items of the stack
     *
     * @return The size of the added stack trace
     */
    fun addStackTrace(): Int {
        val rootStackTrace = Thread.currentThread().stackTrace
        if (rootStackTrace.isEmpty()) return 0
        this.stackTrace = rootStackTrace.copyOfRange(4, rootStackTrace.size)
        return stackTrace.size
    }

    /**
     * popStackTrace Removes the desired [amount] of items from
     * the end of the stack trace
     *
     * @param amount The amount of items to remove
     */
    fun popStackTrace(amount: Int) {
        this.stackTrace = stackTrace.copyOfRange(0, stackTrace.size - amount)
    }

    /**
     * isHeadMatching Checks if the first two elements in the local
     * stack trace match the provided stack trace elements [a] and [b]
     *
     * @param a The first stack trace element to check
     * @param b The second stack trace element to check
     * @return Whether they match
     */
    fun isHeadMatching(a: StackTraceElement?, b: StackTraceElement?): Boolean {
        return (stackTrace.isNotEmpty() && a != null && stackTrace[0] == a
                && stackTrace.size > 1 && b != null && stackTrace[1] == b)
    }

    /**
     * append Appends this report section entries
     * to the provided [StringBuilder] [builder]
     *
     * @param builder The string builder to append to
     */
    fun append(builder: StringBuilder) {
        builder.apply {
            append("-- $name --\nDetails:\n")
            entries.forEach {
                append('\t')
                append(it.key).append(": ").append(it.value)
                append('\n')
            }
            if (stackTrace.isNotEmpty()) {
                append("Stacktrace: ")
                stackTrace.forEach {
                    append("\tat ")
                    append(it.toString())
                    append('\n')
                }
            }
            append('\n')
        }
    }

    /**
     * Entry Used to represent a key=value a pair of section name
     * to section value
     *
     * @property key The name of the entry
     * @property value The value of the entry
     * @constructor Create empty Entry
     */
    data class Entry(val key: String, val value: String)
}