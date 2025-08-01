import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable, KeyListener {

    // Definindo as variáveis para as dimensões da minha janela, para executar o loop do jogo e para controlar o loop.
    protected static final int WIDTH = 1366, HEIGHT = 768, SCALE = 1;
    private Thread thread;
    private boolean isRunning;

    // Variável para armazenar o FPS do jogo que será mostrado na interface.
    private int fps = 0;

    // variáveis de controle de pontos.
    private double score = 0;
    private final double SCORE_SPEED = 0.2;
    private double record = 0;

    // Instanciando as variáveis das minhas entidades Player e Obstáculos.
    protected static Player player;
    protected static List<Obstacle> obstacles = new ArrayList<Obstacle>();

    // Instanciando as listras para serem desenhadas na janela para simularem a faixa de rua.
    private long lastStripeTime = System.currentTimeMillis(); // tempo da última listra.
    protected static List<Stripe> stripes = new ArrayList<Stripe>();

    // Instanciando o objeto rand para aleatorizar o surgimento dos obstáculos.
    private Random rand = new Random();

    // Método construtor sobre essa classe que invoca os principais métodos do programa e inicializa o meu Players.
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
        saveGamePoint();
        thread.start();
    }

    // Método para parar o jogo.
    private void stopGame() {
        isRunning = false;
        saveGamePoint();
    }

    // Método para reiniciar o jogo.
    private void restartGame() {
        // Apagamos cada obstáculo e listra existente.
        removeObject(0);

        // Criamos um novo jogador utilizando a mesma variável.
        player = new Player(300, HEIGHT - HEIGHT / 2);

        // Reiniciamos os pontos.
        score = 0;
        Obstacle.speed = 1;

        // Após tudo pronto, iniciamos o jogo.
        this.startGame();
    }

    private void saveGamePoint() {
        double recordUpdated = this.record;

        File dir = new File("saves");
        if (!dir.exists()) {
            boolean criado = dir.mkdir();
            if (criado) {
                System.out.println("Diretório 'saves' criado com sucesso.");
            } else {
                System.out.println("Falha ao criar o diretório 'saves'.");
                return;
            }
        }

        File save = new File(dir, "score.txt");
        if (save.exists()) {
            String path = "saves/score.txt"; // ou apenas "arquivo.txt" se estiver no diretório atual
            try {
                // Lê todo o conteúdo do arquivo como uma única String
                String docPoints = new String(Files.readAllBytes(Paths.get(path)));
                recordUpdated = Double.parseDouble(docPoints);
                this.record = recordUpdated;
            } catch (IOException e) {
                System.out.println("Erro ao ler o arquivo: " + e.getMessage());
                return;
            }

            if (this.score > recordUpdated) {
                String points = String.valueOf(this.score);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(save))) {
                    writer.write(points);
                    System.out.println("Arquivo criado e pontos salvos com sucesso.");
                } catch (Exception e) {
                    System.out.println("Erro ao escrever no arquivo: " + e.getMessage());
                }
                this.record = this.score;
            }
        } else {
            this.record = this.score;
            String points = String.valueOf(record);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(save))) {
                writer.write(points);
                System.out.println("Arquivo criado e pontos salvos com sucesso.");
            } catch (Exception e) {
                System.out.println("Erro ao escrever no arquivo: " + e.getMessage());
            }
        }
    }

    // Método para remover um objeto (listra ou obstáculo) do meu jogo, baseado em ID. 1 para Carros e 2 para listras.
    private void removeObject(int idObject) {
        switch (idObject) {
            case 0:
                for (int i = 0; i < stripes.size(); i++) {
                    stripes.remove(i);
                    i--;
                }
                for (int i = 0; i < obstacles.size(); i++) {
                    obstacles.remove(i);
                    i--;
                }
                break;
            case 1:
                for (int i = 0; i < obstacles.size(); i++) {
                    if (obstacles.get(i).x + obstacles.get(i).width < 0) {
                        obstacles.remove(i);
                        i--; // evitar pular elementos
                    }
                }
                break;
            case 2:
                for (int i = 0; i < stripes.size(); i++) {
                    if (stripes.get(i).x + stripes.get(i).width < 0) {
                        stripes.remove(i);
                        i--; // evitar pular elementos
                    }
                }
                break;
            default:
                break;
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

    // Método para converter os pontos para texto.
    private String convertScoreToString(double score) {
        int points = (int) Math.floor(score);
        String number = String.valueOf(points);
        return number;
    }

    // Método que chama as atualizações de cada item do meu jogo (Player, obstáculos e listras).
    private void update() {
        player.update();
        score += SCORE_SPEED;

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

        long currentTime = System.currentTimeMillis();
        // Verifica se 1 segundo passou desde o último retângulo
        if (currentTime - lastStripeTime >= 1000) {
            stripes.add(new Stripe(WIDTH, HEIGHT / 2 - 20));
            lastStripeTime = currentTime;
        }

        removeObject(1);
        removeObject(2);
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

        // Score
        graph.setFont(new Font("Arial", Font.BOLD, 50));
        graph.drawString("Score: " + convertScoreToString(this.score), 10, 55);
        graph.drawString("Record: " + convertScoreToString(this.record), 400, 55);

        // FPS
        graph.setColor(Color.WHITE);
        graph.setFont(new Font("Arial", Font.BOLD, 20));
        graph.drawString("FPS: " + this.fps, WIDTH - 100, 35);

        // Message
        if (isCollide(player)) {
            graph.setColor(new Color(40, 40, 40, 180));
            graph.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);

            graph.setColor(Color.WHITE);
            graph.setFont(new Font("Arial", Font.BOLD, 80));
            graph.drawString("Game Over", WIDTH * SCALE / 2 - 210, HEIGHT * SCALE / 2 - 180);

            graph.setFont(new Font("Arial", Font.BOLD, 30));
            graph.drawString("Aperte \"Esc\" para fechar o jogo ou \"R\" para reiniciar.", WIDTH * SCALE / 2 - 380, HEIGHT * SCALE / 2 - 130);
            bs.show();
            stopGame();
        }
        bs.show();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long now;
        double timer = System.currentTimeMillis();
        double maxFPS = 120.0;
        double ns = 1000000000 / maxFPS;
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
                this.fps = frames;
                Obstacle.speed += 0.2;
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

        if (e.getKeyCode() == KeyEvent.VK_R && isRunning == false) {
            restartGame();
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
