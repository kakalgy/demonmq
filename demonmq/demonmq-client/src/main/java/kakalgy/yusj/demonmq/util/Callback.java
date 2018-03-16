package kakalgy.yusj.demonmq.util;

/**
 * A simple callback object used by objects to provide automatic transactional
 * or exception handling blocks.
 * <p>
 * 
 * 此接口是一个事务在执行commit操作时，在没有异常抛出或者有异常抛出时执行的一段代码
 * 
 * @author gyli
 *
 */
public interface Callback {
    /**
     * Executes some piece of code within a transaction performing a commit if there
     * is no exception thrown else a rollback is performed
     * <p>
     * 此方法是一个事务在执行commit操作时，在没有异常抛出或者有异常抛出时执行的一段代码
     * 
     * @throws Exception
     *             TODO
     */
    void execute() throws Exception;
}
