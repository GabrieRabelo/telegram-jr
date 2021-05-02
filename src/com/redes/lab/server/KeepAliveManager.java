package com.redes.lab.server;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class KeepAliveManager extends Thread {

    private static final int TIME_LIMIT_MILLIS = 20000;

    private final List<Client> clients;
    private final Server server;

    public KeepAliveManager(List<Client> clients, Server server) {
        this.clients = clients;
        this.server = server;
    }

    public void run() {

        while (true) {

            if (clients.isEmpty())
                continue;

            for (Client client : clients) {

                // Cálculo que verifica se a diferença do último keep-alive e o horário de agora resulta em mais de 20 segundos
                var now = Instant.now().toEpochMilli();
                var clientKeepAlive = client.getLastKeepAlive().toEpochMilli();
                var result = now - clientKeepAlive;

                // se resultar em mais de 20 segundos remove o cliente
                if (result > TIME_LIMIT_MILLIS) {
                    try {
                        server.removeClient(client.getPort());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
