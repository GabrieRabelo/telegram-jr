package com.redes.lab.client.receivers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Multicast receiver serve para ter controle das mensagens que são recebidas
 * pelo servidor para todos clientes inscritos no grupo de multicast.
 */
public class MulticastReceiver extends Thread {
    protected MulticastSocket socket;
    protected byte[] buffer;

    public void run() {
        try {
            socket = new MulticastSocket(4446);
            InetAddress group = InetAddress.getByName("230.0.0.0");
            socket.joinGroup(group);

            while (true) {
                buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                if ("terminate".equals(received)) {
                    System.out.println("Servidor interrompeu a conexão.");
                    System.exit(1);
                    break;
                }
                System.out.println(received);
            }
            socket.leaveGroup(group);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}