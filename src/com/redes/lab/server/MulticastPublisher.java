package com.redes.lab.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastPublisher {

    DatagramSocket socket;

    public MulticastPublisher(DatagramSocket socket) {
        this.socket = socket;
    }

    public void sendMessage(String multicastMessage) throws IOException {
        InetAddress group = InetAddress.getByName("230.0.0.0");
        byte[] buffer = multicastMessage.getBytes();

        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, group, 4446);

        socket.send(packet);
    }
}
