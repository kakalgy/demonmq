package kakalgy.yusj.demonmq.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for working with thread pools {@link ExecutorService}.
 * <p>
 * ThreadPool线程池的工具类
 * 
 * @author kakalgy
 * @Description: ThreadPool线程池的工具类
 * @date 2018年3月15日 下午10:50:11
 */
public class ThreadPoolUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolUtils.class);

	public static final long DEFAULT_SHUTDOWN_AWAIT_TERMINATION = 10 * 1000L;// 默认的线程池关闭等待时间，10秒钟

	/**
	 * Awaits the termination of the thread pool.
	 * <p/>
	 * This implementation will log every 2nd second at INFO level that we are
	 * waiting, so the end user can see we are not hanging in case it takes longer
	 * time to terminate the pool.
	 * <p>
	 * 这个方法的目的是在ExecutorService.awaitTermination()方法上加上一层封装，
	 * 原有的方法是直到shutdownAwaitTermination超时才会返回false，
	 * 若shutdownAwaitTermination设置的时间比较长，在日志中无法直到线程池一直在关闭中，现在每2秒会在日志中打印记录
	 * 
	 * @param executorService
	 *            the thread pool
	 * @param shutdownAwaitTermination
	 *            time in millis to use as timeout
	 * @return <tt>true</tt> if the pool is terminated, or <tt>false</tt> if we
	 *         timed out
	 * @throws InterruptedException
	 *             is thrown if we are interrupted during the waiting
	 * 
	 */
	public static boolean awaitTermination(ExecutorService executorService, long shutdownAwaitTermination) throws InterruptedException {
		// log progress every 5th second so end user is aware of we are shutting down
		StopWatch watch = new StopWatch();// 默认立即启动StopWatch

		long interval = Math.min(2000, shutdownAwaitTermination);
		boolean done = false;

		while (!done && interval > 0) {// 每隔2s执行一次，直到执行完成
			if (executorService.awaitTermination(interval, TimeUnit.MILLISECONDS)) {// 此方法在超时后返回false
				done = true;
			} else {
				LOGGER.info("ThreadPoolUtils.awaitTermination : Waited {} for ExecutorService: {} to terminate...", TimeUtils.printDuration(watch.taken()), executorService);
				// recalculate interval
				interval = Math.min(2000, shutdownAwaitTermination - watch.taken());
			}
		}

		return done;
	}

}
