package com.nshmura.strictmodenotifier;

import java.util.concurrent.ThreadFactory;

/**
 * This is intended to only be used with a single thread executor.
 */
final class StrictModeNotifierSingleThreadFactory implements ThreadFactory {

  private final String threadName;

  StrictModeNotifierSingleThreadFactory(String threadName) {
    this.threadName = "StrictModeNotifier-" + threadName;
  }

  @Override public Thread newThread(Runnable runnable) {
    return new Thread(runnable, threadName);
  }
}
