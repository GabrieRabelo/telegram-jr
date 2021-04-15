package com.redes.lab.server;

import java.io.File;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class Server {

    private static final String BASE_PATH = "src/com/rabelo/udp/server/disk/";

    public static void main(String[] args)  throws Exception {

        DatagramSocket serverSocket = new DatagramSocket(9876);

        byte[] receiveData = new byte[10000];

        File file = new File( BASE_PATH + "received.txt");
        FileOutputStream f = new FileOutputStream(file);

        int len;
        while (true) {

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            serverSocket.receive(receivePacket);

            len = receivePacket.getLength();

            if (len > 0) {

                f.write(receiveData,0, len);
                f.flush();
            }
        }
    }
}