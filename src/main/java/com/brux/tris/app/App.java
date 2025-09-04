package com.brux.tris.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        /* Loader del file FXML relativo alla GUI */
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/brux/tris/view/MainView.fxml"));
        Scene scene = new Scene(loader.load());


        /* Setting della scena creata a partire dal FXML nello stage */
        primaryStage.setTitle("Tris Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);   // Blocco dimensionamento finestra
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

