package kakalgy.yusj.demonmq.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Time utilities.
 * 
 * @author kakalgy
 * @Description: 时间工具类
 * @date 2018年3月15日 下午11:13:05
 */
public final class TimeUtils {

	private TimeUtils() {

	}

	/**
	 * 根据参数，转换为文本格式的时间
	 * 
	 * @param uptime
	 * @return
	 */
	public static String printDuration(double uptime) {
		// Code taken from Karaf
		// https://svn.apache.org/repos/asf/karaf/trunk/shell/commands/src/main/java/org/apache/karaf/shell/commands/impl/InfoAction.java

		NumberFormat fmtI = new DecimalFormat("###,###", new DecimalFormatSymbols(Locale.ENGLISH));
		NumberFormat fmtD = new DecimalFormat("###,##0.000", new DecimalFormatSymbols(Locale.ENGLISH));

		uptime /= 1000;
		if (uptime < 60) { // 小于60秒
			return fmtD.format(uptime) + " seconds";
		}
		uptime /= 60;
		if (uptime < 60) {// 小于1小时
			long minutes = (long) uptime;
			String s = fmtI.format(minutes) + (minutes > 1 ? " minutes" : " minute");
			return s;
		}
		uptime /= 60;
		if (uptime < 24) {// 小于1天
			long hours = (long) uptime;
			long minutes = (long) ((uptime - hours) * 60);
			String s = fmtI.format(hours) + (hours > 1 ? " hours" : " hour");
			if (minutes != 0) {
				s += " " + fmtI.format(minutes) + (minutes > 1 ? " minutes" : " minute");
			}
			return s;
		}
		uptime /= 24;
		long days = (long) uptime;// 计算一共多少天
		long hours = (long) ((uptime - days) * 24);
		String s = fmtI.format(days) + (days > 1 ? " days" : " day");
		if (hours != 0) {
			s += " " + fmtI.format(hours) + (hours > 1 ? " hours" : " hour");
		}

		return s;
	}

	public static void main(String[] args) {
		System.out.println(printDuration(2349803));
	}
}
