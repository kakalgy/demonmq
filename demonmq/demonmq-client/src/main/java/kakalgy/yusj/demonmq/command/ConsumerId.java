package kakalgy.yusj.demonmq.command;

/**
 * @openwire:marshaller code="122"
 * 
 */
public class ConsumerId implements DataStructure {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.CONSUMER_ID;

    protected String connectionId;
    protected long sessionId;
    protected long value;

    protected transient int hashCode;
    protected transient String key;
    protected transient SessionId parentId;

    public ConsumerId() {
    }

    /**
     * 若参数为String，则格式为 connectionId:sessionId:value
     * 
     * @param str
     */
    public ConsumerId(String str) {
        if (str != null) {
            String[] splits = str.split(":");
            if (splits != null && splits.length >= 3) {
                this.connectionId = splits[0];
                this.sessionId = Long.parseLong(splits[1]);
                this.value = Long.parseLong(splits[2]);
            }
        }
    }

    public ConsumerId(SessionId sessionId, long consumerId) {
        this.connectionId = sessionId.getConnectionId();
        this.sessionId = sessionId.getValue();
        this.value = consumerId;
    }

    public ConsumerId(ConsumerId id) {
        this.connectionId = id.getConnectionId();
        this.sessionId = id.getSessionId();
        this.value = id.getValue();
    }

    public SessionId getParentId() {
        if (parentId == null) {
            parentId = new SessionId(this);
        }
        return parentId;
    }

    public int hashCode() {
        if (hashCode == 0) {
            hashCode = connectionId.hashCode() ^ (int) sessionId ^ (int) value;
        }
        return hashCode;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != ConsumerId.class) {
            return false;
        }
        ConsumerId id = (ConsumerId) o;
        return sessionId == id.sessionId && value == id.value && connectionId.equals(id.connectionId);
    }

    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    public String toString() {
        if (key == null) {
            key = connectionId + ":" + sessionId + ":" + value;
        }
        return key;
    }

    /**
     * @openwire:property version=1
     */
    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    /**
     * @openwire:property version=1
     */
    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * @openwire:property version=1
     */
    public long getValue() {
        return value;
    }

    public void setValue(long consumerId) {
        this.value = consumerId;
    }

    public boolean isMarshallAware() {
        return false;
    }
}
