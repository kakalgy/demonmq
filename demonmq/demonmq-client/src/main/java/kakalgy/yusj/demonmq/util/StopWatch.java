package kakalgy.yusj.demonmq.util;

/**
 * A very simple stop watch.
 * <p/>
 * This implementation is not thread safe and can only time one task at any
 * given time.
 * 
 * @author kakalgy
 * @Description: TODO(用一句话描述该文件做什么)
 * @date 2018年3月15日 下午10:56:37
 */
public final class StopWatch {
	private long start;
	private long stop;

	/**
	 * 构造函数 Starts the stop watch
	 */
	public StopWatch() {
		// TODO Starts the stop watch
		this(true);
	}

	/**
	 * 构造函数 Creates the stop watch
	 * 
	 * @param started
	 *            whether it should start immediately
	 */
	public StopWatch(boolean started) {
		// TODO Starts the stop watch
		if (started) {
			restart();
		}
	}

	/**
	 * Starts or restarts the stop watch，重置start和stop，start为系统当前时间，stop为0
	 */
	public void restart() {
		start = System.currentTimeMillis();
		stop = 0;
	}

	/**
	 * Stops the stop watch
	 * <p>
	 * 停止计时，返回时间差
	 *
	 * @return the time taken in milliseconds.
	 */
	public long stop() {
		stop = System.currentTimeMillis();
		return taken();
	}

	/**
	 * Returns the time taken in milliseconds.
	 * <p>
	 * 返回start和stop的差值
	 *
	 * @return time in milliseconds
	 */
	public long taken() {
		if (start > 0 && stop > 0) {
			return stop - start;
		} else if (start > 0) {
			return System.currentTimeMillis() - start;
		} else {
			return 0;
		}
	}
}
