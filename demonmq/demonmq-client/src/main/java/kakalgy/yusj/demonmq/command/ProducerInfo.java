package kakalgy.yusj.demonmq.command;

import java.util.concurrent.atomic.AtomicLong;

import kakalgy.yusj.demonmq.state.CommandVisitor;

/**
 * 
 * @openwire:marshaller code="6"
 * 
 */
public class ProducerInfo extends BaseCommand {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.PRODUCER_INFO;

    protected ProducerId producerId;
    protected DemonMQDestination destination;
    protected BrokerId[] brokerPath;
    protected boolean dispatchAsync;
    protected int windowSize;
    protected AtomicLong sentCount = new AtomicLong();

    public ProducerInfo() {
    }

    public ProducerInfo(ProducerId producerId) {
        this.producerId = producerId;
    }

    public ProducerInfo(SessionInfo sessionInfo, long producerId) {
        this.producerId = new ProducerId(sessionInfo.getSessionId(), producerId);
    }

    public ProducerInfo copy() {
        ProducerInfo info = new ProducerInfo();
        copy(info);
        return info;
    }

    public void copy(ProducerInfo info) {
        super.copy(info);
        info.producerId = producerId;
        info.destination = destination;
    }

    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    /**
     * @openwire:property version=1 cache=true
     */
    public ProducerId getProducerId() {
        return producerId;
    }

    public void setProducerId(ProducerId producerId) {
        this.producerId = producerId;
    }

    /**
     * @openwire:property version=1 cache=true
     */
    public DemonMQDestination getDestination() {
        return destination;
    }

    public void setDestination(DemonMQDestination destination) {
        this.destination = destination;
    }

    public RemoveInfo createRemoveCommand() {
        RemoveInfo command = new RemoveInfo(getProducerId());
        command.setResponseRequired(isResponseRequired());
        return command;
    }

    /**
     * The route of brokers the command has moved through.
     * 
     * @openwire:property version=1 cache=true
     */
    public BrokerId[] getBrokerPath() {
        return brokerPath;
    }

    public void setBrokerPath(BrokerId[] brokerPath) {
        this.brokerPath = brokerPath;
    }

    public Response visit(CommandVisitor visitor) throws Exception {
        return visitor.processAddProducer(this);
    }

    /**
     * If the broker should dispatch messages from this producer async. Since sync
     * dispatch could potentally block the producer thread, this could be an
     * important setting for the producer.
     * 
     * @openwire:property version=2
     */
    public boolean isDispatchAsync() {
        return dispatchAsync;
    }

    public void setDispatchAsync(boolean dispatchAsync) {
        this.dispatchAsync = dispatchAsync;
    }

    /**
     * Used to configure the producer window size. A producer will send up to the
     * configured window size worth of payload data to the broker before waiting for
     * an Ack that allows him to send more.
     * 
     * @openwire:property version=3
     */
    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public long getSentCount() {
        return sentCount.get();
    }

    public void incrementSentCount() {
        sentCount.incrementAndGet();
    }

    public void resetSentCount() {
        sentCount.set(0);
    }

}
