package kakalgy.yusj.demonmq.command;

/**
 * The Command Pattern so that we can send and receive commands on the different
 * transports
 * <p>
 * Command格式 可以使得消息在各个不同的平台间传输
 */
public interface Command extends DataStructure {

    void setCommandId(int value);

    /**
     * @return the unique ID of this request used to map responses to requests
     */
    int getCommandId();

    void setResponseRequired(boolean responseRequired);

    boolean isResponseRequired();

    boolean isResponse();

    boolean isMessageDispatch();

    boolean isBrokerInfo();

    boolean isWireFormatInfo();

    boolean isMessage();

    boolean isMessageAck();

    boolean isMessageDispatchNotification();

    boolean isShutdownInfo();

    boolean isConnectionControl();

    boolean isConsumerControl();

    Response visit(CommandVisitor visitor) throws Exception;

    /**
     * The endpoint within the transport where this message came from which could be
     * null if the transport only supports a single endpoint.
     */
    Endpoint getFrom();

    void setFrom(Endpoint from);

    /**
     * The endpoint within the transport where this message is going to - null means
     * all endpoints.
     */
    Endpoint getTo();

    void setTo(Endpoint to);
}
