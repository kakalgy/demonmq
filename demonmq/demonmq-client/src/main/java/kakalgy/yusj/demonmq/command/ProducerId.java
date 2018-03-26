package kakalgy.yusj.demonmq.command;

/**
 * @openwire:marshaller code="123"
 * 
 */
public class ProducerId implements DataStructure {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.PRODUCER_ID;

    protected String connectionId;
    protected long sessionId;
    protected long value;

    protected transient int hashCode;
    protected transient String key;
    protected transient SessionId parentId;

    public ProducerId() {
    }

    public ProducerId(SessionId sessionId, long producerId) {
        this.connectionId = sessionId.getConnectionId();
        this.sessionId = sessionId.getValue();
        this.value = producerId;
    }

    public ProducerId(ProducerId id) {
        this.connectionId = id.getConnectionId();
        this.sessionId = id.getSessionId();
        this.value = id.getValue();
    }

    /**
     * 若参数为String，则格式为 connectionId:sessionId:value
     * 
     * @param producerKey
     */
    public ProducerId(String producerKey) {
        // Parse off the producerId
        int p = producerKey.lastIndexOf(":");
        if (p >= 0) {
            value = Long.parseLong(producerKey.substring(p + 1));
            producerKey = producerKey.substring(0, p);
        }
        setProducerSessionKey(producerKey);
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
        if (o == null || o.getClass() != ProducerId.class) {
            return false;
        }
        ProducerId id = (ProducerId) o;
        return sessionId == id.sessionId && value == id.value && connectionId.equals(id.connectionId);
    }

    /**
     * 通过sessionKey得到sessionId和connectionId，格式：connectionId:sessionId
     * 
     * @param sessionKey
     */
    private void setProducerSessionKey(String sessionKey) {
        // Parse off the value
        int p = sessionKey.lastIndexOf(":");
        if (p >= 0) {
            sessionId = Long.parseLong(sessionKey.substring(p + 1));
            sessionKey = sessionKey.substring(0, p);
        }
        // The rest is the value
        connectionId = sessionKey;
    }

    public String toString() {
        if (key == null) {
            key = connectionId + ":" + sessionId + ":" + value;
        }
        return key;
    }

    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    /**
     * @openwire:property version=1 cache=true
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
    public long getValue() {
        return value;
    }

    public void setValue(long producerId) {
        this.value = producerId;
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

    public boolean isMarshallAware() {
        return false;
    }
}
