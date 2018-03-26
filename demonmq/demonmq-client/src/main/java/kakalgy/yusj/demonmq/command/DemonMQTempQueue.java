package kakalgy.yusj.demonmq.command;

import javax.jms.JMSException;
import javax.jms.TemporaryQueue;

/**
 * @org.apache.xbean.XBean element="tempQueue" description="An ActiveMQ
 *                         Temporary Queue Destination"
 * @openwire:marshaller code="102"
 * 
 */
public class DemonMQTempQueue extends DemonMQTempDestination implements TemporaryQueue {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.DEMONMQ_TEMP_QUEUE;
    private static final long serialVersionUID = 6683049467527633867L;

    /**
     * 构造函数
     */
    public DemonMQTempQueue() {
    }

    /**
     * 构造函数
     */
    public DemonMQTempQueue(String name) {
        super(name);
    }

    /**
     * 构造函数
     */
    public DemonMQTempQueue(ConnectionId connectionId, long sequenceId) {
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
    public boolean isQueue() {
        return true;
    }

    /**
     * 实现Queue接口
     */
    public String getQueueName() throws JMSException {
        return getPhysicalName();
    }

    /**
     * DemonMQDestination抽象方法
     */
    @Override
    public byte getDestinationType() {
        return TEMP_QUEUE_TYPE;
    }

    /**
     * DemonMQDestination抽象方法
     */
    @Override
    protected String getQualifiedPrefix() {
        return TEMP_QUEUE_QUALIFED_PREFIX;
    }

}
