package havis.middleware.utils.threading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Interruptible thread pool executor, all {@link Runnable} instances must
 * implement the {@link Task} interface. {@link Task} carries the thread group
 * ID. A thread group can be interrupted by it's ID.
 */
public class InterruptibleThreadPoolExecutor extends ThreadPoolExecutor {

	private class ThreadGroup {

		private List<Thread> threads = new ArrayList<>();
		private List<Runnable> runnables = new ArrayList<>();

		public void add(Thread t, Runnable r) {
			this.threads.add(t);
			this.runnables.add(r);
		}

		public void remove(Runnable r) {
			int index = this.runnables.indexOf(r);
			if (index > -1) {
				this.threads.remove(index);
				this.runnables.remove(index);
			}
		}

		public void interrupt() {
			for (Thread t : this.threads) {
				t.interrupt();
			}
		}

		public boolean isEmpty() {
			return this.runnables.isEmpty();
		}
	}

	private Map<Integer, ThreadGroup> activeThreads = new HashMap<>();
	private Lock lock = new ReentrantLock();

	/**
	 * Creates a new {@code InterruptibleThreadPoolExecutor} with the given
	 * initial parameters and default rejected execution handler.
	 * 
	 * @param corePoolSize
	 *            the number of threads to keep in the pool, even if they are
	 *            idle, unless {@code allowCoreThreadTimeOut} is set
	 * @param maximumPoolSize
	 *            the maximum number of threads to allow in the pool
	 * @param keepAliveTime
	 *            when the number of threads is greater than the core, this is
	 *            the maximum time that excess idle threads will wait for new
	 *            tasks before terminating.
	 * @param unit
	 *            the time unit for the {@code keepAliveTime} argument
	 * @param workQueue
	 *            the queue to use for holding tasks before they are executed.
	 *            This queue will hold only the {@code Runnable} tasks submitted
	 *            by the {@code execute} method.
	 * @param threadFactory
	 *            the factory to use when the executor creates a new thread
	 * @throws IllegalArgumentException
	 *             if one of the following holds:<br>
	 *             {@code corePoolSize < 0}<br>
	 *             {@code keepAliveTime < 0}<br>
	 *             {@code maximumPoolSize <= 0}<br>
	 *             {@code maximumPoolSize < corePoolSize}
	 * @throws NullPointerException
	 *             if {@code workQueue} or {@code threadFactory} is null
	 */
	public InterruptibleThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
			ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);

	}

	@Override
	public void execute(Runnable command) {
		if (!(command instanceof Task)) {
			throw new IllegalArgumentException("Runnable must be of type Task");
		}
		super.execute(command);
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		if (r instanceof Task) {
			Integer id = Integer.valueOf(((Task) r).getGroupId());
			this.lock.lock();
			try {
				ThreadGroup threadGroup = this.activeThreads.get(id);
				if (threadGroup == null) {
					this.activeThreads.put(id, threadGroup = new ThreadGroup());
				}
				threadGroup.add(t, r);
			} finally {
				this.lock.unlock();
			}
		}

	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		if (r instanceof Task) {
			Integer id = Integer.valueOf(((Task) r).getGroupId());
			this.lock.lock();
			try {
				ThreadGroup threadGroup = this.activeThreads.get(id);
				if (threadGroup != null) {
					threadGroup.remove(r);
					if (threadGroup.isEmpty()) {
						this.activeThreads.remove(id);
					}
				}
			} finally {
				this.lock.unlock();
			}
		}
	}

	/**
	 * Interrupt the specified thread group
	 * 
	 * @param threadGroupId
	 *            the ID of the thread group to interrupt
	 */
	public void interruptGroup(int threadGroupId) {
		this.lock.lock();
		try {
			ThreadGroup threadGroup = this.activeThreads.get(Integer.valueOf(threadGroupId));
			if (threadGroup != null) {
				threadGroup.interrupt();
			}
		} finally {
			this.lock.unlock();
		}
	}
}
