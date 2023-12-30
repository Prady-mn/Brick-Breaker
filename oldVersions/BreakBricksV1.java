package oldVersions;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
//import java.lang.*;
class BreakBricksV1 extends JPanel implements KeyListener{
    //scale of the game
    final static int SCALE = 20;
    //starting index of block of bricks
    final static int BRICK_X = 5;
    final static int BRICK_Y = 8;
    //Y index of platform
    final static int PLATFORM_Y = 29;
    final static int PLATFORM_WIDTH = 7;
    final static int PLATFORM_HEIGHT = 1;
    //bounds of frame
    final static int X_INDEX = 170;
    final static int Y_INDEX = 20;
    final static int WIDTH = 60;
    final static int HEIGHT = 35;
    //bounds of boundry'
    final static int BOUNDRY_LEFT = 3;
    final static int BOUNDRY_RIGHT = 57;
    final static int BOUNDRY_TOP = 5;
    final static int BOUNDRY_BOTTOM = 31;

    //platformX,ballX,ballY,
    int platformVx,count;
    double ballVelocity,ballVx,ballVy;
    Brick bricks[][];
    Ellipse2D ball;
    Rectangle2D platform;

    //Graphics
    JFrame frm;
    int scale;
    JLabel heading;
    boolean running;
    BreakBricksV1(){
        scale = SCALE;
        frm = new JFrame();

        bricks = new Brick[6][10];
        //platformX = 26;
        platformVx = 0;
        //ballX = platformX + PLATFORM_WIDTH/2;
        //ballY = PLATFORM_Y - 1;
        ballVelocity = 0.1;
        running = false;
    }

    public static void main(String args[]){
        BreakBricksV1 ob = new BreakBricksV1();
        ob.start();
    }
    void start(){
        //Frame
        frm.setBounds(X_INDEX,Y_INDEX,WIDTH*scale,HEIGHT*scale);
        frm.setVisible(true);
        //frm.setResizable(false);
        //frm.setConstraints(true);

        //Panel
        setBackground(Color.BLACK);
        setLayout(null);
        setFocusable(true);
        setOpaque(true);

        //heading
        heading = new JLabel(Integer.toString(count));
        //heading.setBounds(10,10,heading.getPreferredSize().width,heading.getPreferredSize().height);
        heading.setBounds(10,10,50,50);
        heading.setForeground(Color.WHITE);
        heading.setVisible(true);

        add(heading);
        createBricks();
        createPlatform();
        createBall();
        addKeyListener(this);
        frm.add(this);
        startBall(0,-ballVelocity);
        while(true)
            repaint();
    }

    @Override
    public void paint(Graphics g){
        requestFocus(true);
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        //if(!running){
        //bottom boundry line
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(4.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0f, new float[]{12.0f,4.0f}, 0.0f));
        g2d.drawLine(BOUNDRY_LEFT*scale,BOUNDRY_BOTTOM*scale,BOUNDRY_RIGHT*scale,BOUNDRY_BOTTOM*scale);

        g2d.setColor(Color.WHITE);

        //top boundry line
        g2d.setStroke(new BasicStroke(10.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0f, new float[]{10.0f,0.0f}, 0.0f));
        g2d.drawLine(0,BOUNDRY_TOP*scale,WIDTH*scale,BOUNDRY_TOP*scale);

        g2d.setStroke(new BasicStroke(7.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0f, new float[]{10.0f,0.0f}, 0.0f));
        //left boundry line
        g2d.drawLine(BOUNDRY_LEFT*scale,BOUNDRY_TOP*scale,BOUNDRY_LEFT*scale,HEIGHT*scale);
        //right boundry line
        g2d.drawLine(BOUNDRY_RIGHT*scale,BOUNDRY_TOP*scale,BOUNDRY_RIGHT*scale,HEIGHT*scale);

        //drawBricks(g2d);
        //running = true;
        //}

