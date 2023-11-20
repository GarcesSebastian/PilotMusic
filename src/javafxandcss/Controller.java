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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Base64;
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
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.shape.Rectangle;
import javax.crypto.KeyGenerator;
/**
 *
 * @author Sebxs
 */
public class Controller implements Initializable {
    
    private double xOffset;
    private double yOffset;  
    
    private String nameUser;
    private int userID;
    
    private int numMusic = 1;
    
    private Boolean isFirstMusic = true;
    
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
    
    @FXML
    private Pane containerRectangles;
    
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
        
    @FXML
    private TextField inputSearchMusic;
    
    private static final String ALGORITHM = "AES";
    private static final String CHARSET = "UTF-8";
    private String secretKey;
    
    public static String generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);

        SecretKey secretKey = keyGenerator.generateKey();

        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
    
    public void getKeySecurity(){
        ConexionBD connectNew = new ConexionBD();
        Connection connectDB = connectNew.getConnection();
        
        String getKeyQuery = "SELECT * FROM keysecurity WHERE id = ? ";
        
        try(PreparedStatement getKeyPst = connectDB.prepareStatement(getKeyQuery)){
            
            getKeyPst.setInt(1, 110012);
            
            ResultSet resultGetKey = getKeyPst.executeQuery();
            
            if(resultGetKey.next()){
                this.secretKey = resultGetKey.getString("key");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String data, String secretKey) throws Exception {
        SecretKey key = new SecretKeySpec(secretKey.getBytes(CHARSET), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData, String secretKey) throws Exception {
        SecretKey key = new SecretKeySpec(secretKey.getBytes(CHARSET), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }
    
    private void verifyIsEmptyListMusic() {
        if (listMusic.getItems().isEmpty()) {
            MusicItem newMusicItemEmpty = new MusicItem("No se han encontrado canciones", false);
            listMusic.getItems().add(newMusicItemEmpty);
        } else {
            listMusic.getItems().removeIf(item -> item.getNameMusic().equals("No se han encontrado canciones"));
        }
    }
    
    private void verifyIsFirstMusic(){
        if(listMusic.getItems().size() > 0 && !listMusic.getItems().get(0).getMusicURL().equals("No se han encontrado canciones")){
            nameMusicReproductor.setText(listMusic.getItems().get(0).getNameMusic());
            MusicItem item = listMusic.getItems().get(0);

            Media sound = new Media(item.getMusicURL());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            FontAwesomeIconView playIcon = item.getPlayIcon();


            if(mediaPlayer != null){
                mediaPlayer.setOnReady(() -> {
                   timeSound.setText("0:00/" + formatDuration(mediaPlayer.getTotalDuration()));
                });

            }else{
                System.out.println("MediaPlayer es Nulo");
            }

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
        
    private String formatDuration(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    private MediaPlayer mediaPlayerController;
    
    private void saveRoutesDB(String path) {
        ConexionBD connectNew = new ConexionBD();
        Connection connectDB = connectNew.getConnection();

        if (numMusic > 0) {
            for (int i = 1; i < numMusic; i++) {
                try {
                    DatabaseMetaData metaData = connectDB.getMetaData();
                    ResultSet columns = metaData.getColumns(null, null, "sonidos", "ruta_" + i);

                    if (!columns.next()) {
                        // La columna no existe, entonces la creamos
                        String createColumnRoutes = "ALTER TABLE sonidos ADD COLUMN ruta_" + i + " TEXT";
                        try (PreparedStatement statement = connectDB.prepareStatement(createColumnRoutes)) {
                            statement.executeUpdate();
                        }
                    }

                    // Actualizamos el valor de la columna
                    String url = listMusic.getItems().get(i - 1).getPathURL();
                    String saveRouteItem = "UPDATE sonidos SET ruta_" + i + " = ? WHERE id = ?";

                    try (PreparedStatement saveRoutePst = connectDB.prepareStatement(saveRouteItem)) {
                        saveRoutePst.setString(1, encrypt(url, this.secretKey));
                        saveRoutePst.setInt(2, userID);

                        int rowsAffected = saveRoutePst.executeUpdate();

                        if (!(rowsAffected > 0)) {
                            System.out.println("Hubo un error al agregar la ruta " + i);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Eliminamos la columna si el valor es null
                    String deleteNullRoutes = "UPDATE sonidos SET ruta_" + i + " = NULL WHERE ruta_" + i + " IS NULL";
                    try (PreparedStatement deleteNullRoutesPst = connectDB.prepareStatement(deleteNullRoutes)) {
                        deleteNullRoutesPst.executeUpdate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void createRoutes(String username) {
        ConexionBD connectNew = new ConexionBD();
        Connection connectDB = connectNew.getConnection();

        String getID = "SELECT id FROM registros WHERE username = ?";
        int userIDRoutes;

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

                            if (columnValue != null) {
                                String musicPath = decrypt(columnValue,this.secretKey);

                                String musicFileName = new File(musicPath).getName();

                                String musicURL = new File(musicPath).toURI().toString();

                                MusicItem newMusicItem = new MusicItem(containerRectangles, musicPath, musicFileName, musicURL, true, nameMusicReproductor, durationSound, timeSound, sliderVolume, playAndPauseSound, forwardSound, backwardSound, nextSound, backSound, iconVolume, listMusic);
                                listMusic.getItems().add(newMusicItem);
                                listMusic.getItems().removeIf(item -> item.getNameMusic().equals("No se han encontrado canciones"));
                            }
                        }
                    }
                    
                    verifyIsFirstMusic();
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
        fileChooser.setTitle("Seleccionar archivos MP3");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos MP3", "*.mp3"));

        // Habilitar la selección múltiple
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos MP3", "*.mp3"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(new Stage());

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            ObservableList<MusicItem> itemsToAdd = FXCollections.observableArrayList();

            if (!listMusic.getItems().isEmpty() && listMusic.getItems().get(0).getNameMusic().equals("No se han encontrado canciones")) {
                listMusic.getItems().clear();
            }

            for (File selectedFile : selectedFiles) {
                String musicName = selectedFile.getName();
                String musicPath = selectedFile.getAbsolutePath();
                String musicURL = selectedFile.toURI().toString();

                boolean musicAlreadyExists = listMusic.getItems().stream()
                        .anyMatch(item -> item.getMusicURL().equals(musicURL));

                if (!musicAlreadyExists) {
//                    String projectDirectory = System.getProperty("user.dir") + File.separator + "src" + File.separator + "Sounds" + File.separator + musicName;
//                    String originDirectory = musicPath;

                    MusicItem newMusicItem = new MusicItem(containerRectangles, musicPath, musicName, musicURL, true, nameMusicReproductor, durationSound, timeSound, sliderVolume, playAndPauseSound, forwardSound, backwardSound, nextSound, backSound, iconVolume, listMusic);
                    listMusic.getItems().add(newMusicItem);
                    verifyIsFirstMusic();

//                    try (InputStream inputStream = new FileInputStream(originDirectory);
//                         OutputStream outputStream = new FileOutputStream(projectDirectory)) {
//
//                        byte[] buffer = new byte[4096];
//                        int bytesRead;
//
//                        while ((bytesRead = inputStream.read(buffer)) != -1) {
//                            outputStream.write(buffer, 0, bytesRead);
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                    numMusic = listMusic.getItems().size() + 1;
                    saveRoutesDB(musicPath);
                }
            }
        }
    }

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
                                        
                                        String saveRoutesQuery = "INSERT INTO sonidos (id) VALUES (?)";

                                        try(PreparedStatement saveRoutePst = connectDB.prepareStatement(saveRoutesQuery)){

                                            saveRoutePst.setInt(1, id);

                                            int rowsAffectedRoutes = saveRoutePst.executeUpdate();

                                            if(rowsAffectedRoutes > 0){
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

                            if (!(rowsUpdated > 0)) {
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
        
        for(MusicItem item : listMusic.getItems()){
            if(item.isLoaded()){
                item.pauseSound();
            }
        }
        
        ConexionBD connectNew = new ConexionBD();
        Connection connectDB = connectNew.getConnection();
        
        String changeState = "UPDATE registros SET state = ? WHERE username = ?";

        try (PreparedStatement statePst = connectDB.prepareStatement(changeState)) {
            statePst.setBoolean(1, false);
            statePst.setString(2, nameUser);

            int rowsUpdated = statePst.executeUpdate();

            if (rowsUpdated > 0) {
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getKeySecurity();

        try {
            if(listMusic != null){
                
                verifyIsEmptyListMusic();
                
                if(playAndPauseSound != null){
                    playAndPauseSound.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            try {
                                
                                if(isFirstMusic && nameMusicReproductor.getText().equals(listMusic.getItems().get(0).getNameMusic())){
                                    System.out.println("HAAAAAAAAAAAA");
                                    MusicItem item = listMusic.getItems().get(0);

                                    FontAwesomeIconView playIcon = item.getPlayIcon();

                                    if (item.isLoaded()) {
                                        if ("STOP_CIRCLE".equals(playIcon.getGlyphName()) && "PAUSE".equals(playAndPauseSound.getGlyphName())) {
                                            item.pauseSound();
                                            item.setLoadedState(false);
                                            isFirstMusic = false;
                                            playIcon.setGlyphName("PLAY_CIRCLE");
                                            playAndPauseSound.setGlyphName("PLAY");
                                        }
                                    } else if (!item.isLoaded() && item.getNameMusic().equals(nameMusicReproductor.getText())) {
                                        if ("PLAY_CIRCLE".equals(playIcon.getGlyphName()) && "PLAY".equals(playAndPauseSound.getGlyphName())) {
                                            item.startSound(item.getNameMusic(), item.getMusicURL());
                                            isFirstMusic = false;
                                            item.setLoadedState(false);
                                            
                                            nameMusicReproductor.setText(item.getNameMusic());
                                            playIcon.setGlyphName("STOP_CIRCLE");
                                            playAndPauseSound.setGlyphName("PAUSE");
                                        }
                                    }
                                }else if (listMusic.getItems().filtered(item -> item.getNameMusic().equals("No se han encontrado canciones")) != null) {
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
                            isFirstMusic = false;
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
                                    }else if(item.getNameMusic().equals(nameMusicReproductor.getText())){
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
                            isFirstMusic = false;
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
                                    }else if(item.getNameMusic().equals(nameMusicReproductor.getText())){
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
                            try {
                                ConexionBD connectNew = new ConexionBD();
                                Connection connectDB = connectNew.getConnection();
                                Boolean isEquals = true;
                                
                                if(!newValue.trim().isEmpty()){
                                    String getDataUser = "SELECT * FROM registros WHERE username = ?";
                                
                                    try(PreparedStatement getDataUserPst = connectDB.prepareStatement(getDataUser)){
                                        getDataUserPst.setString(1, labelNameUser.getText());
                                        ResultSet resultGetDataUser = getDataUserPst.executeQuery();

                                        if(resultGetDataUser.next()){
                                            int storedID = resultGetDataUser.getInt("id");

                                            String getURLSounds = "SELECT * FROM sonidos WHERE id = ?";

                                            try(PreparedStatement getURLSoundsPst = connectDB.prepareStatement(getURLSounds)){
                                                
                                                getURLSoundsPst.setInt(1, storedID);
                                                
                                                ResultSet resultGetURLSounds = getURLSoundsPst.executeQuery();

                                                ResultSetMetaData metaData = getURLSoundsPst.getMetaData();

                                                int numColumns = metaData.getColumnCount();
                                                
                                                listMusic.getItems().clear();
                                                
                                                while(resultGetURLSounds.next()){
                                                    for(int i = 2; i <= numColumns; i++){
                                                        isEquals = true;
                                                        String columnName = metaData.getColumnName(i);
                                                        String columnValue = resultGetURLSounds.getString(i);
                                                        
                                                        String getSearchResult = "SELECT * FROM sonidos WHERE id = ?";
                                                        
                                                        try(PreparedStatement getSearchResultPst = connectDB.prepareStatement(getSearchResult)){
                                                            
                                                            getSearchResultPst.setInt(1, storedID);
                                                            
                                                            ResultSet resultGetSearch = getSearchResultPst.executeQuery();
                                                            
                                                            if(resultGetSearch.next()){
                                                                
                                                                String URLColumn = resultGetSearch.getString(i);
                                                                
                                                                URLColumn = decrypt(URLColumn, secretKey);
                                                                
                                                                File file = new File(URLColumn);
                                                                
                                                                char[] nameFileChar = file.getName().toLowerCase().trim().toCharArray();
                                                                char[] newValueChar = newValue.toLowerCase().trim().toCharArray();
                                                                
                                                                for(int j = 0; j < newValueChar.length; j++){
                                                                    if(nameFileChar[j] != newValueChar[j]){
                                                                        isEquals = false;
                                                                    }
                                                                }
                                                                
                                                                if(isEquals){
                                                                    String musicFileName = new File(URLColumn).getName();
                                                                    String musicURL = new File(URLColumn).toURI().toString();
                                                                    MusicItem newMusicItem = new MusicItem(containerRectangles, URLColumn, file.getName(), musicURL, true, nameMusicReproductor, durationSound, timeSound, sliderVolume, playAndPauseSound, forwardSound, backwardSound, nextSound, backSound, iconVolume, listMusic);
                                                                    listMusic.getItems().add(newMusicItem);
                                                                    listMusic.getItems().removeIf(item -> item.getNameMusic().equals("No se han encontrado canciones"));
                                                                }
                                                                
                                                            }else{
                                                                System.out.println("No se encontraron resultados");
                                                            }
                                                            
                                                            
                                                        }catch(Exception e){
                                                            e.printStackTrace();
                                                        }
                                                        
                                                    }
                                                }
                                             
                                            }catch(Exception e){
                                                e.printStackTrace();
                                            }
                                            
                                        }
                                        
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                    
                                }else{
                                    String getID = "SELECT id FROM registros WHERE username = ?";
                                    int userIDRoutes;

                                    try (PreparedStatement getIDPst = connectDB.prepareStatement(getID)) {
                                        getIDPst.setString(1, labelNameUser.getText());

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

                                                        if (columnValue != null) {
                                                            String musicPath = decrypt(columnValue, secretKey);

                                                            String musicFileName = new File(musicPath).getName();

                                                            // Verifica si ya existe un elemento con el mismo nombre
                                                            boolean alreadyExists = listMusic.getItems().stream()
                                                                    .anyMatch(item -> item.getNameMusic().equals(musicFileName));

                                                            if (!alreadyExists) {
                                                                String musicURL = new File(musicPath).toURI().toString();

                                                                MusicItem newMusicItem = new MusicItem(containerRectangles, musicPath, musicFileName, musicURL, true, nameMusicReproductor, durationSound, timeSound, sliderVolume, playAndPauseSound, forwardSound, backwardSound, nextSound, backSound, iconVolume, listMusic);
                                                                listMusic.getItems().add(newMusicItem);
                                                                listMusic.getItems().removeIf(item -> item.getNameMusic().equals("No se han encontrado canciones"));
                                                            }
                                                        }
                                                    }
                                                }

                                                verifyIsFirstMusic();
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
                                
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
    
}
