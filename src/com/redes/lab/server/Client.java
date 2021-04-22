package com.redes.lab.server;

import java.net.InetAddress;

public class Client {
    private String name;
    private InetAddress IPAdress;
    private int port;

    public Client(String name, InetAddress IPAdress, int port) {
        this.name = name;
        this.IPAdress = IPAdress;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getIPAdress() {
        return IPAdress;
    }

    public void setIPAdress(InetAddress IPAdress) {
        this.IPAdress = IPAdress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
