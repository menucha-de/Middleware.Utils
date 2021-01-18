package havis.middleware.utils.threading;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import mockit.Mocked;
import mockit.Verifications;

import org.junit.Assert;
import org.junit.Test;

public class ThreadManagerTest {

	@Test
	public void setMaxThreads(@Mocked final InterruptibleThreadPoolExecutor executor) {
		ThreadManager.setMaxThreads(5);
		
		new Verifications() {{
			executor.setCorePoolSize(5);
			times = 1 ;
		}};		
		Assert.assertEquals(5, ThreadManager.getMaxThreads());
	}
	
	@Test
	public void setMaxThreadsException() {
		try {
			ThreadManager.setMaxThreads(-1);
			Assert.fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("Value has to be bigger then 0", e.getMessage());
		}
	}
	
	@Test
	public void setThreadTimeout(@Mocked final ThreadPoolExecutor executor) {
		ThreadManager.setThreadTimeout(10000);
		
		new Verifications() {{
			executor.setKeepAliveTime(10000, TimeUnit.MILLISECONDS);
			times = 1 ;
		}};		
		Assert.assertEquals(10000, ThreadManager.getThreadTimeout());
	}
	
	@Test
	public void setThreadTimeoutException() {
		try {
			ThreadManager.setThreadTimeout(-1);
			Assert.fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("Value has to be bigger then 0", e.getMessage());
		}
	}
	
	@Test
	public void setQueueWarningTimeout() {
		ThreadManager.setQueueWarningTimeout(2);			
		Assert.assertEquals(2, ThreadManager.getQueueWarningTimeout());
	}
	
	@Test
	public void setQueueWarningTimeoutException() {
		try {
			ThreadManager.setQueueWarningTimeout(-1);
			Assert.fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("Value has to be bigger then 0", e.getMessage());
		}
	}
	
	@Test
	public void enqueue(@Mocked final InterruptibleThreadPoolExecutor executor) {
		final Task runnable = new Task() {			
			@Override
			public void run() {}

			@Override
			public int getGroupId() {
				return 0;
			}
		};
		ThreadManager.enqueue(runnable);
		
		new Verifications() {{
			executor.execute(runnable);
			times = 1;
		}};
	}
	
	@Test
	public void dispose(@Mocked final InterruptibleThreadPoolExecutor executor) {
		ThreadManager.dispose();
		
		new Verifications() {{
			executor.shutdown();
			times = 1 ;
		}};
	}
	
}
