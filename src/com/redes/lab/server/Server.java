package com.redes.lab.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.Time;
import java.time.Instant;

public class Server {

    DatagramSocket serverSocket;
    byte[] buffer = new byte[256];

    public Server() throws IOException {
        serverSocket = new DatagramSocket(9876);
    }

    public void run() throws IOException {

        int len;

        System.out.println("Servidor Telegram Jr.");

        while (true) {

            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

            serverSocket.receive(receivePacket);

            len = receivePacket.getLength();

            if (len > 0) {
                var address = receivePacket.getAddress();
                var message = new String(receivePacket.getData()).trim();
                System.out.println(Time.from(Instant.now()).toString().split(" ")[3] + address + ":" + receivePacket.getPort() + " falou: " + message);
            }
        }
    }
}