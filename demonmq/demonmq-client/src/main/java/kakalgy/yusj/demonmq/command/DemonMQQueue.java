package kakalgy.yusj.demonmq.command;

import javax.jms.JMSException;
import javax.jms.Queue;

/**
 * @org.apache.xbean.XBean element="queue" description="An ActiveMQ Queue
 *                         Destination
 * @openwire:marshaller code="100"
 *                      <p>
 *                      {@link CommandTypes}中DEMONMQ_QUEUE = 100;
 * @author gyli
 *
 */
public class DemonMQQueue extends DemonMQDestination implements Queue {

    /**
     * DemonMQQueue的byte类型为100
     */
    private static final byte DATA_STRUCTURE_TYPE = CommandTypes.DEMONMQ_QUEUE;

    private static final long serialVersionUID = -3885260014960795889L;

    /**
     * 构造函数
     */
    public DemonMQQueue() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 构造函数
     * 
     * @param name
     */
    public DemonMQQueue(String name) {
        super(name);
    }

    /**
     * 实现Queue接口
     */
    public String getQueueName() throws JMSException {
        // TODO Auto-generated method stub
        return getPhysicalName();
    }

    /**
     * 实现DataStructure接口
     */
    public byte getDataStructureType() {
        // TODO Auto-generated method stub
        return DATA_STRUCTURE_TYPE;
    }

    /**
     * 重写DemonMQDestination方法
     */
    public boolean isQueue() {
        return true;
    }

    /**
     * DemonMQDestination抽象方法
     */
    @Override
    public byte getDestinationType() {
        return QUEUE_TYPE;
    }

    /**
     * DemonMQDestination抽象方法
     */
    @Override
    protected String getQualifiedPrefix() {
        return QUEUE_QUALIFIED_PREFIX;
    }

}
