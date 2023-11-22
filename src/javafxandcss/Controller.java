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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.shape.Rectangle;
import javax.crypto.KeyGenerator;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 *
 * @author Sebxs
 */
public class Controller implements Initializable {
    
    private double xOffset;
    private double yOffset;  
    
    private String nameUser;
    private int userID;
    public int codeID;
    
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
    
    @FXML
    private Button forgotPassword;
    
    @FXML
    private Button btnExitSendCode;
    
    @FXML
    private Button btnExitSendCodeEmail;
    
    @FXML
    private Button btnExitSendPassword;
    
    @FXML
    private TextField inputEmailOfSendCode;
    
    @FXML
    private TextField inputCodeOfSendCode;
    
    @FXML
    private PasswordField inputPasswordOfForgot;
    
    @FXML
    private PasswordField inputPasswordOfForgot2;
    
    @FXML
    private Pane containerSendCode;
    
    @FXML
    private Pane containerSendCodeEmail;
    
    @FXML
    private Pane containerSendPassword;
    
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
    
    private Task<Void> createSendEmailTask(String email, int code) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                sendEmail(email, code);
                return null;
            }
        };
    }
    
    private void sendEmail(String email, int code){
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Cambia esto con tu servidor SMTP
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Autenticación
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("sebastiangarces152@gmail.com", "ocxogzwntaaacecg");
            }
        };

        // Creación de la sesión
        Session session = Session.getInstance(props, auth);

        try {
            // Creación del mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("sebastiangarces152@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Codigo de verificacion");
            message.setText("Tu codigo de verificacion es " + code);

            // Envío del mensaje
            Transport.send(message);

            System.out.println("Correo electrónico enviado con éxito.");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error al enviar el correo electrónico: " + e.getMessage());
        }
    }
    
    private void sendCode(String mail, int code){
        System.out.println("Enviando código...");
        
        Task<Void> sendEmailTask = createSendEmailTask(mail, code);

        sendEmailTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                System.out.println("Correo electrónico enviado con éxito.");
            }
        });

        sendEmailTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error al enviar el correo electrónico");
                alert.setContentText("Ocurrió un error al enviar el correo electrónico.");
                alert.showAndWait();
            }
        });

        Thread thread = new Thread(sendEmailTask);
        thread.start();
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
    
    private String formatIntAsDuration(int value) {
        int minutes = value / 60;
        int seconds = value % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private int parseDurationStringToInt(String durationString) {
        String[] parts = durationString.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return minutes * 60 + seconds;
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
    private void eventForgotPassword(ActionEvent event) {
        containerSendCode.setVisible(true);
    }

    @FXML
    private void eventExitSendCode(ActionEvent event){
        containerSendCode.setVisible(false);
        inputEmailOfSendCode.setText("");
    }
    
    @FXML
    private void eventExitSendCodeEmail(ActionEvent event){
        containerSendCodeEmail.setVisible(false);
        inputCodeOfSendCode.setText("");
    }
    
    @FXML
    private void eventExitSendPassword(ActionEvent event){
        containerSendPassword.setVisible(false);
        inputPasswordOfForgot.setText("");
        inputPasswordOfForgot2.setText("");
    }
    
    @FXML
    private void eventSendCode(ActionEvent event){
        if(isValidEmail(inputEmailOfSendCode.getText())){
            String email = inputEmailOfSendCode.getText();
            ConexionBD connectNew = new ConexionBD();
            Connection connectDB = connectNew.getConnection();
            
            String getUserData = "SELECT * FROM registros WHERE email = ?";
            
            try(PreparedStatement getUserDataPst = connectDB.prepareStatement(getUserData)){
                getUserDataPst.setString(1, email);
                
                ResultSet resultGetUserData = getUserDataPst.executeQuery();
                
                if(resultGetUserData.next()){
                    int storedID = resultGetUserData.getInt("id");
                    int randomCode = (int) (Math.random() * 900000) + 100000;
                    
                    String getUserCode = "SELECT * FROM codes WHERE id = ?";
                    
                    try(PreparedStatement getUserCodePst = connectDB.prepareStatement(getUserCode)){
                        getUserCodePst.setInt(1, storedID);
                        
                        ResultSet rlsGetUserCode = getUserCodePst.executeQuery();
                        
                        if(rlsGetUserCode.next()){
                            String setUserCode = "UPDATE codes SET code = ? WHERE id = ?";
                            
                            try(PreparedStatement setUserCodePst = connectDB.prepareStatement(setUserCode)){
                                setUserCodePst.setInt(1, randomCode);
                                setUserCodePst.setInt(2, storedID);
                                
                                int rowsAffected = setUserCodePst.executeUpdate();
                                
                                if(rowsAffected > 0){
                                    sendCode(email,randomCode);
                                    containerSendCode.setVisible(false);
                                    containerSendCodeEmail.setVisible(true);
                                }
                            }
                        }else{
                            String setUserCode = "INSERT INTO codes (id, code) VALUES (?,?)";

                            try(PreparedStatement setUserCodePst = connectDB.prepareStatement(setUserCode)){
                                setUserCodePst.setInt(1, storedID);
                                setUserCodePst.setInt(2, randomCode);

                                int rowsAffected = setUserCodePst.executeUpdate();

                                if(rowsAffected > 0){
                                    sendCode(email,randomCode);
                                    containerSendCode.setVisible(false);
                                    containerSendCodeEmail.setVisible(true);
                                    inputEmailOfSendCode.setText("");
                                }

                            }catch(Exception e){
                                e.printStackTrace();
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
            System.out.println("Correo Electronico Incorrecto.");
        }
        
        
        
    }
    
    @FXML
    private void eventSendCodeEmail(ActionEvent event){
        
        if(inputCodeOfSendCode.getText().matches("\\d+") && inputCodeOfSendCode.getText().length() == 6){
            ConexionBD connectNew = new ConexionBD();
            Connection connectDB = connectNew.getConnection();
            
            String getCodeUser = "SELECT * FROM codes WHERE code = ?";
            
            try(PreparedStatement getCodeUserPst = connectDB.prepareStatement(getCodeUser)){
                
                getCodeUserPst.setInt(1, Integer.parseInt(inputCodeOfSendCode.getText()));
                
                ResultSet resultGetCodeUser = getCodeUserPst.executeQuery();
                
                if(resultGetCodeUser.next()){
                    int codeID = resultGetCodeUser.getInt("id");
                    this.codeID = codeID;
                    containerSendCodeEmail.setVisible(false);
                    containerSendPassword.setVisible(true);
                    inputCodeOfSendCode.setText("");
                }else{
                    System.out.println("Codigo Incorrecto.");
                }
                
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            System.out.println("Codigo Incorrecto");
        }
        
        
        
    }
    
    @FXML
    private void eventSendPassword(ActionEvent event){
        String password_1 = inputPasswordOfForgot.getText();
        String password_2 = inputPasswordOfForgot2.getText();
        
        if(password_1.equals(password_2)){
            
            if(!password_1.isEmpty() || !password_2.isEmpty()){
             
                if(password_1.length() >= 8){
                
                    ConexionBD connectNew = new ConexionBD();
                    Connection connectDB = connectNew.getConnection();
                    
                    String getPasswordUser = "SELECT * FROM registros WHERE id = ?";
                    
                    try(PreparedStatement getPasswordUserPst = connectDB.prepareStatement(getPasswordUser)){
                        
                        getPasswordUserPst.setInt(1, this.codeID);
                        
                        ResultSet resultGetPasswordUser = getPasswordUserPst.executeQuery();
                        
                        if(resultGetPasswordUser.next()){
                            String oldPassword = resultGetPasswordUser.getString("password");
                            
                            if(!oldPassword.equals(password_1)){
                                
                                String setNewPassword = "UPDATE registros SET password = ? WHERE id = ?";

                                try(PreparedStatement setNewPasswordPst = connectDB.prepareStatement(setNewPassword)){

                                    password_1 = hashPassword(password_1);
                                    
                                    setNewPasswordPst.setString(1, password_1);
                                    setNewPasswordPst.setInt(2, this.codeID);

                                    int rowsAffected = setNewPasswordPst.executeUpdate();

                                    if(rowsAffected > 0){
                                        System.out.println("Contraseña cambiada con exito");
                                        inputPasswordOfForgot.setText("");
                                        inputPasswordOfForgot2.setText("");
                                        containerSendPassword.setVisible(false);
                                    }

                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                
                            }else{
                                System.out.println("Su contraseña nueva no puede ser igual a la antigua");
                            }
                            
                        }else{
                            System.out.println("No se encontro el id");
                        }
                        
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                
                }else{
                    System.out.println("La contraseña debe tener un mínimo de 8 caracteres");
                }
                
            }else{
                System.out.println("Ambos campos son obligatorios");
            }
            
        }else{
            System.out.println("Ambas contraseñas deben ser iguales");
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
private void addMusic(ActionEvent event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Seleccionar archivos MP3");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos MP3", "*.mp3"));

    // Habilitar la selección múltiple
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos MP3", "*.mp3"));
    List<File> selectedFiles = fileChooser.showOpenMultipleDialog(new Stage());

    if (selectedFiles != null && !selectedFiles.isEmpty()) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
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
                        MusicItem newMusicItem = new MusicItem(
                                containerRectangles, musicPath, musicName, musicURL, true, nameMusicReproductor,
                                durationSound, timeSound, sliderVolume, playAndPauseSound, forwardSound,
                                backwardSound, nextSound, backSound, iconVolume, listMusic);

                        listMusic.getItems().add(newMusicItem);
                        numMusic = listMusic.getItems().size() + 1;
                        saveRoutesDB(musicPath);
                    }
                }

                // Realizar actualizaciones de la interfaz de usuario después de completar el bucle
                Platform.runLater(() -> {
                    listMusic.getItems().addAll(itemsToAdd);
                    verifyIsFirstMusic();
                });

                return null;
            }
        };

        // Configurar manejo de excepciones, si es necesario
        task.setOnFailed(e -> task.getException().printStackTrace());

        // Iniciar la tarea en segundo plano
        Thread thread = new Thread(task);
        thread.setDaemon(true); // Hacer que el hilo sea daemon para que se cierre cuando la aplicación principal se cierre
        thread.start();
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
                    
            if (inputCodeOfSendCode != null && containerSendCodeEmail != null) {
                inputCodeOfSendCode.textProperty().addListener((observable, oldValue, newValue) -> {
                    Platform.runLater(() -> {
                        if (!newValue.matches("\\d*") || newValue.length() > 6) {
                            inputCodeOfSendCode.setText(oldValue);
                        }
                    });
                });
            } else {
                System.out.println("El objeto inputCodeOfSendCode es nulo.");
            }
        
            if(listMusic != null){
                
                verifyIsEmptyListMusic();
                
                if(playAndPauseSound != null){
                    playAndPauseSound.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            try {
                                
                                if(isFirstMusic && nameMusicReproductor.getText().equals(listMusic.getItems().get(0).getNameMusic())){
                                    
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
                                            item.startSound(item.getNameMusic(), item.getMusicURL(), listMusic);
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
                                            nextItem.startSound(nextItem.getNameMusic(), nextItem.getMusicURL(), listMusic);
                                        
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
                                            nextItem.startSound(nextItem.getNameMusic(), nextItem.getMusicURL(), listMusic);
                                        
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
                                            nextItem.startSound(nextItem.getNameMusic(), nextItem.getMusicURL(), listMusic);
                                            
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
                                            nextItem.startSound(nextItem.getNameMusic(), nextItem.getMusicURL(), listMusic);
                                            
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
                                    }else if(!item.isLoaded() && item.getNameMusic().equals(nameMusicReproductor.getText())){
                                        String[] partsTimeSound = timeSound.getText().split("/");
                                        String totalDurationSound = partsTimeSound[1].trim();
                                        int totalDurationSoundINT = parseDurationStringToInt(totalDurationSound);
                                        durationSound.setMax((double) totalDurationSoundINT);
                                        durationSound.setValue(durationSound.getValue() + 10.0);
                                        timeSound.setText(formatIntAsDuration((int) durationSound.getValue() ) + "/" + totalDurationSound);
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
                                    }else if(!item.isLoaded() && item.getNameMusic().equals(nameMusicReproductor.getText())){
                                        String[] partsTimeSound = timeSound.getText().split("/");
                                        String totalDurationSound = partsTimeSound[1].trim();
                                        int totalDurationSoundINT = parseDurationStringToInt(totalDurationSound);
                                        durationSound.setMax((double) totalDurationSoundINT);
                                        durationSound.setValue(durationSound.getValue() - 10.0);
                                        timeSound.setText(formatIntAsDuration((int) durationSound.getValue() ) + "/" + totalDurationSound);
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
