package kakalgy.yusj.demonmq.broker.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.jms.MessageFormatException;

public class CronParser {

    private static final int NUMBER_TOKENS = 5;

    // 以下是cron表达式中的位置,corn从左到右（用空格隔开）： * * * * *, 分 小时 月份中的日期 月份 星期中的日期
    private static final int MINUTES = 0;
    private static final int HOURS = 1;
    private static final int DAY_OF_MONTH = 2;
    private static final int MONTH = 3;
    private static final int DAY_OF_WEEK = 4;

    /**
     * 得到下一次的执行时间
     * 
     * @param cronEntry
     * @param currentTime
     * @return
     * @throws MessageFormatException
     */
    public static long getNextScheduledTime(final String cronEntry, long currentTime) throws MessageFormatException {
        long result = 0;

        //cron表达式不能为空
        if (cronEntry == null || cronEntry.length() == 0) {
            return result;
        }

        // Handle the once per minute case "* * * * *"
        // starting the next event at the top of the minute.
        // 当cronEntry值为"* * * * *"，把时间置为下一分钟的开始
        if (cronEntry.equals("* * * * *")) {
            result = currentTime + 60 * 1000;
            result += result / 60000 * 60000;
            return result;
        }

        List<String> list = tokenize(cronEntry);// 分割成cron格式
        List<CronEntry> entries = buildCronEntries(list);// 计算所有的时间点

        //将当前时间的秒设为0，MQ里面最小只能精确到分
        Calendar working = Calendar.getInstance();
        working.setTimeInMillis(currentTime);
        working.set(Calendar.SECOND, 0);

        CronEntry minutes = entries.get(MINUTES);
        CronEntry hours = entries.get(HOURS);
        CronEntry dayOfMonth = entries.get(DAY_OF_MONTH);
        CronEntry month = entries.get(MONTH);
        CronEntry dayOfWeek = entries.get(DAY_OF_WEEK);

        // Start at the top of the next minute, cron is only guaranteed to be
        // run on the minute.
        //将时间调到下一分钟开始
        int timeToNextMinute = 60 - working.get(Calendar.SECOND);
        working.add(Calendar.SECOND, timeToNextMinute);

        // If its already to late in the day this will roll us over to tomorrow
        // so we'll need to check again when done updating month and day.
        int currentMinutes = working.get(Calendar.MINUTE);
        if (!isCurrent(minutes, currentMinutes)) {// 如cron表达式不包含当前的分钟数
            int nextMinutes = getNext(minutes, currentMinutes, working);
            working.add(Calendar.MINUTE, nextMinutes);
        }

        int currentHours = working.get(Calendar.HOUR_OF_DAY);
        if (!isCurrent(hours, currentHours)) {
            int nextHour = getNext(hours, currentHours, working);
            working.add(Calendar.HOUR_OF_DAY, nextHour);
        }

        // We can roll into the next month here which might violate the cron setting
        // rules so we check once then recheck again after applying the month settings.
        doUpdateCurrentDay(working, dayOfMonth, dayOfWeek);

        // Start by checking if we are in the right month, if not then calculations
        // need to start from the beginning of the month to ensure that we don't end
        // up on the wrong day. (Can happen when DAY_OF_WEEK is set and current time
        // is ahead of the day of the week to execute on).
        doUpdateCurrentMonth(working, month);

        // Now Check day of week and day of month together since they can be specified
        // together in one entry, if both "day of month" and "day of week" are
        // restricted
        // (not "*"), then either the "day of month" field (3) or the "day of week"
        // field
        // (5) must match the current day or the Calenday must be advanced.
        doUpdateCurrentDay(working, dayOfMonth, dayOfWeek);

        // Now we can chose the correct hour and minute of the day in question.

        currentHours = working.get(Calendar.HOUR_OF_DAY);
        if (!isCurrent(hours, currentHours)) {
            int nextHour = getNext(hours, currentHours, working);
            working.add(Calendar.HOUR_OF_DAY, nextHour);
        }

        currentMinutes = working.get(Calendar.MINUTE);
        if (!isCurrent(minutes, currentMinutes)) {
            int nextMinutes = getNext(minutes, currentMinutes, working);
            working.add(Calendar.MINUTE, nextMinutes);
        }

        result = working.getTimeInMillis();

        if (result <= currentTime) {
            throw new ArithmeticException("Unable to compute next scheduled exection time.");
        }

        return result;

    }

    /**
     * 根据空白字符（“ ”，“\t”，“\n”）为分隔符分割字符串,分割之后的字符数组为5个，即需要4个空白字符，对应的cron应该是类似于"* * * *
     * *"
     * 
     * @param cron
     * @return
     * @throws IllegalArgumentException
     */
    static List<String> tokenize(String cron) throws IllegalArgumentException {
        // 根据空白字符（“ ”，“\t”，“\n”）为分隔符分割字符串
        StringTokenizer tokenize = new StringTokenizer(cron);
        List<String> result = new ArrayList<String>();
        while (tokenize.hasMoreTokens()) {
            result.add(tokenize.nextToken());
        }

        if (result.size() != NUMBER_TOKENS) {
            // 按照这里的判断，分割之后的字符数组为5个，即需要4个空白字符，对应的cron应该是类似于"* * * * *"
            throw new IllegalArgumentException(
                    "Not a valid cron entry - wrong number of tokens(" + result.size() + "): " + cron);
        }

        return result;
    }

