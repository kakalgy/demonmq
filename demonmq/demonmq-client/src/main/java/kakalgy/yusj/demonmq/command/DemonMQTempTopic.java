package kakalgy.yusj.demonmq.command;

import javax.jms.JMSException;
import javax.jms.TemporaryTopic;

/**
 * @org.apache.xbean.XBean element="tempTopic" description="An ActiveMQ
 *                         Temporary Topic Destination"
 * @openwire:marshaller code="103"
 * 
 */
public class DemonMQTempTopic extends DemonMQTempDestination implements TemporaryTopic {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.DEMONMQ_TEMP_TOPIC;
    private static final long serialVersionUID = -4325596784597300253L;

    /**
     * 构造函数
     */
    public DemonMQTempTopic() {
    }

    /**
     * 构造函数
     */
    public DemonMQTempTopic(String name) {
        super(name);
    }

    /**
     * 构造函数
     */
    public DemonMQTempTopic(ConnectionId connectionId, long sequenceId) {
        super(connectionId.getValue(), sequenceId);
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
     * 实现Queue接口
     */
    public String getTopicName() throws JMSException {
        return getPhysicalName();
    }

    /**
     * DemonMQDestination抽象方法
     */
    @Override
    public byte getDestinationType() {
        return TEMP_TOPIC_TYPE;
    }

    /**
     * DemonMQDestination抽象方法
     */
    @Override
    protected String getQualifiedPrefix() {
        return TEMP_TOPIC_QUALIFED_PREFIX;
    }
}
