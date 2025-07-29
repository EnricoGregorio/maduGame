import java.awt.Graphics;
import java.awt.Rectangle;

public class Player extends Rectangle {
    protected boolean up, down;
    private int speed = 3;
    private int width = 120, height = 140;

    private boolean isMove = true;
    private int curFrames = 0, targetFrames = 13;
    private int curAnimation = 0;
    private boolean isCollides = !Game.isCollide(Game.player);

    protected Player(int x, int y) {
        super(x, y,90, 140);
    }

    private void anime() {
        if (isMove) {
            curFrames++;
            if (curFrames == targetFrames) {
                curFrames = 0;
                curAnimation++;
                if (curAnimation == Spritesheet.player.length) {
                    curAnimation = 0;
                }
            }
        } else {
            curAnimation = 0;
        }
    }

    protected void update() {
        anime();

        if (up && y >= 0 && isCollides) {
            y -= speed;
        }

        if (down && y <= Game.HEIGHT - height && isCollides) {
            y += speed;
        }
    }

    protected void render(Graphics g) {
        g.drawImage(Spritesheet.player[curAnimation], x, y, width, height, null);
    }
}
