package havis.middleware.utils.threading;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is used to manage the resources of the software. There are i.e.
 * reports that have to be generated and delivered to the subscribers. To
 * prevent that such not time critical processes paralyze the software, this
 * processes will be queued and processed step by step. Retrieves the maxThreads
 * property to define how many threads will be invoked simultaneous.
 */
public class ThreadManager {

	private static int maxThreads = 10;
	private static int threadTimeout = 60000;
	private static int queueWarningTimeout = 5;

	private static Lock lock = new ReentrantLock();

	private static InterruptibleThreadPoolExecutor executor;
	static {
		init();
	}

	/**
	 * Initialize the executor
	 */
	public static void init() {
		if (executor == null) {
			executor = new InterruptibleThreadPoolExecutor(maxThreads, Integer.MAX_VALUE, threadTimeout, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory(ThreadManager.class.getSimpleName()));
			executor.allowCoreThreadTimeOut(true);
		}
	}

	private static void checkInit() {
		if (executor == null) {
			throw new IllegalStateException("init() was not called");
		}
	}

	/**
	 * Gets the maximum number of simultaneous threads
	 * 
	 * @return The maximum number of threads
	 */
	public static int getMaxThreads() {
		return maxThreads;
	}

	/**
	 * Sets the maximum number of simultaneous threads
	 * 
	 * @param maxThreads
	 *            The maximum number of threads
	 */
	public static void setMaxThreads(int maxThreads) {
		checkInit();
		lock.lock();
		try {
			if (maxThreads < 1) {
				throw new IllegalArgumentException("Value has to be bigger then 0");
			}
			executor.setCorePoolSize(ThreadManager.maxThreads = maxThreads);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Gets the time span in milliseconds after which a thread should terminate.
	 * 
	 * @return The thread timeout in milliseconds
	 */
	public static int getThreadTimeout() {
		return threadTimeout;
	}

	/**
	 * Sets the time span in milliseconds after which a thread should terminate.
	 * 
	 * @param threadTimeout
	 *            The thread timeout in milliseconds
	 */
	public static void setThreadTimeout(int threadTimeout) {
		checkInit();
		lock.lock();
		try {
			if (threadTimeout < 1) {
				throw new IllegalArgumentException("Value has to be bigger then 0");
			}
			executor.setKeepAliveTime(ThreadManager.threadTimeout = threadTimeout, TimeUnit.MILLISECONDS);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Gets the time span in minutes after which a full queue leads to a
	 * warning.
	 * 
	 * @return The queue warning timeout
	 */
	public static int getQueueWarningTimeout() {
		return queueWarningTimeout;
	}

	/**
	 * Sets the time span in minutes after which a full queue leads to a
	 * warning.
	 * 
	 * @param queueWarningTimeout
	 *            The queue warning timeout
	 */
	public static void setQueueWarningTimeout(int queueWarningTimeout) {
		checkInit();
		lock.lock();
		try {
			if (queueWarningTimeout < 1) {
				throw new IllegalArgumentException("Value has to be bigger then 0");
			}
			ThreadManager.queueWarningTimeout = queueWarningTimeout;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Enqueues a single runnable instance.
	 * 
	 * @param task
	 *            The task
	 */
	public static void enqueue(Task task) {
		checkInit();
		executor.execute(task);
	}

	public static void interrupt(int groupId) {
		checkInit();
		executor.interruptGroup(groupId);
	}

	/**
	 * Disposes this instance.
	 */
	public static void dispose() {
		if (executor != null) {
			lock.lock();
			try {
				executor.shutdown();
				executor = null;
			} finally {
				lock.unlock();
			}
		}
	}
}