import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Stripe extends Rectangle {
    private final int width = 90, height = 20;

    protected Stripe(int x, int y) {
        super(x, y, 90, 20);
    }

    protected void render(Graphics g) {
        g.setColor(new Color(233, 233, 233));
        g.fillRect(x-=2 * Obstacle.speed, y, width, height);
    }
}
