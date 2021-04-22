package com.redes.lab.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private final DatagramSocket serverSocket;
    private final List<Client> clients = new ArrayList<>();
    private final MulticastPublisher multicastPublisher;
    private byte[] buffer = new byte[256];

    public Server() throws IOException {
        serverSocket = new DatagramSocket(9876);
        multicastPublisher = new MulticastPublisher();
    }

    public void run() throws IOException {

        int len;

        System.out.println("Servidor Telegram Jr.");

        while (true) {
            buffer = new byte[256];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

            serverSocket.receive(receivedPacket);

            len = receivedPacket.getLength();

            if (len > 0) {
                var message = receivedPacket.getData();
                var stringMessage = new String(message).trim();
                var splitMessage = stringMessage.split(" ");
                String command = "";
                var argument = "";
                if(splitMessage[0].startsWith("/")) {
                    command = splitMessage[0];
                    argument = splitMessage [1];
                }


                switch(command) {
                    case "/register":
                        this.registerClient(receivedPacket, argument);
                        break;
                    default:
                        this.yellMessage(receivedPacket, stringMessage);
                        break;
                }
            }
        }
    }

    private void registerClient(DatagramPacket receivedPacket, String name) throws IOException {
        System.out.println("Nova solicitação de registro para " + name);
        var client = new Client(name, receivedPacket.getAddress(), receivedPacket.getPort());
        clients.add(client);

        multicastPublisher.sendMessage(getHour() + " Servidor: Um " + name + " selvagem chegou no chat.");
    }

    private void yellMessage(DatagramPacket receivedPacket, String message) throws IOException {
        var client = clients.stream().filter(it -> it.getPort() == receivedPacket.getPort()).findFirst();
        if (client.isPresent()){
            multicastPublisher.sendMessage(getHour() + " " + client.get().getName() + ": " + message);
        }
    }

    private String getHour(){
        var localTime = LocalTime.now();
        return localTime.getHour() + ":" + localTime.getMinute();
    }
}