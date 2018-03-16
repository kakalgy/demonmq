package kakalgy.yusj.demonmq.command;

import java.io.Externalizable;
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

    // ###
    public static final byte QUEUE_TYPE = 0x01;// 1
    public static final byte TOPIC_TYPE = 0x02;// 2
    public static final byte TEMP_MASK = 0x04;// 4
    public static final byte TEMP_TOPIC_TYPE = TOPIC_TYPE | TEMP_MASK;// 6
    public static final byte TEMP_QUEUE_TYPE = QUEUE_TYPE | TEMP_MASK;// 5

    // ###
    public static final String QUEUE_QUALIFIED_PREFIX = "queue://";
    public static final String TOPIC_QUALIFIED_PREFIX = "topic://";
    public static final String TEMP_QUEUE_QUALIFED_PREFIX = "temp-queue://";
    public static final String TEMP_TOPIC_QUALIFED_PREFIX = "temp-topic://";
    public static final String IS_DLQ = "isDLQ";

    public static final String TEMP_DESTINATION_NAME_PREFIX = "ID:";

    protected String physicalName;

    protected transient DemonMQDestination[] compositeDestinations;
    protected transient String[] destinationPaths;
    protected transient boolean isPattern;
    protected transient int hashValue;
    protected Map<String, String> options;

    protected static UnresolvedDestinationTransformer unresolvableDestinationTransformer = new DefaultUnresolvedDestinationTransformer();

    /**
     * 构造函数
     */
    public DemonMQDestination() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
        System.out.println(QUEUE_TYPE); // 1
        System.out.println(TOPIC_TYPE); // 2
        System.out.println(TEMP_MASK);// 4
        System.out.println(TEMP_TOPIC_TYPE);// 6
        System.out.println(TEMP_QUEUE_TYPE);// 5
    }
}
