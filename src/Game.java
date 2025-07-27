import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable, KeyListener {

    private final int WIDTH = 1366, HEIGHT = 768, SCALE = 1;
    private Thread thread;
    private boolean isRunning;

    private Game() {
        this.addKeyListener(this);
        this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        initFrame();
    }

    private void initFrame() {
        JFrame frame = new JFrame();
        frame.setTitle("Jogo da Madu");
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

    private void update() {

    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();

        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics graph = bs.getDrawGraphics();
        graph.setColor(new Color(43, 43, 43));
        graph.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
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
            // TODO player up true.
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN ^ e.getKeyCode() == KeyEvent.VK_S) {
            // TODO player down true.
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP ^ e.getKeyCode() == KeyEvent.VK_W) {
            // TODO player up false.
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN ^ e.getKeyCode() == KeyEvent.VK_S) {
            // TODO player down false.
        }
    }

    public static void main(String[] args) {
        Game game = new Game();

        game.startGame();
    }
}