    /**
     * 计算所有的时间点
     * 
     * @param tokens
     * @return
     */
    static List<CronEntry> buildCronEntries(List<String> tokens) {
        List<CronEntry> result = new ArrayList<CronParser.CronEntry>();

        CronEntry minutes = new CronEntry("Minutes", tokens.get(MINUTES), 0, 60);
        minutes.currentWhen = calculateValues(minutes);
        result.add(minutes);

        CronEntry hours = new CronEntry("Hours", tokens.get(HOURS), 0, 24);
        hours.currentWhen = calculateValues(hours);
        result.add(hours);

        CronEntry dayOfMonth = new CronEntry("DayOfMonth", tokens.get(DAY_OF_MONTH), 1, 32);
        dayOfMonth.currentWhen = calculateValues(dayOfMonth);
        result.add(dayOfMonth);

        CronEntry month = new CronEntry("Month", tokens.get(MONTH), 1, 12);
        month.currentWhen = calculateValues(month);
        result.add(month);

        CronEntry dayOfWeek = new CronEntry("DayOfWeek", tokens.get(DAY_OF_WEEK), 0, 6);
        dayOfWeek.currentWhen = calculateValues(dayOfWeek);
        result.add(dayOfWeek);

        return result;
    }

    /**
     * 
     * @param entry
     * @return
     */
    protected static List<Integer> calculateValues(final CronEntry entry) {
        List<Integer> result = new ArrayList<Integer>();
        if (isAll(entry.token)) {
            for (int i = entry.start; i <= entry.end; i++) {
                result.add(i);
            }
        } else if (isAStep(entry.token)) {
            int denominator = getDenominator(entry.token);
            String numerator = getNumerator(entry.token);
            CronEntry ce = new CronEntry(entry.name, numerator, entry.start, entry.end);
            List<Integer> list = calculateValues(ce);
            for (Integer i : list) {
                if (i.intValue() % denominator == 0) {
                    result.add(i);
                }
            }
        } else if (isAList(entry.token)) {
            StringTokenizer tokenizer = new StringTokenizer(entry.token, ",");
            while (tokenizer.hasMoreTokens()) {
                String str = tokenizer.nextToken();
                CronEntry ce = new CronEntry(entry.name, str, entry.start, entry.end);
                List<Integer> list = calculateValues(ce);
                result.addAll(list);
            }
        } else if (isARange(entry.token)) {
            int index = entry.token.indexOf('-');
            int first = Integer.parseInt(entry.token.substring(0, index));
            int last = Integer.parseInt(entry.token.substring(index + 1));
            for (int i = first; i <= last; i++) {
                result.add(i);
            }
        } else {
            int value = Integer.parseInt(entry.token);
            result.add(value);
        }
        Collections.sort(result);
        return result;
    }

    /**
     * 判断token是*或者？，表示的是 cron表达式此位置是所有的意思
     * 
     * @param token
     * @return
     */
    protected static boolean isAll(String token) {
        return token != null && token.length() == 1 && (token.charAt(0) == '*' || token.charAt(0) == '?');
    }

    /**
     * 判断token含有-字符，表示的是 cron表达式此位置是一个时间区间
     * 
     * @param token
     * @return
     */
    protected static boolean isARange(String token) {
        return token != null && token.indexOf('-') >= 0;
    }

    /**
     * 判断token含有/字符，表示的是 cron表达式此位置是每隔多长时间
     * 
     * @param token
     * @return
     */
    protected static boolean isAStep(String token) {
        return token != null && token.indexOf('/') >= 0;
    }

    /**
     * 判断token含有,字符，表示的是 cron表达式此位置是多个时间位置
     * 
     * @param token
     * @return
     */
    protected static boolean isAList(String token) {
        return token != null && token.indexOf(',') >= 0;
    }

    /**
     * /：表示起始时间开始触发，然后每隔固定时间触发一次。例如在Minutes域使用5/20,则意味着5分钟触发一次，而25，45等分别触发一次
     * <p>
     * 获取/符号后的值，即每隔多久的时间
     * 
     * @param token
     * @return
     */
    protected static int getDenominator(final String token) {
        int result = 0;
        int index = token.indexOf('/');
        String str = token.substring(index + 1);
        result = Integer.parseInt(str);
        return result;
    }

    /**
     * /：表示起始时间开始触发，然后每隔固定时间触发一次。例如在Minutes域使用5/20,则意味着5分钟触发一次，而25，45等分别触发一次
     * <p>
     * 获取/符号前的值，即第一次开始的时间
     * 
     * @param token
     * @return
     */
    protected static String getNumerator(final String token) {
        int index = token.indexOf('/');
        String str = token.substring(0, index);
        return str;
    }

