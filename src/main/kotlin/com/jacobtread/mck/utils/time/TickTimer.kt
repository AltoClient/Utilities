package com.jacobtread.mck.utils.time

/**
 * TickTimer A class for converting elapsed time into
 * "ticks"
 *
 * @constructor Create empty TickTimer
 */
class TickTimer {

    companion object {
        const val TICKS_PER_SECOND = 20.0
    }

    /**
     * lastSync The last time that was reported by the
     * high-resolution system time in milliseconds
     */
    var lastSync: Long = Time.getHighResolution()

    /**
     * lastSyncSeconds The last time that was reported by the
     * high-resolution system time in seconds
     */
    var lastSyncSeconds: Double = 0.0

    /**
     * syncAdjustment A ratio used to sync the high-resolution system clock
     * updated once every second
     */
    var syncAdjustment: Double = 1.0

    var counter = 0L

    /**
     * partialTicks The total time elapased since the last tick
     * (a non-full tick) ranges between 0 and 1
     */
    var partialTicks: Float = 0f

    /**
     * ticks The total number of ticks that have passed since the last
     * time sync
     */
    var ticks: Int = 0

    /**
     * timerSpeed The speed of the in-game timer a multiplier to modify
     * the speed of how many ticks need to happen
     * (lower is slower higher is faster 1.0 = normal speed 0.5 = half speed)
     */
    var timerSpeed: Double = 1.0

    fun update() {
        val time = Time.getHighResolution()
        val timeSinceSync = time - lastSync
        val timeSeconds = time / 1000.0
        if (timeSinceSync in 0L..1000L) {
            counter += timeSinceSync
            if (counter > 1000L) {
                val moe = counter / timeSinceSync
                syncAdjustment += (moe - syncAdjustment) * 0.2
                counter = 0L
            }
        } else {
            lastSyncSeconds = timeSeconds
        }
        lastSync = timeSinceSync
        val adjusted = (timeSeconds - lastSyncSeconds) * syncAdjustment
        lastSyncSeconds = adjusted
        partialTicks += (adjusted.coerceIn(0.0, 1.0) * timerSpeed * TICKS_PER_SECOND).toFloat()
        ticks = partialTicks.toInt()
        partialTicks -= ticks
        if (ticks > 10) ticks = 10
    }
}