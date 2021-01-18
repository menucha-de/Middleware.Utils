package havis.middleware.utils.threading;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread factory which names the created threads as specified
 */
public class NamedThreadFactory implements ThreadFactory {
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;

	/**
	 * Creates a new named thread factory
	 * 
	 * @param namePrefix
	 *            the name prefix to use
	 */
	public NamedThreadFactory(String namePrefix) {
		SecurityManager s = System.getSecurityManager();
		this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		this.namePrefix = namePrefix;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(this.group, r, this.namePrefix + " #" + threadNumber.getAndIncrement(), 0);
		if (t.isDaemon())
			t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY)
			t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}
}
