package com.scout;

/*
  FusionCorps 6672 Scouting App
  Built with JavaFX framework
  @author FusionCorps-Rishabh Rengarajan
  each page is a separate scene, whose layout is defined in a separate FXML file
  Utility classes:
 * LimitedTextField.java - restrictive text field
 * QRFuncs.java - specific implementations of the ZXing library
 * AlertBox.java -  pop-up box for error messages
 * CopyImageToClipboard.java - copies output QR code/raw text data to clipboard for debugging
 *
 AppMain.java - main class, launches app
 AppRun.java - main class for running app as an executable JAR
 FXMLController.java is the main controller that controls all scenes. It handles all user input and data storage.
 */

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class AppMain extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLController.setPage(primaryStage, 0);
    }

    public static void main(String[] args) {
        launch();
    }
}

