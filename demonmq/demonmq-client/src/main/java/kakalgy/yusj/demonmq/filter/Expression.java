package kakalgy.yusj.demonmq.filter;

import javax.jms.JMSException;

/**
 * Represents an expression
 * 
 * 
 */
public interface Expression {

    /**
     * @return the value of this expression
     */
    Object evaluate(MessageEvaluationContext message) throws JMSException;

}
