package kakalgy.yusj.demonmq.jndi;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Properties;

import javax.naming.NamingException;
import javax.naming.Reference;

/**
 * Facilitates objects to be stored in JNDI as properties
 * <p>
 * 使对象能够作为property被存储到JNDI中
 * <p>
 * Externalizable接口使对象能够被序列化，与Serializable接口的区别是
 * 
 * @author gyli
 *
 */
public abstract class JNDIBaseStorable implements JNDIStorableInterface, Externalizable {

	private Properties properties;

	/**
	 * Set the properties that will represent the instance in JNDI
	 * <p>
	 * set方法，注意与{@link kakalgy.yusj.demonmq.jndi.JNDIBaseStorable.setProperties(Properties
	 * properties)}方法的区别
	 * 
	 * @param properties
	 */
	protected abstract void buildFromProperties(Properties properties);

	/**
	 * Initialize the instance from properties stored in JNDI
	 * <p>
	 * get方法
	 * 
	 * @param properties
	 */
	protected abstract void populateProperties(Properties properties);

	/**
	 * Retrive a Reference for this instance to store in JNDI
	 * <p>
	 * 生成当前类的Reference
	 * 
	 * @return the built Reference
	 * @throws NamingException
	 *             if error on building Reference
	 */
	public Reference getReference() throws NamingException {
		// TODO Auto-generated method stub
		return JNDIReferenceFactory.createReference(this.getClass().getName(), this);
	}

	/**
	 * @param out
	 * @throws IOException
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeObject(getProperties());
	}

	/**
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Properties props = (Properties) in.readObject();
		if (props != null) {
			setProperties(props);
		}
	}

	/**
	 * set the properties for this instance as retrieved from JNDI
	 */
	public synchronized void setProperties(Properties properties) {
		// TODO Auto-generated method stub
		this.properties = properties;
		buildFromProperties(properties);
	}

	/**
	 * Get the properties from this instance for storing in JNDI
	 */
	public Properties getProperties() {
		// TODO Auto-generated method stub
		if (this.properties == null) {
			this.properties = new Properties();
		}
		populateProperties(properties);
		return this.properties;
	}

}
