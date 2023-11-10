package Class;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;

public class MusicItem extends AnchorPane {
    private Label nameLabel;
    private FontAwesomeIconView playIcon;
    private FontAwesomeIconView ellipsisIcon;
    private String musicURL;
    private MediaPlayer mediaPlayer;

    public MusicItem(String musicName, String musicURL) {
        this.musicURL = musicURL;

        nameLabel = new Label(musicName);
        playIcon = new FontAwesomeIconView();
        ellipsisIcon = new FontAwesomeIconView();

        initializeUI();

        setCursor(Cursor.HAND);

        getChildren().addAll(nameLabel, playIcon, ellipsisIcon);

        playIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if ((musicURL != null && !musicURL.isEmpty()) && "PLAY_CIRCLE".equals(playIcon.getGlyphName())) {
                    // Código para reproducir el sonido
                    Media sound = new Media(musicURL);
                    mediaPlayer = new MediaPlayer(sound);
                    mediaPlayer.play();

                    // Cambiar el ícono a "PAUSE_CIRCLE"
                    playIcon.setGlyphName("STOP_CIRCLE");
                }else if ((musicURL != null && !musicURL.isEmpty()) && "STOP_CIRCLE".equals(playIcon.getGlyphName())) {
                    if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                        mediaPlayer.pause();
                        // Cambiar el ícono a "PLAY_CIRCLE"
                        playIcon.setGlyphName("PLAY_CIRCLE");
                    }
                }
            }
        });
    }

    private void initializeUI() {
        nameLabel.setFont(Font.font("Calibri Bold Italic", 14.0));
        nameLabel.setLayoutX(47.0);
        nameLabel.setLayoutY(12.0);
        nameLabel.setPrefHeight(17.0);
        nameLabel.setPrefWidth(127.0);
        nameLabel.setWrapText(true);

        playIcon.setFill(Color.valueOf("#0c0c0c"));
        playIcon.setGlyphName("PLAY_CIRCLE");
        playIcon.setLayoutX(14.0);
        playIcon.setLayoutY(30.0);
        playIcon.setSize("25");
        playIcon.setCursor(Cursor.HAND);

        ellipsisIcon.setGlyphName("ELLIPSIS_V");
        ellipsisIcon.setLayoutX(321.0);
        ellipsisIcon.setLayoutY(30.0);
        ellipsisIcon.setSize("25");

        setPrefHeight(39.0);
        setPrefWidth(349.0);
    }

    public String getMusicURL() {
        return musicURL;
    }

    public void setMusicURL(String musicURL) {
        this.musicURL = musicURL;
    }
}
