package kakalgy.yusj.demonmq.command;

import javax.jms.Destination;
import javax.jms.JMSException;

/**
 * 当 目的地(Destination)
 * 无法分辨时的转换方法，此接口的默认实现方法是{@link DefaultUnresolvedDestinationTransformer}}
 * 
 * @author gyli
 *
 */
public interface UnresolvedDestinationTransformer {
    public DemonMQDestination transform(Destination dest) throws JMSException;

    public DemonMQDestination transform(String dest) throws JMSException;
}
