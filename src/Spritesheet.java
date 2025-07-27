import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Spritesheet {
    protected static BufferedImage spritesheet;

    protected Spritesheet() {
        try {
            spritesheet = ImageIO.read(getClass().getResource("../res/spritesheet.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage getSprite(int x, int y, int width, int height) {
        return spritesheet.getSubimage(x, y, width, height);
    }
}
