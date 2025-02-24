package org.example.appchat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LectorConfig {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = LectorConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("No se encontró el archivo de configuración.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getUsuario() {
        return properties.getProperty("usuario", "UsuarioPorDefecto");
    }

    public static String getMensaje() {
        return properties.getProperty("mensaje", "MensajePorDefecto");
    }

    public static int getPuerto() {
        return Integer.parseInt(properties.getProperty("puerto", "9876"));
    }

    public static String getTema() {
        return properties.getProperty("tema", "claro").toLowerCase(); // Retorna "claro" o "oscuro"
    }
}
