import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static int delay = 200;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    Button start;

    /***
     * Constructor of the class
     */
    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        start = new Button("Start");
        //this.add(start);
        startGame();
    }

    /***
     * Method to start the snake game by displaying a new apple on the
     * screen and starting a new timer
     */
    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(delay,this);
        timer.start();
    }

    /***
     * Call the paint component method of the super class
     * @param g the Graphics object to pass into the draw method and super method
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    /***
     * Method to draw the elements of the game
     * @param g the Graphics object to draw the game elements
     */
    public void draw(Graphics g) {
        if(running) { // draw the apple
            g.setColor(Color.RED);
            g.fillOval(appleX,appleY,UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) { // draw the head of the snake with a random color
                    g.setColor(new Color(random.nextInt(255)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else { // draw the body of the snake with random colors
                    g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            // display score as a measure of apples eaten
            g.setColor(Color.WHITE);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
            FontMetrics fontMetrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - fontMetrics.stringWidth("Score: " + applesEaten))/2,
                    g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    /***
     * Method to display an apple at random positions on the screen
     */
    public void newApple() {
        // set x and y coordinates of apple
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }

    /***
     * Method to move the snake around the screen
     */
    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
        // increase speed of game when certain score is reached
        if (applesEaten >= 10) {
            delay = 100;
        }
        if (applesEaten >= 50) {
            delay = 75;
        }
        if (applesEaten >= 100) {
            delay = 50;
        }
        if (applesEaten >= 150) {
            delay = 25;
        }
    }

    /***
     * Method to count the number of apples eaten and increment snake size as apples are being eaten
     */
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    /***
     * Method to check if the snake has collided with the borders or with a part of its own body
     */
    public void checkCollisions() {
        // check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && y[0] == y[i]) {
                running = false;
                break;
            }
        }
        // check if head collides with left border
        if (x[0] < 0) {
            running = false;
        }
        // check if head collides with right border
        if (x[0] > SCREEN_WIDTH-UNIT_SIZE) {
            running = false;
        }
        // check if head collides with top border
        if (y[0] < 0) {
            running = false;
        }
        // check if head collides with bottom border
        if (y[0] > SCREEN_HEIGHT-UNIT_SIZE) {
            running = false;
        }

        if (!running) timer.stop();
    }

    /***
     * Method to display score and game over screen
     * @param g the Graphics object to display game over and score
     */
    public void gameOver(Graphics g) {
        g.setColor(Color.WHITE);
        // score text
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2,
                g.getFont().getSize());
        // game over text
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 60));
        FontMetrics fontMetrics = getFontMetrics(g.getFont());
        g.drawString("Game Over..", (SCREEN_WIDTH - fontMetrics.stringWidth("Game Over"))/2,
                SCREEN_HEIGHT/2);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        /***
         * Method to move the snake as keys are pressed
         * @param k the event to be processed
         */
        @Override
        public void keyPressed(KeyEvent k) {
            switch (k.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R') direction = 'L';
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') direction = 'R';
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') direction = 'U';
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') direction = 'D';
                }
            }
        }
    }
}
