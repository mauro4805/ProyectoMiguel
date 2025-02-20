module org.example.appchat {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.appchat to javafx.fxml;
    exports org.example.appchat;
}