import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BlockGame {

    static class MyFrame extends JFrame {

        static int Ball_DIAMETER = 20;
        static int Ball_HEIGHT = 20;
        static int Block_ROWS = 5;
        static int Block_COLUMNS = 10;
        static int Block_WIDTH = 40;
        static int Block_HEIGHT = 20;
        static int Block_SPACE = 3;
        static int Bar_WIDTH = 80;
        static int Bar_HEIGHT = 20;
        static int Window_WIDTH = 400 + (Block_SPACE * Block_COLUMNS) - Block_SPACE;
        static int Window_HEIGHT = 600;

        static MyPanel myPanel = null;
        static int score = 0;
        static Timer timer = null;
        static Block[][] blocks = new Block[Block_ROWS][Block_COLUMNS];
        static Ball ball = new Ball();
        static Bar bar = new Bar();
        static int barXTarget = bar.x;
        static int direction = 0;
        static int Ball_SPEED = 5;
        static boolean isGameFinished = false;

        static class Ball {
            int x = (Window_WIDTH / 2) - (Ball_DIAMETER / 2);
            int y = (Window_HEIGHT / 2) - (Ball_HEIGHT / 2);
            int width = Ball_DIAMETER;
            int height = Ball_HEIGHT;

            Point getCenter() {
                return new Point(x + (Ball_DIAMETER / 2), y + (Ball_HEIGHT / 2));
            }
            Point getBottomCenter() {
                return new Point(x + (Ball_DIAMETER / 2), y + (Ball_HEIGHT));
            }
            Point getTopCenter() {
                return new Point(x + (Ball_DIAMETER / 2), y);
            }
            Point getLeftCenter() {
                return new Point(x, y + (Ball_HEIGHT / 2));
            }
            Point getRightCenter() {
                return new Point(x + (Ball_DIAMETER), y + (Ball_HEIGHT / 2));
            }

        }

        static class Bar {
            int x = (Window_WIDTH / 2) - (Bar_WIDTH / 2);
            int y = (Window_HEIGHT - 100);
            int width = Bar_WIDTH;
            int height = Bar_HEIGHT;

        }

        static class Block {
            int x;
            int y;
            int width = Block_WIDTH;
            int height = Block_HEIGHT;
            int color = 0;
            boolean isDisappeared = false;
        }

        static class MyPanel extends JPanel {

            public MyPanel() {
                this.setSize(Window_WIDTH, Window_HEIGHT);
                this.setBackground(Color.BLACK);
            }

            public void paint(Graphics graphics) {
                super.paint(graphics);
                Graphics2D graphics2D = (Graphics2D) graphics;

                drawUI(graphics2D);

            }

            void drawUI(Graphics2D graphics2D) {
                for(int i = 0; i < Block_ROWS; i++) {
                    for(int j = 0; j < Block_COLUMNS; j++) {
                        if(blocks[i][j].isDisappeared) {
                            continue;
                        }
                        if(blocks[i][j].color == 0) {
                            graphics2D.setColor(Color.WHITE);
                        }
                        else if(blocks[i][j].color == 1) {
                            graphics2D.setColor(Color.BLUE);
                        }
                        else if(blocks[i][j].color == 2) {
                            graphics2D.setColor(Color.YELLOW);
                        }
                        else if(blocks[i][j].color == 3) {
                            graphics2D.setColor(Color.GREEN);
                        }
                        else if(blocks[i][j].color == 4) {
                            graphics2D.setColor(Color.RED);
                        }
                        graphics2D.fillRect(blocks[i][j].x, blocks[i][j].y, blocks[i][j].width, blocks[i][j].height);
                    }

                    //drawing score
                    graphics2D.setColor(Color.WHITE);
                    graphics2D.setFont(new Font("TimesRoman", Font.BOLD, 20));
                    if(isGameFinished) {
                        graphics2D.setColor(Color.RED);
                        graphics2D.drawString(" Game Completed!", Window_WIDTH / 2 - 60, 50);
                    }
                    graphics2D.drawString("Score: " + score, (Window_WIDTH / 2) - 30, 20);

                    //drawing the ball
                    graphics2D.setColor(Color.WHITE);
                    graphics2D.fillOval(ball.x, ball.y, Ball_DIAMETER, Ball_HEIGHT);

                    //drawing the bar
                    graphics2D.setColor(Color.WHITE);
                    graphics2D.fillRect(bar.x, bar.y, bar.width, bar.height);
                }
            }
        }


        public MyFrame(String title) {
            super(title);
            this.setVisible(true);
            this.setSize(Window_WIDTH, Window_HEIGHT);
            this.setLocation(400, 300);
            this.setLayout(new BorderLayout());
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            initData();

            myPanel = new MyPanel();
            this.add("Center", myPanel);

            setKeyListener();
            startTimer();
        }

        public void initData() {
            for(int i = 0; i < Block_ROWS; i++) {
                for(int j = 0; j < Block_COLUMNS; j++) {
                    blocks[i][j] = new Block();
                    blocks[i][j].x = (Block_WIDTH * j) + (Block_SPACE * j);
                    blocks[i][j].y = 100 + (Block_HEIGHT * i) + (Block_SPACE * i);
                    blocks[i][j].width = Block_WIDTH;
                    blocks[i][j].height = Block_HEIGHT;
                    blocks[i][j].color = 4 - i;
                    blocks[i][j].isDisappeared = false;
                }
            }
        }

        public void setKeyListener() {
            this.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent event) {
                    if(event.getKeyCode() == KeyEvent.VK_LEFT) {
                        System.out.println("Pressed the left key");
                        barXTarget -= 20;
                        if(bar.x < barXTarget) { //when the key is pressed repeatedly
                            barXTarget = bar.x;
                        }
                    }
                    else if(event.getKeyCode() == KeyEvent.VK_RIGHT) {
                        System.out.println("Pressed the right key");
                        barXTarget += 20;
                        if(bar.x > barXTarget) { //when the key is pressed repeatedly
                            barXTarget = bar.x;
                        }
                    }
                }
            });
        }

        public void startTimer() {
            timer = new Timer(20, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    movement();
                    checkCollision(); //collisions with the wall and the bar
                    checkCollisionBlock(); //collisions with the blocks
                    myPanel.repaint(); //redraw

                    //game completed
                    isGameFinished();
                }
            });
            timer.start(); //required to start the timer.
        }

        public void isGameFinished() {
            //game completed
            int count = 0;
            for(int i = 0; i < Block_ROWS; i++) {
                for(int j = 0; j < Block_COLUMNS; j++) {
                    Block block = blocks[i][j];
                    if(block.isDisappeared) {
                        count++;
                    }
                }
            }
            if(count == Block_ROWS * Block_COLUMNS) {
                isGameFinished = true;
            }
        }

        public void movement() {
            if(bar.x < barXTarget) {
                bar.x += 5;
            }
            else if(bar.x > barXTarget) {
                bar.x -= 5;
            }

            if(direction == 0) { // Up-right
                ball.x += Ball_SPEED;
                ball.y -= Ball_SPEED;
            }
            else if(direction == 1) { //Down-right
                ball.x += Ball_SPEED;
                ball.y += Ball_SPEED;
            }
            else if(direction == 2) { //Up-left
                ball.x -= Ball_SPEED;
                ball.y -= Ball_SPEED;
            }
            else if(direction == 3) { //Down-left
                ball.x -= Ball_SPEED;
                ball.y += Ball_SPEED;
            }
        }

        public boolean intersectRect(Rectangle rect1, Rectangle rect2) {
            return rect1.intersects(rect2);
        }

        public void checkCollision() {
            if(direction == 0) { // Up-right
                if(ball.y < 0) direction = 1; //with upper wall
                if(ball.x > Window_WIDTH - Ball_DIAMETER) direction = 2; //with right side wall
            }
            else if(direction == 1) { //Down-right
                if(ball.y > Window_HEIGHT - 2*Ball_HEIGHT - 10) { //maybe add a game over sign
                    direction = 0; //with bottom wall

                    //re-start
                    direction = 0;
                    ball.x = (Window_WIDTH / 2) - (Ball_DIAMETER / 2);
                    ball.y = (Window_HEIGHT / 2) - (Ball_HEIGHT / 2);
                    score = 0;
                }
                if(ball.x > Window_WIDTH - Ball_DIAMETER) direction = 3; //with right side wall

                if(ball.getBottomCenter().y >= bar.y) { //with bar
                    if(intersectRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
                            new Rectangle(bar.x, bar.y, bar.width, bar.height))) {
                        direction = 0;
                    }
                }
            }
            else if(direction == 2) { //Up-left
                if(ball.y < 0) direction = 3; //with upper wall
                if(ball.x < 0) direction = 0; //with left side wall
            }
            else if(direction == 3) { //Down-left
                if(ball.y > Window_HEIGHT - 2*Ball_HEIGHT - 10) { //maybe add a game over sign
                    direction = 2; //with bottom wall

                    //re-start
                    direction = 0;
                    ball.x = (Window_WIDTH / 2) - (Ball_DIAMETER / 2);
                    ball.y = (Window_HEIGHT / 2) - (Ball_HEIGHT / 2);
                    score = 0;

                }
                if(ball.x < 0) direction = 1; //with left side wall

                if(ball.getBottomCenter().y >= bar.y) { //with bar
                    if(intersectRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
                            new Rectangle(bar.x, bar.y, bar.width, bar.height))) {
                        direction = 2;
                    }
                }
            }
        }

        public void checkCollisionBlock() {
            for(int i = 0; i < Block_ROWS; i++) {
                for(int j = 0; j < Block_COLUMNS; j++) {
                    Block block = blocks[i][j];
                    if(block.isDisappeared == false) {
                        if(direction ==0) { //Up-right
                            if(intersectRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
                                    new Rectangle(block.x, block.y, block.width, block.height))) {
                                if((ball.x > block.x + 2) && (ball.getRightCenter().x <= block.x + block.width - 2)) {
                                    direction = 1;
                                }
                                else {
                                    direction = 2;
                                }
                                block.isDisappeared = true;

                                if(block.color == 0) score += 10;
                                else if(block.color == 1) score += 20;
                                else if(block.color == 2) score += 30;
                                else if(block.color == 3) score += 40;
                                else if(block.color == 4) score += 50;
                            }
                        }
                        else if(direction == 1) { //Down-right
                            if(intersectRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
                                    new Rectangle(block.x, block.y, block.width, block.height))) {
                                if((ball.x > block.x + 2) && (ball.getRightCenter().x <= block.x + block.width - 2)) {
                                    direction = 0;
                                }
                                else {
                                    direction = 3;
                                }
                                block.isDisappeared = true;

                                if(block.color == 0) score += 10;
                                else if(block.color == 1) score += 20;
                                else if(block.color == 2) score += 30;
                                else if(block.color == 3) score += 40;
                                else if(block.color == 4) score += 50;
                            }
                        }
                        else if(direction == 2) { //Up-left
                            if(intersectRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
                                    new Rectangle(block.x, block.y, block.width, block.height))) {
                                if((ball.x > block.x + 2) && (ball.getRightCenter().x <= block.x + block.width - 2)) {
                                    direction = 3;
                                }
                                else {
                                    direction = 0;
                                }
                                block.isDisappeared = true;

                                if(block.color == 0) score += 10;
                                else if(block.color == 1) score += 20;
                                else if(block.color == 2) score += 30;
                                else if(block.color == 3) score += 40;
                                else if(block.color == 4) score += 50;
                            }
                        }
                        else if(direction == 3) { //Down-left
                            if(intersectRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
                                    new Rectangle(block.x, block.y, block.width, block.height))) {
                                if((ball.x > block.x + 2) && (ball.getRightCenter().x <= block.x + block.width - 2)) {
                                    direction = 2;
                                }
                                else {
                                    direction = 1;
                                }
                                block.isDisappeared = true;

                                if(block.color == 0) score += 10;
                                else if(block.color == 1) score += 20;
                                else if(block.color == 2) score += 30;
                                else if(block.color == 3) score += 40;
                                else if(block.color == 4) score += 50;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new MyFrame("Block Game");
    }
}
