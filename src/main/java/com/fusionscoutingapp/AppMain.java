package com.fusionscoutingapp;

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