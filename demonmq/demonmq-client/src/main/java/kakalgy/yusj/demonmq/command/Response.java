package kakalgy.yusj.demonmq.command;

import kakalgy.yusj.demonmq.state.CommandVisitor;

/**
 * @openwire:marshaller code="30"
 * 
 */
public class Response extends BaseCommand {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.RESPONSE;
    int correlationId;

    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    /**
     * @openwire:property version=1
     */
    public int getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(int responseId) {
        this.correlationId = responseId;
    }

    public boolean isResponse() {
        return true;
    }

    public boolean isException() {
        return false;
    }

    public Response visit(CommandVisitor visitor) throws Exception {
        return null;
    }
}
