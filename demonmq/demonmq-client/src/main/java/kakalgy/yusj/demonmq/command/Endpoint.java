package kakalgy.yusj.demonmq.command;

/**
 * Represents the logical endpoint where commands come from or are sent to.
 * <p>
 * 代表了command从哪里发出，发送到哪里的逻辑端点
 * 
 * For connection based transports like TCP / VM then there is a single endpoint
 * for all commands. For transports like multicast there could be different
 * endpoints being used on the same transport.
 * <p>
 * 
 * 
 */
public interface Endpoint {

    /**
     * Returns the name of the endpoint.
     */
    String getName();

    /**
     * Returns the broker ID for this endpoint, if the endpoint is a broker or null
     */
    BrokerId getBrokerId();

    /**
     * Returns the broker information for this endpoint, if the endpoint is a broker
     * or null
     */
    BrokerInfo getBrokerInfo();

    void setBrokerInfo(BrokerInfo brokerInfo);

}
