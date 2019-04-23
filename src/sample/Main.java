package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import java.util.ArrayList;


public class Main extends Application {
    private final static int xSize=887+54, ySize=444+53, nullXSize=54,nullYSize=53, up=1, right=2, down=3, left=4, enter=5, space = 6, escape=7;
    public final static int dot_size=34;
    public final static double dt=0.025, acceleration=250;
    private static ArrayList<Ball> Balls = new ArrayList<>();
    private static ArrayList<Ball> BallsOut = new ArrayList<>();
    int delay=40, dir=6;
    Canvas canvas;
    GraphicsContext gc;
    Thread game;

    public static Cue cue = new Cue(0,60,200,200);

    @Override
    public void start(Stage primaryStage) throws Exception {

        StackPane root = new StackPane();
        canvas=new Canvas(995,550);
        gc=canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);

        Image image = new Image("table.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(550);
        imageView.setFitWidth(995);
        root.getChildren().add(imageView);
        root.getChildren().add(canvas);

        canvas.setOnKeyPressed(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent e) {
                KeyCode key=e.getCode();
                if(key.equals(KeyCode.UP)) dir=up;
                if(key.equals(KeyCode.DOWN)) dir=down;
                if(key.equals(KeyCode.LEFT)) dir=left;
                if(key.equals(KeyCode.RIGHT)) dir=right;
                if(key.equals(KeyCode.ENTER)) dir=enter;
                if(key.equals(KeyCode.SPACE)) dir=space;
                if(key.equals(KeyCode.ESCAPE)) dir=escape;

            }

        });



        Scene scene = new Scene(root, 1200, 700);

        primaryStage.setTitle("Russian billiard");
        primaryStage.setScene(scene);
        primaryStage.show();

        startGame();
    }
    private void draw(){
        gc.clearRect(0, 0, 995, 550);
        if(Balls.size()!=0){
            gc.setFill(Paint.valueOf("white"));
            for (Ball ball : Balls) {
                gc.fillOval(ball.xBall,ball.yBall, dot_size, dot_size);
            }

            if (cue.isVisible()) {
                gc.setStroke(Color.DARKGREY);
                gc.setLineWidth(10);
                double cos = Math.cos(Math.toRadians(cue.angle));
                double sin = Math.sin(Math.toRadians(cue.angle));
                if (cue.strength>0) {
                    int deltaStrengthX = (int)(cos*cue.strength/20);
                    int deltaStrengthY = (int)(sin*cue.strength/20);
                    gc.strokeLine(cue.xCue+cos*((dot_size/2)+5)+deltaStrengthX,cue.yCue+sin*((dot_size/2)+5)+deltaStrengthY,cue.xCue+cos*300+deltaStrengthX,cue.yCue+sin*300+deltaStrengthY);
                }
                else gc.strokeLine(cue.xCue+cos*((dot_size/2)+5),cue.yCue+sin*((dot_size/2)+5),cue.xCue+cos*300,cue.yCue+sin*300 );
            }
        }else{
            gc.setFill(Paint.valueOf("black"));
            gc.fillText("Game Over", xSize/2-50, ySize/2-15);
            game.stop();
        }
    }

    private void highlightBall(Ball ball){

        gc.setFill(Paint.valueOf("BLUE"));
        gc.fillOval(ball.xBall,ball.yBall, dot_size, dot_size);

    }


    private void clearHighlightBall(Ball ball){

        gc.setFill(Paint.valueOf("white"));
        gc.fillOval(ball.xBall,ball.yBall, dot_size, dot_size);

    }



    public static void main(String[] args) {
        launch(args);
    }

    private void startGame() throws Exception {

        placeBalls();
        draw();


        game=new Thread(new Runnable() {
            @Override
            public void run() {
                while (Balls.size()!=0) {

                    if (isBallsStandStill()) {
                        if (!cue.visible)selectBall();
                        else selectStrengthAngle();

                    }
                    else {
                        while ((!isBallsStandStill()&&(Balls.size()!=0))) {
                            for (Ball ball : Balls) {
                                ball.move();
                                checkCollision(ball);
                            }
                            draw();
                            if (BallsOut.size()!=0) removeBalls();

                            try {
                                Thread.sleep(delay);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                }
            }
        });
        game.start();
    }

    private void removeBalls() {

        Balls.removeAll(BallsOut);
        BallsOut.clear();
        draw();
    }

    private void checkCollision(Ball ball) {

        if ((((ball.xBall>nullXSize-10)&&(ball.xBall<nullXSize+10))&&((ball.yBall<nullYSize+10)&&(ball.yBall>nullYSize-10)))
                || ((((ball.xBall)>xSize-dot_size-10)&&((ball.xBall)<xSize-dot_size+10))&&(((ball.yBall)<ySize-dot_size+10)&&((ball.yBall)>ySize-dot_size-10)))
                || ((ball.xBall>(xSize+nullXSize)/2-10-dot_size/2)&&(ball.xBall<(xSize+nullXSize)/2+10-dot_size/2)&&(ball.yBall<nullYSize))
                || ((ball.xBall>(xSize+nullXSize)/2-10-dot_size/2)&&(ball.xBall<(xSize+nullXSize)/2+10-dot_size/2)&&(ball.yBall>ySize-dot_size))
                || ((((ball.xBall)>xSize-dot_size-10)&&((ball.xBall)<xSize-dot_size+10))&&((ball.yBall<nullYSize+10)&&(ball.yBall>nullYSize-10)))
                || (((ball.xBall>nullXSize-10)&&(ball.xBall<nullXSize+10))&&((ball.yBall<ySize-dot_size+10)&&(ball.yBall>ySize-dot_size-10))) )
            BallsOut.add(ball);

        else  {

            if (((ball.xBall + dot_size) >= xSize) && (ball.velocityX > 0)) {
                ball.xBall = xSize - dot_size;
                ball.changeDirectionX();
            }
            if (((ball.xBall) <= nullXSize) && (ball.velocityX < 0)) {
                ball.xBall = nullXSize;
                ball.changeDirectionX();
            }
            if (((ball.yBall + dot_size) >= ySize) && (ball.velocityY > 0)) {
                ball.yBall = ySize - dot_size;
                ball.changeDirectionY();
            }
            if (((ball.yBall) <= nullYSize) && (ball.velocityY < 0)) {
                ball.yBall = nullYSize;
                ball.changeDirectionY();
            }


            for (Ball ballIn : Balls) {
                if (!ballIn.equals(ball)) {
                    int dX = ballIn.xBall - ball.xBall;
                    int dY = ballIn.yBall - ball.yBall;
                    double d = Math.sqrt(dX * dX + dY * dY);
                    if ((!ballIn.standStill() || !ball.standStill()) && (d <= dot_size)) ballIn.bounce(ball, dX, dY, d);
                }


            }
        }
    }

    private void placeBalls(){


        Balls.add(new Ball(nullXSize+200,ySize/2,0, 0));


        int x = xSize/2, y = ySize/2+dot_size;
        for (int i=0; i<5; i++){
            int y0=y+(dot_size/2)*i;
            for (int j=0; j<(i+1); j++){
                y0 = y0 - dot_size-1;
                Balls.add(new Ball(x,y0,0,0));
            }
            x=x+29;
        }
    }


    private boolean isBallsStandStill(){
        for (Ball ball : Balls) {
            if (!ball.standStill()) return false;
        }
        return true;
    }

    private void selectStrengthAngle(){

        draw();

        int oldAngle=0;
        dir=0;

        while (dir!=enter){
            oldAngle = cue.angle;
            if ((dir!=0)&& (dir!=space)) {
                if (dir == up) {cue.angle=cue.angle+5; dir=0;}
                if (dir == down) {cue.angle=cue.angle-5; dir=0;}
                if (cue.angle < 0) cue.angle = 359;
                if (cue.angle>360) cue.angle=1;
                if (oldAngle != cue.angle) {
                    draw();
                }
            }
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        dir=0;
        while (dir!=enter){

            if ((dir!=0)&& (dir!=enter)) {
                if (dir == up) {cue.strength=cue.strength+50; dir=0;}
                if (dir == down) {cue.strength=cue.strength-50; dir=0;}
                if (dir == escape) {cue.strength=0; cue.angle=0; cue.visible=false; draw(); break;}
                if (cue.strength < 0) cue.strength = 0;
                if (cue.strength>1000) cue.strength=1000;
                draw();
            }
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (dir!=escape) {
            for (Ball ball : Balls) {
                if (ball.cueBall) {
                    ball.cueBall = false;
                    ball.velocityX = -cue.strength * Math.cos(Math.toRadians(cue.angle));
                    ball.velocityY = -cue.strength * Math.sin(Math.toRadians(cue.angle));
                }
            }

            cue.visible = false;
            cue.strength = 0;
            cue.angle = 0;
        }



    }

    private void selectBall() {

        int i=0,j=0; dir=0;
        highlightBall(Balls.get(0));

        while (dir!=enter){
            j=i;
            if ((dir!=0)&& (dir!=enter)) {
                if (dir == up) {i++; dir=0;}
                if (dir == down) {i--; dir=0;}
                if (i < 0) i = Balls.size()-1;
                if (i > Balls.size()) i = 0;
                if (i != j) {
                    clearHighlightBall(Balls.get(j));
                    highlightBall(Balls.get(i));
                }
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Balls.get(i).cueBall=true;
        cue.xCue = Balls.get(i).xBall+(dot_size/2);
        cue.yCue = Balls.get(i).yBall+(dot_size/2);
        cue.visible=true;

    }



}