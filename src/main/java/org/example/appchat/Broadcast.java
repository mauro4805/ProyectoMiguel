package org.example.appchat;

import java.net.*;
import java.util.Enumeration;
import java.util.Scanner;

public class Broadcast {
    private static final int PUERTO = 9876;

    public static void main(String[] args) {
        new Thread(Broadcast::recibirMensajes).start();
        enviarMensajeInicial();
        enviarMensajes();
    }

    private static void enviarMensajeInicial() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);

            InetAddress broadcastAddress = obtenerDireccionBroadcast();
            if (broadcastAddress == null) {
                System.out.println("No se pudo obtener la dirección de broadcast.");
                return;
            }

            String mensajeInicial = "[Cliente A] Hola, estoy conectado: " + InetAddress.getLocalHost().getHostAddress();
            byte[] buffer = mensajeInicial.getBytes();

            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, broadcastAddress, PUERTO);
            socket.send(paquete);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void enviarMensajes() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            Scanner scanner = new Scanner(System.in);

            InetAddress broadcastAddress = obtenerDireccionBroadcast();

            if (broadcastAddress == null) {
                System.out.println("No se pudo obtener la dirección de broadcast.");
                return;
            }

            System.out.println("[Cliente A] Escribe tus mensajes (escribe 'salir' para terminar):");
            while (true) {
                String mensaje = scanner.nextLine();
                if (mensaje.equalsIgnoreCase("salir")) break;

                mensaje = "[Cliente A] " + InetAddress.getLocalHost().getHostAddress() + ": " + mensaje;
                byte[] buffer = mensaje.getBytes();
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, broadcastAddress, PUERTO);
                socket.send(paquete);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void recibirMensajes() {
        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            byte[] buffer = new byte[1024];
            System.out.println("[Cliente A] Escuchando mensajes de la red...");

            while (true) {
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);
                String mensaje = new String(paquete.getData(), 0, paquete.getLength());
                System.out.println(mensaje);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static InetAddress obtenerDireccionBroadcast() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();

                if (ni.isLoopback() || !ni.isUp()) {
                    continue;
                }

                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast != null) {
                        return broadcast;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
