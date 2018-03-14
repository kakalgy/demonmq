package kakalgy.yusj.demonmq.management;

/**
 * Represents some statistic that is capable of being reset
 * <p>
 * 代表着统计信息可以被重置
 * 
 * @author gyli
 *
 */
public interface Resettable {
    /**
     * Reset the statistic
     */
    void reset();
}
