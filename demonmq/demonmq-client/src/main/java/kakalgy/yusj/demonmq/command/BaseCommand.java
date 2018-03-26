package kakalgy.yusj.demonmq.command;

import java.util.Map;

import kakalgy.yusj.demonmq.util.IntrospectionSupport;

/**
 *
 * @openwire:marshaller
 *
 */
public abstract class BaseCommand implements Command {

    protected int commandId;
    protected boolean responseRequired;

    private transient Endpoint from;
    private transient Endpoint to;

    public void copy(BaseCommand copy) {
        copy.commandId = commandId;
        copy.responseRequired = responseRequired;
    }

    /**
     * @openwire:property version=1
     *                    <p>
     *                    实现Command接口
     */
    public int getCommandId() {
        return commandId;
    }

    /**
     * 实现Command接口
     */
    public void setCommandId(int commandId) {
        this.commandId = commandId;
    }

    /**
     * @openwire:property version=1
     *                    <p>
     *                    实现Command接口
     */
    public boolean isResponseRequired() {
        return responseRequired;
    }

    /**
     * 实现Command接口
     */
    public void setResponseRequired(boolean responseRequired) {
        this.responseRequired = responseRequired;
    }

    @Override
    public String toString() {
        return toString(null);
    }

    public String toString(Map<String, Object> overrideFields) {
        return IntrospectionSupport.toString(this, BaseCommand.class, overrideFields);
    }

    /**
     * 实现Command接口
     */
    public boolean isWireFormatInfo() {
        return false;
    }

    /**
     * 实现Command接口
     */
    public boolean isBrokerInfo() {
        return false;
    }

    /**
     * 实现Command接口
     */
    public boolean isResponse() {
        return false;
    }

    /**
     * 实现Command接口
     */
    public boolean isMessageDispatch() {
        return false;
    }

    /**
     * 实现Command接口
     */
    public boolean isMessage() {
        return false;
    }

    /**
     * 实现Command接口
     */
    public boolean isMarshallAware() {
        return false;
    }

    /**
     * 实现Command接口
     */
    public boolean isMessageAck() {
        return false;
    }

    /**
     * 实现Command接口
     */
    public boolean isMessageDispatchNotification() {
        return false;
    }

    /**
     * 实现Command接口
     */
    public boolean isShutdownInfo() {
        return false;
    }

    /**
     * 实现Command接口
     */
    public boolean isConnectionControl() {
        return false;
    }

    /**
     * 实现Command接口
     */
    public boolean isConsumerControl() {
        return false;
    }

    /**
     * The endpoint within the transport where this message came from.
     */
    /**
     * 实现Command接口
     */
    public Endpoint getFrom() {
        return from;
    }

    /**
     * 实现Command接口
     */
    public void setFrom(Endpoint from) {
        this.from = from;
    }

    /**
     * The endpoint within the transport where this message is going to - null means
     * all endpoints.
     */
    /**
     * 实现Command接口
     */
    public Endpoint getTo() {
        return to;
    }

    /**
     * 实现Command接口
     */
    public void setTo(Endpoint to) {
        this.to = to;
    }
}
