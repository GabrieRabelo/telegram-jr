package com.redes.lab.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MessageReceiver extends Thread {
    private final DatagramSocket datagramSocket;

    public MessageReceiver(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    public void run() {

        var buffer = new byte[1024];
        DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
        try {
            datagramSocket.receive(receivedPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        var message = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

        if (message.equals("terminate")) {
            System.exit(1);
        } else {
            System.out.println(message);
        }
    }
}
