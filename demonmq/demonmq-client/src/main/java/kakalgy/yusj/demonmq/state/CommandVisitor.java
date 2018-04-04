package kakalgy.yusj.demonmq.state;

import kakalgy.yusj.demonmq.command.BrokerInfo;
import kakalgy.yusj.demonmq.command.ConnectionId;
import kakalgy.yusj.demonmq.command.ConsumerId;
import kakalgy.yusj.demonmq.command.ProducerId;
import kakalgy.yusj.demonmq.command.ProducerInfo;
import kakalgy.yusj.demonmq.command.Response;
import kakalgy.yusj.demonmq.command.SessionId;

public interface CommandVisitor {

    Response processAddConnection(ConnectionInfo info) throws Exception;

    Response processAddSession(SessionInfo info) throws Exception;

    Response processAddProducer(ProducerInfo info) throws Exception;

    Response processAddConsumer(ConsumerInfo info) throws Exception;

    Response processRemoveConnection(ConnectionId id, long lastDeliveredSequenceId) throws Exception;

    Response processRemoveSession(SessionId id, long lastDeliveredSequenceId) throws Exception;

    Response processRemoveProducer(ProducerId id) throws Exception;

    Response processRemoveConsumer(ConsumerId id, long lastDeliveredSequenceId) throws Exception;

    Response processAddDestination(DestinationInfo info) throws Exception;

    Response processRemoveDestination(DestinationInfo info) throws Exception;

    Response processRemoveSubscription(RemoveSubscriptionInfo info) throws Exception;

    Response processMessage(Message send) throws Exception;

    Response processMessageAck(MessageAck ack) throws Exception;

    Response processMessagePull(MessagePull pull) throws Exception;

    Response processBeginTransaction(TransactionInfo info) throws Exception;

    Response processPrepareTransaction(TransactionInfo info) throws Exception;

    Response processCommitTransactionOnePhase(TransactionInfo info) throws Exception;

    Response processCommitTransactionTwoPhase(TransactionInfo info) throws Exception;

    Response processRollbackTransaction(TransactionInfo info) throws Exception;

    Response processWireFormat(WireFormatInfo info) throws Exception;

    Response processKeepAlive(KeepAliveInfo info) throws Exception;

    Response processShutdown(ShutdownInfo info) throws Exception;

    Response processFlush(FlushCommand command) throws Exception;

    Response processBrokerInfo(BrokerInfo info) throws Exception;

    Response processBrokerSubscriptionInfo(BrokerSubscriptionInfo info) throws Exception;

    Response processRecoverTransactions(TransactionInfo info) throws Exception;

    Response processForgetTransaction(TransactionInfo info) throws Exception;

    Response processEndTransaction(TransactionInfo info) throws Exception;

    Response processMessageDispatchNotification(MessageDispatchNotification notification) throws Exception;

    Response processProducerAck(ProducerAck ack) throws Exception;

    Response processMessageDispatch(MessageDispatch dispatch) throws Exception;

    Response processControlCommand(ControlCommand command) throws Exception;

    Response processConnectionError(ConnectionError error) throws Exception;

    Response processConnectionControl(ConnectionControl control) throws Exception;

    Response processConsumerControl(ConsumerControl control) throws Exception;

}
