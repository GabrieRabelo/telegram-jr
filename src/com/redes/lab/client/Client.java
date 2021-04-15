package com.redes.lab.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class Client {

    private final static String BASE_PATH = "src/com/rabelo/udp/client/disk/";

    public static void main(String[] args) {

        try(DatagramSocket clientSocket = new DatagramSocket(2000)) {
            InetAddress IPAddress = InetAddress.getByName("localhost");

            sendFile(clientSocket, IPAddress, "file10000bytes.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Fim do algoritmo :)");
    }

    private static void sendFile(DatagramSocket clientSocket, InetAddress IPAddress, String fileName) throws IOException {

        byte[] sendBuffer = new byte[10000];
        File sourceFile = new File(BASE_PATH + fileName);
        FileInputStream inputStream = new FileInputStream(sourceFile);

        while ((inputStream.read(sendBuffer)) != -1) {
            DatagramPacket pack = new DatagramPacket(sendBuffer, getLength(sourceFile), IPAddress, 9876);
            clientSocket.send(pack);
        }

        inputStream.close();
    }

    /**
     * Método para retornar o tamanho do conteúdo de um arquivo txt, evitando enviar bytes desnecessários.
     */
    private static int getLength(File file) throws IOException {
        FileInputStream f = new FileInputStream(file);
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        f.close();
        return resultStringBuilder.toString().length();
    }
}