/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package javafxandcss;

import Class.MusicItem;
import Database.ConexionBD;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author Sebxs
 */
public class Controller implements Initializable {
    
    private double xOffset;
    private double yOffset;  
    
    public String nameUser;
    public int userID;
    
    public int numMusic = 1;
    
    @FXML
    private Label labelNameUser;
    
    @FXML
    private ListView<MusicItem> listMusic;
    
    @FXML
    private Button btnRegisterOfLogin;
    
    @FXML
    private Button btnLoginOfSingUp;
    
    @FXML
    private Button btnLoginOfLogin;
    
    @FXML
    private Label nameMusicReproductor;
    
    @FXML
    private Slider durationSound;
    
    @FXML
    private Label timeSound;
    
    @FXML
    private Slider sliderVolume;
    
    @FXML
    private FontAwesomeIconView iconVolume;
    
    @FXML
    private FontAwesomeIconView playAndPauseSound;
    
    @FXML
    private FontAwesomeIconView forwardSound;
    
    @FXML
    private FontAwesomeIconView backwardSound;
    
    @FXML
    private FontAwesomeIconView nextSound;
    
    @FXML
    private FontAwesomeIconView backSound;
    
    private void verifyIsEmptyListMusic() {
        if (listMusic.getItems().isEmpty()) {
            MusicItem newMusicItemEmpty = new MusicItem("No se han encontrado canciones", false);
            listMusic.getItems().add(newMusicItemEmpty);
        } else {
            listMusic.getItems().removeIf(item -> item.getNameMusic().equals("No se han encontrado canciones"));
        }
    }
    
    private boolean isValidEmail(String email) {
    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    Pattern pattern = Pattern.compile(emailRegex);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());

            byte[] byteData = md.digest();

            // Convertir bytes a representación hexadecimal
            StringBuilder hashedPassword = new StringBuilder();
            for (byte byteDatum : byteData) {
                hashedPassword.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
            }

