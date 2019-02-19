package tank.objects;



import tank.ID;
import tank.background.TileLayer;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Iterator;


public class Tank extends GameObject {

    private int angle;

    private final double R = 2.1;
    private final double ROTATIONSPEED = 1.0001;

    private TileLayer tileLayer; // this is for tank to know where walls are
    Rectangle bulletHearts;

    private BufferedImage img, bulletimg;
    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;
    private boolean ShootingPressed;
    private TileLayer tlayer;
    //private BulletControl bc1;
    private tank.objects.Bullet tempbullet;
    Graphics g;
    private GameBoard game;
    private ID Bullet;
    private ObjectUpdater objectUpdater;
    private int powerbar, speed =5;
    private int heartHealth=10;
    private int lives = 3;
    private boolean running, isPower;
    private boolean isAlive;
    private int wallLife = 3;
    private GameStatus tempH;
    private Wall tempW;


    Tank(int x, int y, ID Tank, int vx, int vy, int angle, BufferedImage img, TileLayer tlayer, GameBoard game, ObjectUpdater objectUpdater) {
        super(x,y,Tank);
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.img = img;
        this.game = game;
        this.angle = angle;
        this.tlayer = tlayer;
        this.objectUpdater = objectUpdater;
        this.heartHealth = 10;
        this.lives = 3;
        this.powerbar = 0;
        this.isAlive = true;
        this.isPower = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getPowerbar() {
        return powerbar;
    }

    public void setPowerbar(int powerbar) {
        this.powerbar = powerbar;
    }

    public int getHeartHealth() {
        return heartHealth;
    }

    public void setHeartHealth(int heartHealth) {
        this.heartHealth = heartHealth;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    void toggleUpPressed() {
        this.UpPressed = true;
    }

    void toggleDownPressed() {
        this.DownPressed = true;
    }

    void toggleRightPressed() {
        this.RightPressed = true;
    }

    void toggleLeftPressed() {
        this.LeftPressed = true;
    }

    void toggleShootingPressed() {
        this.ShootingPressed = true;
    }


    void unToggleUpPressed() {
        this.UpPressed = false;
    }

    void unToggleDownPressed() {
        this.DownPressed = false;
    }

    void unToggleRightPressed() {
        this.RightPressed = false;
    }

    void unToggleLeftPressed() {
        this.LeftPressed = false;
    }

    void unToggleShootingPressed() {
        this.ShootingPressed = false;
        System.out.println("Fire");
        this.shootBullets(this);
    }

    public void tick() {


        if (this.UpPressed) {
            this.moveForwards();
        }
        if (this.DownPressed) {
            this.moveBackwards();
        }

        if (this.LeftPressed) {
            this.rotateLeft();
        }
        if (this.RightPressed) {
            this.rotateRight();
        }
        if (this.ShootingPressed) {
        }
        collision();


    }

    public int getImgW() {
        return this.img.getWidth();
    }
    public int getImgH() {
        return this.img.getWidth();
    }

    public void render(Graphics g) {

        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;

        g2d.drawImage(this.img, rotation, null);


    }
    public int getAngle(){
        return this.angle;
    }

    public Rectangle getRect() {

        return new Rectangle(x,y, 50,50);
    }

    private void rotateLeft() {

        this.angle -= this.ROTATIONSPEED;

    }

    private void rotateRight() {

        this.angle += this.ROTATIONSPEED;


    }

    private void moveBackwards() {

        vx = (int) Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = (int) Math.round(R * Math.sin(Math.toRadians(angle)));
        x -= vx;
        y -= vy;
        checkBorder();

    }

    private void moveForwards() {
        vx = (int) Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = (int) Math.round(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
        checkBorder();

    }

   private void checkBorder() {
        if (x < 20) {
            x = 20;
        }
        if (x >= GameBoard.SCREEN_WIDTH - 88) {
            x = GameBoard.SCREEN_WIDTH - 88;
        }
        //check height
        if (y < 40) {
            y = 40;
        }
        if (y >= GameBoard.SCREEN_HEIGHT - 128) {
            y = GameBoard.SCREEN_HEIGHT - 128;
        }

   }

    //tank class needs game and bullet control class
    protected void shootBullets(Tank t) {

        vx = t.vx;
        vy = t.vy;
        this.game.loadTankBullets(t, vx, vy, isPower, this.objectUpdater);

    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", angle=" + angle;
    }


    private void collision (){
        GameObject tempObj;

        Iterator<GameObject> it = this.objectUpdater.getObj().iterator();
        while (it.hasNext()) {
            tempObj = it.next();

            if (tempObj.id == ID.Wall ){
                if(getRect().intersects(tempObj.getRect())){

                    x -= vx;
                    y -= vy;
                }
            }else if (tempObj.id == ID.BreakableWall){
                if(getRect().intersects(tempObj.getRect())){
                    x -= vx;
                    y -= vy;

                    //remove object
                    this.wallLife -= 2;
                    if (this.wallLife <=0){
                        it.remove();
                        this.heartHealth--;
                        if (heartHealth <=0){
                            this.heartHealth = 0;
                            this.lives--;
                            if (this.lives <=0){
                                this.lives = 0;
                                game.running = false;
                                System.out.println("Game Over");
                            }

                        }

                    }

                }
            }else if (tempObj.id == ID.PowerUp){
                if(getRect().intersects(tempObj.getRect())){
                    //initial value 100
                    x -= vx;
                    y -= vy;
                    if (this.id == id.Tank1){
                        this.powerbar += 10;
                        this.isPower = true;
                        if (this.powerbar >=30){
                            this.heartHealth++;
                            this.powerbar = 0;
                            this.isPower = false;
                            if (this.heartHealth >=15){
                                this.lives++;
                                this.heartHealth = 0;
                            }
                        }


                        }else if (this.id == id.Tank2){
                        this.powerbar +=10;
                        this.isPower = true;
                            if (this.powerbar >=30){
                                this.heartHealth++;
                                this.powerbar = 0;
                                this.isPower = false;

                                if (this.heartHealth >=15){
                                    this.lives++;
                                    this.heartHealth = 0;
                                }
                            }

                    }
                    it.remove();
                }
            }else if (tempObj.id == ID.Tank1 || tempObj.id == ID.Tank2){
            }



        }

    }

}
