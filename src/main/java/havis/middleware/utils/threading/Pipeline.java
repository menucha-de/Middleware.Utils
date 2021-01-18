package havis.middleware.utils.threading;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class provides a mechanism to block a dequeue() call on the queue
 * as long as no elements available. This queue is thread safely
 *
 * @param <T>
 *            Type of elements to queue
 */
public class Pipeline<T> extends LinkedList<T> {

	private static final long serialVersionUID = 1L;

	private Lock lock = new ReentrantLock();
	private Condition wait = lock.newCondition();

	private volatile boolean active = true;

	/**
	 * Locks this call as long as not elements available. After then dequeues
	 * the first one.
	 *
	 * @return The first element in queue or null if pipeline was disposed
	 */
	public T dequeue() {
	    lock.lock();
		try {
			while (active && isEmpty()) {
				wait.awaitUninterruptibly();
			}
			return poll();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Enqueues a single element and gives a notification to the first client
	 * waiting for an element
	 *
	 * @param item
	 */
	public void enqueue(T item) {
        lock.lock();
        try {
            add(item);
            wait.signal();
        } finally {
            lock.unlock();
        }
	}

	/**
	 * Disposes this instance.
	 */
    public void dispose() {
        lock.lock();
        try {
            active = false;
            wait.signalAll();
        } finally {
            lock.unlock();
        }
	}
}
