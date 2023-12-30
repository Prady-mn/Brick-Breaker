package oldVersions;
import javafx.application.Application;
import javafx.event.EventHandler;
////////import javafx.scene.input.KeyCode;
//import javafx.scene.input.
//////import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.Group;
//import javafx.scene.Node;
//import javafx.geometry.Insets;
import javafx.scene.control.Button;
///////import javafx.scene.control.Label;
//import javafx.scene.layout.GridPane;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.*;
//import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
///////import javafx.stage.Screen;
import javafx.animation.Timeline;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
///////import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.util.Duration;
import static lib.Constants.*;

public class BreakBricksV2FX extends Application {
    
    int count;
    double platformDx, ballVelocity, ballDx, ballDy;
    // BrickFx ;
    Group pane;
    Circle ball;
    Rectangle[][] bricks;
    Rectangle platform;
    Shape block;
    Line topLine, bottomLine, rightLine, leftLine;
    int scale;
    Timeline platformTimeline, ballTimeline;
    Button rightButton, leftButton;
    boolean cheats, running;
    TextFlow topHeading, bottonHeading;
    CheckBox coloring;

    // TranslateTransition right,left;
    // KeyEvent controls;
    public BreakBricksV2FX() {
        scale = SCALE;
        bricks = new Rectangle[6][10];
        count = 6 * 10;
        platformDx = 0;
        ballVelocity = BALL_VELOCITY;
        running = false;
    }

    public static void main(String args[]) {
        // System.out.println("testing");
        // BreakBricksV2FX ob = new BreakBricksV2FX();
        // ob.start(new Stage());
        launch(args);
    }

    /**
     * The start method is the main entry point for every JavaFX application. It is
     * called after the init() method has returned and after the system is ready for
     * the application to begin running.
     *
     * @param stage the primary stage for this application.
     */
    @Override
    public void start(Stage stage) {
        // creating group for all elements
        pane = new Group();

        // pane.setScaleX(20);
        // pane.setScaleY(20);
        // pane.scaleXProperty();
        // pane.scaleYProperty();

        // Platform
        createPlatform();
        pane.getChildren().add(platform);
        platformTimeline.play();

        // Bricks
        createBricks();
        for (Rectangle temp1[] : bricks)
            for (Rectangle temp2 : temp1)
                pane.getChildren().add(temp2);

        // Ball
        createBall();
        pane.getChildren().add(ball);
        // ballTimeline.play();

        // Borders
        createBorders();
        pane.getChildren().add(topLine);
        pane.getChildren().add(leftLine);
        pane.getChildren().add(rightLine);
        pane.getChildren().add(bottomLine);

        // Buttons
        createButtons();
        pane.getChildren().add(rightButton);
        pane.getChildren().add(leftButton);

        // headings
        createHeadings();
        pane.getChildren().add(topHeading);
        // JavaFX must have a Scene (window content) inside a Stage (window)
        ////// Scene scene = new Scene(pane,WIDTH*scale,HEIGHT*scale,Color.BLACK);
        Scene scene = new Scene(pane, Color.BLACK);

        // checkbox
        createCheckBox();
        pane.getChildren().add(coloring);
        // Event handler
        // controls = new EventHandler<KeyEvent>();
        scene.setOnKeyPressed(controls);
        scene.setOnKeyReleased((event) -> {
            platformDx = 0;
            ballTimeline.play();
        });
        // Stage
        stage.setTitle("JavaFX Example");
        stage.setScene(scene);
        // Show the Stage (window)
        stage.show();
        // System.out.println("Test complete");
        cheats = true;

    }

    void setScale() {

    }

    void createPlatform() {
        platform = new Rectangle(26 * scale, PLATFORM_Y * scale, PLATFORM_WIDTH * scale, PLATFORM_HEIGHT * scale);
        platform.setFill(Color.WHITE);

        platformTimeline = new Timeline(new KeyFrame(Duration.millis(FRAME_RATE), (event) -> {
            movePlatform();
        }));
        platformTimeline.setAutoReverse(false);
        platformTimeline.setCycleCount(Timeline.INDEFINITE);

    }

    // void drawPlatform(Graphics2D g2d){
    // g2d.fill(platform);
    // }

