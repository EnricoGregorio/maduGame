import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Spritesheet {
    protected static BufferedImage spritesheet;

    protected static BufferedImage[] player;
    protected static BufferedImage[] obstacle;

    protected Spritesheet() {
        try {
            spritesheet = ImageIO.read(getClass().getResource("res/spritesheet.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        player = new BufferedImage[3];
        obstacle = new BufferedImage[2];

        player[0] = Spritesheet.getSprite(18, 36, 151, 216);
        player[1] = Spritesheet.getSprite(177, 36, 147, 216);
        player[2] = Spritesheet.getSprite(332, 36, 148, 216);
        // player[3] = Spritesheet.getSprite(18, 262, 151, 216);
        // player[4] = Spritesheet.getSprite(177, 262, 147, 216);
        // player[5] = Spritesheet.getSprite(332, 262, 148, 216);

        obstacle[0] = Spritesheet.getSprite(20, 584, 225, 87);
        obstacle[1] = Spritesheet.getSprite(274, 584, 222, 87);
        obstacle[1] = Spritesheet.getSprite(20, 753, 225, 87);
    }

    private static BufferedImage getSprite(int x, int y, int width, int height) {
        return spritesheet.getSubimage(x, y, width, height);
    }
}
