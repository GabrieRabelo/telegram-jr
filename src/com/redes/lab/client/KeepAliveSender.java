package com.redes.lab.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class KeepAliveSender extends Thread {

    private final DatagramSocket clientSocket;
    private final InetAddress IPAddress;
    private final int port;

    public KeepAliveSender(DatagramSocket clientSocket, InetAddress IPAddress, int port) {
        this.clientSocket = clientSocket;
        this.IPAddress = IPAddress;
        this.port = port;
    }

    public void run() {

        while (true) {
            try {
                var message = "keep-alive".getBytes();
                DatagramPacket dp = new DatagramPacket(message, message.length, IPAddress, port);
                clientSocket.send(dp);
            } catch (IOException e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
