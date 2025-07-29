import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable, KeyListener {

    // Definindo as variáveis para as dimensões da minha janela, para executar o
    // loop do jogo e para controlar o loop.
    protected static final int WIDTH = 1366, HEIGHT = 768, SCALE = 1;
    private Thread thread;
    private boolean isRunning;

    // Instanciando as variáveis das minhas entidades Player e Obstáculos.
    protected static Player player;
    protected static List<Obstacle> obstacles = new ArrayList<Obstacle>();

    // Instanciando as listras para serem desenhadas na janela para simularem a
    // faixa de rua.
    private long lastStripeTime = System.currentTimeMillis(); // tempo da última listra.
    protected static List<Stripe> stripes = new ArrayList<Stripe>();

    // Instanciando o objeto rand para aleatorizar o surgimento dos obstáculos.
    private Random rand = new Random();

    // Método construtor sobre essa classe que invoca os principais métodos do
    // programa e inicializa o meu Players.
    private Game() {
        this.addKeyListener(this);
        this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        new Spritesheet();
        initFrame();
        player = new Player(300, HEIGHT - HEIGHT / 2);
    }

    // Método para criar a minha janela.
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

    // Método para iniciar o jogo.
    private void startGame() {
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }

    // Método para parar o jogo.
    private void stopGame() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Método para verificar se as entidades Player e Obstáculos colidiu com algum obstáculo.
    protected static boolean isCollide(Rectangle rect) {
        for (Obstacle obs : obstacles) {
            if (obs.intersects(rect)) {
                return true;
            }
        }
        return false;
    }

    // Método que chama as atualizações de cada item do meu jogo (Player, obstáculos
    // e listras).
    private void update() {
        player.update();

        if (rand.nextInt(1, 300) == 1) {
            int maxTries = 10;
            for (int i = 0; i < maxTries; i++) {
                Obstacle newObstacle = new Obstacle(WIDTH, rand.nextInt(0, HEIGHT - 120));
                if (!isCollide(newObstacle)) {
                    obstacles.add(newObstacle);
                    break; // sucesso
                }
            }
        }

        for (int i = 0; i < obstacles.size(); i++) {
            if (obstacles.get(i).x + 240 < 0) {
                obstacles.remove(i);
                i--; // evitar pular elementos
            }
        }

        long currentTime = System.currentTimeMillis();

        // Verifica se 1 segundo passou desde o último retângulo
        if (currentTime - lastStripeTime >= 1000) {
            stripes.add(new Stripe(WIDTH, HEIGHT / 2 - 20));
            lastStripeTime = currentTime;
        }

        for (int i = 0; i < stripes.size(); i++) {
            if (stripes.get(i).x + 90 < 0) {
                stripes.remove(i);
                i--; // evitar pular elementos
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
        for (int i = 0; i < stripes.size(); i++) {
            stripes.get(i).render(graph);
        }

        // Player
        player.render(graph);

        // Obstacle
        for (int i = 0; i < obstacles.size(); i++) {
            obstacles.get(i).render(graph);
        }

        // Message
        if (isCollide(player)) {
            graph.setColor(new Color(40, 40, 40, 1));
            graph.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);

            graph.setColor(new Color(255, 255, 255));
            graph.setFont(new Font("Arial", Font.BOLD, 80));
            graph.drawString("Game Over", WIDTH * SCALE / 2 - 210, HEIGHT * SCALE / 2 - 180);
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
                Obstacle.speed += 0.02;
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
