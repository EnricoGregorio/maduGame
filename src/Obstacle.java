import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Obstacle extends Rectangle {
    protected Obstacle(int x, int y) {
        super(x, y, 32, 32);
    }

    protected void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x--, y, 32, 32);
    }
}
