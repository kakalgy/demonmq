package kakalgy.yusj.demonmq.filter;

import kakalgy.yusj.demonmq.command.DemonMQDestination;

/**
 * allow match to any set of composite destinations, both queues and topics
 * <p>
 * 可以用来匹配任何类型的DemonMQDestination
 * 
 * @author gyli
 *
 */
public class AnyDestination extends DemonMQDestination {
    /**
     * 构造函数
     * 
     * @param destinations
     */
    public AnyDestination(DemonMQDestination[] destinations) {
        super(destinations);
        // ensure we are small when it comes to comparison in DestinationMap
        physicalName = "!0";
    }

    /**
     * 继承自DemonMQDestination
     */
    @Override
    protected String getQualifiedPrefix() {
        // TODO Auto-generated method stub
        return "Any://";
    }

    /**
     * 继承自DemonMQDestination
     */
    @Override
    public byte getDestinationType() {
        // TODO Auto-generated method stub
        return DemonMQDestination.QUEUE_TYPE & DemonMQDestination.TOPIC_TYPE;
    }

    /**
     * 实现DataStructure接口
     */
    public byte getDataStructureType() {
        // TODO Auto-generated method stub
        throw new IllegalStateException("not for marshalling");
    }

    /**
     * 继承自DemonMQDestination
     */
    @Override
    public boolean isQueue() {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * 继承自DemonMQDestination
     */
    @Override
    public boolean isTopic() {
        // TODO Auto-generated method stub
        return true;
    }
}
