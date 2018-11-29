package org.st.framework.gb28181.listener.handlers.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.st.framework.gb28181.listener.annotation.MethodTarget;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.URI;
import javax.sip.header.ContactHeader;
import javax.sip.header.Header;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

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
            ToHeader head = (ToHeader)request.getHeader(ToHeader.NAME);
            Address toAddress = head.getAddress();
            URI toURI = toAddress.getURI();
            ContactHeader contactHeader = (ContactHeader) request.getHeader("Contact");
            Address contactAddr = contactHeader.getAddress();
            URI contactURI = contactAddr.getURI();
            Response response = msgFactory.createResponse(401, request);

            Header header = headerFactory.createWWWAuthenticateHeader("Digest realm=\"3402000000\",noce=\"6fe9ba4a76be4a\"");
            response.addHeader(header);
            if(serverTransactionId == null)
            {
                serverTransactionId = sipProvider.getNewServerTransaction(request);
                serverTransactionId.sendResponse(response);
                //serverTransactionId.terminate();
                System.out.println("register serverTransaction: " + serverTransactionId);
            }
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
