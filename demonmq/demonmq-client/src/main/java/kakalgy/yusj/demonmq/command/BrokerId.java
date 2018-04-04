package kakalgy.yusj.demonmq.command;

/**
 * @openwire:marshaller code="124"
 * 
 */
public class BrokerId implements DataStructure {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.BROKER_ID;
    protected String value;

    public BrokerId() {
    }

    public BrokerId(String brokerId) {
        this.value = brokerId;
    }

    public int hashCode() {
        return value.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != BrokerId.class) {
            return false;
        }
        BrokerId id = (BrokerId) o;
        return value.equals(id.value);
    }

    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
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

    public void setValue(String brokerId) {
        this.value = brokerId;
    }

    public boolean isMarshallAware() {
        return false;
    }
}
