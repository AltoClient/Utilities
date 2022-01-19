package com.jacobtread.mck.utils.thread

import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

/**
 * SingleThreadQueuedExecutor An executor for executing tasks
 * on the desired thread. When called from another thread the
 * task will be added to a queue which is then executed by
 * [processTaskQueue] on the specific thread
 *
 * @constructor Create empty SingleThreadQueuedExecutor
 */
abstract class SingleThreadQueuedExecutor : Executor {
    /**
     * thread The thread that tasks should be executed on
     */
    abstract val thread: Thread

    /**
     * queue An [ArrayDeque] of the tasks represented as [QueueItem]
     */
    private val queue = ArrayDeque<QueueItem<*>>()

    /**
     * isOnThread Whether we are on the desired thread
     */
    val isOnThread get() = Thread.currentThread() == thread

    /**
     * Submit a [Callable] task this task will be executed
     * on [thread] and then the result will be provided to
     * the returned [CompletableFuture]
     *
     * @param T the type of object to be returned by the task
     * @param task The task to execute
     * @return The future that indicated the completion
     */
    fun <T : Any?> submit(task: Callable<T>): CompletableFuture<T> {
        if (isOnThread) {
            return try {
                CompletableFuture.completedFuture(task.call())
            } catch (e: Exception) {
                CompletableFuture.failedFuture(e)
            }
        }
        val future = CompletableFuture<T>()
        queue.add(QueueItem(future, task))
        return future
    }

    /**
     * Wrapping method of [submit] which makes this a valid
     * [Executor]
     *
     * @see submit
     * @param command The command to execute
     */
    override fun execute(command: Runnable) {
        submit(command)
    }

    /**
     * Submit a [Runnable] task this will execute wrapper method
     * of [submit] to allow usage of runnable tasks. These will
     * automatically return null as the value for completable future
     * so that you can still tell that task has been completed
     *
     * @see submit
     * @param task The task to execute
     * @return The future that indicated the completion
     */
    fun submit(task: Runnable): CompletableFuture<*> {
        return submit(Callable {
            task.run()
            null
        })
    }

    /**
     * Intended to be called by a loop on the target thread.
     * Goes through the current task queue and executes all
     * the tasks
     */
    fun processTaskQueue() {
        while (queue.isNotEmpty()) {
            executeTask(queue.removeFirst())
        }
    }

    /**
     * executeTask This is a wrapping method to call
     * the [CompletableFuture] and [Callable] with the
     * correct generics type to prevent the compiler from
     * getting mad. Also catches any exceptions and completes
     * the future exceptionally if there were any
     *
     * @param V The type of object the task should return
     * @param queueItem The queue item to execute
     */
    fun <V> executeTask(queueItem: QueueItem<V>) {
        try {
            val result = queueItem.callable.call()
            queueItem.future.complete(result)
        } catch (e: Exception) {
            queueItem.future.completeExceptionally(e)

        }
    }

    /**
     * QueueItem
     *
     * @param V The type of object the queue item callable will return
     * @property future The future used to find out when the task is completed
     * @property callable The task itself to run
     * @constructor Create empty QueueItem
     */
    data class QueueItem<V>(val future: CompletableFuture<V>, val callable: Callable<V>)
}