        //if(detectCollision()){
        detectCollision();
        drawBricks(g2d);
        //Toolkit.getDefaultToolkit().beep();
        // }
        drawPlatform(g2d);
        drawBall(g2d);
        moveBall();
    }

    void createPlatform(){
        platform = new Rectangle2D.Double(26*scale,PLATFORM_Y*scale,PLATFORM_WIDTH*scale,PLATFORM_HEIGHT*scale);
    }

    void drawPlatform(Graphics2D g2d){
        g2d.fill(platform);
    }

    void createBall(){
        ball = new Ellipse2D.Double((platform.getX()/scale + PLATFORM_WIDTH/2)*scale,(PLATFORM_Y - 1)*scale,scale,scale);
    }

    void drawBall(Graphics2D g2d){
        g2d.fill(ball);
    }

    void createBricks(){
        int x = BRICK_X,y = BRICK_Y;
        for(int i = 0;i<bricks.length;i++,y+=Brick.HEIGHT,x = BRICK_X)
            for(int j = 0;j<bricks[0].length;j++,x+=Brick.WIDTH)
                bricks[i][j] = Brick.getRandomBrick(x,y,scale);
    }

    void drawBricks(Graphics2D g2d){
        for(int i = 0;i<bricks.length;i++)
            for(int j = 0;j<bricks[0].length;j++)
                if(bricks[i][j].isVisible()){
                    g2d.setColor(bricks[i][j].getColor());
                    //g2d.fillRect(bricks[i][j].x*scale,bricks[i][j].y*scale,Brick.WIDTH*scale,Brick.HEIGHT*scale);
                    g2d.fill(bricks[i][j]);
                }
        g2d.setColor(Color.WHITE);
    }

    void refreshColor(){
        for(int i = 0;i<bricks.length;i++)
            for(int j = 0;j<bricks[0].length;j++)
                bricks[i][j].changeColor();
    }

    void drawHeading(){
        heading.setText(Integer.toString(count));
        add(heading);
    }

    boolean detectCollision(){
        double lineThickness = scale/1.5;
        //platform collision
        if(ball.intersects(platform)){
            platformCollision();
            return true;
        }
        //brick collision
        else if(ball.intersects(new Rectangle2D.Double(BRICK_X*scale,BRICK_Y*scale,bricks[0].length*Brick.WIDTH*scale,bricks.length*Brick.HEIGHT*scale))){
            brickCollision();
            return true;
        }
        //left/right boundry collision
        else if((ball.intersects(BOUNDRY_LEFT*scale,BOUNDRY_TOP*scale,lineThickness,HEIGHT*scale)) || (ball.intersects(BOUNDRY_RIGHT*scale,BOUNDRY_TOP*scale,lineThickness,HEIGHT*scale))){
            verticalCollision();
            return true;
        }
        //top boundry collision
        else if(ball.intersects(BOUNDRY_LEFT*scale,BOUNDRY_TOP*scale,WIDTH*scale,lineThickness)){
            horizontalCollision();
            return true;
        }
        //bottom boundry collision
        else if(ball.intersects(BOUNDRY_LEFT*scale,BOUNDRY_BOTTOM*scale,WIDTH*scale,HEIGHT*scale)){
            //endGame();
            horizontalCollision();
            return true;
        }
        return false;
    }

    void movePlatform(){
        int x = (int)platform.getX() / scale;
        x += platformVx;
        if(x < BOUNDRY_LEFT || x > BOUNDRY_RIGHT - (platform.getWidth()/scale))
            x -= platformVx;
        platform.setRect(x*scale,platform.getY(),platform.getWidth(),platform.getHeight());
    }

    void startBall(double vx, double vy){
        ballVx = vx;
        ballVy = vy;
    }

    void pauseBall(){
        ballVx = 0;
        ballVy = 0;
    }

    void moveBall(){
        ball.setFrame(ball.getX() + (ballVx*scale),ball.getY() + (ballVy*scale),scale,scale);
    }

    void increaseBallVelocity(){
        ballVelocity += 0.02;
    }

    void decreaseBallVelocity(){
        ballVelocity -= 0.02;
    }

    void platformCollision(){
        double x = platform.getX(),y = platform.getY();
        double angle = Math.toRadians(-90);
        //leftmost
        if(ball.intersects(x,y,scale,scale)){
            angle = Math.toRadians(-135);
        }
        //leftcentre
        else if(ball.intersects(x + scale,y,2*scale,scale)){
            angle = Math.toRadians(-120);
        }
        //centre
        else if(ball.intersects(x + (3*scale),y,scale,scale)){
            angle = Math.toRadians(-90);
        }
        //right-centre
        else if(ball.intersects(x + (4*scale),y,2*scale,scale)){
            angle = Math.toRadians(-60);
        }
        //rightmost
        else if(ball.intersects(x + (6*scale),y,scale,scale)){
            angle = Math.toRadians(-45);
        }
        ballVx = ballVelocity * Math.cos(angle);
        ballVy = ballVelocity * Math.sin(angle);
    }

    // void platformCollision(){
    // double x = platform.getX(),y = platform.getY();
    // double angle = -90;
    // if(ball.intersects(x,y,scale*2,scale)){
    // ballVx = -ballVelocity;
    // }
    // //leftcentre
    // else if(ball.intersects(x + 2*scale,y,3*scale,scale)){

    // }
    // //centre
    // else if(ball.intersects(x + (5*scale),y,2*scale,scale)){
    // ballVx = ballVelocity;
    // }
    // ballVx = ballVelocity * Math.cos(angle);
    // ballVy = -ballVelocity;
    // }

    void verticalCollision(){
        ballVx *= -1;
    }

    void horizontalCollision(){
        ballVy *= -1;
    }

    // void brickCollision(){

    // for(int j = 0,i = bricks.length - 1;j<bricks[0].length;j++,i = bricks.length - 1){
    // while(i>=0 && !bricks[i][j].isVisible()){
    // i--;
    // }
    // if(i<0)
    // continue;
    // if(ball.intersects(bricks[i][j])){
    // horizontalCollision();
    // bricks[i][j].setVisible(false);
    // count++;
    // return;
    // }
    // }
    // }
    void brickCollision(){
        //Outer:
        for(int i = bricks.length - 1;i>=0;i--){
            Inner:
            for(int j = 0;j<bricks[0].length;j++){
                if(!bricks[i][j].isVisible()){
                    continue Inner;
                }
                if(ball.intersects(bricks[i][j])){
                    horizontalCollision();
                    bricks[i][j].setVisible(false);
                    count++;
                    return;
                }
            }
        }
    }

    void endGame(){
        running = false;
    }

    public void keyPressed(KeyEvent event){
        //System.out.println("keyPressed");
        int keyCode = event.getKeyCode();
        if(keyCode == KeyEvent.VK_RIGHT){
            platformVx = 2;
        }
        else if(keyCode == KeyEvent.VK_LEFT){
            platformVx = -2;
        }
        else if(keyCode == KeyEvent.VK_E){
            System.exit(0);
        }
        else if(keyCode == KeyEvent.VK_C){
            refreshColor();
        }
        else if(keyCode == KeyEvent.VK_O){
            startBall(0,ballVelocity);
        }
        else if(keyCode == KeyEvent.VK_P){
            pauseBall();
        }
        else if(keyCode == KeyEvent.VK_Z){
            increaseBallVelocity();
        }
        else if(keyCode == KeyEvent.VK_X){
            decreaseBallVelocity();
        }
        else if(keyCode == KeyEvent.VK_W){
            ball.setFrame(ball.getX(),ball.getY() - (scale),scale,scale);
        }
        else if(keyCode == KeyEvent.VK_A){
            ball.setFrame(ball.getX() - (scale),ball.getY(),scale,scale);
        }
        else if(keyCode == KeyEvent.VK_S){
            ball.setFrame(ball.getX() ,ball.getY() + (scale),scale,scale);
        }
        else if(keyCode == KeyEvent.VK_D){
            ball.setFrame(ball.getX() + (scale),ball.getY(),scale,scale);
        }
        movePlatform();
    }

    public void keyReleased(KeyEvent event){
        //System.out.println("keyReleased");
        platformVx = 0;
    }

    public void keyTyped(KeyEvent event){
        //System.out.println("keyTyped");
    }
}
