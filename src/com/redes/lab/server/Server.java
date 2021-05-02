package com.redes.lab.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import static com.redes.lab.server.Utils.extractText;
import static com.redes.lab.server.Utils.getClientsAsString;
import static com.redes.lab.server.Utils.getHour;
import static com.redes.lab.server.Utils.helloMessages;
import static com.redes.lab.server.Utils.logo;

public class Server {

    private static final Logger LOGGER = Logger.getLogger("Telegram Jr.");
    private static final Random r = new Random();
    private static final int SERVER_PORT = 9876;
    private static final int KEEP_ALIVE_PORT = 9875;
    private static final int BUFFER_SIZE = 1024;

    private final DatagramSocket serverSocket;
    private final List<Client> clients = new CopyOnWriteArrayList<>();
    private final MulticastPublisher multicastPublisher;

    public Server() throws IOException {

        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");

        this.serverSocket = new DatagramSocket(SERVER_PORT);

        LOGGER.info(": Starting server at port " + SERVER_PORT);
        LOGGER.info(": Starting keep-alive server at port " + KEEP_ALIVE_PORT);

        this.multicastPublisher = new MulticastPublisher(serverSocket);

        new KeepAliveManager(KEEP_ALIVE_PORT, clients, this).start();
    }

    public void run() throws IOException {

        LOGGER.info(": Telegram Jr. Server Started");

        // Command "thread"
        while (true) {

            // novo buffer;
            var buffer = new byte[BUFFER_SIZE];

            // recebe mensagem;
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            serverSocket.receive(receivedPacket);

            // verifica se o pacote não veio vazio, se sim ignora
            if (receivedPacket.getLength() == 0)
                continue;

            // pega mensagem e separa conforme necessario
            var message = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
            var splitMessage = message.split(" ");
            var text = extractText(splitMessage);

            // verifica se a mensagem recebida é comando
            String command = "";
            var argument = "";
            if (splitMessage[0].startsWith("!")) {
                command = splitMessage[0].toLowerCase();
                if (splitMessage.length > 1)
                    argument = splitMessage[1];
            }

            switch (command) {
                case "!register":
                    this.registerClient(receivedPacket.getAddress(), receivedPacket.getPort(), argument);
                    break;

                case "!pm":
                    this.sendPrivateMessage(receivedPacket.getAddress(), receivedPacket.getPort(), argument, text);
                    break;

                case "!leave":
                    this.removeClient(receivedPacket.getPort());
                    break;

                case "!online":
                    this.showOnlineClients(receivedPacket.getAddress(), receivedPacket.getPort());
                    break;
                default:
                    //Se o comando não existir, ou não for comando, envia para todos como fala;
                    this.sendDefaultMessage(receivedPacket.getAddress(), receivedPacket.getPort(), message);
                    break;
            }
        }
    }

    private void registerClient(InetAddress IPAddress, int port, String name) throws IOException {
        LOGGER.info(": Nova solicitação de registro para " + name + ".");
        var client = new Client(name, IPAddress, port);
        clients.add(client);

        // publica mensagem de boas vindas à todos usuários do chat
        sendMessage("registered", IPAddress, port);
        var helloMessage = helloMessages[r.nextInt(helloMessages.length - 1)];
        var message = getHour() + " Servidor: " + String.format(helloMessage, name);
        multicastPublisher.sendMessage(message);
    }

    private void sendDefaultMessage(InetAddress IPAddress, int port, String message) throws IOException {

        if (this.invalidateClient(IPAddress, port)) {
            LOGGER.info(": Cliente não registrado ou expirado.");
            return;
        }

        var client = this.getClientByPort(port);
        if (client.isPresent()) {
            multicastPublisher.sendMessage(getHour() + " " + client.get().getName() + ": " + message);
        }
    }

    private void sendPrivateMessage(InetAddress IPAddress, int senderPort, String receiverName, String text) throws IOException {
        if (this.invalidateClient(IPAddress, senderPort)) {
            LOGGER.info(": Cliente não registrado ou expirado.");
            return;
        }

        var receiverClientOpt = this.getClientByName(receiverName);


        if (receiverClientOpt.isEmpty()) {
            LOGGER.info(": Destinatário não registrado ou expirado.");
            return;
        }

        var receiverClient = receiverClientOpt.get();

        if (text.equals("")) return;

        var senderName = getClientByPort(senderPort).get().getName();

        var message = getHour() + " " + senderName + " (private): " + text;
        this.sendMessage(message, receiverClient.getIPAdress(), receiverClient.getPort());
    }

    private void showOnlineClients(InetAddress IPAddress, int port) throws IOException {
        if (this.invalidateClient(IPAddress, port)) {
            LOGGER.warning(": Cliente não registrado ou expirado.");
            return;
        }

        var message = getClientsAsString(clients);

        this.sendMessage(message, IPAddress, port);
    }


    private void sendMessage(String message, InetAddress IPAddress, int port) throws IOException {
        var buffer = message.getBytes();

        DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, IPAddress, port);

        serverSocket.send(datagram);
    }

    public void removeClient(int port) throws IOException {
        var clientOpt = this.getClientByPort(port);
        if (clientOpt.isEmpty())
            return;

        var client = clientOpt.get();
        clients.removeIf(x -> x.getPort() == port);
        LOGGER.info(": " + client.getName() + " foi removido do servidor.");
        multicastPublisher.sendMessage(getHour() + " " + client.getName() + " saiu do servidor. '-' ");

        this.sendMessage("terminate", client.getIPAdress(), client.getPort());
    }

    /**
     * Get sender in client list
     */
    private Optional<Client> getClientByPort(int port) {
        return clients.stream()
                .filter(it -> it.getPort() == port)
                .findFirst();
    }

    private Optional<Client> getClientByName(String name) {
        return clients.stream()
                .filter(it -> it.getName().equals(name))
                .findFirst();
    }

    /**
     * Checks if given clients exists.
     */
    private boolean invalidateClient(InetAddress IPAddress, int port) throws IOException {
        var client = this.getClientByPort(port);
        if (client.isEmpty())
            sendMessage("Você não está registrado, favor utilizar o comando !register [nome] para acessar o chat.", IPAddress, port);
        return client.isEmpty();
    }

    /**
     * Get now time and convert to string
     */

    public static void main(String[] args) throws IOException {

        System.out.println(logo);

        var server = new Server();
        server.run();

    }
}