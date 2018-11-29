package org.st.framework.udp.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.*;

@Component
public class ProxyServer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyServer.class);
    /**
     * 监听客户端
     */
    private DatagramSocket selfDatagramSocket;
    /**
     * 客户端消息
     */
    private DatagramPacket recieveDatagramPacket;

    @Value("${udp.server.port}")
    private int port;
    @Value("${udp.server.buffered}")
    private int buffered;
    @Value("${udp.server}")
    private String server;

    @Value("${udp.proxy.server}")
    private String targetServer;
    @Value("${udp.proxy.server.port}")
    private int targetPort;

    @Autowired
    private ProxyThread proxyThread;

    @PostConstruct
    public void init(){
        try {
            selfDatagramSocket = new DatagramSocket(port, InetAddress.getByName(server));
            selfDatagramSocket.setReceiveBufferSize(buffered);
            byte[] bytes = new byte[buffered];
            recieveDatagramPacket = new DatagramPacket(bytes,buffered);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        new Thread(this).start();
    }
    public void run() {
        while(true){
            try {
                //接收数据
                selfDatagramSocket.receive(recieveDatagramPacket);
                //client
                String clientHost = recieveDatagramPacket.getAddress().getHostAddress();
                int clientPort = recieveDatagramPacket.getPort();
                LOGGER.info("接收到client数据："+clientHost+":"+clientPort);
                proxyThread.exec(selfDatagramSocket,clientHost,clientPort,recieveDatagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
