package com.redes.lab.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Server {

    private final DatagramSocket serverSocket;
    private final List<Client> clients = new ArrayList<>();
    private final MulticastPublisher multicastPublisher;

    public Server() throws IOException {
        serverSocket = new DatagramSocket(9876);
        multicastPublisher = new MulticastPublisher();
    }

    public void run() throws IOException {

        int len;

        System.out.println("Servidor Telegram Jr.");

        while (true) {

            // novo buffer;
            var buffer = new byte[256];

            //recebe mensagem;
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            serverSocket.receive(receivedPacket);

            //verifica se o pacote não veio vazio, se sim ignora
            len = receivedPacket.getLength();
            if (len == 0) continue;

            //pega mensagem e separa conforme necessario
            var message = new String(receivedPacket.getData(),0, receivedPacket.getLength());
            var splitMessage = message.split(" ");

            //verifica se a mensagem recebida é comando
            String command = "";
            var argument = "";
            if (splitMessage[0].startsWith("/")) {
                command = splitMessage[0];
                argument = splitMessage[1];
            }

            switch (command) {
                case "/register":
                    this.registerClient(receivedPacket.getAddress(), receivedPacket.getPort(), argument);
                    break;

                default:
                    //Se o comando não existir, ou não for comando, envia para todos como fala;
                    if (!validateClient(receivedPacket.getPort())) {
                        System.out.println("Cliente não registrado ou expirado.");
                        break;
                    }
                    this.defaultMessage(receivedPacket.getPort(), message);
                    break;
            }
        }
    }

    private void registerClient(InetAddress IPAddress, int port, String name) throws IOException {
        System.out.println("Nova solicitação de registro para " + name + ".");
        var client = new Client(name, IPAddress, port);
        clients.add(client);

        multicastPublisher.sendMessage(getHour() + " Servidor: Um " + name + " selvagem chegou no chat.");
    }

    private void defaultMessage(int port, String message) throws IOException {

        var client = getSender(port);
        if (client.isPresent()) {
            multicastPublisher.sendMessage(getHour() + " " + client.get().getName() + ": " + message);
        }
    }

    /**
     * Get sender in client list
     */
    private Optional<Client> getSender(int port) {
        return clients.stream()
                .filter(it -> it.getPort() == port)
                .findFirst();
    }

    private boolean validateClient(int port) {
        var client = getSender(port);
        return client.isPresent();
    }

    private String getHour() {
        var localTime = LocalTime.now();
        return localTime.getHour() + ":" + localTime.getMinute();
    }

    public static void main(String[] args) throws IOException {

        var server = new Server();
        server.run();
    }
}