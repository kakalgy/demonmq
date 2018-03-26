package kakalgy.yusj.demonmq.command;

import javax.jms.JMSException;
import javax.jms.Topic;

/**
 * @org.apache.xbean.XBean element="topic" description="An ActiveMQ Topic
 *                         Destination"
 * @openwire:marshaller code="101"
 * @author gyli
 *
 */
public class DemonMQTopic extends DemonMQDestination implements Topic {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.DEMONMQ_TOPIC;
    private static final long serialVersionUID = 7300307405896488588L;

    /**
     * 构造函数
     */
    public DemonMQTopic() {
    }

    /**
     * 构造函数
     */
    public DemonMQTopic(String name) {
        super(name);
    }

    /**
     * 实现DataStructure接口
     */
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    /**
     * 重写DemonMQDestination方法
     */
    public boolean isTopic() {
        return true;
    }

    /**
     * 实现Topic接口
     */
    public String getTopicName() throws JMSException {
        return getPhysicalName();
    }

    /**
     * DemonMQDestination抽象方法
     */
    @Override
    public byte getDestinationType() {
        return TOPIC_TYPE;
    }

    /**
     * DemonMQDestination抽象方法
     */
    @Override
    protected String getQualifiedPrefix() {
        return TOPIC_QUALIFIED_PREFIX;
    }

}
