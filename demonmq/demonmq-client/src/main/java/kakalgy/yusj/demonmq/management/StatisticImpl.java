package kakalgy.yusj.demonmq.management;

import javax.management.j2ee.statistics.Statistic;

/**
 * Base class for a Statistic implementation
 * <p>
 * 一行统计信息的基础类
 * 
 * @author gyli
 *
 */
public class StatisticImpl implements Statistic, Resettable {

    /**
     * 
     */
    protected boolean enabled;

    private String name;// 统计名称
    private String unit;// 统计单位
    private String description;// 统计描述
    private long startTime;// 开始时间
    private long lastSampleTime;// 上次统计时间
    private boolean doReset = true;

    /**
     * 构造函数
     * <p>
     * 默认将startTime和lastSampleTime都置为当前系统时间
     * 
     * @param name
     * @param unit
     * @param description
     */
    public StatisticImpl(String name, String unit, String description) {
        this.name = name;
        this.unit = unit;
        this.description = description;
        this.startTime = System.currentTimeMillis();
        this.lastSampleTime = this.startTime;
    }

    /**
     * 重置统计数据，将startTime和lastSampleTime都置为当前系统时间
     */
    public synchronized void reset() {
        if (isDoReset()) {
            this.startTime = System.currentTimeMillis();
            this.lastSampleTime = this.startTime;
        }
    }

    /**
     * 将lastSampleTime更新为系统当前时间
     */
    public synchronized void updateSampleTime() {
        this.lastSampleTime = System.currentTimeMillis();
    }

    public synchronized String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(name);
        buffer.append("{");
        appendFieldDescription(buffer);
        buffer.append(" }");
        return buffer.toString();
    }

    public String getDescription() {
        return this.description;
    }

    public synchronized long getLastSampleTime() {
        return this.lastSampleTime;
    }

    public String getName() {
        return this.name;
    }

    public synchronized long getStartTime() {
        return this.getStartTime();
    }

    public String getUnit() {
        return this.unit;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDoReset() {
        return this.doReset;
    }

    public void setDoReset(boolean doReset) {
        this.doReset = doReset;
    }

    /**
     * 将对象信息 用字符串形式转换
     * 
     * @param buffer
     */
    protected synchronized void appendFieldDescription(StringBuffer buffer) {
        buffer.append(" unit: ");
        buffer.append(this.unit);
        buffer.append(" startTime: ");
        // buffer.append(new Date(startTime));
        buffer.append(this.startTime);
        buffer.append(" lastSampleTime: ");
        // buffer.append(new Date(lastSampleTime));
        buffer.append(this.lastSampleTime);
        buffer.append(" description: ");
        buffer.append(this.description);
    }

}
