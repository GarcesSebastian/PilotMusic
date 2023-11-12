package Class;

import Database.ConexionBD;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

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

    public MusicItem(String musicPath, String musicName, String musicURL, Boolean isSound, Label nameMusicReproductor, Slider durationSound, Label timeSound, Slider sliderVolume, FontAwesomeIconView playAndPauseSound, FontAwesomeIconView forwardSound, FontAwesomeIconView backwardSound, FontAwesomeIconView nextSound, FontAwesomeIconView backSound, FontAwesomeIconView iconVolume, ListView<MusicItem> listMusic) {
        if (isSound) {
            if (nameMusicReproductor != null) {
                this.nameMusicReproductor = nameMusicReproductor;
            }
            
            if(musicPath != null){
                this.musicPath = musicPath;
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
                System.out.println(forwardSound);
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
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                        }

                        Media sound = new Media(musicURL);
                        mediaPlayer = new MediaPlayer(sound);
                        
                       
                        mediaPlayer.getAudioEqualizer().setEnabled(true);

                        mediaPlayer.getAudioEqualizer().getBands().clear();

                        mediaPlayer.getAudioEqualizer().getBands().add(new EqualizerBand(0, 59, 59));
                        mediaPlayer.getAudioEqualizer().getBands().add(new EqualizerBand(60, 169, 169));
                        mediaPlayer.getAudioEqualizer().getBands().add(new EqualizerBand(170, 309, 309));
                        mediaPlayer.getAudioEqualizer().getBands().add(new EqualizerBand(310, 599, 599));
                        mediaPlayer.getAudioEqualizer().getBands().add(new EqualizerBand(600, 999, 999));
                        mediaPlayer.getAudioEqualizer().getBands().add(new EqualizerBand(1000, 2999, 2999));
                        mediaPlayer.getAudioEqualizer().getBands().add(new EqualizerBand(3000, 5999, 5999));
                        mediaPlayer.getAudioEqualizer().getBands().add(new EqualizerBand(6000, 11999, 11999));
                        mediaPlayer.getAudioEqualizer().getBands().add(new EqualizerBand(12000, 13999, 13999));
                        mediaPlayer.getAudioEqualizer().getBands().add(new EqualizerBand(14000, 15999, 15999));
                        mediaPlayer.getAudioEqualizer().getBands().add(new EqualizerBand(16000, 22000, 22000));

                        for (EqualizerBand item : mediaPlayer.getAudioEqualizer().getBands()) {
                            System.out.println("Band: " + item.getGain());
                        }
                        
                        mediaPlayer.setOnReady(() -> {
                            
                            System.out.println(mediaPlayer.getTotalDuration().toSeconds());
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
                                    }
                                });
                            });
                            
                            mediaPlayer.setOnEndOfMedia(() -> {
                                mediaPlayer.pause();
                                stateLoaded = false;
                                durationSound.setValue(0);
                                
                                playIcon.setGlyphName("PLAY_CIRCLE");
                                playAndPauseSound.setGlyphName("PLAY");
                            });
                            
                            sliderVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
                                mediaPlayer.setVolume((double) newValue);
                                System.out.println(mediaPlayer.getTotalDuration());
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
                    listMusic.getItems().remove(MusicItem.this);
                    
                    if (listMusic.getItems().isEmpty()) {
                        MusicItem newMusicItemEmpty = new MusicItem("No se han encontrado canciones", false);
                        listMusic.getItems().add(newMusicItemEmpty);
                    } else {
                        listMusic.getItems().removeIf(item -> item.getNameMusic().equals("No se han encontrado canciones"));
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
                                                System.out.println(columnValue + " -/- " + musicPath);
                                                if(columnValue.equals(musicPath)){
                                                    String columnName = metaData.getColumnName(i);
                                                
                                                    String deleteColumnQuery = "ALTER TABLE sonidos DROP COLUMN " + columnName;

                                                    try(PreparedStatement deleteColumnQueryPst = connectDB.prepareStatement(deleteColumnQuery)){

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
        nameLabel.setPrefWidth(275.0);
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
                System.out.println(mediaPlayer.getTotalDuration().toSeconds());
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
                        }
                    });
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
