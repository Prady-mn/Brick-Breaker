import javafx.application.Application;
import javafx.event.EventHandler;
////////import javafx.scene.input.KeyCode;
//import javafx.scene.input.
//////import javafx.event.Event;
import javafx.scene.Scene;
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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
///////import javafx.stage.Screen;
import javafx.animation.Timeline;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
///////import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.util.Duration;
import static lib.Constants.*;


public class BrickBreakerV3 extends Application {
    
    int count;
    double platformDx, ballVelocity, ballDx, ballDy;

    Pane pane;
    Circle ball;
    Rectangle[][] bricks;
    Rectangle platform;
    Shape block;
    Line topLine, bottomLine, rightLine, leftLine;
    int scale;
    Timeline platformTimeline, ballTimeline;
    Button rightButton, leftButton,colorChangerButton,restartButton;
    boolean cheats, running;
    //TextFlow topHeading, bottomHeading;
    Text scoreText;
    ImageView endScreen;
    CheckBox coloring;

    public BrickBreakerV3() {
        scale = 20;
        bricks = new Rectangle[6][10];
        count = 6 * 10;
        platformDx = 0;
        ballVelocity = BALL_VELOCITY;
        running = false;
        cheats = true;
    }

    public static void main(String args[]) {
    //     System.out.println("testing");
    //     BrickBreakerV3 ob = new BrickBreakerV3();
    //     ob.start(new Stage());
        launch(args);
    }


    @Override
    public void start(Stage stage) {
        // creating pane for all elements
        pane = new Pane();
        pane.setStyle("-fx-background-color: black;");
        //pane.getTransforms().add(new Scale(10,10));
        
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
        pane.getChildren().add(colorChangerButton);
        pane.getChildren().add(restartButton);

        // headings
        createHeadings();
        

        //create Scene
        Scene scene = new Scene(pane, Color.BLACK);
        scene.setOnKeyPressed(controls);
        scene.setOnKeyReleased((event) -> {
            platformDx = 0;
            ballTimeline.play();
        });
        // create stage
        stage.setTitle("");
        stage.setScene(scene);
        
        stage.show();
    }

    void setScale() {

    }

    void createPlatform() {
        platform = new Rectangle(26 * scale, PLATFORM_Y * scale, PLATFORM_WIDTH * scale, PLATFORM_HEIGHT * scale);
        platform.setFill(Color.WHITE);
        platform.setCache(true);
        platformTimeline = new Timeline(new KeyFrame(Duration.millis(FRAME_RATE/2), (event) -> {
            movePlatform();
        }));
        platformTimeline.setAutoReverse(false);
        platformTimeline.setCycleCount(Timeline.INDEFINITE);

    }

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

