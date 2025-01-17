package game.engine.sound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SoundEngine {
    private Clip musicClip;
    private Thread musicThread;
    private boolean looping;
    private String currentFilePath;

    public static SoundEngine instance;

    public SoundEngine() {
        if (instance == null) {
            instance = this;
        }
    }

    public void playMusic(String filePath, boolean looping) {
        this.looping = looping;

        if (filePath.equals(currentFilePath)) {
            return;
        }
        currentFilePath = filePath;
        stopMusic(); // Stop any currently playing music

        musicThread = new Thread(() -> {
            loadMusic(filePath);
            if (musicClip != null) {

                musicClip.setFramePosition(0); // Start from the beginning
                if (looping) {
                    musicClip.loop(Clip.LOOP_CONTINUOUSLY);
                } else {
                    musicClip.start();
                }

                // Wait for the clip to finish if not looping
                if (!looping) {
                    while (musicClip.isRunning() && !Thread.currentThread().isInterrupted()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                    stopMusic();
                }
            }
        });

        musicThread.start();

    }

    public void stopMusic() {
        // Stop and close the current music thread
        if (musicThread != null && musicThread.isAlive()) {
            musicThread.interrupt();
            musicThread = null;
        }

        // Stop and close the current clip
        if (musicClip != null && musicClip.isOpen()) {
            musicClip.stop();
            musicClip.close();
        }
    }

    ThreadPoolExecutor executer = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    public void playSound(Clip soundClip) {
        executer.execute(() -> {
            try {
                soundClip.setFramePosition(0);

                soundClip.start();

                // Wait for the sound to finish playing
                while (soundClip.isRunning()) {
                    Thread.sleep(50);
                }

                //soundClip.close();
            } catch (InterruptedException e) {
                System.err.println("Error playing sound: " + e.getMessage());
            }
        });
    }

    private void loadMusic(String filePath) {
        try {
            // Close the existing clip if open
            if (musicClip != null && musicClip.isOpen()) {
                musicClip.stop();
                musicClip.close();
            }

            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioStream);
            FloatControl gainControl =
                    (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
            // lower Music gain 8db
            gainControl.setValue(-8.0f);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("Error loading music: " + e.getMessage());
            }
    }


}
