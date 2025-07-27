import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Player extends Rectangle {
    protected boolean up, down;
    private int speed = 3;

    protected Player(int x, int y) {
        super(x, y, 32, 32);
    }

    protected void update() {
        boolean moved = false;

        if (up) {
            y -= speed;
            moved = true;
        } else if (down) {
            y += speed;
            moved = true;
        }
    }

    protected void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, 32, 32);
    }
}