    void createBricks() {
        int x = BRICK_X, y = BRICK_Y;
        block = new Rectangle(0, 0, 0, 0);
        for (int i = 0; i < bricks.length; i++, y += BRICK_HEIGHT, x = BRICK_X)
            for (int j = 0; j < bricks[0].length; j++, x += BRICK_WIDTH) {
                bricks[i][j] = new Rectangle(x * scale, y * scale, BRICK_WIDTH * scale, BRICK_HEIGHT * scale);
                bricks[i][j].setVisible(true);
                bricks[i][j].setCache(true);
                bricks[i][j].setFill(Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255),(int) (Math.random() * 255)));
                // bricks[i][j].setFill(Color.PINK);        
                block = Shape.union(block, bricks[i][j]);
            }
        block.setVisible(false);
    }

    void refreshColor() {

        for (int i = 0; i < bricks.length; i++)
            for (int j = 0; j < bricks[0].length; j++) {
                FillTransition refresh = new FillTransition();
                refresh.setCycleCount(1);
                refresh.setAutoReverse(false);
                refresh.setShape(bricks[i][j]);
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
        topLine.setCache(true);

        // LEFT LINE
        leftLine = new Line(BOUNDRY_LEFT * scale, BOUNDRY_TOP * scale, BOUNDRY_LEFT * scale, HEIGHT * scale);
        leftLine.setStroke(Color.WHITE);
        // topLine.getStrokeDashArray().addAll(20d,10d);
        leftLine.setStrokeWidth(scale / 3);
        leftLine.setCache(true);

        // RIGHT LINE
        rightLine = new Line(BOUNDRY_RIGHT * scale, BOUNDRY_TOP * scale, BOUNDRY_RIGHT * scale, HEIGHT * scale);
        rightLine.setStroke(Color.WHITE);
        // topLine.getStrokeDashArray().addAll(20d,10d);
        rightLine.setStrokeWidth(scale / 3);
        rightLine.setCache(true);

        // BOTTOM LINE
        bottomLine = new Line(BOUNDRY_LEFT * scale, BOUNDRY_BOTTOM * scale, BOUNDRY_RIGHT * scale,
                BOUNDRY_BOTTOM * scale);
        bottomLine.setStroke(Color.RED);
        bottomLine.setStrokeWidth(scale / 3);
        bottomLine.setCache(true);
    }

    void createButtons() {
        ImageView 
            rightImg = new ImageView(new Image("icons/rightButtonIcon.png")),
            leftImg = new ImageView(new Image("icons/leftButtonIcon.png")),
            colorImg = new ImageView(new Image("icons/colorChangerIcon.png")),
            restartImg = new ImageView(new Image("icons/restartButtonIcon.png"));

        rightImg.setFitWidth(IMAGE_SIZE * scale);
        rightImg.setFitHeight(IMAGE_SIZE * scale);
        leftImg.setFitWidth(IMAGE_SIZE * scale);
        leftImg.setFitHeight(IMAGE_SIZE * scale);
        colorImg.setFitHeight(4*scale);
        colorImg.setFitWidth(4*scale);
        restartImg.setFitHeight(4*scale);
        restartImg.setFitWidth(5*scale);

        rightButton = new Button();
        rightButton.setFocusTraversable(false);
        rightButton.setGraphic(rightImg);
        rightButton.setShape(new Circle());
        rightButton.setLayoutX(RIGHT_BUTTON_X * scale);
        rightButton.setLayoutY(BUTTON_Y * scale);
        rightButton.setOnMousePressed((event) -> {
            if(!running){
                runGame();
            }
            platformDx = 1;
        });
        rightButton.setOnMouseReleased((event) -> {
            platformDx = 0;
        });
        rightButton.setOnTouchPressed((event) -> {
            if(!running){
                runGame();
            }
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
            if(!running){
                runGame();
            }
            platformDx = -1;
        });
        leftButton.setOnMouseReleased((event) -> {
            platformDx = 0;
        });
        leftButton.setOnTouchPressed((event) -> {
            if(!running){
               runGame();
            }
            platformDx = -1;
        });
        leftButton.setOnTouchReleased((event) -> {
            platformDx = 0;
        });

        colorChangerButton = new Button();
        colorChangerButton.setFocusTraversable(false);
        colorChangerButton.setGraphic(colorImg);
        colorChangerButton.setShape(new Circle());
        colorChangerButton.setLayoutX((RIGHT_BUTTON_X-5) * scale);
        colorChangerButton.setLayoutY(BUTTON_Y * scale); 
        colorChangerButton.setOnMousePressed((event) -> {refreshColor();});
        colorChangerButton.setOnTouchPressed((event) -> {refreshColor();});

        restartButton = new Button();
        restartButton.setFocusTraversable(false);
        restartButton.setGraphic(restartImg);
        restartButton.setShape(new Circle());
        restartButton.setLayoutX((LEFT_BUTTON_X+6) * scale);
        restartButton.setLayoutY(BUTTON_Y * scale); 
        restartButton.setOnMousePressed((event) -> {restartGame();;});
        restartButton.setOnTouchPressed((event) -> {restartGame();});
    }

    void createHeadings() {
        ImageView heading = new ImageView(new Image("icons/headingImg.png"));
        heading.setX(BOUNDRY_LEFT*scale);
        heading.setY(0);
        heading.setFitWidth((BOUNDRY_RIGHT-BOUNDRY_LEFT)*scale);
        heading.setFitHeight(BOUNDRY_TOP * scale);

        scoreText = new Text("PRESS ARROWS TO START");
        scoreText.setFill(Color.WHITE);
        scoreText.setTextAlignment(TextAlignment.CENTER);
        scoreText.setFont(new Font("Berlin Sans FB",2*scale));
        scoreText.setX((WIDTH*scale - scoreText.getLayoutBounds().getWidth())/2);
        scoreText.setY((BOUNDRY_BOTTOM+2)*scale);
        scoreText.setCache(true);

        endScreen = new ImageView();
        endScreen.setX(BRICK_X*scale);
        endScreen.setY(BRICK_Y*scale);
        endScreen.setFitWidth((BRICK_WIDTH*10)*scale);
        endScreen.setFitHeight((BRICK_HEIGHT*6) * scale);

        pane.getChildren().addAll(heading,scoreText,endScreen);
    }

    void createCheckBox() {
        coloring = new CheckBox("SURPRISE");
        coloring.setIndeterminate(false);
        coloring.setLayoutX(WIDTH / 2 * scale);
        coloring.setLayoutY((BOUNDRY_BOTTOM + 2) * scale);
        coloring.setFocusTraversable(false);
        coloring.setCache(true);
        // coloring.setBackground(Color.WHITE);
    }

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
            endGame(false);
            //horizontalCollision();
            return true;
        }
        return false;
    }

    void platformCollision() {
        double x = platform.getX(), dx = platform.getWidth() / 7,ballX = ball.getCenterX(),angle = Math.toRadians(-90);;
        //leftmost
        if(ballX < x + (dx)){
        angle = Math.toRadians(-135);
        }
        //leftcentre
        else if(ballX < x + (3*dx)){
        angle = Math.toRadians(-120);
        }
        //centre
        else if(ballX < x + (4*dx)){
        angle = Math.toRadians(-90);
        }
        //right-centre
        else if(ballX < x + (6*dx)){
        angle = Math.toRadians(-60);
        }
        //rightmost
        else{
        angle = Math.toRadians(-45);
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
                    updateScore();
                    return;
                }
            }
        }
    }

    void movePlatform() {
        double x = platform.getX() / scale + (platformDx);
        if (x < BOUNDRY_LEFT || x + PLATFORM_WIDTH > BOUNDRY_RIGHT) {
            return;
        }
        x *= scale;
        platform.setX(x);
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

    void updateScore(){
        scoreText.setText("Bricks Left: "+count);
        if(count==0){
            endGame(true);
        }
    }

    void endGame(boolean win) {
        // System.out.println("Game has ended");
        // running = false;
        // pane.fireEvent(new
        // KeyEvent(KeyEvent.KEY_PRESSED,"E","",KeyCode.E,false,false,false,false));
        // running = false;
        pauseBall();
        
        endScreen.setImage(new Image(("icons/"+(win?"win":"loss")+"ScreenImg.png")));
    }

    void restartGame(){
        pauseBall();
        resetBall();
        running = false;
        block = new Rectangle(0, 0, 0, 0);
        for (Rectangle temp1[] : bricks)
            for (Rectangle temp2 : temp1){
                temp2.setVisible(true);
                block = Shape.union(block,temp2);
                temp2.setFill(Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255),
                (int) (Math.random() * 255)));
            }
        count = 6*10;
        updateScore();
        endScreen.setImage(null);
    }

    void runGame(){
        startBall(0, ballVelocity);
        updateScore();
        running = true;
    }

    EventHandler<KeyEvent> controls = event -> {
        if (!running) {
            runGame();
        }
        switch (event.getCode()) {
            case RIGHT:
                platformDx = 1;
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
                case Q:
                    resetBall();
                    break;
                case W:
                    ball.setCenterX(ball.getCenterX());
                    ball.setCenterY(ball.getCenterY() - (scale));
                    break;
                case A:
                    ball.setCenterX(ball.getCenterX() - (scale));
                    ball.setCenterY(ball.getCenterY());
                    break;
                case S:
                    ball.setCenterX(ball.getCenterX());
                    ball.setCenterY(ball.getCenterY() + (scale));
                    break;
                case D:
                    ball.setCenterX(ball.getCenterX() + (scale));
                    ball.setCenterY(ball.getCenterY());
                    break;
                case R:
                    restartGame();
                    break;
                default:
                    break;
            }
    };
}
