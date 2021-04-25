package com.redes.lab.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastPublisher {

    public void sendMessage(String multicastMessage) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName("230.0.0.0");
        byte[] buffer = multicastMessage.getBytes();

        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, group, 4446);

        socket.send(packet);
        socket.close();
    }
}
