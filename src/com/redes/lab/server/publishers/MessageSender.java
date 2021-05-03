package com.redes.lab.server.publishers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MessageSender {

    private final DatagramSocket serverSocket;

    public MessageSender(DatagramSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void sendMessage(String message, InetAddress IPAddress, int port) throws IOException {
        var buffer = message.getBytes();

        DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, IPAddress, port);

        serverSocket.send(datagram);
    }
}
