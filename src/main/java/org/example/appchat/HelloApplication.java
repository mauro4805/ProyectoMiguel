package org.example.appchat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Cargar el archivo FXML
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/org/example/appchat/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Configuración del tema CSS
        String tema = LectorConfig.getTema();
        String cssFile = tema.equalsIgnoreCase("oscuro") ? "/org/example/appchat/css/tema-oscuro.css" : "/org/example/appchat/css/tema-claro.css";
        URL cssURL = HelloApplication.class.getResource(cssFile);

        if (cssURL != null) {
            scene.getStylesheets().add(cssURL.toExternalForm());
        } else {
            System.err.println("No se encontró el archivo CSS: " + cssFile);
        }

        primaryStage.setTitle("Aplicación de Chat");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
