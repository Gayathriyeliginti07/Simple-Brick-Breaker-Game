import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class BrickBreakerGame {
    public static void main(String args[]) {
        JFrame f = new JFrame();
        f.setTitle("BRICK BREAKER GAME");
        GamePlay gamePlay = new GamePlay();
        f.setSize(700, 600);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(gamePlay);
    }
}

class GamePlay extends JPanel implements KeyListener, ActionListener {
    boolean play = true;
    int totalBricks = 21;
    Timer timer;
    int delay = 4;
    int score = 0;
    int ballposX = 120;
    int ballposY = 350;
    int ballXdir = -2;
    int ballYdir = -3;
    int playerX = 310;
    Generator map;

    public GamePlay() {
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
        map = new Generator(3, 7);
    }

    public void paint(Graphics g) {
        g.setColor(new Color(173, 216, 230));
        g.fillRect(1, 1, 692, 592);

        g.setColor(new Color(25, 25, 112));
        g.fillRect(0, 0, 692, 3);
        g.fillRect(0, 3, 3, 592);
        g.fillRect(692, 3, 3, 592);

        g.setColor(new Color(0, 191, 255));
        g.fillRect(playerX, 550, 100, 12);

        map.draw((Graphics2D) g);

        g.setColor(new Color(255, 20, 147));
        g.fillOval(ballposX, ballposY, 20, 20);

        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(550, 30, new Color(0, 255, 255), 650, 30, new Color(255, 0, 255), true);
        g2d.setPaint(gp);
        g2d.setFont(new Font("serif", Font.BOLD, 25));
        g2d.drawString("Score : " + score, 550, 30);

        if (totalBricks <= 0) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(new Color(255, 0, 255));
            g.setFont(new Font("serif", Font.BOLD, 40));
            g.drawString("You Won!! Score: " + score, 180, 300);
            g.setFont(new Font("serif", Font.BOLD, 25));
            g.drawString("Press Enter to Restart.", 220, 350);
        }

        if (ballposY > 570) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(new Color(220, 20, 60));
            g.setFont(new Font("serif", Font.BOLD, 40));
            g.drawString("Game Over!! Score: " + score, 180, 300);
            g.setFont(new Font("serif", Font.BOLD, 25));
            g.drawString("Press Enter to Restart!!", 220, 350);
        }

        g.dispose();
    }

    public void moveLeft() {
        play = true;
        playerX -= 30;
    }

    public void moveRight() {
        play = true;
        playerX += 30;
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX <= 0)
                playerX = 0;
            else
                moveLeft();
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600)
                playerX = 600;
            else
                moveRight();
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                play = true;
                ballposX = 120;
                ballposY = 350;
                ballXdir = -2;
                ballYdir = -3;
                score = 0;
                totalBricks = 21;
                map = new Generator(3, 7);
                repaint();
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (play) {
            if (ballposX <= 0)
                ballXdir = -ballXdir;

            if (ballposY <= 0)
                ballYdir = -ballYdir;

            if (ballposX >= 670)
                ballXdir = -ballXdir;

            Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);
            Rectangle paddleRect = new Rectangle(playerX, 550, 100, 8);

            if (ballRect.intersects(paddleRect))
                ballYdir = -ballYdir;

            A:
            for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int width = map.brickWidth;
                        int height = map.brickHeight;
                        int brickXpos = j * width + 80;
                        int brickYpos = i * height + 50;

                        Rectangle brickRect = new Rectangle(brickXpos, brickYpos, width, height);

                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;

                            if (ballposX + 19 <= brickXpos || ballposX + 1 >= brickXpos + width)
                                ballXdir = -ballXdir;
                            else
                                ballYdir = -ballYdir;

                            break A;
                        }
                    }
                }
            }

            ballposX += ballXdir;
            ballposY += ballYdir;
        }

        repaint();
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}

class Generator {
    int map[][];
    int brickWidth;
    int brickHeight;

    public Generator(int r, int c) {
        map = new int[r][c];
        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++)
                map[i][j] = 1;

        brickWidth = 540 / c;
        brickHeight = 150 / r;
    }

    public void setBrickValue(int value, int r, int c) {
        map[r][c] = value;
    }

    public void draw(Graphics2D g) {
        Color[] colors = {Color.RED, Color.CYAN, new Color(255, 105, 180)};
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    g.setColor(colors[(i + j) % colors.length]);
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                    g.setStroke(new BasicStroke(4));
                    g.setColor(Color.BLACK);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
        }
    }
}