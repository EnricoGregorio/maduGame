import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Stripe extends Rectangle {
    protected final int width = 90, height = 20;

    protected Stripe(int x, int y) {
        super(x, y, 90, 20);
    }

    protected void render(Graphics g) {
        g.setColor(new Color(233, 233, 233));
        g.fillRect(x -= Obstacle.speed, y, width, height);
    }
}
