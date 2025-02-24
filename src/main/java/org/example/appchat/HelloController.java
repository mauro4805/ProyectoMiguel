package org.example.appchat;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloController {

   // @FXML
   // private Label labelUsuario;
    @FXML
    private TextArea chatArea;
    @FXML
    private TextField mensajeField;
    @FXML
    private ListView<String> listUsuarios;

    private DatagramSocket socket;
    private InetAddress ipServidor;
    private int puerto;
    private String usuario;
    private List<String> usuariosConectados = new ArrayList<>();

    private ExecutorService executorService;

    public void initialize() {
        // Cargar la configuración desde el archivo de configuración
        usuario = LectorConfig.getUsuario();
        puerto = LectorConfig.getPuerto();
        try {
            ipServidor = InetAddress.getByName("255.255.255.255"); // Dirección de difusión para la red local
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Mostrar el nombre de usuario en la interfaz
       // labelUsuario.setText("Usuario: " + usuario);

        // Inicializar el socket UDP
        try {
            socket = new DatagramSocket(puerto);
            socket.setBroadcast(true); // Habilitar la difusión en la red
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // Hilo para escuchar mensajes entrantes
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this::escucharMensajes);

        // Enviar un mensaje de "presencia" a los demás usuarios en la red
        enviarMensaje("¡Soy " + usuario + " y estoy conectado!", true);
    }

    // Metodo para escuchar mensajes entrantes
    private void escucharMensajes() {
        try {
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String mensaje = new String(packet.getData(), 0, packet.getLength());
                String nuevoUsuario = mensaje.substring(4, mensaje.length() - 19);
                // Si el mensaje es de un nuevo usuario
                if (mensaje.startsWith("¡Soy ")) {
                    if (!usuariosConectados.contains(nuevoUsuario)) {
                        usuariosConectados.add(nuevoUsuario);
                        actualizarUsuarios();
                    }
                } else {
                    // Mostrar el mensaje recibido en el área de chat
                    if(!mensaje.contains(usuario)){
                        chatArea.appendText(mensaje + "\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metodo para actualizar la lista de usuarios conectados
    private void actualizarUsuarios() {
        listUsuarios.getItems().clear();
        listUsuarios.getItems().addAll(usuariosConectados);
    }

    // Metodo para enviar un mensaje
    @FXML
    private void onEnviarMensaje() {
        String mensaje = mensajeField.getText();
        if (!mensaje.trim().isEmpty()) {
            enviarMensaje(usuario + ": " + mensaje, false);
            mensajeField.clear();
        }
    }

    // Metodo para enviar un mensaje UDP
    private void enviarMensaje(String mensaje, boolean esPresencia) {
        try {
            byte[] buffer = mensaje.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ipServidor, puerto);
            socket.send(packet);

            // Si es un mensaje de presencia, lo enviamos a la red
            if (esPresencia) {
                chatArea.appendText(usuario + " se ha unido al chat.\n");
            } else {
                chatArea.appendText(mensaje + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

