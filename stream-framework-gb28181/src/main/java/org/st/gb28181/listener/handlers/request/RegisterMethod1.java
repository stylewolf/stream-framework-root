package org.st.gb28181.listener.handlers.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.st.gb28181.listener.annotation.MethodTarget;

import javax.sip.*;
import javax.sip.message.Request;

@MethodTarget(name= Request.REGISTER,cSeq = 1)
@Component
public class RegisterMethod1 extends RequestMethodHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterMethod1.class);
    @Override
    public void processRequest(RequestEvent requestEvent) {
        super.processRequest(requestEvent);
        Request request = requestEvent.getRequest();
        ServerTransaction serverTransactionId = requestEvent.getServerTransaction();
        try
        {
            System.out.println(serverTransactionId);
        }
        catch (Exception e)
        {
            LOGGER.error("processRequest error",e);
        }
    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        super.processTimeout(timeoutEvent);
    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {
        super.processIOException(exceptionEvent);
    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        super.processTransactionTerminated(transactionTerminatedEvent);
    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        super.processDialogTerminated(dialogTerminatedEvent);
    }
}
