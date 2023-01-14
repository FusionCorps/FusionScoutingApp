module com.fusionscoutingapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;
    requires java.desktop;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires org.controlsfx.controls;


    opens com.fusionscoutingapp to javafx.fxml;
    exports com.fusionscoutingapp;
}