package Class;

import Database.ConexionBD;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Base64;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MusicItem extends AnchorPane {
        
    private Label nameLabel;
    private FontAwesomeIconView playIcon;
    private FontAwesomeIconView trashIcon;
    private String musicURL;
    private String musicPath;
    private Boolean stateLoaded = false;
    
    private MediaPlayer mediaPlayer;
    private Label nameMusicReproductor;
    private Slider durationSound;
    private Label timeSound;
    private Slider sliderVolume;
    
    private BorderPane containerAll;
    
    private FontAwesomeIconView playAndPauseSound;
    private FontAwesomeIconView forwardSound;
    private FontAwesomeIconView backwardSound;
    private FontAwesomeIconView nextSound;
    private FontAwesomeIconView backSound;
    private FontAwesomeIconView iconVolume;
    
    private Pane containerRectangles;
    private AudioEqualizer equalizer;
    
    private Node getLastParentContainer(Node node) {
        Node current = node;
        Node lastParent = null;

        while (current != null) {
            lastParent = current;
            current = current.getParent();
        }

        if (lastParent != null) {
            return lastParent;
        } else {
            return null;
        }
    }
    
    private static final String ALGORITHM = "AES";
    private static final String CHARSET = "UTF-8";
    private String secretKey;
    
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

    public MusicItem(Pane containerRectangles, String musicPath, String musicName, String musicURL, Boolean isSound, Label nameMusicReproductor, Slider durationSound, Label timeSound, Slider sliderVolume, FontAwesomeIconView playAndPauseSound, FontAwesomeIconView forwardSound, FontAwesomeIconView backwardSound, FontAwesomeIconView nextSound, FontAwesomeIconView backSound, FontAwesomeIconView iconVolume, ListView<MusicItem> listMusic) {
        if (isSound) {
            
            if (nameMusicReproductor != null) {
                this.nameMusicReproductor = nameMusicReproductor;
            }
            
            if(musicPath != null){
                this.musicPath = musicPath;
            }

            if(containerRectangles != null){
                this.containerRectangles = containerRectangles;
            }
            
            if (durationSound != null) {
                this.durationSound = durationSound;
            }
            
            if (sliderVolume != null) {
                this.sliderVolume = sliderVolume;
            }

            if (iconVolume != null) {
                this.iconVolume = iconVolume;
            }
            
            if (musicURL != null) {
                this.musicURL = musicURL;
            }

            if (timeSound != null) {
                this.timeSound = timeSound;
            }
            
            if(playAndPauseSound != null){
                this.playAndPauseSound = playAndPauseSound;
            }
            
            if(forwardSound != null){
                this.forwardSound = forwardSound;
            }
            
            if(backwardSound != null){
                this.backwardSound = backwardSound;
            }
            
            if(nextSound != null){
                this.nextSound = nextSound;
            }
            
            if(backSound != null){
                this.backSound = backSound;
            }

            nameLabel = new Label(musicName);
            playIcon = new FontAwesomeIconView();
            trashIcon = new FontAwesomeIconView();

            initializeUI();

            setCursor(Cursor.HAND);

            getChildren().addAll(nameLabel, playIcon, trashIcon);

            playIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if ((musicURL != null || !musicURL.isEmpty()) && "PLAY_CIRCLE".equals(playIcon.getGlyphName())) {
                        
                        if(listMusic.getItems().size() > 0){
                            for(MusicItem item : listMusic.getItems()){
                                if(item.isLoaded()){
                                    item.pauseSound();
                                }
                            }
                        }else{
                            System.out.println("No se encontraron items");
                        }
                        
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                        }

                        Media sound = new Media(musicURL);
                        mediaPlayer = new MediaPlayer(sound);
                        
                        mediaPlayer.setOnReady(() -> {
                            
                            mediaPlayer.setVolume((double) sliderVolume.getValue());
                            
                            timeSound.setText("0:00/" + formatDuration(mediaPlayer.getTotalDuration()));

                            double valueSlider = durationSound.getValue();
                            mediaPlayer.seek(Duration.seconds(valueSlider));
                            mediaPlayer.play();
                            stateLoaded = true;
                            durationSound.setMax(mediaPlayer.getTotalDuration().toSeconds());

                            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                                Platform.runLater(() -> {
                                    if (!durationSound.isValueChanging()) {
                                        durationSound.setValue(newValue.toSeconds());
                                        timeSound.setText(formatDuration(newValue) + "/" + formatDuration(mediaPlayer.getTotalDuration()));
                                        mediaPlayer.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> {
                                            double volume = mediaPlayer.getVolume();

                                            for (int i = 0; i < magnitudes.length && i < 10; i++) {
                                                double normalizedMagnitude = magnitudes[i] / 100.0;
                                                double volumeAdjustedMagnitude = normalizedMagnitude * volume;

                                                double minHeight = 0;
                                                double maxHeight = 360;
                                                double heightValue = (minHeight + volumeAdjustedMagnitude * (maxHeight - minHeight));

                                                Rectangle rectangle = (Rectangle) containerRectangles.lookup("#rectangle" + (i + 1) + "");
                                                rectangle.setHeight((heightValue + 216 * volume) + 24);
                                            }
                                        });
                                    }
                                });
                            });
                            
                            mediaPlayer.setOnEndOfMedia(() -> {
                                mediaPlayer.pause();
                                stateLoaded = false;
                                durationSound.setValue(0);
                                
                                timeSound.setText("0:00/" + formatDuration(mediaPlayer.getTotalDuration()));
                                playIcon.setGlyphName("PLAY_CIRCLE");
                                playAndPauseSound.setGlyphName("PLAY");
                            });
                            
                            sliderVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
                                mediaPlayer.setVolume((double) newValue);
                            });

                            nameMusicReproductor.setText(musicName);
                            playIcon.setGlyphName("STOP_CIRCLE");
                            playAndPauseSound.setGlyphName("PAUSE");
                        });
                    } else if ((musicURL != null && !musicURL.isEmpty()) && "STOP_CIRCLE".equals(playIcon.getGlyphName())) {
                        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                            mediaPlayer.pause();
                            stateLoaded = false;
                            
                            playIcon.setGlyphName("PLAY_CIRCLE");
                            playAndPauseSound.setGlyphName("PLAY");
                        }
                    }
                }
            });
            


            trashIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    getKeySecurity();
                    
                    if(MusicItem.this.isLoaded()){
                        MusicItem.this.pauseSound();
                        durationSound.setValue(0);
                    }
                            
                    listMusic.getItems().remove(MusicItem.this);
                    
                    if (listMusic.getItems().isEmpty()) {
                        MusicItem newMusicItemEmpty = new MusicItem("No se han encontrado canciones", false);
                        nameMusicReproductor.setText("No se han encontrado canciones");
                        durationSound.setValue(0);
                        listMusic.getItems().add(newMusicItemEmpty);
                    } else {
                        listMusic.getItems().removeIf(item -> item.getNameMusic().equals("No se han encontrado canciones"));
                        if(listMusic.getItems().size() > 0){
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
                    
                        ConexionBD connectNew = new ConexionBD();
                        Connection connectDB = connectNew.getConnection();
                        
                        String user;
                        
                        containerAll = (BorderPane) getLastParentContainer(sliderVolume);
                        
                        Label labelNameUser = (Label) containerAll.lookup("#labelNameUser");

                        if (labelNameUser != null) {
                            user = labelNameUser.getText();
                            
                            String getIdUser = "SELECT * FROM registros WHERE username = ?";
                            
                            try(PreparedStatement getIdUserPst = connectDB.prepareStatement(getIdUser)){
                                
                                getIdUserPst.setString(1, user);
                                
                                ResultSet resultIdUser = getIdUserPst.executeQuery();
                                
                                if(resultIdUser.next()){
                                    int storedID = resultIdUser.getInt("id");
                                    
                                    String getColumnRoutes = "SELECT * FROM sonidos WHERE id = ?";
                                    
                                    try(PreparedStatement getColumnRoutesPst = connectDB.prepareStatement(getColumnRoutes)){
                                        
                                        getColumnRoutesPst.setInt(1, storedID);
                                        
                                        ResultSet resultGetColumnRoutes = getColumnRoutesPst.executeQuery();
                                        
                                        ResultSetMetaData metaData = resultGetColumnRoutes.getMetaData();

                                        int numColumns = metaData.getColumnCount();
                                        

                                        while (resultGetColumnRoutes.next()) {
                                            for (int i = 2; i <= numColumns; i++) {
                                                String columnValue = resultGetColumnRoutes.getString(i);
                                                columnValue = decrypt(columnValue, secretKey);
                                                if(columnValue.equals(musicPath)){
                                                    String columnName = metaData.getColumnName(i);
                                                
                                                    String deleteColumnQuery = "ALTER TABLE sonidos DROP COLUMN " + columnName;

                                                    try(PreparedStatement deleteColumnQueryPst = connectDB.prepareStatement(deleteColumnQuery)){
                                                        File fileColumn = new File(columnValue);
                                                        String routePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "Sounds" + File.separator + fileColumn.getName();
                                                        File fileToDelete = new File(routePath);

                                                        try {
                                                            if (fileToDelete.delete()) {
                                                                System.out.println("Sonido eliminado con éxito");
                                                            } else {
                                                                System.out.println("No se pudo eliminar el sonido. Verifica los permisos o si el archivo está siendo utilizado.");
                                                            }
                                                        } catch (SecurityException e) {
                                                            System.out.println("No tienes permisos para eliminar el sonido.");
                                                            e.printStackTrace();
                                                        } catch (Exception e) {
                                                            System.out.println("Ocurrió un problema al intentar eliminar el sonido.");
                                                            e.printStackTrace();
                                                        }
                                                        
                                                        int rowsEffected = deleteColumnQueryPst.executeUpdate();
                                                    }catch(Exception e){
                                                        e.printStackTrace();
                                                    }
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
                        } else {
                            System.out.println("No se encontró el Label con el id 'labelNameUser'");
                        }
                }
            });
            
        }
    }
   

    public MusicItem(String musicName, Boolean isSound){
        if(!isSound){
            nameLabel = new Label(musicName);
            nameLabel.setFont(Font.font("Calibri Bold Italic", 14.0));
            nameLabel.setLayoutX(11.0);
            nameLabel.setLayoutY(5.0);
            nameLabel.setPrefHeight(17.0);
            nameLabel.setPrefWidth(327);
            nameLabel.setWrapText(true);
            nameLabel.setTextFill(Color.valueOf("#fafafa"));
            getChildren().addAll(nameLabel);
        }
    }
    
    private String formatDuration(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() % 60;
        return String.format("%d:%02d", minutes, seconds);
    }


    private void initializeUI() {
        nameLabel.setFont(Font.font("Calibri Bold Italic", 14.0));
        nameLabel.setLayoutX(47.0);
        nameLabel.setLayoutY(12.0);
        nameLabel.setPrefHeight(17.0);
        nameLabel.setPrefWidth(265.0);
        nameLabel.setWrapText(true);
        nameLabel.setTextFill(Color.valueOf("#fafafa"));

        playIcon.setFill(Color.valueOf("#fafafa"));
        playIcon.setGlyphName("PLAY_CIRCLE");
        playIcon.setLayoutX(14.0);
        playIcon.setLayoutY(30.0);
        playIcon.setSize("25");
        playIcon.setCursor(Cursor.HAND);

        trashIcon.setGlyphName("TRASH");
        trashIcon.setLayoutX(315.0);
        trashIcon.setLayoutY(30.0);
        trashIcon.setSize("25");
        trashIcon.setFill(Color.valueOf("#fafafa"));

        setPrefHeight(39.0);
        setPrefWidth(349.0);
    }

    public String getMusicURL() {
        return musicURL;
    }
    
    public String getPathURL(){
        return musicPath;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    
    public FontAwesomeIconView getPlayIcon() {
        return playIcon;
    }

    public String getNameMusic() {
        return nameLabel.getText();
    }
    
    public void setMusicURL(String musicURL) {
        this.musicURL = musicURL;
    }
    
    public void setLoadedState(Boolean stateLoaded){
        this.stateLoaded = stateLoaded;
    }
    
    public Boolean isLoaded(){
        return stateLoaded;
    }
    
    public void pauseSound(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
            stateLoaded = false;

            playIcon.setGlyphName("PLAY_CIRCLE");
            playAndPauseSound.setGlyphName("PLAY");
        }
    }
    
    public void startSound(String nameMusic, String URLmusic){
        if ((musicURL != null || !musicURL.isEmpty()) && "PLAY_CIRCLE".equals(playIcon.getGlyphName())) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            Media sound = new Media(URLmusic);
            mediaPlayer = new MediaPlayer(sound);


            mediaPlayer.setOnReady(() -> {
                mediaPlayer.setVolume((double) sliderVolume.getValue());

                timeSound.setText("0:00/" + formatDuration(mediaPlayer.getTotalDuration()));

                double valueSlider = durationSound.getValue();
                mediaPlayer.seek(Duration.seconds(valueSlider));
                mediaPlayer.play();
                stateLoaded = true;
                durationSound.setMax(mediaPlayer.getTotalDuration().toSeconds());

                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    Platform.runLater(() -> {
                        if (!durationSound.isValueChanging()) {
                            durationSound.setValue(newValue.toSeconds());
                            timeSound.setText(formatDuration(newValue) + "/" + formatDuration(mediaPlayer.getTotalDuration()));
                            mediaPlayer.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> {
                                double volume = mediaPlayer.getVolume();

                                for (int i = 0; i < magnitudes.length && i < 10; i++) {
                                    double normalizedMagnitude = magnitudes[i] / 100.0;
                                    double volumeAdjustedMagnitude = normalizedMagnitude * volume;

                                    double minHeight = 0;
                                    double maxHeight = 360;
                                    double heightValue = (minHeight + volumeAdjustedMagnitude * (maxHeight - minHeight));

                                    Rectangle rectangle = (Rectangle) containerRectangles.lookup("#rectangle" + (i + 1) + "");
                                    rectangle.setHeight((heightValue + 216 * volume) + 24);
                                }
                            });
                        }
                    });
                });

                mediaPlayer.setOnEndOfMedia(() -> {
                    mediaPlayer.pause();
                    stateLoaded = false;
                    durationSound.setValue(0);

                    timeSound.setText("0:00/" + formatDuration(mediaPlayer.getTotalDuration()));
                    playIcon.setGlyphName("PLAY_CIRCLE");
                    playAndPauseSound.setGlyphName("PLAY");
                });

                sliderVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
                    mediaPlayer.setVolume((double) newValue);
                });

                nameMusicReproductor.setText(nameMusic);
                playIcon.setGlyphName("STOP_CIRCLE");
                playAndPauseSound.setGlyphName("PAUSE");
            });
        } else if ((musicURL != null && !musicURL.isEmpty()) && "STOP_CIRCLE".equals(playIcon.getGlyphName())) {
            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                stateLoaded = false;

                playIcon.setGlyphName("PLAY_CIRCLE");
                playAndPauseSound.setGlyphName("PLAY");
            }
        }
    }
    

}
