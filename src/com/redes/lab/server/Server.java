package com.redes.lab.server;

import com.redes.lab.server.connections.Client;
import com.redes.lab.server.connections.KeepAliveManager;
import com.redes.lab.server.connections.KeepAliveReceiver;
import com.redes.lab.server.publishers.MessageSender;
import com.redes.lab.server.publishers.MulticastPublisher;

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

    private static final Logger LOGGER = Logger.getLogger("Server");

    private static final Random r = new Random();
    private static final int SERVER_PORT = 9876;
    private static final int KEEP_ALIVE_PORT = 9875;
    private static final int BUFFER_SIZE = 1024;
    private static final List<String> COMMAND_LIST = Arrays.asList("!register", "!pm", "!leave", "!online", "!commands");

    private final DatagramSocket serverSocket;
    private final List<Client> clients = new CopyOnWriteArrayList<>();
    private final MulticastPublisher multicastPublisher;
    private final MessageSender messageSender;

    public Server() throws IOException {

        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");

        this.serverSocket = new DatagramSocket(SERVER_PORT);
        LOGGER.info(": Starting server at port: " + SERVER_PORT);

        this.multicastPublisher = new MulticastPublisher(serverSocket);
        multicastPublisher.sendMessage("terminate");
        LOGGER.info(": Closing previously connections.");

        this.messageSender = new MessageSender(serverSocket);

        new KeepAliveManager(clients, this).start();

        LOGGER.info(": Starting keep-alive server at port " + KEEP_ALIVE_PORT);
        new KeepAliveReceiver(clients, KEEP_ALIVE_PORT).start();
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
                    this.removeClient(receivedPacket.getPort(), "Client left by command.");
                    break;

                case "!online":
                    this.showOnlineClients(receivedPacket.getAddress(), receivedPacket.getPort());
                    break;

                case "!commands":
                    this.showCommands(COMMAND_LIST.toString(), receivedPacket.getAddress(), receivedPacket.getPort());
                    break;
                default:
                    //Se o comando não existir, ou não for comando, envia para todos como fala;
                    this.sendDefaultMessage(message, receivedPacket.getAddress(), receivedPacket.getPort());
                    break;
            }
        }
    }

    private void registerClient(InetAddress IPAddress, int port, String name) throws IOException {
        LOGGER.info(": New registration request for user name " + name + ".");
        var existingClient = this.getClientByPort(port);
        if (existingClient.isPresent()){
            messageSender.sendMessage(getHour() + " Servidor (privado): Você já está registrado, pare de tentar achar bugs. hehehe", IPAddress, port);
            return;
        }
        var client = new Client(name, IPAddress, port);
        clients.add(client);

        messageSender.sendMessage("registered", IPAddress, port);
        messageSender.sendMessage(getHour() + " Servidor (privado): Registrado com sucesso, digite algum texto para falar no chat. Ou utilize o comando !commands para ver os comandos disponíveis", IPAddress, port);

        // publica mensagem de boas vindas à todos usuários do chat
        var helloMessage = helloMessages[r.nextInt(helloMessages.length - 1)];
        var message = getHour() + " Servidor (para todos): " + String.format(helloMessage, name);
        multicastPublisher.sendMessage(message);
    }

    private void sendDefaultMessage(String message, InetAddress IPAddress, int port) throws IOException {

        if (this.invalidateClient(IPAddress, port)) {
            LOGGER.info(": Invalid action for not registered client.");
            return;
        }

        var client = this.getClientByPort(port);
        if (client.isPresent()) {
            multicastPublisher.sendMessage(getHour() + " " + client.get().getName() + ": " + message);
        }
    }

    private void sendPrivateMessage(InetAddress IPAddress, int senderPort, String receiverName, String text) throws IOException {
        if (this.invalidateClient(IPAddress, senderPort)) {
            LOGGER.info(": Invalid action for not registered client.");
            return;
        }

        var receiverClientOpt = this.getClientByName(receiverName);


        if (receiverClientOpt.isEmpty()) {
            LOGGER.info(": Receiver " + receiverName + " not registered or expired.");
            return;
        }

        var receiverClient = receiverClientOpt.get();

        if (text.isBlank()) return;

        var senderName = getClientByPort(senderPort).get().getName();

        var message = getHour() + " " + senderName + " (privado): " + text;
        var messageReceiver = getHour() + " " + receiverName + " (para " + senderName + "): " + text;

        messageSender.sendMessage(messageReceiver, IPAddress, senderPort);
        messageSender.sendMessage(message, receiverClient.getIPAddress(), receiverClient.getPort());
    }

    private void showOnlineClients(InetAddress IPAddress, int port) throws IOException {
        if (this.invalidateClient(IPAddress, port)) {
            LOGGER.info(": Invalid action for not registered client.");
            return;
        }

        var message = getClientsAsString(clients);

        messageSender.sendMessage(message, IPAddress, port);
    }

    public void removeClient(int port, String reason) throws IOException {
        var clientOpt = this.getClientByPort(port);
        if (clientOpt.isEmpty())
            return;

        var client = clientOpt.get();
        clients.removeIf(x -> x.getPort() == port);
        LOGGER.info(String.format(": %s was removed from the server. Reason: %s", client.getName(), reason));

        multicastPublisher.sendMessage(getHour() + " " + client.getName() + " saiu do servidor. '-' ");
        messageSender.sendMessage("terminate", client.getIPAddress(), client.getPort());
    }

    public void showCommands(String message, InetAddress IPAddress, int port) throws IOException {
        if (this.invalidateClient(IPAddress, port)) {
            LOGGER.info(": Invalid action for not registered client.");
            return;
        }
        messageSender.sendMessage(message, IPAddress, port);
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
     * Checks if given client exists.
     */
    private boolean invalidateClient(InetAddress IPAddress, int port) throws IOException {
        var client = this.getClientByPort(port);
        if (client.isEmpty())
            messageSender.sendMessage("Você não está registrado, favor utilizar o comando !register [nome] para acessar o chat.", IPAddress, port);
        return client.isEmpty();
    }

    public static void main(String[] args) throws IOException {

        System.out.println(logo);

        var server = new Server();
        server.run();

    }
}