            return hashedPassword.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // Manejar adecuadamente en tu aplicación
        }
    }
    
    private boolean checkPassword(String inputPassword, String storedHash) {
        String hashedInputPassword = hashPassword(inputPassword);
        return hashedInputPassword.equals(storedHash);
    }
    
    public void setUserAndInitialize(String nameUser, int userID) {
        this.nameUser = nameUser;
        this.userID = userID;

        if (labelNameUser != null) {
            labelNameUser.setText(nameUser);
            createRoutes(nameUser);
        }

    }
    
    private MediaPlayer mediaPlayerController;
    
    private void playMp3(String filePath) {
        Media hit = new Media(new File(filePath).toURI().toString());

        if (mediaPlayerController != null) {
            mediaPlayerController.stop();
            mediaPlayerController.dispose();
        }

        mediaPlayerController = new MediaPlayer(hit);

        mediaPlayerController.setOnReady(() -> {
            Duration duration = hit.getDuration();
            int durationInSeconds = (int) duration.toSeconds();

            mediaPlayerController.play();

            System.out.println("Duración del archivo MP3 en segundos: " + durationInSeconds);

            mediaPlayerController.setOnEndOfMedia(() -> {
                mediaPlayerController.stop();
            });
        });

        mediaPlayerController.setOnError(() -> {
            System.out.println("Error al reproducir el archivo MP3");
        });
    }
    
    private void saveRoutesDB(String path) {
        ConexionBD connectNew = new ConexionBD();
        Connection connectDB = connectNew.getConnection();
        
        if (numMusic > 0) {
            for (int i = 1; i < numMusic; i++) {
                String createColumnRoutes = "ALTER TABLE sonidos ADD COLUMN ruta_" + i + " TEXT";

                try (PreparedStatement statement = connectDB.prepareStatement(createColumnRoutes)) {
                    statement.executeUpdate();
                    String url = listMusic.getItems().get(i-1).getPathURL();
                    
                    String saveRouteItem = "UPDATE sonidos SET ruta_" + i + " = ? WHERE id = ?";
                    
                    try(PreparedStatement saveRoutePst = connectDB.prepareStatement(saveRouteItem)){
                        saveRoutePst.setString(1, url);
                        saveRoutePst.setInt(2, userID);
                        
                        int rowsAffected = saveRoutePst.executeUpdate();
                        
                        if(rowsAffected > 0){
                            System.out.println("Ruta " + i + " agregada con exito");
                        }else{
                            System.out.println("Hubo un error al agregar la ruta " + i);
                        }
                        
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createRoutes(String username) {
        ConexionBD connectNew = new ConexionBD();
        Connection connectDB = connectNew.getConnection();
        
        String getID = "SELECT id FROM registros WHERE username = ?";
        int userIDRoutes;
        
        System.out.println(username);

        try (PreparedStatement getIDPst = connectDB.prepareStatement(getID)) {
            getIDPst.setString(1, username);
            
            ResultSet resultSet = getIDPst.executeQuery();
            
            if (resultSet.next()) {
                userIDRoutes = resultSet.getInt("id");
                
                String existRoutes = "SELECT * FROM sonidos WHERE id = ?";

                try (PreparedStatement existRoutesPst = connectDB.prepareStatement(existRoutes)) {
                    existRoutesPst.setInt(1, userIDRoutes);

                    ResultSet resultSetRoutes = existRoutesPst.executeQuery();

                    ResultSetMetaData metaData = resultSetRoutes.getMetaData();

                    int numColumns = metaData.getColumnCount();

                    while (resultSetRoutes.next()) {
                        for (int i = 2; i <= numColumns; i++) {
                            String columnName = metaData.getColumnName(i);
                            String columnValue = resultSetRoutes.getString(i);

                            String musicPath = columnValue;

                            // Obtener el nombre real del archivo desde la ruta
                            String musicFileName = new File(musicPath).getName();

                            String musicURL = new File(musicPath).toURI().toString();

                            System.out.println(musicFileName + " " + musicPath + " " + musicURL);

                            MusicItem newMusicItem = new MusicItem(musicPath, musicFileName, musicURL, true, nameMusicReproductor, durationSound, timeSound, sliderVolume, playAndPauseSound, forwardSound, backwardSound, nextSound, backSound, iconVolume, listMusic);
                            listMusic.getItems().add(newMusicItem);
                            listMusic.getItems().removeIf(item -> item.getNameMusic().equals("No se han encontrado canciones"));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            } else {
                System.out.println("Usuario no encontrado en la base de datos.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
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
    private void changeScreenToReproductor(ActionEvent event) {
        try {
            Stage currentStage = (Stage) btnLoginOfLogin.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("reproductor.fxml"));
            Parent root = loader.load();

            // Obtén el controlador del nuevo FXML
            Controller reproductorController = loader.getController();
            
            // Establece el nombre de usuario y luego inicializa la interfaz de usuario
            reproductorController.setUserAndInitialize(nameUser, userID);

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.DECORATED.UNDECORATED);

            root.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });

            root.setOnMouseDragged(new EventHandler<MouseEvent>() {
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
    private void addMusic(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo MP3");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos MP3", "*.mp3"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            String musicName = selectedFile.getName();
            String musicPath = selectedFile.getAbsolutePath();
            String musicURL = selectedFile.toURI().toString();
            
            System.out.println(musicName + " " + musicPath + " " + musicURL);

            ObservableList<MusicItem> itemsToAdd = FXCollections.observableArrayList();

            if (!listMusic.getItems().isEmpty() && listMusic.getItems().get(0).getNameMusic().equals("No se han encontrado canciones")) {
                listMusic.getItems().clear();
            }

            boolean musicAlreadyExists = listMusic.getItems().stream()
                    .anyMatch(item -> item.getMusicURL().equals(musicURL));

            if (!musicAlreadyExists) {
                MusicItem newMusicItem = new MusicItem(musicPath, musicName, musicURL, true, nameMusicReproductor, durationSound, timeSound, sliderVolume, playAndPauseSound, forwardSound, backwardSound, nextSound, backSound, iconVolume, listMusic);
                listMusic.getItems().add(newMusicItem);

                String projectDirectory = System.getProperty("user.dir") + File.separator + "src"  + File.separator + "Sounds" + File.separator + "sound2.mp3";
                String originDirectory = musicPath;

                System.out.println(projectDirectory);
                System.out.println(originDirectory);

                try (InputStream inputStream = new FileInputStream(originDirectory);
                     OutputStream outputStream = new FileOutputStream(projectDirectory)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                numMusic = listMusic.getItems().size() + 1;
                saveRoutesDB(projectDirectory);
            }
        }
    }

    @FXML
    private TextField inputUserOfRegister;
    
    @FXML
    private TextField inputEmailOfRegister;
    
    @FXML
    private PasswordField inputPasswordOfRegister;
    
    @FXML
    public TextField inputUserOfLogin;
    
    @FXML
    private PasswordField inputPasswordOfLogin;
    
    @FXML 
    private Label spawnAlert;
    
    @FXML 
    private Label spawnAlertLogin;
    
    @FXML
    private Pane spawnProfile;
    
    @FXML
    private Button btnLogout;
    
    
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
   
    @FXML
    private void eventRegister() throws SQLException {
        String user = inputUserOfRegister.getText();
        String email = inputEmailOfRegister.getText();
        String pass = inputPasswordOfRegister.getText();

        if (!user.isEmpty() && !email.isEmpty() && !pass.isEmpty()) {

            if (user.length() < 16) {

                if (pass.length() >= 8) {

                    if (isValidEmail(email)) {

                        ConexionBD connectNew = new ConexionBD();
                        Connection connectDB = connectNew.getConnection();

                        String validAccQuery = "SELECT * FROM registros WHERE username = ? OR email = ?";

                        try (PreparedStatement validAccPst = connectDB.prepareStatement(validAccQuery)) {
                            validAccPst.setString(1, user);
                            validAccPst.setString(2, email);

                            ResultSet resultSet = validAccPst.executeQuery();

                            if (resultSet.next()) {
                                spawnAlert.setVisible(true);
                                spawnAlert.setText("El usuario o el correo ya están registrados");

                                PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
                                visiblePause.setOnFinished(event -> spawnAlert.setVisible(false));
                                visiblePause.play();
                            } else {
                                int id = (int) (Math.random() * 10000);

                                String connectQuery = "INSERT INTO registros (id, username, email, password, state) VALUES (?,?,?,?,?)";

                                try (PreparedStatement pst = connectDB.prepareStatement(connectQuery)) {
                                    pst.setInt(1, id);
                                    pst.setString(2, user);
                                    pst.setString(3, email);

                                    String hashedPassword = hashPassword(pass);
                                    pst.setString(4, hashedPassword);
                                    pst.setBoolean(5, false);
                                    
                                    int rowsAffected = pst.executeUpdate();

                                    if (rowsAffected > 0) {
                                        
                                        String saveRoutesQuery = "INSERT INTO sonidos (id, ruta_1) VALUES (?,?)";

                                        try(PreparedStatement saveRoutePst = connectDB.prepareStatement(saveRoutesQuery)){

                                            saveRoutePst.setInt(1, id);
                                            saveRoutePst.setString(2, "");

                                            int rowsAffectedRoutes = saveRoutePst.executeUpdate();

                                            if(rowsAffectedRoutes > 0){
                                                System.out.println("Ruta guardada con exito");
                                                System.out.println("Registro exitoso");
                                                inputUserOfRegister.setText("");
                                                inputEmailOfRegister.setText("");
                                                inputPasswordOfRegister.setText("");
                                                changeScreenToLogin(null);
                                            }

                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                        
                                    } else {
                                        System.out.println("Hubo un error en el registro");
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    } else {
                        spawnAlert.setVisible(true);
                        spawnAlert.setText("Ingrese un correo electrónico válido");

                        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
                        visiblePause.setOnFinished(event -> spawnAlert.setVisible(false));
                        visiblePause.play();
                    }

                } else {
                    spawnAlert.setVisible(true);
                    spawnAlert.setText("La contraseña debe tener un mínimo de 8 caracteres");

                    PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
                    visiblePause.setOnFinished(event -> spawnAlert.setVisible(false));
                    visiblePause.play();
                }

            } else {
                spawnAlert.setVisible(true);
                spawnAlert.setText("El nombre de usuario debe tener un máximo de 16 caracteres");

                PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
                visiblePause.setOnFinished(event -> spawnAlert.setVisible(false));
                visiblePause.play();
            }

        } else {
            spawnAlert.setVisible(true);
            spawnAlert.setText("Todos los campos son obligatorios");

            PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
            visiblePause.setOnFinished(event -> spawnAlert.setVisible(false));
            visiblePause.play();
        }
    }
    
    @FXML
    private void eventLogin() throws SQLException {
        String user = inputUserOfLogin.getText();
        String pass = inputPasswordOfLogin.getText();
    
        if (!user.isEmpty() && !pass.isEmpty()) {
            ConexionBD connectNew = new ConexionBD();
            Connection connectDB = connectNew.getConnection();
    
            String loginQuery = "SELECT * FROM registros WHERE username = ?";
            
            try (PreparedStatement loginPst = connectDB.prepareStatement(loginQuery)) {
                loginPst.setString(1, user);
    
                ResultSet resultSet = loginPst.executeQuery();
    
                if (resultSet.next()) {
                    String storedHash = resultSet.getString("password");
    
                    if (checkPassword(pass, storedHash)) {
                        System.out.println("Inicio de sesión exitoso");
                        inputUserOfLogin.setText("");
                        inputPasswordOfLogin.setText("");
                        
                        String storedNameUser = resultSet.getString("username");
                        int storedID = resultSet.getInt("id");
                        nameUser = storedNameUser;
                        userID = storedID;
                        
                        String changeState = "UPDATE registros SET state = ? WHERE username = ?";

                        try (PreparedStatement statePst = connectDB.prepareStatement(changeState)) {
                            statePst.setBoolean(1, true);
                            statePst.setString(2, nameUser);

                            int rowsUpdated = statePst.executeUpdate();

                            if (rowsUpdated > 0) {
                                System.out.println("Se actualizaron los cambios correctamente");
                            } else {
                                System.out.println("No se realizaron cambios");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                                                
                        changeScreenToReproductor(null);
                    } else {
                        spawnAlertLogin.setVisible(true);
                        spawnAlertLogin.setText("Nombre de usuario o contraseña incorrectos");
    
                        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
                        visiblePause.setOnFinished(event -> spawnAlertLogin.setVisible(false));
                        visiblePause.play();
                    }
                } else {
                    spawnAlertLogin.setVisible(true);
                    spawnAlertLogin.setText("Nombre de usuario o contraseña incorrectos");
    
                    PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
                    visiblePause.setOnFinished(event -> spawnAlertLogin.setVisible(false));
                    visiblePause.play();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            spawnAlertLogin.setVisible(true);
            spawnAlertLogin.setText("Todos los campos son obligatorios");
    
            PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
            visiblePause.setOnFinished(event -> spawnAlertLogin.setVisible(false));
            visiblePause.play();
        }
    }
    
    @FXML
    private void eventLogout(){
        ConexionBD connectNew = new ConexionBD();
        Connection connectDB = connectNew.getConnection();
        
        String changeState = "UPDATE registros SET state = ? WHERE username = ?";

        try (PreparedStatement statePst = connectDB.prepareStatement(changeState)) {
            statePst.setBoolean(1, false);
            statePst.setString(2, nameUser);

            int rowsUpdated = statePst.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Se actualizaron los cambios correctamente");
                try {
                    Stage currentStage = (Stage) btnLogout.getScene().getWindow();

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
            } else {
                System.out.println("No se realizaron cambios");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private String formatDuration(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    
    @FXML
    private TextField inputSearchMusic;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            if(listMusic != null){
                
                verifyIsEmptyListMusic();
                
                
                if(playAndPauseSound != null){
                    playAndPauseSound.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            try {
                                if (listMusic.getItems().filtered(item -> item.getNameMusic().equals("No se han encontrado canciones")) != null) {
                                    for (MusicItem item : listMusic.getItems()) {
                                        MediaPlayer mediaPlayer = item.getMediaPlayer();
                                        FontAwesomeIconView playIcon = item.getPlayIcon();
                                        
                                        
                                        if (item.isLoaded()) {
                                            if (mediaPlayer != null && "STOP_CIRCLE".equals(playIcon.getGlyphName()) && "PAUSE".equals(playAndPauseSound.getGlyphName())) {
                                                mediaPlayer.pause();
                                                item.setLoadedState(false);

                                                playIcon.setGlyphName("PLAY_CIRCLE");
                                                playAndPauseSound.setGlyphName("PLAY");
                                            }
                                        } else if (!item.isLoaded() && item.getNameMusic().equals(nameMusicReproductor.getText())) {
                                            if (mediaPlayer != null && "PLAY_CIRCLE".equals(playIcon.getGlyphName()) && "PLAY".equals(playAndPauseSound.getGlyphName())) {
                                                sliderVolume.setValue(mediaPlayer.getVolume());

                                                timeSound.setText("0:00/" + formatDuration(mediaPlayer.getTotalDuration()));

                                                double valueSlider = durationSound.getValue();

                                                mediaPlayer.seek(Duration.seconds(valueSlider));
                                                mediaPlayer.play();
                                                item.setLoadedState(true);

                                                nameMusicReproductor.setText(item.getNameMusic());
                                                playIcon.setGlyphName("STOP_CIRCLE");
                                                playAndPauseSound.setGlyphName("PAUSE");
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            
                if(nextSound != null){
                    nextSound.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if (listMusic.getItems().filtered(item -> item.getNameMusic().equals("No se han encontrado canciones")) != null) {
                                for (MusicItem item : listMusic.getItems()) {
                                    if (item.isLoaded()) {
                                        int currentPosition = listMusic.getItems().indexOf(item);
                                        int nextPosition = currentPosition + 1;
                                        if (nextPosition < listMusic.getItems().size()) {
                                            durationSound.setValue(0);
                                            item.pauseSound();
                                            MusicItem nextItem = listMusic.getItems().get(nextPosition);
                                            nextItem.startSound(nextItem.getNameMusic(), nextItem.getMusicURL());
                                        
                                            MediaPlayer mediaPlayer = nextItem.getMediaPlayer();
                                            FontAwesomeIconView playIcon = nextItem.getPlayIcon();
                                        
                                            mediaPlayer.setOnEndOfMedia(() -> {
                                                mediaPlayer.pause();
                                                item.setLoadedState(false);
                                                durationSound.setValue(0);
                                                
                                                playIcon.setGlyphName("PLAY_CIRCLE");
                                                playAndPauseSound.setGlyphName("PLAY");
                                            });
                                        } else {
                                            System.out.println("No hay siguiente elemento en la lista.");
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
                
                if(backSound != null) {
                    backSound.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if (listMusic.getItems().filtered(item -> item.getNameMusic().equals("No se han encontrado canciones")) != null) {
                                for (MusicItem item : listMusic.getItems()) {
                                    if (item.isLoaded()) {
                                        int currentPosition = listMusic.getItems().indexOf(item);
                                        int nextPosition = currentPosition - 1;
                                        if (nextPosition >= 0 && nextPosition < listMusic.getItems().size()) {
                                            durationSound.setValue(0);
                                            item.pauseSound();
                                            MusicItem nextItem = listMusic.getItems().get(nextPosition);
                                            nextItem.startSound(nextItem.getNameMusic(), nextItem.getMusicURL());
                                            
                                            MediaPlayer mediaPlayer = nextItem.getMediaPlayer();
                                            FontAwesomeIconView playIcon = nextItem.getPlayIcon();
                                            
                                            mediaPlayer.setOnEndOfMedia(() -> {
                                                mediaPlayer.pause();
                                                item.setLoadedState(false);
                                                durationSound.setValue(0);

                                                playIcon.setGlyphName("PLAY_CIRCLE");
                                                playAndPauseSound.setGlyphName("PLAY");
                                            });
                                        } else {
                                            System.out.println("No hay siguiente elemento en la lista.");
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
                
                if(forwardSound != null){
                    forwardSound.setOnMouseClicked(new EventHandler<MouseEvent>(){
                        @Override
                        public void handle(MouseEvent event){
                            if (listMusic.getItems().filtered(item -> item.getNameMusic().equals("No se han encontrado canciones")) != null) {
                                for (MusicItem item : listMusic.getItems()) {
                                    if (item.isLoaded()) {
                                        MediaPlayer mediaPlayer = item.getMediaPlayer();
                                        FontAwesomeIconView playIcon = item.getPlayIcon();
                                        double currentSeconds = mediaPlayer.getCurrentTime().toSeconds();
                                        double newTime = currentSeconds + 10.0;
                                        mediaPlayer.seek(Duration.seconds(newTime));
                                    }
                                }
                            }
                        }
                    });
                }

                if(backwardSound != null){
                    backwardSound.setOnMouseClicked(new EventHandler<MouseEvent>(){
                        @Override
                        public void handle(MouseEvent event){
                            if (listMusic.getItems().filtered(item -> item.getNameMusic().equals("No se han encontrado canciones")) != null) {
                                for (MusicItem item : listMusic.getItems()) {
                                    if (item.isLoaded()) {
                                        MediaPlayer mediaPlayer = item.getMediaPlayer();
                                        FontAwesomeIconView playIcon = item.getPlayIcon();
                                        double currentSeconds = mediaPlayer.getCurrentTime().toSeconds();
                                        double newTime = currentSeconds - 10.0;
                                        mediaPlayer.seek(Duration.seconds(newTime));
                                    }
                                }
                            }
                        }
                    });
                }
                
                if(sliderVolume != null){
                    sliderVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
                        System.out.println(listMusic.getSelectionModel().getSelectedItem().getMediaPlayer());
                        if((double) newValue <= 0.0){
                            iconVolume.setGlyphName("VOLUME_OFF");
                        }else if((double) newValue <= 0.5){
                            iconVolume.setGlyphName("VOLUME_DOWN");
                        }else if((double) newValue >= 1.0){
                            iconVolume.setGlyphName("VOLUME_UP");
                        }
                    });
                }
                
                if(inputSearchMusic != null){
                    inputSearchMusic.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            System.out.println("Texto cambiado a: " + newValue);
                        }
                    });
                }
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
    
}
