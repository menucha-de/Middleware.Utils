package havis.middleware.utils.threading;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

public class PipelineTest {
	
	private class ValueHolder {
		String value;
	}

	@Test
	public void checkDequeueAndEnqueue() throws Exception {
		final Pipeline<Integer> pipeline = new Pipeline<Integer>();
		final CountDownLatch ready = new CountDownLatch(2);
		final CountDownLatch set1 = new CountDownLatch(1);
		final CountDownLatch set2 = new CountDownLatch(1);
		final AtomicInteger i1 = new AtomicInteger(0);
		final AtomicInteger i2 = new AtomicInteger(0);
		boolean success = false;
		Thread t1 = null;
		Thread t2 = null;
		try {

			t1 = new Thread(new Runnable() {
				@Override
				public void run() {
					ready.countDown();
					i1.set(pipeline.dequeue());
					set1.countDown();
				}
			});
			t1.start();

			t2 = new Thread(new Runnable() {
				@Override
				public void run() {
					ready.countDown();
					i2.set(pipeline.dequeue());
					set2.countDown();
				}
			});
			t2.start();

			Assert.assertTrue(ready.await(50, TimeUnit.MILLISECONDS));
			Thread.sleep(10);

			pipeline.enqueue(1);
			Assert.assertTrue(set1.await(50, TimeUnit.MILLISECONDS));
			Assert.assertEquals(1, i1.get());
			Assert.assertFalse(set2.await(1, TimeUnit.MILLISECONDS));
			Assert.assertEquals(0, i2.get());

			pipeline.enqueue(2);
			Assert.assertTrue(set2.await(50, TimeUnit.MILLISECONDS));
			Assert.assertEquals(2, i2.get());

			success = true;
		} finally {
			if (!success) {
				if (t1 != null) {
					t1.interrupt();
				}
				if (t2 != null) {
					t2.interrupt();
				}
			}
		}
	}
	
	@Test
	public void checkDispose() throws Exception {
		final Pipeline<String> pipeline = new Pipeline<String>();
		final CountDownLatch ready = new CountDownLatch(2);
		final CountDownLatch set1 = new CountDownLatch(1);
		final CountDownLatch set2 = new CountDownLatch(1);
		final ValueHolder v1 = new ValueHolder();
		final ValueHolder v2 = new ValueHolder();
		boolean success = false;
		Thread t1 = null;
		Thread t2 = null;
		
		try {
			t1 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					ready.countDown();
					v1.value = pipeline.dequeue();
					set1.countDown();
				}
			});
			t1.start();
			
			t2 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					ready.countDown();
					v2.value = pipeline.dequeue();
					set2.countDown();
				}
			});
			t2.start();
			
			Assert.assertTrue(ready.await(50, TimeUnit.MILLISECONDS));
			Thread.sleep(10);

			pipeline.dispose();
			Assert.assertNull(v1.value);
			Assert.assertNull(v2.value);
			Assert.assertTrue(set1.await(50, TimeUnit.MILLISECONDS));
			Assert.assertTrue(set2.await(50, TimeUnit.MILLISECONDS));

			success = true;
		} finally {
			if (!success) {
				if (t1 != null) {
					t1.interrupt();
				}
				if (t2 != null) {
					t2.interrupt();
				}
			}
		}
	}
}