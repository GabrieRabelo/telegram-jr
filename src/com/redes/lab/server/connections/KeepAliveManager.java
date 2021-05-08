package com.redes.lab.server.connections;

import com.redes.lab.server.Server;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

/**
 * KeepAliveManager é responsável por verificar cliente por cliente, seus últimos horários de keep-alive
 * Se a diferença de tempo entre o ultimo keep-alive e o tempo de agora
 * forem maiores que o tempo determinado na variável TIME_LIMIT_MILLIS a "conexão" é encerrada.
 */
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
                        server.removeClient(client.getPort(), "Server did not receive keep-alive for more than " + TIME_LIMIT_MILLIS / 1000 + " seconds from the client." );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
