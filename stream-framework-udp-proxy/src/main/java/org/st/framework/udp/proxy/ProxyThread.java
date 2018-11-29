package org.st.framework.udp.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

@Async
@Component
public class ProxyThread{
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyThread.class);
    //建立proxy至target的连接
    private DatagramSocket datagramSocket;
    //client至proxy的连接
    private DatagramSocket proxyDatagramSocket;

    private String clientHost;
    private int clientPort;

    @Value("${udp.proxy.server}")
    private String targetServer;
    @Value("${udp.proxy.server.port}")
    private int targetPort;

    @Value("${udp.server.port}")
    private int port;
    @Value("${udp.server.buffered}")
    private int buffered;
    @Value("${udp.server}")
    private String server;

    @Async
    public void exec(DatagramSocket proxyDatagramSocket,String clientHost, int clientPort,DatagramPacket datagramPacket) {
        try {
            //建立server至proxy的连接
            datagramSocket = new DatagramSocket(0);
            datagramSocket.setSoTimeout(1000);
            datagramPacket.setPort(targetPort);
            datagramPacket.setAddress(InetAddress.getByName(targetServer));
            datagramSocket.send(datagramPacket);
            LOGGER.info("发送至targetServer："+targetServer+":"+targetPort);

            datagramSocket.receive(datagramPacket);
            LOGGER.info("接收targetServer："+targetServer+":"+targetPort);
            datagramPacket.setPort(clientPort);
            datagramPacket.setAddress(InetAddress.getByName(clientHost));
            datagramSocket.disconnect();
            datagramSocket.close();
            proxyDatagramSocket.send(datagramPacket);
            LOGGER.info("回发客户端："+clientHost+":"+clientPort);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
