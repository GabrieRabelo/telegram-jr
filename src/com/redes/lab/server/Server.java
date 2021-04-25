package com.redes.lab.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

import static com.redes.lab.server.Useless.helloMessages;
import static com.redes.lab.server.Useless.logo;

public class Server {

    private static final Logger LOGGER = Logger.getLogger("Telegram Jr.");
    private static final Random r = new Random();

    private final DatagramSocket serverSocket;
    private final List<Client> clients = new ArrayList<>();
    private final MulticastPublisher multicastPublisher;



    public Server() throws IOException {
        serverSocket = new DatagramSocket(9876);
        multicastPublisher = new MulticastPublisher();

        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
    }

    public void run() throws IOException {

        int len;

        LOGGER.info(": Telegram Jr. Server Started");

        // Command "thread"
        while (true) {

            // novo buffer;
            var buffer = new byte[1024];

            // recebe mensagem;
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            serverSocket.receive(receivedPacket);

            // verifica se o pacote não veio vazio, se sim ignora
            if (receivedPacket.getLength() == 0)
                continue;

            // pega mensagem e separa conforme necessario
            var message = new String(receivedPacket.getData(),0, receivedPacket.getLength());
            var splitMessage = message.split(" ");

            // verifica se a mensagem recebida é comando
            String command = "";
            var argument = "";
            if (splitMessage[0].startsWith("!")) {
                command = splitMessage[0].toLowerCase();
                argument = splitMessage[1];
            }

            switch (command) {
                case "!register":
                    this.registerClient(receivedPacket.getAddress(), receivedPacket.getPort(), argument);
                    break;

                case "!pm":
                    break;

                default:
                    //Se o comando não existir, ou não for comando, envia para todos como fala;
                    this.defaultMessage(receivedPacket.getPort(), message);
                    break;
            }
        }
    }

    private void registerClient(InetAddress IPAddress, int port, String name) throws IOException {
        LOGGER.info(": Nova solicitação de registro para " + name + ".");
        var client = new Client(name, IPAddress, port);
        clients.add(client);

        // publica mensagem de boas vindas à todos usuários do chat
        var helloMessage = helloMessages[r.nextInt(helloMessages.length-1)];
        multicastPublisher.sendMessage(this.getHour() + " Servidor: " + String.format(helloMessage, name));
    }

    private void defaultMessage(int port, String message) throws IOException {

        if (!this.validateClient(port)) {
            LOGGER.warning(": Cliente não registrado ou expirado.");
            return;
        }

        var client = this.getSender(port);
        if (client.isPresent()) {
            multicastPublisher.sendMessage(this.getHour() + " " + client.get().getName() + ": " + message);
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

    /**
     * Checks if given clients exists.
     */
    private boolean validateClient(int port) {
        var client = this.getSender(port);
        return client.isPresent();
    }

    /**
     * Get now time and convert to string
     */
    private String getHour() {
        var localTime = LocalTime.now();
        return localTime.getHour() + ":" + localTime.getMinute();
    }

    public static void main(String[] args) throws IOException {

        System.out.println(logo);

        var server = new Server();
        server.run();

    }
}