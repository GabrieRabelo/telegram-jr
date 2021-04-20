package com.redes.lab.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import java.util.Scanner;

class Client {

    private DatagramSocket clientSocket;
    private InetAddress IPAddress;
    private Scanner scanner;
    private byte[] sendBuffer = new byte[256];

    public Client() throws IOException{
        Random r = new Random();
        clientSocket = new DatagramSocket(r.nextInt(9999));
        IPAddress = InetAddress.getByName("localhost");
        scanner = new Scanner(System.in);
    }

    public void run() throws IOException {

        System.out.println("Escreva sua mensagem");

        while(true){

            sendBuffer = scanner.nextLine().getBytes();
            DatagramPacket pack = new DatagramPacket(sendBuffer, sendBuffer.length, IPAddress, 9876);
            clientSocket.send(pack);
        }


    }

}