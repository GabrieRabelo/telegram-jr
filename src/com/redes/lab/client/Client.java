package com.redes.lab.client;

import com.redes.lab.client.receivers.ImageReceiver;
import com.redes.lab.client.receivers.MessageReceiver;
import com.redes.lab.client.converter.ImageConverter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

class Client {
    private static final int MAX_BUFFER_SIZE = 1024;
    private static final int KEEP_ALIVE_PORT = 9875;
    private static final int SERVER_IMAGE_PORT = 9874;
    private static final int SERVER_PORT = 9876;

    private final DatagramSocket clientSocket;
    private final InetAddress IPAddress;
    private final Scanner scanner;
    private final ImageConverter imageConverter;

    public Client() throws IOException{

        scanner = new Scanner(System.in);

        clientSocket = new DatagramSocket();
        IPAddress = InetAddress.getByName("localhost");

        //Inicia thread do receptor de mensagem
        new MessageReceiver(clientSocket, IPAddress, KEEP_ALIVE_PORT).start();
        imageConverter = new ImageConverter();
    }

    public void run() throws IOException {

        System.out.println("Bem-vindo ao Telegram JR. Para se cadastrar use o comando !register [nome]");

        while(true){

            var input = scanner.nextLine();
            if(input.isBlank()){
                continue;
            }

            byte[] sendBuffer = input.getBytes();

            if(sendBuffer.length > MAX_BUFFER_SIZE){
                System.out.println("Mensagem grande demais para ser enviada.");
                continue;
            }

            DatagramPacket pack = new DatagramPacket(sendBuffer, sendBuffer.length, IPAddress, SERVER_PORT);
            clientSocket.send(pack);

            if(input.startsWith("!image")) {
                var arg = input.split(" ");
                var buffer = imageConverter.getImageBytes(arg[1]);
                this.sendImage(buffer);
            }
        }
    }

    public void sendImage(byte[] buffer) throws IOException {

        var packet = new DatagramPacket(buffer, buffer.length, IPAddress, SERVER_IMAGE_PORT);

        clientSocket.send(packet);
    }

    public static void main(String[] args) throws IOException {
        var client = new Client();
        client.run();
    }

}