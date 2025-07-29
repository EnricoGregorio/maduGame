import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

public class Obstacle extends Rectangle {
    private boolean isMove = true;
    protected int height = 90, width = 170;
    protected static double speed = 1;

    private int curFrames = 0, targetFrames = 15;
    private int curAnimation = 0;
    Random rand = new Random();

    protected Obstacle(int x, int y) {
        super(x, y, 170, 90);
    }

    private void anime() {
        if (isMove) {
            curFrames++;
            if (curFrames == targetFrames) {
                curFrames = 0;
                curAnimation++;
                if (curAnimation == Spritesheet.obstacle.length) {
                    curAnimation = 0;
                }
            }
        } else {
            curAnimation = 0;
        }
    }

    protected void render(Graphics g) {
        anime();
        g.drawImage(Spritesheet.obstacle[curAnimation], x-=2 * speed, y, width, height, null);
    }
}
