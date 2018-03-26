package kakalgy.yusj.demonmq.command;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;

import kakalgy.yusj.demonmq.filter.AnyDestination;
import kakalgy.yusj.demonmq.filter.DestinationFilter;
import kakalgy.yusj.demonmq.jndi.JNDIBaseStorable;
import kakalgy.yusj.demonmq.util.IntrospectionSupport;
import kakalgy.yusj.demonmq.util.URISupport;

/**
 * 
 * @author gyli
 *
 */
public abstract class DemonMQDestination extends JNDIBaseStorable
        implements DataStructure, Destination, Externalizable, Comparable<Object> {
    private static final long serialVersionUID = -3885260014960795889L;

    public static final String PATH_SEPERATOR = ".";
    public static final char COMPOSITE_SEPERATOR = ',';

    // ###MQ的类型
    public static final byte QUEUE_TYPE = 0x01;// 1
    public static final byte TOPIC_TYPE = 0x02;// 2
    public static final byte TEMP_MASK = 0x04;// 4
    public static final byte TEMP_TOPIC_TYPE = TOPIC_TYPE | TEMP_MASK;// 6
    public static final byte TEMP_QUEUE_TYPE = QUEUE_TYPE | TEMP_MASK;// 5

    // ###MQ的类型前缀
    public static final String QUEUE_QUALIFIED_PREFIX = "queue://";
    public static final String TOPIC_QUALIFIED_PREFIX = "topic://";
    public static final String TEMP_QUEUE_QUALIFED_PREFIX = "temp-queue://";
    public static final String TEMP_TOPIC_QUALIFED_PREFIX = "temp-topic://";
    public static final String IS_DLQ = "isDLQ";// DLQ-死信队列(Dead Letter Queue)用来保存处理失败或者过期的消息

    public static final String TEMP_DESTINATION_NAME_PREFIX = "ID:";

    protected String physicalName;

    protected transient DemonMQDestination[] compositeDestinations;// DemonMQDestination数组，组合DemonMQDestination
    protected transient String[] destinationPaths;
    protected transient boolean isPattern;
    protected transient int hashValue;
    /**
     * 队列选项（Destination
     * options）队列选项是给consumer在JMS规范之外添加的功能特性，通过在队列名称后面使用类似URL的语法添加多个选项
     * 
     * <pre>
     * http://blog.csdn.net/kimmking/article/details/8440994
     * </pre>
     */
    protected Map<String, String> options;//

    protected static UnresolvedDestinationTransformer unresolvableDestinationTransformer = new DefaultUnresolvedDestinationTransformer();

    /**
     * 构造函数
     */
    public DemonMQDestination() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 构造函数
     * 
     * @param name
     */
    protected DemonMQDestination(String name) {
        setPhysicalName(name);
    }

    /**
     * 构造函数
     * 
     * @param composites
     */
    public DemonMQDestination(DemonMQDestination composites[]) {
        setCompositeDestinations(composites);
    }

    /**
     * 实现Comparable<Object>接口
     */
    public int compareTo(Object that) {
        // TODO Auto-generated method stub
        if (that instanceof DemonMQDestination) {
            return compare(this, (DemonMQDestination) that);
        }
        if (that == null) {
            return 1;
        } else {
            return getClass().getName().compareTo(that.getClass().getName());
        }
    }

    // ####################################################
    // static helper methods for working with destinations
    // 以下几个方法是 用来操作 目的地的静态方法
    // ####################################################
    /**
     * 创建目的地，根据name的前缀来判断应该生成哪一种目的地（DemonMQQueue，DemonMQTopic， DemonMQTempQueue，
     * DemonMQTempTopic），若name中不包括这些前缀，则通过defaultType来判断
     * 
     * @param name
     * @param defaultType
     * @return
     */
    public static DemonMQDestination createDestination(String name, byte defaultType) {
        if (name.startsWith(QUEUE_QUALIFIED_PREFIX)) {
            return new DemonMQQueue(name.substring(QUEUE_QUALIFIED_PREFIX.length()));
        } else if (name.startsWith(TOPIC_QUALIFIED_PREFIX)) {
            return new DemonMQTopic(name.substring(TOPIC_QUALIFIED_PREFIX.length()));
        } else if (name.startsWith(TEMP_QUEUE_QUALIFED_PREFIX)) {
            return new DemonMQTempQueue(name.substring(TEMP_QUEUE_QUALIFED_PREFIX.length()));
        } else if (name.startsWith(TEMP_TOPIC_QUALIFED_PREFIX)) {
            return new DemonMQTempTopic(name.substring(TEMP_TOPIC_QUALIFED_PREFIX.length()));
        }

        switch (defaultType) {
        case QUEUE_TYPE:
            return new DemonMQQueue(name);
        case TOPIC_TYPE:
            return new DemonMQTopic(name);
        case TEMP_QUEUE_TYPE:
            return new DemonMQTempQueue(name);
        case TEMP_TOPIC_TYPE:
            return new DemonMQTempTopic(name);
        default:
            throw new IllegalArgumentException("Invalid default destination type: " + defaultType);
        }
    }

    /**
     * 将JMS类型的Destination转换为DemonMQDestination
     * 
     * @param dest
     * @return
     * @throws JMSException
     */
    public static DemonMQDestination transform(Destination dest) throws JMSException {
        if (dest == null) {
            return null;
        }
        if (dest instanceof DemonMQDestination) {
            return (DemonMQDestination) dest;
        }

        if (dest instanceof Queue && dest instanceof Topic) {
            String queueName = ((Queue) dest).getQueueName();
            String topicName = ((Topic) dest).getTopicName();
            if (queueName != null && topicName == null) {
                return new DemonMQQueue(queueName);
            } else if (queueName == null && topicName != null) {
                return new DemonMQTopic(topicName);
            } else {
                return unresolvableDestinationTransformer.transform(dest);
            }
        }
        if (dest instanceof TemporaryQueue) {
            return new DemonMQTempQueue(((TemporaryQueue) dest).getQueueName());
        }
        if (dest instanceof TemporaryTopic) {
            return new DemonMQTempTopic(((TemporaryTopic) dest).getTopicName());
        }
        if (dest instanceof Queue) {
            return new DemonMQQueue(((Queue) dest).getQueueName());
        }
        if (dest instanceof Topic) {
            return new DemonMQTopic(((Topic) dest).getTopicName());
        }
        throw new JMSException("Could not transform the destination into a ActiveMQ destination: " + dest);
    }

    /**
     * 比较destination和destination2
     * 
     * @param destination
     * @param destination2
     * @return
     */
    public static int compare(DemonMQDestination destination, DemonMQDestination destination2) {
        if (destination == destination2) {
            return 0;
        }
        if (destination == null || destination2 instanceof AnyDestination) {
            return -1;
        } else if (destination2 == null || destination instanceof AnyDestination) {
            return 1;
        } else {
            if (destination.getDestinationType() == destination2.getDestinationType()) {

                if (destination.isPattern() && destination2.isPattern()) {
                    if (destination.getPhysicalName().compareTo(destination2.getPhysicalName()) == 0) {
                        return 0;
                    }
                }
                if (destination.isPattern()) {
                    DestinationFilter filter = DestinationFilter.parseFilter(destination);
                    if (filter.matches(destination2)) {
                        return 1;
                    }
                }
                if (destination2.isPattern()) {
                    DestinationFilter filter = DestinationFilter.parseFilter(destination2);
                    if (filter.matches(destination)) {
                        return -1;
                    }
                }
                return destination.getPhysicalName().compareTo(destination2.getPhysicalName());

            } else {
                return destination.isQueue() ? -1 : 1;
            }
        }
    }

    /**
     * 根据传入值physicalName得到真正的physicalName，以及compositeDestinations，传入的值应该是
     * physicalName?a=**&b=**
     * 
     * @openwire:property version=1
     * @param physicalName
     */
    public void setPhysicalName(String physicalName) {
        physicalName = physicalName.trim();
        final int length = physicalName.length();

        if (physicalName.isEmpty()) {
            throw new IllegalArgumentException("Invalid destination name: a non-empty name is required");
        }
        // options offset
        int p = -1;
        boolean composite = false;
        for (int i = 0; i < length; i++) {
            char c = physicalName.charAt(i);
            if (c == '?') {
                p = i;
                break;
            }
            if (c == COMPOSITE_SEPERATOR) {
                // won't be wild card
                isPattern = false;
                composite = true;
            } else if (!composite && (c == '*' || c == '>')) {
                isPattern = true;
            }
        }
        // Strip off any options
        if (p >= 0) {
            String optstring = physicalName.substring(p + 1);
            physicalName = physicalName.substring(0, p);
            try {
                options = URISupport.parseQuery(optstring);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(
                        "Invalid destination name: " + physicalName + ", it's options are not encoded properly: " + e);
            }
        }
        this.physicalName = physicalName;
        this.destinationPaths = null;
        this.hashValue = 0;
        if (composite) {
            // Check to see if it is a composite.
            Set<String> l = new HashSet<String>();
            StringTokenizer iter = new StringTokenizer(physicalName, "" + COMPOSITE_SEPERATOR);
            while (iter.hasMoreTokens()) {
                String name = iter.nextToken().trim();
                if (name.length() == 0) {
                    continue;
                }
                l.add(name);
            }
            compositeDestinations = new DemonMQDestination[l.size()];
            int counter = 0;
            for (String dest : l) {
                compositeDestinations[counter++] = createDestination(dest);
            }
        }
    }

    /**
     * 
     * @param destinations
     */
    public void setCompositeDestinations(DemonMQDestination[] destinations) {
        this.compositeDestinations = destinations;
        this.destinationPaths = null;
        this.hashValue = 0;
        this.isPattern = false;

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < destinations.length; i++) {
            if (i != 0) {
                sb.append(COMPOSITE_SEPERATOR);
            }
            if (getDestinationType() == destinations[i].getDestinationType()) {
                sb.append(destinations[i].getPhysicalName());
            } else {
                sb.append(destinations[i].getQualifiedName());
            }
        }
        physicalName = sb.toString();
    }

    public boolean isComposite() {
        return compositeDestinations != null;
    }

    public DemonMQDestination[] getCompositeDestinations() {
        return compositeDestinations;
    }

    public String getQualifiedName() {
        if (isComposite()) {
            return physicalName;
        }
        return getQualifiedPrefix() + physicalName;
    }

    /**
     * @openwire:property version=1
     */
    public String getPhysicalName() {
        return physicalName;
    }

    public DemonMQDestination createDestination(String name) {
        return createDestination(name, getDestinationType());
    }

    public String[] getDestinationPaths() {

        if (destinationPaths != null) {
            return destinationPaths;
        }

        List<String> l = new ArrayList<String>();
        StringBuilder level = new StringBuilder();
        final char separator = PATH_SEPERATOR.charAt(0);
        for (char c : physicalName.toCharArray()) {
            if (c == separator) {
                l.add(level.toString());
                level.delete(0, level.length());
            } else {
                level.append(c);
            }
        }
        l.add(level.toString());

        destinationPaths = new String[l.size()];
        l.toArray(destinationPaths);
        return destinationPaths;
    }

    public abstract byte getDestinationType();

    protected abstract String getQualifiedPrefix();

    public boolean isQueue() {
        return false;
    }

    public boolean isTopic() {
        return false;
    }

    public boolean isTemporary() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DemonMQDestination d = (DemonMQDestination) o;
        return physicalName.equals(d.physicalName);
    }

    @Override
    public int hashCode() {
        if (hashValue == 0) {
            hashValue = physicalName.hashCode();
        }
        return hashValue;
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }

    /**
     * 序列化的时候 只需要 String PhysicalName和 Map<String, String> options
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.getPhysicalName());
        out.writeObject(options);
    }

    /**
     * 反序列化的时候 只需要 String PhysicalName和 Map<String, String> options
     */
    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setPhysicalName(in.readUTF());
        this.options = (Map<String, String>) in.readObject();
    }

    /**
     * 根据byte类型确认是哪一种终端
     * 
     * @return
     */
    public String getDestinationTypeAsString() {
        switch (getDestinationType()) {
        case QUEUE_TYPE:
            return "Queue";
        case TOPIC_TYPE:
            return "Topic";
        case TEMP_QUEUE_TYPE:
            return "TempQueue";
        case TEMP_TOPIC_TYPE:
            return "TempTopic";
        default:
            throw new IllegalArgumentException("Invalid destination type: " + getDestinationType());
        }
    }

    public Map<String, String> getOptions() {
        return this.options;
    }

    /**
     * DataStructure接口
     */
    public boolean isMarshallAware() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * JNDIBaseStorable的抽象方法
     * <p>
     * 将Properties里的键值对设置到本对象中
     */
    @Override
    public void buildFromProperties(Properties properties) {
        if (properties == null) {
            properties = new Properties();
        }

        IntrospectionSupport.setProperties(this, properties);
    }

    /**
     * JNDIBaseStorable的抽象方法
     * <p>
     * 将physicalName加入Properties的键值对
     */
    @Override
    public void populateProperties(Properties props) {
        props.setProperty("physicalName", getPhysicalName());
    }

    public boolean isPattern() {
        return this.isPattern;
    }

    /**
     * http://blog.csdn.net/xiaxiaorui2003/article/details/53031986
     * <p>
     * 出现以下情况时，消息会被redelivered
     * <ul>
     * <li>A transacted session is used and rollback() is called.</li>
     * <li>A transacted session is closed before commit is called.</li>
     * <li>A session is using CLIENT_ACKNOWLEDGE and Session.recover() is
     * called.</li>
     * </ul>
     * 当一个消息被redelivered超过maximumRedeliveries(缺省为6次，具体设置请参考后面的链接)次数时，会给broker发送一个"Poison
     * ack"，这个消息被认为是a poison pill，这时broker会将这个消息发送到DLQ，以便后续处理。
     * <p>
     * 缺省的死信队列是ActiveMQ.DLQ，如果没有特别指定，死信都会被发送到这个队列。
     * <p>
     * 缺省持久消息过期，会被送到DLQ，非持久消息不会送到DLQ
     * <p>
     * 可以通过配置文件(activemq.xml)来调整死信发送策略。
     * 
     * @return
     */
    public boolean isDLQ() {
        return this.options != null && this.options.containsKey(IS_DLQ);
    }

    public void setDLQ(boolean val) {
        if (this.options == null) {
            this.options = new HashMap<String, String>();
        }
        this.options.put(IS_DLQ, String.valueOf(val));
    }

    public static UnresolvedDestinationTransformer getUnresolvableDestinationTransformer() {
        return unresolvableDestinationTransformer;
    }

    public static void setUnresolvableDestinationTransformer(
            UnresolvedDestinationTransformer unresolvableDestinationTransformer) {
        DemonMQDestination.unresolvableDestinationTransformer = unresolvableDestinationTransformer;
    }

    public static void main(String[] args) {
        System.out.println(QUEUE_TYPE); // 1
        System.out.println(TOPIC_TYPE); // 2
        System.out.println(TEMP_MASK);// 4
        System.out.println(TEMP_TOPIC_TYPE);// 6
        System.out.println(TEMP_QUEUE_TYPE);// 5
    }
}
