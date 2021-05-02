package com.redes.lab.server;

import java.io.IOException;
import java.net.SocketException;
import java.time.Instant;
import java.util.List;

public class KeepAliveManager extends Thread {

    private final List<Client> clients;
    private static final int TIME_LIMIT_MILLIS = 20000;
    private final Server server;

    public KeepAliveManager(int port, List<Client> clients, Server server) throws SocketException {
        this.clients = clients;
        this.server = server;

        new KeepAliveReceiver(clients, port).start();
    }

    public void run() {
        while (true) {
            if (!clients.isEmpty()) {
                for (Client client : clients) {
                    var now = Instant.now().toEpochMilli();
                    var clientKeepAlive = client.getLastKeepAlive().toEpochMilli();
                    var result = now - clientKeepAlive;

                    if (result > TIME_LIMIT_MILLIS) {
                        try {
                            server.removeClient(client.getPort());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //kick
                    }
                }
            }

        }
    }
}
