package com.redes.lab.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastPublisher {
    private DatagramSocket socket;
    private InetAddress group;
    private byte[] buffer;

    public void sendMessage(String multicastMessage) throws IOException {
        this.socket = new DatagramSocket();;
        this.group = InetAddress.getByName("230.0.0.0");
        this.buffer = multicastMessage.getBytes();

        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, group, 4446);

        System.out.println(multicastMessage);
        socket.send(packet);
        socket.close();
    }
}
