package io.github.juanimoli.strictmode.notifier

import java.util.concurrent.ThreadFactory

/**
 * This is intended to only be used with a single thread executor.
 */
internal class StrictModeNotifierSingleThreadFactory(threadName: String) : ThreadFactory {
    private val threadName: String = "StrictModeNotifier-$threadName"

    override fun newThread(runnable: Runnable): Thread {
        return Thread(runnable, threadName)
    }
}
