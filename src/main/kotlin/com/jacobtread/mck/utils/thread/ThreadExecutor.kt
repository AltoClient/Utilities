package com.jacobtread.mck.utils.thread



import com.jacobtread.mck.logger.Logger
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import java.util.concurrent.locks.LockSupport

abstract class ThreadExecutor : Executor {
    companion object {
        val LOGGER: Logger = Logger.get()
    }

    abstract val thread: Thread

    val isOnThread get() = Thread.currentThread() == thread
    val tasks = ConcurrentLinkedQueue<Runnable>()
    var inProgress = 0

    abstract fun createThread(): Thread
    abstract fun canExecute(runnable: Runnable): Boolean
    abstract fun createTask(runnable: Runnable): Runnable

    fun <T : Any?> submit(task: Callable<T>): CompletableFuture<T> {
        if (isOnThread) {
            return try {
                CompletableFuture.completedFuture(task.call())
            } catch (e: Exception) {
                CompletableFuture.failedFuture(e)
            }
        }
        return CompletableFuture.supplyAsync({
            return@supplyAsync task.call()
        }, this)
    }

    fun submit(task: Runnable): CompletableFuture<*> {
        if (isOnThread) {
            return try {
                task.run()
                CompletableFuture.completedFuture(null)
            } catch (e: Exception) {
                CompletableFuture.failedFuture<Any?>(e)
            }
        }
        return CompletableFuture.supplyAsync({
            task.run()
        }, this)
    }


    fun submitAndJoin(runnable: Runnable) {
        if (!isOnThread) {
            submit(runnable).join()
        } else {
            runnable.run()
        }
    }

    fun addTask(runnable: Runnable) {
        tasks.add(createTask(runnable))
        LockSupport.unpark(thread)
    }

    override fun execute(command: Runnable) {
        if (isOnThread) {
            command.run()
        } else {
            addTask(command)
        }
    }

    fun clear() {
        tasks.clear()
    }

    fun runTasks() {
        while (true) {
            if (!runTask()) break
        }
    }

    fun runTasks(stopCondition: () -> Boolean) {
        ++inProgress
        try {
            while (!stopCondition()) {
                if (!runTask()) {
                    waitForTasks()
                }
            }
        } finally {
            --inProgress
        }
    }

    open fun waitForTasks() {
        Thread.yield()
        LockSupport.parkNanos("waiting for tasks", 100000L)
    }

    fun runTask(): Boolean {
        val task = tasks.peek() ?: return false
        if (inProgress == 0 && !canExecute(task)) return false
        executeTask(tasks.remove())
        return true
    }

    protected open fun executeTask(task: Runnable) {
        try {
            task.run()
        } catch (var3: java.lang.Exception) {
            LOGGER.fatal("Error executing task on {}", thread.name, var3)
        }
    }
}