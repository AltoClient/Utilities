package com.jacobtread.mck.utils.time

import com.jacobtread.mck.utils.math.Maths

/**
 * FrameTimer Keeps track of the length (time in nanoseconds)
 * since the last 240 frames were rendered. Used to calculate
 * the minimum fps that occurred
 *
 * @constructor Create empty FrameTimer
 */
class FrameTimer {
    companion object {
        private const val TOTAL_FRAMES = 240
        private const val LENGTH_MUL = 1.0E9
    }

    private val frames: LongArray = LongArray(TOTAL_FRAMES)
    private var lastIndex = 0
    private var fillCounter = 0
    private var index = 0

    /**
     * add Adds a new frame to the frames array [frames]
     * and increases the current index
     *
     * [fillCounter] is also incremented until it reaches [TOTAL_FRAMES]
     * then the frame timer is considered "full"
     *
     * @param length The length of the current frame (duration in nanoseconds)
     */
    fun add(length: Long) {
        frames[index] = length
        index++
        if (index == 240) index = 0
        if (fillCounter < 240) {
            lastIndex = 0
            fillCounter++
        } else {
            lastIndex = (index + 1) % 240
        }
    }

    private var lastLongest: Int = 0

    /**
     * getMinFPS Calculates the minimum amount of frames that
     * were able to be rendered per second
     *
     * @param fps The current game fps
     * @return The minimum number of frames that were able to pass
     */
    fun getMinFPS(fps: Int): Int {
        if (index != lastIndex) {
            var longest = (1.0 / fps * LENGTH_MUL).toLong()
            var total = 0L
            var index = this.index
            do {
                val time = frames[index]
                if (time > longest) longest = time
                total += time
                index = Maths.wrapAbs(index - 1, 240)
            } while (index != lastIndex && total < LENGTH_MUL)
            val frames = longest / LENGTH_MUL
            lastLongest = (1.0 / frames).toInt()
        }
        return lastLongest
    }

}