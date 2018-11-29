package org.st.framework.gb28181.listener.handlers.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.*;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;

/**
 * 监听方法适配器
 */
public class RequestMethodHandlerAdapter implements IRequestMethodHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestMethodHandlerAdapter.class);

     AddressFactory addressFactory = null;

     MessageFactory msgFactory = null;

     HeaderFactory headerFactory = null;

     SipProvider sipProvider = null;

     SipFactory sipFactory = null;

    public void setAddressFactory(AddressFactory addressFactory) {
        this.addressFactory = addressFactory;
    }

    public void setMsgFactory(MessageFactory msgFactory) {
        this.msgFactory = msgFactory;
    }

    public void setHeaderFactory(HeaderFactory headerFactory) {
        this.headerFactory = headerFactory;
    }

    public void setSipProvider(SipProvider sipProvider) {
        this.sipProvider = sipProvider;
    }

    public void setSipFactory(SipFactory sipFactory) {
        this.sipFactory = sipFactory;
    }

    @Override
    public void processRequest(RequestEvent requestEvent) {
        LOGGER.debug("processRequest");
    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        LOGGER.debug("processTimeout");
    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {
        LOGGER.debug("processIOException");
    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        LOGGER.debug("processTransactionTerminated");
    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        LOGGER.debug("processDialogTerminated");
    }
}
