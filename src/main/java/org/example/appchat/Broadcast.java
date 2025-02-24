package org.example.appchat;

import java.net.*;
import java.util.Enumeration;
import java.util.Scanner;

public class Broadcast {
    private static final int PUERTO = 9876;

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            InetAddress broadcastAddress = obtenerDireccionBroadcast();
            if (broadcastAddress == null) {
                System.out.println("No se pudo obtener la dirección de broadcast.");
                return;
            }

            String direccionIP = obtenerDireccionIP();

            // Iniciar hilo para recibir mensajes
            new Thread(() -> recibirMensajes(socket)).start();

            // Enviar mensaje inicial
            enviarMensaje(socket, broadcastAddress, "[Cliente " + direccionIP + "] Hola, estoy conectado: " + direccionIP);

            // Enviar mensajes desde la consola
            enviarMensajes(socket, broadcastAddress, direccionIP);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void enviarMensajes(DatagramSocket socket, InetAddress broadcastAddress, String direccionIP) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("[Cliente " + direccionIP + "] Escribe tus mensajes (escribe 'salir' para terminar):");
            while (true) {
                String mensaje = scanner.nextLine();
                if (mensaje.equalsIgnoreCase("salir")) break;
                System.out.println("[Enviando] " + mensaje);
                enviarMensaje(socket, broadcastAddress, "[Cliente " + direccionIP + "] " + mensaje);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void enviarMensaje(DatagramSocket socket, InetAddress broadcastAddress, String mensaje) {
        try {
            byte[] buffer = mensaje.getBytes();
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, broadcastAddress, PUERTO);
            socket.send(paquete);
            System.out.println("[Enviado] " + mensaje);  // Confirmación de mensaje enviado
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void recibirMensajes(DatagramSocket socket) {
        try {
            byte[] buffer = new byte[1024];
            System.out.println("[Cliente] Escuchando mensajes de la red...");

            socket.setSoTimeout(1000); // Establecer un tiempo de espera para evitar bloqueos indefinidos.

            while (true) {
                try {
                    DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                    socket.receive(paquete);  // Recibe el mensaje
                    String mensaje = new String(paquete.getData(), 0, paquete.getLength());

                    // No mostrar los mensajes enviados por el propio equipo
                    if (!paquete.getAddress().equals(InetAddress.getLocalHost())) {
                        System.out.println("[Recibido] " + mensaje);
                    }
                } catch (SocketTimeoutException e) {
                    // No hacer nada si no hay mensaje recibido.
                }
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

                if (ni.isLoopback() || !ni.isUp() || ni.getDisplayName().contains("Virtual")) {
                    continue; // Saltar interfaces virtuales o inactivas
                }

                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    InetAddress address = interfaceAddress.getAddress();

                    if (broadcast != null && address.isSiteLocalAddress()) {
                        System.out.println("Dirección de Broadcast encontrada: " + broadcast);
                        return broadcast;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String obtenerDireccionIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();

                if (ni.isLoopback() || !ni.isUp() || ni.getDisplayName().contains("Virtual")) {
                    continue; // Saltar interfaces virtuales o inactivas
                }

                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                    InetAddress address = interfaceAddress.getAddress();
                    if (address.isSiteLocalAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Desconocida";
    }
}
