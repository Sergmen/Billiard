package sample;

class Ball  {
    int xBall, yBall;
    double velocityX,velocityY;
    boolean cueBall;

    Ball(int xBall, int yBall, double velocityX, double velocityY) {
        this.xBall = xBall;
        this.yBall = yBall;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.cueBall=false;

    }


    boolean standStill(){
        if (velocityX==0 && velocityY==0) return true;
        else return false;
    }

    void changeDirectionX(){
        velocityX = -velocityX;

    }

    void changeDirectionY(){
        velocityY = -velocityY;

    }

    void bounce(Ball ball, int dX, int dY, double d){


        double cos = dX/d;
        double sin = dY/d;

        double dt=0;
        if (d<Main.dot_size) {
            double Vn1 = this.velocityX * cos + this.velocityY * sin;
            double Vn2 = ball.velocityX * cos + ball.velocityY * sin;
            dt = Math.abs((Main.dot_size - d) / (Vn1 - Vn2));

            ball.xBall -= ball.velocityX * dt;
            ball.yBall -= ball.velocityY * dt;

            this.xBall -= this.velocityX * dt;
            this.yBall -= this.velocityY * dt;
        }




        double Vn1 = this.velocityX * cos + this.velocityY * sin;
        double Vt1 = -this.velocityX * sin + this.velocityY * cos;
        double Vn2 = ball.velocityX * cos + ball.velocityY * sin;
        double Vt2 = -ball.velocityX * sin + ball.velocityY * cos;

        double V = Vn1;
        Vn1 = Vn2;
        Vn2 = V;

        ball.velocityX = (int) (Vn2 * cos - Vt2 * sin);
        ball.velocityY = (int) (Vn2 * sin + Vt2 * cos);

        this.velocityX = (int) (Vn1 * cos - Vt1 * sin);
        this.velocityY = (int) (Vn1 * sin + Vt1 * cos);


        ball.xBall += ball.velocityX * dt;
        ball.yBall += ball.velocityY * dt;

        this.xBall += this.velocityX * dt;
        this.yBall += this.velocityY * dt;





        dX = this.xBall - ball.xBall;
        dY = this.yBall - ball.yBall;
        d =  Math.sqrt(dX * dX + dY * dY);


        if  (d<Main.dot_size) {


            ball.xBall += ball.velocityX * dt;
            ball.yBall += ball.velocityY * dt;

            this.xBall += this.velocityX * dt;
            this.yBall += this.velocityY * dt;


        }

    }

    void move(){

        if (velocityX!=0|| velocityY!=0) {

            xBall=xBall+(int) Math.round(velocityX * Main.dt);
            yBall=yBall+(int) Math.round(velocityY * Main.dt);

            double velocity = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
            double cos = Math.abs(velocityX / velocity);
            double sin = Math.abs(velocityY / velocity);

            if (velocity > 40) {
                velocity = velocity - Main.acceleration * Main.dt;
                velocityX = Math.signum(velocityX) * cos * velocity;
                velocityY = Math.signum(velocityY) * sin * velocity;
            } else {
                velocityX = 0;
                velocityY = 0;
            }
        }
    }
}
