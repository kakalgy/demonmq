package kakalgy.yusj.demonmq.util;

import java.util.List;
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
     * Shutdown the given executor service only (ie not graceful shutdown).
     *
     * @see java.util.concurrent.ExecutorService#shutdown()
     */
    public static void shutdown(ExecutorService executorService) {
        doShutdown(executorService, 0);
    }

    /**
     * Shutdown now the given executor service aggressively.
     * <p>
     * 方法中调用的是线程池的shutdownNow()方法，执行该方法，
     * 线程池的状态立刻变成STOP状态，并试图停止所有正在执行的线程，不再处理还在池队列中等待的任务， 返回的是那些未执行的任务。
     *
     * @param executorService
     *            the executor service to shutdown now
     * @return list of tasks that never commenced execution
     * @see java.util.concurrent.ExecutorService#shutdownNow()
     */
    public static List<Runnable> shutdownNow(ExecutorService executorService) {
        List<Runnable> answer = null;
        if (!executorService.isShutdown()) {
            LOGGER.debug("Forcing shutdown of ExecutorService: {}", executorService);
            answer = executorService.shutdownNow();
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Shutdown of ExecutorService: {} is shutdown: {} and terminated: {}.",
                        new Object[] { executorService, executorService.isShutdown(), executorService.isTerminated() });
            }
        }

        return answer;
    }

    /**
     * Shutdown the given executor service graceful at first, and then aggressively
     * if the await termination timeout was hit.
     * <p/>
     * This implementation invokes the
     * {@link #shutdownGraceful(java.util.concurrent.ExecutorService, long)} with a
     * timeout value of {@link #DEFAULT_SHUTDOWN_AWAIT_TERMINATION} millis.
     */
    public static void shutdownGraceful(ExecutorService executorService) {
        doShutdown(executorService, DEFAULT_SHUTDOWN_AWAIT_TERMINATION);
    }

    /**
     * Shutdown the given executor service graceful at first, and then aggressively
     * if the await termination timeout was hit.
     * <p/>
     * Will try to perform an orderly shutdown by giving the running threads time to
     * complete tasks, before going more aggressively by doing a
     * {@link #shutdownNow(java.util.concurrent.ExecutorService)} which forces a
     * shutdown. The parameter <tt>shutdownAwaitTermination</tt> is used as timeout
     * value waiting for orderly shutdown to complete normally, before going
     * aggressively. If the shutdownAwaitTermination value is negative the shutdown
     * waits indefinitely for the ExecutorService to complete its shutdown.
     *
     * @param executorService
     *            the executor service to shutdown
     * @param shutdownAwaitTermination
     *            timeout in millis to wait for orderly shutdown
     */
    public static void shutdownGraceful(ExecutorService executorService, long shutdownAwaitTermination) {
        doShutdown(executorService, shutdownAwaitTermination);
    }

    /**
     * 关闭线程池，分为两个步骤，首先是优雅的关闭线程池，当在shutdownAwaitTermination时间内关闭失败时，再尝试第二种方法，强制关闭线程池
     * 
     * @param executorService
     * @param shutdownAwaitTermination
     */
    private static void doShutdown(ExecutorService executorService, long shutdownAwaitTermination) {
        // code from Apache Camel - org.apache.camel.impl.DefaultExecutorServiceManager

        if (executorService == null) {
            return;
        }

        // shutting down a thread pool is a 2 step process. First we try graceful, and
        // if that fails, then we go more aggressively
        // and try shutting down again. In both cases we wait at most the given shutdown
        // timeout value given
        // (total wait could then be 2 x shutdownAwaitTermination, but when we shutdown
        // the 2nd time we are aggressive and thus
        // we ought to shutdown much faster)
        if (!executorService.isShutdown()) {
            boolean warned = false;
            StopWatch watch = new StopWatch();

            LOGGER.trace("Shutdown of ExecutorService: {} with await termination: {} millis", executorService,
                    shutdownAwaitTermination);
            executorService.shutdown();

            if (shutdownAwaitTermination > 0) {
                try {
                    if (!awaitTermination(executorService, shutdownAwaitTermination)) {
                        warned = true;
                        LOGGER.warn("Forcing shutdown of ExecutorService: {} due first await termination elapsed.",
                                executorService);
                        executorService.shutdownNow();
                        // we are now shutting down aggressively, so wait to see if we can completely
                        // shutdown or not
                        if (!awaitTermination(executorService, shutdownAwaitTermination)) {
                            LOGGER.warn(
                                    "Cannot completely force shutdown of ExecutorService: {} due second await termination elapsed.",
                                    executorService);
                        }
                    }
                } catch (InterruptedException e) {
                    warned = true;
                    LOGGER.warn("Forcing shutdown of ExecutorService: {} due interrupted.", executorService);
                    // we were interrupted during shutdown, so force shutdown
                    try {
                        executorService.shutdownNow();
                    } finally {
                        Thread.currentThread().interrupt();
                    }
                }
            } else if (shutdownAwaitTermination < 0) {
                try {
                    awaitTermination(executorService);
                } catch (InterruptedException e) {
                    warned = true;
                    LOGGER.warn("Forcing shutdown of ExecutorService: {} due interrupted.", executorService);
                    // we were interrupted during shutdown, so force shutdown
                    try {
                        executorService.shutdownNow();
                    } finally {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            // if we logged at WARN level, then report at INFO level when we are complete so
            // the end user can see this in the log
            if (warned) {
                LOGGER.info("Shutdown of ExecutorService: {} is shutdown: {} and terminated: {} took: {}.",
                        new Object[] { executorService, executorService.isShutdown(), executorService.isTerminated(),
                                TimeUtils.printDuration(watch.taken()) });
            } else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Shutdown of ExecutorService: {} is shutdown: {} and terminated: {} took: {}.",
                        new Object[] { executorService, executorService.isShutdown(), executorService.isTerminated(),
                                TimeUtils.printDuration(watch.taken()) });
            }
        }
    }

    /**
     * Awaits the termination of the thread pool indefinitely (Use with Caution).
     * <p/>
     * This implementation will log every 2nd second at INFO level that we are
     * waiting, so the end user can see we are not hanging in case it takes longer
     * time to terminate the pool.
     * <p>
     * 这个方法的目的是在ExecutorService.awaitTermination()方法上加上一层封装，
     * 设置超时时间为2s，若2s内没有关闭线程池，则打印日志
     * 
     * @param executorService
     *            the thread pool
     *
     * @throws InterruptedException
     *             is thrown if we are interrupted during the waiting
     */
    public static void awaitTermination(ExecutorService executorService) throws InterruptedException {
        // log progress every 5th second so end user is aware of we are shutting down
        StopWatch watch = new StopWatch();
        final long interval = 2000;
        while (true) {
            if (executorService.awaitTermination(interval, TimeUnit.MILLISECONDS)) {
                return;
            } else {
                LOGGER.info("ThreadPoolUtils.awaitTermination1 : Waited {} for ExecutorService: {} to terminate...",
                        TimeUtils.printDuration(watch.taken()), executorService);
            }
        }
    }

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
    public static boolean awaitTermination(ExecutorService executorService, long shutdownAwaitTermination)
            throws InterruptedException {
        // log progress every 5th second so end user is aware of we are shutting down
        StopWatch watch = new StopWatch();// 默认立即启动StopWatch

        long interval = Math.min(2000, shutdownAwaitTermination);
        boolean done = false;

        while (!done && interval > 0) {// 每隔2s执行一次，直到执行完成
            if (executorService.awaitTermination(interval, TimeUnit.MILLISECONDS)) {// 此方法在超时后返回false
                done = true;
            } else {
                LOGGER.info("ThreadPoolUtils.awaitTermination2 : Waited {} for ExecutorService: {} to terminate...",
                        TimeUtils.printDuration(watch.taken()), executorService);
                // recalculate interval
                interval = Math.min(2000, shutdownAwaitTermination - watch.taken());
            }
        }

        return done;
    }

}