    void createBall() {
        ball = new Circle();
        ball.setRadius(scale / 1.5);
        ball.setCenterX((platform.getX() + PLATFORM_WIDTH * scale / 2));
        ball.setCenterY((PLATFORM_Y - 1) * scale - ball.getRadius());
        ball.setFill(Color.WHITE);
        ball.setCache(true);

        ballTimeline = new Timeline(new KeyFrame(Duration.millis(FRAME_RATE), (event) -> {
            detectCollision();
            moveBall();
        }));
        ballTimeline.setAutoReverse(false);
        ballTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    // void drawBall(Graphics2D g2d){
    // g2d.fill(ball);
    // }

    void createBricks() {
        int x = BRICK_X, y = BRICK_Y;
        block = new Rectangle(0, 0, 0, 0);
        for (int i = 0; i < bricks.length; i++, y += BRICK_HEIGHT, x = BRICK_X)
            for (int j = 0; j < bricks[0].length; j++, x += BRICK_WIDTH) {
                bricks[i][j] = new Rectangle(x * scale, y * scale, BRICK_WIDTH * scale, BRICK_HEIGHT * scale);
                bricks[i][j].setVisible(true);
                bricks[i][j].setFill(Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255),
                        (int) (Math.random() * 255)));
                block = Shape.union(block, bricks[i][j]);
            }
        block.setVisible(false);
    }

    // void drawBricks(Graphics2D g2d){
    // for(int i = 0;i<bricks.length;i++)
    // for(int j = 0;j<bricks[0].length;j++)
    // if(bricks[i][j].isVisible()){
    // g2d.setColor(bricks[i][j].getColor());
    // //g2d.fillRect(bricks[i][j].x*scale,bricks[i][j].y*scale,Brick.WIDTH*scale,Brick.HEIGHT*scale);
    // g2d.fill(bricks[i][j]);
    // }
    // //g2d.setColor(Color.WHITE);
    // }

    void refreshColor() {

        for (int i = 0; i < bricks.length; i++)
            for (int j = 0; j < bricks[0].length; j++) {
                // bricks[i][j].setFill(Color.rgb((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));
                FillTransition refresh = new FillTransition();
                refresh.setCycleCount(1);
                refresh.setAutoReverse(false);
                refresh.setShape(bricks[i][j]);
                // refresh.setFromValue(bricks[i][j].getFill());
                refresh.setToValue(Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255),
                        (int) (Math.random() * 255)));
                refresh.play();
            }
    }

    void createBorders() {
        // TOP LINE
        topLine = new Line(0, BOUNDRY_TOP * scale, WIDTH * scale, BOUNDRY_TOP * scale);
        topLine.setStroke(Color.WHITE);
        // topLine.getStrokeDashArray().addAll(20d,10d);
        topLine.setStrokeWidth(scale / 3);

        // LEFT LINE
        leftLine = new Line(BOUNDRY_LEFT * scale, BOUNDRY_TOP * scale, BOUNDRY_LEFT * scale, HEIGHT * scale);
        leftLine.setStroke(Color.WHITE);
        // topLine.getStrokeDashArray().addAll(20d,10d);
        leftLine.setStrokeWidth(scale / 3);

        // RIGHT LINE
        rightLine = new Line(BOUNDRY_RIGHT * scale, BOUNDRY_TOP * scale, BOUNDRY_RIGHT * scale, HEIGHT * scale);
        rightLine.setStroke(Color.WHITE);
        // topLine.getStrokeDashArray().addAll(20d,10d);
        rightLine.setStrokeWidth(scale / 3);

        // BOTTOM LINE
        bottomLine = new Line(BOUNDRY_LEFT * scale, BOUNDRY_BOTTOM * scale, BOUNDRY_RIGHT * scale,
                BOUNDRY_BOTTOM * scale);
        bottomLine.setStroke(Color.RED);
        bottomLine.setStrokeWidth(scale / 3);
    }

    void createButtons() {
        ImageView rightImg = new ImageView(new Image("icons/rightButtonIcon.png")),
                leftImg = new ImageView(new Image("icons/leftButtonIcon.png"));
        rightImg.setFitWidth(IMAGE_SIZE * scale);
        rightImg.setFitHeight(IMAGE_SIZE * scale);
        leftImg.setFitWidth(IMAGE_SIZE * scale);
        leftImg.setFitHeight(IMAGE_SIZE * scale);

        rightButton = new Button();
        rightButton.setFocusTraversable(false);
        rightButton.setGraphic(rightImg);
        rightButton.setShape(new Circle());
        rightButton.setLayoutX(RIGHT_BUTTON_X * scale);
        rightButton.setLayoutY(BUTTON_Y * scale);
        rightButton.setOnMousePressed((event) -> {
            platformDx = 1;
        });
        rightButton.setOnMouseReleased((event) -> {
            platformDx = 0;
        });
        rightButton.setOnTouchPressed((event) -> {
            platformDx = 1;
        });
        rightButton.setOnTouchReleased((event) -> {
            platformDx = 0;
        });

        leftButton = new Button();
        leftButton.setFocusTraversable(false);
        leftButton.setGraphic(leftImg);
        leftButton.setShape(new Circle());
        leftButton.setLayoutX(LEFT_BUTTON_X * scale);
        leftButton.setLayoutY(BUTTON_Y * scale);
        leftButton.setOnMousePressed((event) -> {
            platformDx = -1;
        });
        leftButton.setOnMouseReleased((event) -> {
            platformDx = 0;
        });
        leftButton.setOnTouchPressed((event) -> {
            platformDx = -1;
        });
        leftButton.setOnTouchReleased((event) -> {
            platformDx = 0;
        });
    }

    void createHeadings() {
        topHeading = new TextFlow();
        topHeading.setLineSpacing(2);

        Text heading = new Text();
        heading.setFill(Color.WHITE);
        heading.setText("BRICK BREAKER");
        heading.setTextAlignment(TextAlignment.CENTER);
        // heading.setX(2*scale);
        // heading.setY(3*scale);
        heading.setFont(new Font(50));

        Text name = new Text("\n - PRADYUMN AGARWAL");
        name.setFill(Color.WHITE);
        name.setFont(new Font(20));
        name.setTextAlignment(TextAlignment.RIGHT);

        topHeading.setMaxWidth(WIDTH * scale);
        topHeading.setMaxHeight(BOUNDRY_TOP * scale);
        // topHeading.setLayoutX((WIDTH*scale - topHeading.comp)/2);
        // topHeading.setLayoutY(0.2*scale);
        topHeading.getChildren().addAll(heading, name);
        // topHeading.getChildren().add(name);
    }

    void createCheckBox() {
        coloring = new CheckBox("SURPRISE");
        coloring.setIndeterminate(false);
        coloring.setLayoutX(WIDTH / 2 * scale);
        coloring.setLayoutY((BOUNDRY_BOTTOM + 2) * scale);
        coloring.setFocusTraversable(false);
        // coloring.setBackground(Color.WHITE);
    }

    /** MARKER */
    boolean detectCollision() {
        if (ball.intersects(platform.getLayoutBounds())) {
            platformCollision();
            return true;
        }
        // brick collision
        else if (ball.intersects(block.getBoundsInLocal())) {
            brickCollision();
            return true;
        }
        // left/right boundry collision
        else if (ball.intersects(leftLine.getBoundsInLocal()) || ball.intersects(rightLine.getBoundsInParent())) {
            verticalCollision();
            return true;
        }
        // top boundry collision
        else if (ball.intersects(topLine.getBoundsInLocal())) {
            horizontalCollision();
            return true;
        }
        // bottom boundry collision
        else if (ball.intersects(bottomLine.getBoundsInLocal())) {
            // pauseBall();
            // endGame();
            horizontalCollision();
            return true;
        }
        return false;
    }

    void platformCollision() {
        double x = platform.getX(), y = platform.getY(), dx = platform.getWidth() / 7;
        double angle = Math.toRadians(-90);
        System.out.println(platform.getWidth());
        // leftmost
        // if(ball.intersects(x,y,dx,scale)){
        // angle = Math.toRadians(-135);
        // System.out.println("LEFTMOST");
        // }
        // //leftcentre
        // else if(ball.intersects(x + dx,y,2*dx,scale)){
        // angle = Math.toRadians(-120);
        // System.out.println("LEFT CENTRE");
        // }
        // //centre
        // else if(ball.intersects(x + (3*dx),y,dx,scale)){
        // angle = Math.toRadians(-90);
        // System.out.println("CENTRE");
        // }
        // //right-centre
        // else if(ball.intersects(x + (4*dx),y,2*dx,scale)){
        // angle = Math.toRadians(-60);
        // System.out.println("RIGHT CENTRE");
        // }
        // //rightmost
        // else if(ball.intersects(x + (6*dx),y,dx,scale)){
        // angle = Math.toRadians(-45);
        // System.out.println("RIGHTMOST");
        // }
        if (ball.intersects(x, y, dx, scale)) {
            System.out.println("1");
        } else if (ball.intersects(x + dx, y, dx, scale)) {
            System.out.println("2");
        } else if (ball.intersects(x + (2 * dx), y, dx, scale)) {
            System.out.println("3");
        } else if (ball.intersects(x + (3 * dx), y, dx, scale)) {
            System.out.println("4");
        } else if (ball.intersects(x + (4 * dx), y, dx, scale)) {
            System.out.println("5");
        } else if (ball.intersects(x + (5 * dx), y, dx, scale)) {
            System.out.println("6");
        } else if (ball.intersects(x + (6 * dx), y, dx, scale)) {
            System.out.println("7");
        }
        ballDx = ballVelocity * Math.cos(angle);
        ballDy = ballVelocity * Math.sin(angle);
    }

    void verticalCollision() {
        ballDx *= -1;
    }

    void horizontalCollision() {
        ballDy *= -1;
    }

    void brickCollision() {
        //Outer: 
        for (int i = bricks.length - 1; i >= 0; i--) {
            Inner: for (int j = 0; j < bricks[0].length; j++) {
                if (!bricks[i][j].isVisible()) {
                    continue Inner;
                }
                if (ball.intersects(bricks[i][j].getLayoutBounds())) {
                    horizontalCollision();
                    bricks[i][j].setVisible(false);
                    block = Shape.subtract(block, bricks[i][j]);
                    count--;
                    // System.out.println(count);
                    // update count
                    if (count == 0) {
                        endGame();
                    }
                    if (coloring.isSelected()) {
                        refreshColor();
                    }
                    return;
                }
            }
        }
    }

    void movePlatform() {
        double x = platform.getX() / scale + (platformDx);
        if (x < BOUNDRY_LEFT || x + PLATFORM_WIDTH > BOUNDRY_RIGHT) {
            // x -= platformVx;
            return;
        }
        x *= scale;
        // Timeline
        // platform.setRect(x*scale,platform.getY(),platform.getWidth(),platform.getHeight());
        // platform.setLayoutX(x);
        // platform.relocate(x,platform.getY());
        platform.setX(x);
        // TranslateTransition move = new TranslateTransition();
        // move.setAutoReverse(false);
        // move.setNode(platform);
        // move.setCycleCount(1);
        // move.setByX(platformVx*scale);
        // move.play();
        // platformDx = 0;
    }

    void startBall(double vx, double vy) {
        ballDx = vx;
        ballDy = vy;
        ballTimeline.play();
    }

    void pauseBall() {
        // ballTimeline.pause();
        ballDx = 0;
        ballDy = 0;
    }

    void resetBall() {
        ball.setCenterX((platform.getX() + PLATFORM_WIDTH * scale / 2));
        ball.setCenterY((PLATFORM_Y - 1) * scale - ball.getRadius());
        ballTimeline.pause();
    }

    void moveBall() {
        // ball.setFrame(ball.getX() + (ballVx*scale),ball.getY() +
        // (ballVy*scale),scale,scale);
        // double x = ,y = ;
        // ball.relocate(x,y);
        ball.setCenterX(ball.getCenterX() + (ballDx * scale));
        ball.setCenterY(ball.getCenterY() + (ballDy * scale));
    }

    void increaseBallVelocity() {
        ballVelocity += 0.02;
    }

    void decreaseBallVelocity() {
        ballVelocity -= 0.02;
    }

    void toggleCheats() {
        cheats = !cheats;
    }

    void endGame() {
        // System.out.println("Game has ended");
        // running = false;
        // pane.fireEvent(new
        // KeyEvent(KeyEvent.KEY_PRESSED,"E","",KeyCode.E,false,false,false,false));
        // running = false;
    }

    //public void handle(Event e) {
        //
      //  System.out.println("EVENT NOT RECOGNISED");
    //}

    EventHandler<KeyEvent> controls = event -> {
        if (!running) {
            startBall(0, ballVelocity);
            running = true;
        }
        // System.out.println("EVENT RECOGNISED");
        // System.out.println(event.getText());
        // System.out.println(event.getCode());
        switch (event.getCode()) {
            case RIGHT:
                platformDx = 1;
                // movePlatformRight();
                break;
            case LEFT:
                platformDx = -1;
                break;
            case E:
                System.exit(0);
                break;
            case C:
                refreshColor();
                break;
            case I:
                toggleCheats();
                break;
            default:
                break;
        }
        if (cheats)
            switch (event.getCode()) {
                case O:
                    startBall(0, ballVelocity);
                    break;
                case P:
                    pauseBall();
                    break;
                case Z:
                    increaseBallVelocity();
                    break;
                case X:
                    decreaseBallVelocity();
                    break;
                case R:
                    resetBall();
                    break;
                case W:
                    // ball.setFrame(ball.getX(),ball.getY() - (scale),scale,scale);
                    ball.setCenterX(ball.getCenterX());
                    ball.setCenterY(ball.getCenterY() - (scale));
                    break;
                case A:
                    // ball.setFrame(ball.getX() - (scale),ball.getY(),scale,scale);
                    ball.setCenterX(ball.getCenterX() - (scale));
                    ball.setCenterY(ball.getCenterY());
                    break;
                case S:
                    // ball.setFrame(ball.getX() ,ball.getY() + (scale),scale,scale);
                    ball.setCenterX(ball.getCenterX());
                    ball.setCenterY(ball.getCenterY() + (scale));
                    break;
                case D:
                    // ball.setFrame(ball.getX() + (scale),ball.getY(),scale,scale);
                    ball.setCenterX(ball.getCenterX() + (scale));
                    ball.setCenterY(ball.getCenterY());
                    break;
                default:
                    break;

                // deafult:
                // break;
            }
        // movePlatform();
    };
}
