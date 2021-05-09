package com.redes.lab.server.publishers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class MulticastPublisher {

    private static final Logger LOGGER = Logger.getLogger("MulticastPublisher");

    private final DatagramSocket serverSocket;
    private static final String MULTICAST_ADDRESS = "230.0.0.0";
    private static final int PORT = 4446;
    private static final int IMAGE_PORT = 4447;
    private final InetAddress group;
    private final InetAddress imageGroup;

    public MulticastPublisher(DatagramSocket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
        this.group = InetAddress.getByName(MULTICAST_ADDRESS);
        this.imageGroup = InetAddress.getByName(MULTICAST_ADDRESS);
        LOGGER.info(String.format(": Creating multicast group at %s:%s", MULTICAST_ADDRESS, PORT));
    }

    public void sendMessage(String multicastMessage) throws IOException {
        byte[] buffer = multicastMessage.getBytes();

        var packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        serverSocket.send(packet);
    }

    public void sendMessage(byte[] buffer) throws IOException {
        var packet = new DatagramPacket(buffer, buffer.length, imageGroup, IMAGE_PORT);
        serverSocket.send(packet);
    }
}
