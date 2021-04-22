package com.redes.lab.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import java.util.Scanner;

class Client {

    private final DatagramSocket clientSocket;
    private final InetAddress IPAddress;
    private final Scanner scanner;
    private final MulticastReceiver multicastReceiver;
    private byte[] sendBuffer;

    public Client() throws IOException{
        Random r = new Random();
        scanner = new Scanner(System.in);

        clientSocket = new DatagramSocket(r.nextInt(9999));
        IPAddress = InetAddress.getByName("localhost");

        //Inicia receptor multicast
        multicastReceiver = new MulticastReceiver();
        multicastReceiver.start();
    }

    public void run() throws IOException {

        System.out.println("Bem-vindo ao Telegram JR. Para se cadastrar use o comando /register [nome]");

        while(true){

            sendBuffer = scanner.nextLine().getBytes();
            DatagramPacket pack = new DatagramPacket(sendBuffer, sendBuffer.length, IPAddress, 9876);
            clientSocket.send(pack);
        }


    }

}