package com.redes.lab.server;

import java.io.IOException;

public class ApplicationServer {

    public static void main(String[] args) throws IOException {

        var server = new Server();

        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
