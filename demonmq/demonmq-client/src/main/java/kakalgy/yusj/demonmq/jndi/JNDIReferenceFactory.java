package kakalgy.yusj.demonmq.jndi;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts objects implementing JNDIStorable into a property fields so they can
 * be stored and regenerated from JNDI
 * <p>
 * 将实现JNDIStorable接口的对象转换为可以在JNDI中存入和读取的property属性
 * 
 * @author gyli
 *
 */
public class JNDIReferenceFactory implements ObjectFactory {
    private static final Logger logger = LoggerFactory.getLogger(JNDIReferenceFactory.class);

    /**
     * This will be called by a JNDIprovider when a Reference is retrieved from a
     * JNDI store - and generates the orignal instance
     * 
     * @param object
     *            the Reference object
     * @param name
     *            the JNDI name
     * @param nameCtx
     *            the context
     * @param environment
     *            the environment settings used by JNDI
     * @return the instance built from the Reference object
     * @throws Exception
     *             if building the instance from Reference fails (usually class not
     *             found)
     */
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        // TODO Auto-generated method stub
        Object result = null;
    }

    /**
     * Create a Reference instance from a JNDIStorable object
     * <p>
     * 从一个JNDIStorable对象生成一个Reference实例
     * 
     * @param instanceClassName
     * @param po
     * @return
     * @throws NamingException
     */
    public static Reference createReference(String instanceClassName, JNDIStorableInterface po) throws NamingException {
        if (logger.isTraceEnabled()) {
            logger.trace(
                    "Creating reference [instanceClassName : " + instanceClassName + ", JNDIStorableInterface: " + po + "]");
        }
        Reference result = new Reference(instanceClassName, JNDIReferenceFactory.class.getName(), null);
        try {
            Properties props = po.getProperties();
            for (Enumeration iter = props.propertyNames(); iter.hasMoreElements();) {
                String key = (String) iter.nextElement();
                String value = props.getProperty(key);
                javax.naming.StringRefAddr addr = new StringRefAddr(key, value);
                result.add(addr);
            }
        } catch (Exception e) {
            // TODO: handle exception
            logger.error("JNDIReferenceFactory.createReference Exception: " + e.getMessage(), e);
            throw new NamingException("JNDIReferenceFactory.createReference Exception: " + e.getMessage());
        }
        return result;
    }

    /**
     * Retrieve the class loader for a named class
     * <p>
     * 根据className来获得Class
     * 
     * @param thisObj
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    public static Class loadClass(Object thisObj, String className) throws ClassNotFoundException {
        // try local ClassLoader first.
        ClassLoader loader = thisObj.getClass().getClassLoader();
        Class theClass;
        if (loader != null) {
            theClass = loader.loadClass(className);
        } else {
            // Will be null in jdk1.1.8,use default classLoader
            theClass = Class.forName(className);
        }
        return theClass;
    }
}
