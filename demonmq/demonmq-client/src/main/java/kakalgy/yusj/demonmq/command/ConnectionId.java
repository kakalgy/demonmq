package kakalgy.yusj.demonmq.command;

/**
 * @openwire:marshaller code="120"
 * 
 */
public class ConnectionId implements DataStructure, Comparable<ConnectionId> {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.CONNECTION_ID;

    protected String value;

    public ConnectionId() {
    }

    public ConnectionId(String connectionId) {
        this.value = connectionId;
    }

    public ConnectionId(ConnectionId id) {
        this.value = id.getValue();
    }

    public ConnectionId(SessionId id) {
        this.value = id.getConnectionId();
    }

    public ConnectionId(ProducerId id) {
        this.value = id.getConnectionId();
    }

    public ConnectionId(ConsumerId id) {
        this.value = id.getConnectionId();
    }

    public int hashCode() {
        return value.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != ConnectionId.class) {
            return false;
        }
        ConnectionId id = (ConnectionId) o;
        return value.equals(id.value);
    }

    public String toString() {
        return value;
    }

    /**
     * @openwire:property version=1
     */
    public String getValue() {
        return value;
    }

    public void setValue(String connectionId) {
        this.value = connectionId;
    }

    /**
     * 实现DataStructure接口
     */
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    /**
     * 实现DataStructure接口
     */
    public boolean isMarshallAware() {
        return false;
    }

    /**
     * 实现Comparable<ConnectionId>接口
     */
    public int compareTo(ConnectionId o) {
        return value.compareTo(o.value);
    }
}
