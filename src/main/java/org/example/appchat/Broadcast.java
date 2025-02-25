package org.example.appchat;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Broadcast {

    private static final int PORT = 9876;
    private static final String BROADCAST_ADDRESS = "255.255.255.255";
    private static volatile boolean running = true;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce tu nombre de usuario: ");
        String username = scanner.nextLine();

        Thread receiveThread = new Thread(() -> {
            try (DatagramSocket receiveSocket = new DatagramSocket(PORT)) {
                byte[] buffer = new byte[1024];
                while (running) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    receiveSocket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());

                    if (!message.startsWith(username + ":")) {
                        System.out.println("\n" + message);
                    }
                }
            } catch (IOException e) {
                if (running) e.printStackTrace();
            }
        });

        receiveThread.start();

        try (DatagramSocket sendSocket = new DatagramSocket()) {
            sendSocket.setBroadcast(true);

            String joinMessage = "Usuario: " + username + " está en línea";
            sendBroadcast(sendSocket, joinMessage);

            while (true) {
                System.out.print(username+": ");
                String message = scanner.nextLine();
                if (message.equalsIgnoreCase("salir")) {
                    sendBroadcast(sendSocket, "Usuario: " + username + " ha salido del chat");
                    running = false;
                    receiveThread.interrupt();
                    break;
                }
                sendBroadcast(sendSocket, username + ": " + message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Saliendo del chat...");
            System.exit(0);
        }
    }

    private static void sendBroadcast(DatagramSocket socket, String message) throws IOException {
        byte[] buffer = message.getBytes();
        InetAddress address = InetAddress.getByName(BROADCAST_ADDRESS);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        socket.send(packet);
    }
}
