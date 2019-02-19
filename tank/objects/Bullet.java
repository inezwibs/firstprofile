package tank.objects;

import tank.ID;
import tank.background.TileLayer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static javax.imageio.ImageIO.read;

public class Bullet extends GameObject {

    private int vx, vy,  bulletx, bullety;
    private BufferedImage bulletimg;
    private int height, width, angle;
    private Tank t;
    private boolean isShot=false;
    private boolean powerShot=false;
    private int bulletSpeed, currentframe,endframe;
    private GameBoard game;
    private TileLayer tlayer;
    private ObjectUpdater objectUpdater;
    private final double R = 2.1;
    private final double ROTATIONSPEED = 1.0001;
    ID BulletID;
    Rectangle bulletRect;

    public Bullet (GameBoard gameBoard, Tank t, int x, int y , ID BulletID,boolean isPower, ObjectUpdater objectUpdater){
        super(x,y,BulletID);
        this.game = gameBoard;
        this.t = t;
        this.angle = t.getAngle();

        this.vx = x;//forward vector
        this.vy = y;//up vector
        this.bulletSpeed= 2;

        this.bulletx = t.getX()+ t.getImgW();//bulletxcoord +50
        this.bullety = t.getY()+ t.getImgH()/2;//bulletxcoord +25

        this.powerShot = isPower;
        this.objectUpdater = objectUpdater;

        /*
        this.BulletID = BulletID;
         */


        //grabbing image
        this.bulletimg= getImage();
        this.height = bulletimg.getHeight();
        this.width = bulletimg.getWidth();
        bulletRect = getRect();
    }

    public int getBulletx() {
        return bulletx;
    }

    public int getBullety() {
        return bullety;
    }

    public void tick(){
        //currentframe = game.getFrames();
        if (powerShot == true){
            if (t.getAngle() >0){
                //1,2,3,4,5,...
                this.angle += this.ROTATIONSPEED;
                vx = (int) Math.round(R * Math.cos(Math.toRadians(this.angle)));
                vy = (int) Math.round(R * Math.sin(Math.toRadians(this.angle)));
                this.bulletx += vx;
                this.bullety += vy;

            }else if (t.getAngle() <0){
                this.angle -= this.ROTATIONSPEED;
                vx = (int) Math.round(R * Math.cos(Math.toRadians(this.angle)));
                vy = (int) Math.round(R * Math.sin(Math.toRadians(this.angle)));
                this.bulletx += vx;
                this.bullety += vy;
            }else if (t.getAngle() ==0){

                this.bulletx += vx;
            }
        }else {//circle bullets for 5 frames

            //endframe = currentframe + 5;
            //while (currentframe != endframe) {
                if (t.getAngle() > 0) {
                    //1,2,3,4,5,...

                    vx = (int) Math.round(R * Math.cos(Math.toRadians(t.getAngle())));
                    vy = (int) Math.round(R * Math.sin(Math.toRadians(t.getAngle())));

                    this.bulletx += vx;
                    this.bullety += vy;

                } else if (t.getAngle() < 0) {
                    vx = (int) Math.round(R * Math.cos(Math.toRadians(t.getAngle())));
                    vy = (int) Math.round(R * Math.sin(Math.toRadians(t.getAngle())));

                    this.bulletx += vx;
                    this.bullety += vy;
                } else if (t.getAngle() == 0) {

                    this.bulletx += vx;
                }
            }
        //end while loop
        collision();
    }


    public void render(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        g2d.drawImage(this.bulletimg, this.bulletx, this.bullety,null );

    }


    public Rectangle getRect() {
        this.bulletRect = new Rectangle(this.bulletx, this.bullety, this.width, this.height);
        return this.bulletRect;
    }

    public void collision() {
        //check if it's intersecting with tank
        GameObject otherObj;
        Iterator<GameObject> it = this.objectUpdater.getObj().iterator();
        while(it.hasNext()){
            otherObj = it.next();
            if (otherObj.id == ID.BreakableWall) {
                if (this.getRect().intersects(otherObj.getRect())) {
                    it.remove();
                }
            } else if ((otherObj.id == id.Tank1 || otherObj.id == id.Tank2)) {
                if (this.getRect().intersects(otherObj.getRect())) {
                    if (t.getLives() > 0 && t.getHeartHealth() > 0) {

                        int tempHealth = t.getHeartHealth();
                        tempHealth--;

                        t.setHeartHealth(tempHealth);

                        this.bulletx = t.getX();
                        this.bullety = t.getY();
                        //tank1 has no health but still has lives
                    } else if (t.getHeartHealth() == 0 && t.getLives() > 0) {

                        int tempLives = t.getLives();
                        tempLives--;
                        t.setLives(tempLives);
                        this.bulletx -= vx;
                        this.bullety -= vy;
                    } else if (t.getHeartHealth() == 0 && t.getLives() == 0) {

                        t.setLives(0);
                        t.setHeartHealth(0);
                        it.remove();
                        game.isAlive = false;
                        game.running=false;
                        System.out.println("Game Over");
                    }
                }
            }

        }

    }

    private BufferedImage getImage() {
            try {
                bulletimg = read(new File("resources/heart_bullet.png"));

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bulletimg;

        }
}
