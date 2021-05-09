package com.redes.lab.client.receivers;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

public class ImageReceiver extends Thread {

    private MulticastSocket socket;
    private byte[] buffer;
    private InetAddress group;
    private Random r = new Random();


    public void run() {
        try {
            socket = new MulticastSocket(4447);
            group = InetAddress.getByName("230.0.0.0");
            socket.joinGroup(group);

            while (true) {
                buffer = new byte[50000];
                var packet = new DatagramPacket(buffer, buffer.length);

                System.out.println("aqui chegou");
                socket.receive(packet);

                var data = packet.getData();

                var bis = new ByteArrayInputStream(
                        data);

                var bufferedImage = ImageIO.read(bis);

                System.out.println("chegou aqui");

                var outputfile = new File("src/com/redes/lab/client/disk/receive/" + r.nextInt() + ".jpg/");
                ImageIO.write(bufferedImage, "jpg", outputfile);


            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.leaveGroup(group);
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket.close();
        }
    }
}