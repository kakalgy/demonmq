package kakalgy.yusj.demonmq.management;

/**
 * Represents an object which is capable of providing some stats
 * <p>
 * 代表着一个对象可以有能力提供一些统计信息
 * 
 * @author gyli
 *
 */
public interface StatsCapable {
    /**
     * @return the Stats for this object
     */
    StatsImpl getStats();
}
