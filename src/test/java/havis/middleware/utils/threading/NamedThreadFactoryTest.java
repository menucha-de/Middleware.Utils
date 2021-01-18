package havis.middleware.utils.threading;

import mockit.Deencapsulation;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

import org.junit.Assert;
import org.junit.Test;

public class NamedThreadFactoryTest {
	
	private final String namePrefix = "Testing";
	private Runnable r = new Runnable() {		
		@Override
		public void run() {}
	};
	private final NamedThreadFactory threadFactory = new NamedThreadFactory(namePrefix);
	
	@Test
	public void checkNamedThread() {
		Thread thread = threadFactory.newThread(r);
		Thread thread2 = threadFactory.newThread(r);
		
		Assert.assertEquals(namePrefix + " #1", thread.getName());
		Assert.assertEquals(namePrefix + " #2", thread2.getName());
		Assert.assertEquals(thread.getThreadGroup(), Deencapsulation.getField(threadFactory, "group"));		
	}
	
	@Test
	public void checkThreadDeamonAndPriority(@Mocked final Thread thread,
			@Mocked final SecurityManager manager, @Mocked final ThreadGroup group) {
		
		new NonStrictExpectations() {{		
			thread.isDaemon();
			result = true;
			thread.getPriority();
			result = Thread.MIN_PRIORITY;
		}};
		
		threadFactory.newThread(r);
		
		new Verifications() {{
			boolean on;
			thread.setDaemon(on = withCapture());
			times = 1;
			Assert.assertEquals(false, on);
			
			thread.setPriority(Thread.NORM_PRIORITY);
			times = 1;
		}};
	}
}
