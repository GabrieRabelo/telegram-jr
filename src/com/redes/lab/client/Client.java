package com.redes.lab.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

class Client {

    private final DatagramSocket clientSocket;
    private final InetAddress IPAddress;
    private final Scanner scanner;
    private final MulticastReceiver multicastReceiver;

    public Client() throws IOException{
        scanner = new Scanner(System.in);

        clientSocket = new DatagramSocket();
        IPAddress = InetAddress.getByName("localhost");

        //Inicia receptor multicast
        multicastReceiver = new MulticastReceiver();
        multicastReceiver.start();
    }

    public void run() throws IOException {

        System.out.println("Bem-vindo ao Telegram JR. Para se cadastrar use o comando /register [nome]");

        while(true){

            byte[] sendBuffer = scanner.nextLine().getBytes();
            DatagramPacket pack = new DatagramPacket(sendBuffer, sendBuffer.length, IPAddress, 9876);
            clientSocket.send(pack);

        }


    }

    public static void main(String[] args) throws IOException {
        var client = new Client();
        client.run();
    }

}