/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package javafxandcss;

import Class.MusicItem;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Sebxs
 */
public class Controller implements Initializable {
    
    private double xOffset;
    private double yOffset;    

    @FXML
    private Button btnRegisterOfLogin;
    
    @FXML
    private Button btnLoginOfSingUp;
    
    @FXML
    private Button btnLoginOfLogin;
    
    @FXML
    private void handleExit(ActionEvent event) {
        System.exit(0);
    }
    
    @FXML
    private void changeScreenToSingUp(ActionEvent event){
    try {
        
        Stage currentStage = (Stage) btnRegisterOfLogin.getScene().getWindow();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("singUp.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED.UNDECORATED);

        root.setOnMousePressed(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
            
        });
        
        root.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
            
        });
        
        stage.show();
        currentStage.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    @FXML
    private void changeScreenToLogin(ActionEvent event){
    try {
        
        Stage currentStage = (Stage) btnLoginOfSingUp.getScene().getWindow();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED.UNDECORATED);

        root.setOnMousePressed(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
            
        });
        
        root.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
            
        });
        
        stage.show();
        currentStage.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
    
    @FXML
    private void changeScreenToReproductor(ActionEvent event){
    try {
        
        Stage currentStage = (Stage) btnLoginOfLogin.getScene().getWindow();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("reproductor.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED.UNDECORATED);

        root.setOnMousePressed(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
            
        });
        
        root.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
            
        });
        
        stage.show();
        currentStage.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
    
    @FXML
    private void addMusic(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo MP3");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos MP3", "*.mp3"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            String musicName = selectedFile.getName();
            String musicURL = selectedFile.toURI().toString();

            MusicItem newMusicItem = new MusicItem(musicName, musicURL);

            listMusic.getItems().add(newMusicItem);
        }
    }

    
    @FXML
    private ListView<MusicItem> listMusic;
    private AnchorPane itemEmptyListView;
    private Label labelItemEmptyListView;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            if (listMusic != null) {
                if (listMusic.getItems().isEmpty()) {
                    if (itemEmptyListView != null) {
                        labelItemEmptyListView = (Label) itemEmptyListView.lookup("#labelItemEmptyListView");
                        if (labelItemEmptyListView != null) {
                            System.out.println(labelItemEmptyListView.getText());
                        }

                        // Mostrar el AnchorPane si la lista está vacía
                        itemEmptyListView.setVisible(true);
                    }
                } else {
                    if (itemEmptyListView != null) {
                        System.out.println("No está vacío");
                        itemEmptyListView.setOpacity(0);
                        // Ocultar el AnchorPane si hay elementos en la lista
                        itemEmptyListView.setVisible(false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



   
    
}
