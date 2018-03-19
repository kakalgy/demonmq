package kakalgy.yusj.demonmq.command;

import java.io.Externalizable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;

import kakalgy.yusj.demonmq.jndi.JNDIBaseStorable;

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
            compositeDestinations = new ActiveMQDestination[l.size()];
            int counter = 0;
            for (String dest : l) {
                compositeDestinations[counter++] = createDestination(dest);
            }
        }
    }

    public Map<String, String> getOptions() {
        return this.options;
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
