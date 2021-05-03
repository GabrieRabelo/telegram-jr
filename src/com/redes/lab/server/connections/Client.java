package com.redes.lab.server.connections;

import java.net.InetAddress;
import java.time.Instant;

public class Client {
    private String name;
    private InetAddress IPAddress;
    private int port;
    private Instant lastKeepAlive;

    public Client(String name, InetAddress IPAddress, int port) {
        this.name = name;
        this.IPAddress = IPAddress;
        this.port = port;
        this.lastKeepAlive = Instant.now();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(InetAddress IPAddress) {
        this.IPAddress = IPAddress;
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
