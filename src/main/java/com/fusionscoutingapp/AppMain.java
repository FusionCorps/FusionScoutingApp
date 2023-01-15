package com.fusionscoutingapp;

/**
 * FusionCorps 6672 Scouting App
 * Built off JavaFX and SceneBuilder frameworks
 * @author FusionCorps-Rishabh Rengarajan
 * each page is a separate scene, whose layout is defined in a separate FXML file
 * @FXMLController.java is the main controller for each scene. It handles all user input and data storage.
 *
 * Utililty classes:
 * @LimitedTextField.java - restrictive text field elements
 * @QRFuncs.java - specific implementations of the ZXing library
 * @CopyImageToClipboard.java - copies output QR code/raw text data to clipboard for debugging
 *
 * @AppMain.java - main class, launches app
 * @AppRun.java - main class for running app as an executable JAR
 **/

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class AppMain extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLController.setPage(primaryStage);
    }

    public static void main(String[] args) {
        launch();
    }


}