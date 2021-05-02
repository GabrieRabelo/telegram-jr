package com.redes.lab.server;

import java.net.InetAddress;
import java.time.Instant;

public class Client {
    private String name;
    private InetAddress IPAdress;
    private int port;
    private Instant lastKeepAlive;

    public Client(String name, InetAddress IPAdress, int port) {
        this.name = name;
        this.IPAdress = IPAdress;
        this.port = port;
        this.lastKeepAlive = Instant.now();
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

    public Instant getLastKeepAlive() {
        return lastKeepAlive;
    }

    public void setLastKeepAlive(Instant lastKeepAlive) {
        this.lastKeepAlive = lastKeepAlive;
    }
}
