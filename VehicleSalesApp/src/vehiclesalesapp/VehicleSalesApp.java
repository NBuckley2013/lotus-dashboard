/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vehiclesalesapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Neil
 */
public class VehicleSalesApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent parent = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        stage.getIcons().add(new Image(VehicleSalesApp.class.getResourceAsStream("resources/lotus_logo.png")));
        Scene scene = new Scene(parent, 1280, 720);
        stage.setScene(scene);
        stage.setTitle("Lotus Dashboard");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}