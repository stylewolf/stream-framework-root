package org.st.framework.gb28181.listener;

import gov.nist.javax.sip.message.SIPRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.st.framework.gb28181.utils.SpringUtil;
import org.st.framework.gb28181.listener.handlers.request.RequestMethodHandlerAdapter;

import javax.annotation.PostConstruct;
import javax.sip.*;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import java.util.Properties;

@Component
public class Sip28181Listener implements SipListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sip28181Listener.class);

    private static SipStack sipStack = null;

    private static AddressFactory addressFactory = null;

    private static MessageFactory msgFactory = null;

    private static HeaderFactory headerFactory = null;

    private static SipProvider sipProvider = null;

    private static SipFactory sipFactory = null;

    @PostConstruct
    public void init(){
        sipFactory = SipFactory.getInstance();
        if (null == sipFactory)
        {
            LOGGER.warn("init sipFactory is null.");
            return;
        }
        sipFactory.setPathName("gov.nist");
        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", "sipphone");
        // You need 16 for logging traces. 32 for debug + traces.
        // Your code will limp at 32 but it is best for debugging.
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
                "sipphonedebug.txt");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
                "sipphonelog.txt");
        try
        {
            sipStack = sipFactory.createSipStack(properties);
        }
        catch (PeerUnavailableException e)
        {
            throw new RuntimeException(e);
        }

        try
        {
            headerFactory = sipFactory.createHeaderFactory();
            addressFactory = sipFactory.createAddressFactory();
            msgFactory = sipFactory.createMessageFactory();
            ListeningPoint lp = sipStack.createListeningPoint("172.16.10.180",
                    5060, "udp");
            sipProvider = sipStack.createSipProvider(lp);
            sipProvider.addSipListener(this);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            LOGGER.error("init:",ex);
        }

    }

    /**
     * 获取处理方法的处理类
     * @param request
     * @return
     */
    private RequestMethodHandlerAdapter getRequestMethodHandlerAdapter(Request request){
        String methodName = request.getMethod();
        int cSeq = (int) ((SIPRequest) request).getCSeq().getSeqNumber();
        RequestMethodHandlerAdapter adapter = SpringUtil.methodsAdapter(methodName,cSeq);
        adapter.setMsgFactory(msgFactory);
        adapter.setSipProvider(sipProvider);
        adapter.setAddressFactory(addressFactory);
        adapter.setHeaderFactory(headerFactory);
        adapter.setSipFactory(sipFactory);
        return adapter;
    }
    @Override
    public void processRequest(RequestEvent requestEvent) {
        System.out.println("processRequest");
        Request request = requestEvent.getRequest();
        if (null == request)
        {
            LOGGER.warn("request is null！");
        }
        System.out.println(request);
        RequestMethodHandlerAdapter adapter = getRequestMethodHandlerAdapter(request);
        adapter.processRequest(requestEvent);
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {
        System.out.println("processResponse");
    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        System.out.println("processTimeout");
    }

    @Override
    public void processIOException(IOExceptionEvent ioExceptionEvent) {
        System.out.println("processIOException");
    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        System.out.println("processTransactionTerminated");
    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        System.out.println("processDialogTerminated");
    }
}
