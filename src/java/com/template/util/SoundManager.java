package com.template.util;

import java.io.File;
import java.net.URL;
import javafx.scene.media.AudioClip;

/**
 * Utilitário para reprodução de efeitos sonoros temáticos do Bob Esponja.
 */
public class SoundManager {

    public static final String GARY_MEOW = "gary_meow.mp3";
    public static final String STANK_NOISE = "sponge-stank-noise.mp3";
    public static final String BOOWOMP = "spongebob-boowomp.mp3";
    public static final String FAIL = "spongebob-fail.mp3";
    public static final String SAD_SONG = "spongebob-sad-song.mp3";
    public static final String SHIVER = "spongebob-shiver-sound.mp3";
    public static final String WALK = "spongebob-walk.mp3";

    private static long lastWalkTime = 0;

    public static void play(String soundName) {
        if (!ThemeContext.isBobEsponja()) {
            return;
        }

        new Thread(() -> {
            try {
                // Tenta carregar via JavaFX AudioClip primeiro
                URL resource = SoundManager.class.getResource("/com/template/spongebob/sounds/" + soundName);
                if (resource != null) {
                    try {
                        AudioClip clip = new AudioClip(resource.toExternalForm());
                        clip.play();
                        return;
                    } catch (Throwable ignored) {
                        // Fallback para afplay
                    }
                }

                // Fallback direto com o comando afplay do macOS
                File soundFile = new File("src/resources/com/template/spongebob/sounds/" + soundName);
                if (!soundFile.exists()) {
                    soundFile = new File(System.getProperty("user.home") + "/Downloads/" + soundName);
                }
                if (soundFile.exists()) {
                    new ProcessBuilder("afplay", soundFile.getAbsolutePath()).start();
                }
            } catch (Exception ignored) {
            }
        }).start();
    }

    /**
     * Toca o som dos passos/rastejo com ritmo controlado para animações de movimento.
     */
    public static void playWalkThrottled() {
        long now = System.currentTimeMillis();
        if (now - lastWalkTime > 850) {
            lastWalkTime = now;
            play(WALK);
        }
    }
}
