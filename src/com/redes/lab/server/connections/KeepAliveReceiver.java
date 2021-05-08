package com.redes.lab.server.connections;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * KeepAliveReceiver é responsável por receber as mensagens de keep alive recebidas e
 * atualizar o ultimo keep-alive de cada usuário.
 */
public class KeepAliveReceiver extends Thread{

    private final DatagramSocket keepAliveSocket;
    private final List<Client> clients;

    public KeepAliveReceiver(List<Client> clients, int port) throws SocketException {
        this.clients = clients;
        keepAliveSocket = new DatagramSocket(port);
    }

    public void run() {

        while (true) {
            var buffer = new byte[128];
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

            try {
                keepAliveSocket.receive(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }

            var clientOpt = getClientByPort(dp.getPort());
            if (clientOpt.isEmpty())
                continue;

            var client = clientOpt.get();
            client.setLastKeepAlive(Instant.now());
        }
    }

    private Optional<Client> getClientByPort(int port) {
        return clients.stream()
                .filter(it -> it.getPort() == port)
                .findFirst();
    }
}
