package kakalgy.yusj.demonmq.management;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.management.j2ee.statistics.Statistic;
import javax.management.j2ee.statistics.Stats;

/**
 * Base class for a Stats implementation
 * <p>
 * 一组统计信息的基础类，可以看做是{@link StatisticImpl}的集合
 * 
 * @author gyli
 *
 */
public class StatsImpl extends StatisticImpl implements Stats, Resettable {

    /**
     * 使用Set来代替Map，用来节省空间
     */
    private Set<StatisticImpl> set;// StatisticImpl的集合，表示一组统计信息

    /**
     * 构造函数
     * 
     * @param set
     */
    public StatsImpl(Set<StatisticImpl> set) {
        // name,unit,description
        super("stats", "many", "Used only as container, not Statistic");
        this.set = set;
    }

    /**
     * 构造函数
     * <p>
     * CopyOnWriteArraySet内容： http://www.cnblogs.com/skywang12345/p/3498497.html
     */
    public StatsImpl() {
        this(new CopyOnWriteArraySet<StatisticImpl>());
    }

    /**
     * 根据statisticName获得一条统计信息
     */
    public Statistic getStatistic(String statisticName) {
        // TODO Auto-generated method stub
        for (StatisticImpl stat : this.set) {
            if (stat.getName() != null && stat.getName().equals(statisticName)) {
                return stat;
            }
        }
        return null;
    }

    /**
     * 返回所有统计信息的name
     */
    public String[] getStatisticNames() {
        // TODO Auto-generated method stub
        List<String> names = new ArrayList<String>();
        for (StatisticImpl stat : this.set) {
            names.add(stat.getName());
        }
        String[] answer = new String[names.size()];
        names.toArray(answer);
        return answer;
    }

    /**
     * 返回所有的统计信息数组
     */
    public Statistic[] getStatistics() {
        // TODO Auto-generated method stub
        Statistic[] answer = new Statistic[this.set.size()];
        set.toArray(answer);
        return answer;
    }

    /**
     * 添加statisticImpl
     * 
     * @param name
     * @param statisticImpl
     */
    public void addStatistic(String name, StatisticImpl statisticImpl) {
        this.set.add(statisticImpl);
    }

    /**
     * 重置所有的统计信息，在重置过程中，循环遍历Set，先判断StatisticImpl的doReset属性是否为true，若为true则重置StatisticImpl的startTime和lastSampleTime
     */
    @Override
    public synchronized void reset() {
        Statistic[] stats = getStatistics();
        int size = stats.length;
        for (int i = 0; i < size; i++) {
            Statistic stat = stats[i];
            if (stat instanceof Resettable) {
                Resettable r = (Resettable) stat;
                r.reset();
            }
        }
    }

}
