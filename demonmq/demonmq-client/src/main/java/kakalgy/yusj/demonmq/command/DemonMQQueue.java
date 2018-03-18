package kakalgy.yusj.demonmq.command;

import java.util.Properties;

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

    public String getQueueName() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    public byte getDataStructureType() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isMarshallAware() {
        // TODO Auto-generated method stub
        return false;
    }

    public int compareTo(Object o) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void buildFromProperties(Properties properties) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void populateProperties(Properties properties) {
        // TODO Auto-generated method stub

    }

}
