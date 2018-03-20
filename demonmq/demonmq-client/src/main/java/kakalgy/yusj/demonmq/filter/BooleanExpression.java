package kakalgy.yusj.demonmq.filter;

/**
 * A BooleanExpression is an expression that always produces a Boolean result.
 * 
 * @author gyli
 *
 */
public interface BooleanExpression extends Expression {
    /**
     * @param message
     * @return true if the expression evaluates to Boolean.TRUE.
     * @throws JMSException
     */
    boolean matches(MessageEvaluationContext message) throws JMSException;
}
