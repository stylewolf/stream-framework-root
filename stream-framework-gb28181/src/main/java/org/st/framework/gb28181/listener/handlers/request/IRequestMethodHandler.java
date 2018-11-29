package org.st.framework.gb28181.listener.handlers.request;

import javax.sip.*;

public interface IRequestMethodHandler
{
    public void processRequest(RequestEvent requestEvent);
    public void processTimeout(TimeoutEvent timeoutEvent);
    public void processIOException(IOExceptionEvent exceptionEvent);
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent);
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent);
}