    /**
     * 判断当前时间的 参数 是否在cron表达式中
     * 
     * @param entry
     *            cron表达式中 是否包含current
     * @param current
     *            当前时间的 分钟数
     * @return
     * @throws MessageFormatException
     */
    static boolean isCurrent(final CronEntry entry, final int current) throws MessageFormatException {
        boolean result = entry.currentWhen.contains(new Integer(current));
        return result;
    }

    /**
     * 
     * @param entry
     * @param current
     * @param working
     * @return
     * @throws MessageFormatException
     */
    static int getNext(final CronEntry entry, final int current, final Calendar working) throws MessageFormatException {
        int result = 0;

        if (entry.currentWhen == null) {
            entry.currentWhen = calculateValues(entry);
        }

        List<Integer> list = entry.currentWhen;
        int next = -1;
        for (Integer i : list) {
            if (i.intValue() > current) {
                next = i.intValue();
                break;
            }
        }
        if (next != -1) {
            result = next - current;
        } else {
            int first = list.get(0).intValue();

            int fixedEnd = entry.end;

            // months have different max values
            if ("DayOfMonth".equals(entry.name)) {
                fixedEnd = working.getActualMaximum(Calendar.DAY_OF_MONTH) + 1;
            }

            result = fixedEnd + first - entry.start - current;

            // Account for difference of one vs zero based indices.
            if (entry.name.equals("DayOfWeek") || entry.name.equals("Month")) {
                result++;
            }
        }

        return result;
    }

    protected static long doUpdateCurrentMonth(Calendar working, CronEntry month) throws MessageFormatException {

        int currentMonth = working.get(Calendar.MONTH) + 1;
        if (!isCurrent(month, currentMonth)) {
            int nextMonth = getNext(month, currentMonth, working);
            working.add(Calendar.MONTH, nextMonth);

            // Reset to start of month.
            resetToStartOfDay(working, 1);

            return working.getTimeInMillis();
        }

        return 0L;
    }

    protected static long doUpdateCurrentDay(Calendar working, CronEntry dayOfMonth, CronEntry dayOfWeek)
            throws MessageFormatException {

        int currentDayOfWeek = working.get(Calendar.DAY_OF_WEEK) - 1;
        int currentDayOfMonth = working.get(Calendar.DAY_OF_MONTH);

        // Simplest case, both are unrestricted or both match today otherwise
        // result must be the closer of the two if both are set, or the next
        // match to the one that is.
        if (!isCurrent(dayOfWeek, currentDayOfWeek) || !isCurrent(dayOfMonth, currentDayOfMonth)) {

            int nextWeekDay = Integer.MAX_VALUE;
            int nextCalendarDay = Integer.MAX_VALUE;

            if (!isCurrent(dayOfWeek, currentDayOfWeek)) {
                nextWeekDay = getNext(dayOfWeek, currentDayOfWeek, working);
            }

            if (!isCurrent(dayOfMonth, currentDayOfMonth)) {
                nextCalendarDay = getNext(dayOfMonth, currentDayOfMonth, working);
            }

            if (nextWeekDay < nextCalendarDay) {
                working.add(Calendar.DAY_OF_WEEK, nextWeekDay);
            } else {
                working.add(Calendar.DAY_OF_MONTH, nextCalendarDay);
            }

            // Since the day changed, we restart the clock at the start of the day
            // so that the next time will either be at 12am + value of hours and
            // minutes pattern.
            resetToStartOfDay(working, working.get(Calendar.DAY_OF_MONTH));

            return working.getTimeInMillis();
        }

        return 0L;
    }

    protected static void resetToStartOfDay(Calendar target, int day) {
        target.set(Calendar.DAY_OF_MONTH, day);
        target.set(Calendar.HOUR_OF_DAY, 0);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);
    }

    public static void validate(final String cronEntry) throws MessageFormatException {
        List<String> list = tokenize(cronEntry);
        List<CronEntry> entries = buildCronEntries(list);
        for (CronEntry e : entries) {
            validate(e);
        }
    }

    static void validate(final CronEntry entry) throws MessageFormatException {

        List<Integer> list = entry.currentWhen;
        if (list.isEmpty() || list.get(0).intValue() < entry.start || list.get(list.size() - 1).intValue() > entry.end) {
            throw new MessageFormatException("Invalid token: " + entry);
        }
    }

    /**
     * 
     * @author gyli
     *
     */
    static class CronEntry {
        final String name;
        final String token;
        final int start;
        final int end;

        List<Integer> currentWhen;

        /**
         * 构造函数
         * 
         * @param name
         * @param token
         * @param start
         * @param end
         */
        CronEntry(String name, String token, int start, int end) {
            this.name = name;
            this.token = token;
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return this.name + ":" + token;
        }
    }

    public static void main(String[] args) {
        String a = "sdf sdf dft gfg trt dgg";
        String b = "sdf sdf dft gfg trt";
        System.out.println(tokenize(b));

        CronEntry e = new CronEntry("test", "0/5", 0, 60);
        System.out.println(calculateValues(e));

        String as = "0/5 14,18 * * ?";
        buildCronEntries(tokenize(as));
    }
}
