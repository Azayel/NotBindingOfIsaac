package Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static javax.imageio.ImageIO.read;

public final class AvatarTexture {
    public static BufferedImage avatarDefault;

    static {
        try {
            // Load the player avatar image
            avatarDefault = read(new File("src/assets/Player/Wizard.png"));

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading textures!");
        }
    }
}