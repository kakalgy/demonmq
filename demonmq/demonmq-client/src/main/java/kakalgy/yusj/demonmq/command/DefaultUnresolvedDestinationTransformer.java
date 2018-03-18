package kakalgy.yusj.demonmq.command;

import java.lang.reflect.Method;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * 当 目的地(Destination) 无法分辨时的转换方法类
 * 
 * @author gyli
 *
 */
public class DefaultUnresolvedDestinationTransformer implements UnresolvedDestinationTransformer {

    /**
     * 当dest为Destination类型时，根据Destination的类型返回对应的DemonMQ队列或者DemonMQ主题
     */
    public DemonMQDestination transform(Destination dest) throws JMSException {
        // TODO Auto-generated method stub

        // 首先获得dest的Queue或者是Topic名称，MQ必须符合这两种规范，因此其中有一个String有值，有一个为null
        String queueName = ((Queue) dest).getQueueName();
        String topicName = ((Topic) dest).getTopicName();

        /**
         * 若queueName和topicName都为null，则dest参数有问题
         */
        if (queueName == null && topicName == null) {
            throw new JMSException("Unresolvable destination: Both queue and topic names are null: " + dest);
        }
        // 根据Queue或者Topic返回DemonMQQueue或者DemonMQTopic
        try {
            Method isQueueMethod = dest.getClass().getMethod("isQueue");
            Method isTopicMethod = dest.getClass().getMethod("isTopic");
            Boolean isQueue = (Boolean) isQueueMethod.invoke(dest);
            Boolean isTopic = (Boolean) isTopicMethod.invoke(dest);
            if (isQueue) {
                return new DemonMQQueue(queueName);
            } else if (isTopic) {
                return new DemonMQTopic(topicName);
            } else {
                throw new JMSException("Unresolvable destination: Neither Queue nor Topic: " + dest);
            }
        } catch (Exception e) {
            throw new JMSException("Unresolvable destination: " + e.getMessage() + ": " + dest);
        }
    }

    /**
     * 当dest为String类型时，返回的目的地是DemonMQ队列
     */
    public DemonMQDestination transform(String dest) throws JMSException {
        // TODO Auto-generated method stub

        return new DemonMQQueue(dest);
    }

}
