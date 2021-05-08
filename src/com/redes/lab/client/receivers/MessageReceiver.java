package com.redes.lab.client.receivers;

import com.redes.lab.client.senders.KeepAliveSender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Message receiver serve para ter controle das mensagens
 * que são recebidas pelo servidor somente para o cliente.
 */

public class MessageReceiver extends Thread {
    private final DatagramSocket datagramSocket;
    private final InetAddress IPAddress;
    private final int keepAlivePort;

    public MessageReceiver(DatagramSocket datagramSocket, InetAddress IPAddress, int keepAlivePort) {
        this.datagramSocket = datagramSocket;
        this.IPAddress = IPAddress;
        this.keepAlivePort = keepAlivePort;
    }

    public void run() {

        while(true){
            var buffer = new byte[1024];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            try {
                datagramSocket.receive(receivedPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            var message = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

            switch (message){
                case "terminate":
                    System.exit(1);
                    break;
                case "registered":
                    //Ao ser registrado, inicia 2 threads novas, a de receptor multicast e a de envio de keep-alive
                    new MulticastReceiver().start();
                    new KeepAliveSender(datagramSocket, IPAddress, keepAlivePort).start();
                    break;
                default:
                    System.out.println(message);
            }
        }

    }
}
