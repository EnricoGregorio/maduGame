import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable, KeyListener {
    protected static final int WIDTH = 1366, HEIGHT = 768, SCALE = 1;
    private Thread thread;
    private boolean isRunning;

    protected static Player player = new Player(300, HEIGHT - HEIGHT / 2);
    protected static List<Obstacle> obstacles = new ArrayList<Obstacle>();
    private Random rand = new Random();

    private Game() {
        this.addKeyListener(this);
        this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        initFrame();
    }

    private void initFrame() {
        JFrame frame = new JFrame();
        frame.setTitle("Madu Driver");
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void startGame() {
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }

    private void stopGame() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected static boolean isFree() {
        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle actualObs = obstacles.get(i);
            if (actualObs.intersects(player)) {
                return false;
            }
        }
        return true;
    }

    private void update() {
        player.update();

        if (rand.nextInt(1, 300) == 1) {
            obstacles.add(new Obstacle(WIDTH, rand.nextInt(0, HEIGHT - 32)));
        }

        for (int i = 0; i < obstacles.size(); i++) {
            if (obstacles.get(i).x + 32 < 0) {
                obstacles.remove(i);
            }
        }

    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();

        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics graph = bs.getDrawGraphics();

        // World
        graph.setColor(new Color(43, 43, 43));
        graph.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);

        // Player
        player.render(graph);

        // Obstacle
        for (int i = 0; i < obstacles.size(); i++) {
            obstacles.get(i).render(graph);
        }

        // Message
        if (!isFree()) {
            graph.setColor(new Color(40, 40, 40, 1));
            graph.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);

            graph.setColor(new Color(255, 255, 255));
            graph.setFont(new Font("Arial", Font.BOLD, 20));
            graph.drawString("Game Over.", WIDTH * SCALE / 2, HEIGHT * SCALE / 2);
            bs.show();
            isRunning = false;
        }

        bs.show();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long now;
        double timer = System.currentTimeMillis();
        double fps = 120.0;
        double ns = 1000000000 / fps;
        double delta = 0;
        int frames = 0;

        while (isRunning) {
            now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                update();
                render();
                frames++;
                delta--;
            }

            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }
        }
        stopGame();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP ^ e.getKeyCode() == KeyEvent.VK_W) {
            player.up = true;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN ^ e.getKeyCode() == KeyEvent.VK_S) {
            player.down = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP ^ e.getKeyCode() == KeyEvent.VK_W) {
            player.up = false;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN ^ e.getKeyCode() == KeyEvent.VK_S) {
            player.down = false;

        }
    }

    public static void main(String[] args) {
        Game game = new Game();

        game.startGame();
    }
}
