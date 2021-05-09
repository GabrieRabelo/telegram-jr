package com.redes.lab.server.transmitter;

import com.redes.lab.server.connections.Client;
import com.redes.lab.server.publishers.MulticastPublisher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;
import java.util.Optional;

public class ImageTransmitter extends Thread {

    private static final int PORT = 9874;

    private final DatagramSocket imageSocket;
    private final List<Client> clients;
    private final MulticastPublisher multicastPublisher;

    public ImageTransmitter(List<Client> clients, MulticastPublisher multicastPublisher) throws SocketException {
        this.imageSocket = new DatagramSocket(PORT);
        this.clients = clients;
        this.multicastPublisher = multicastPublisher;
    }

    public void run() {

        while (true) {
            var buffer = new byte[50000];
            var dp = new DatagramPacket(buffer, buffer.length);

            System.out.println("chegou antes de receber");

            try {
                imageSocket.receive(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("dps antes de receber");

            var clientOpt = getClientByPort(dp.getPort());
            if (clientOpt.isEmpty())
                continue;

            try {
                multicastPublisher.sendMessage(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Optional<Client> getClientByPort(int port) {
        return clients.stream()
                .filter(it -> it.getPort() == port)
                .findFirst();
    }
}
