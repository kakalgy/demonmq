package kakalgy.yusj.demonmq.jndi;

import java.util.Properties;

import javax.naming.Referenceable;

/**
 * Facilitates objects to be stored in JNDI as properties
 * <p>
 * 使对象能够作为property被存储到JNDI中
 * <p>
 * Referenceable接口中的getReference()方法可以实现一个对象提供一个指向自己的引用
 * 
 * @author gyli
 *
 */
public interface JNDIStorableInterface extends Referenceable {
    /**
     * set the properties for this instance as retrieved(恢复) from JNDI
     *
     * @param properties
     */
    void setProperties(Properties properties);

    /**
     * Get the properties from this instance for storing in JNDI
     *
     * @return the properties that should be stored in JNDI
     */
    Properties getProperties();
}
