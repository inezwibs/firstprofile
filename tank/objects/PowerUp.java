package tank.objects;

import tank.ID;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static javax.imageio.ImageIO.read;

public class PowerUp extends GameObject {
    BufferedImage powerUp;
    ObjectUpdater obj;
    GameBoard game;
    int vx = 0, vy=0;

    public PowerUp(int x, int y, ID id, ObjectUpdater obj){
        super (x,y,id);
        this.obj = obj;
        getImage();
    }


    public void tick() {

    }


    public void render(Graphics g){
        g.drawImage(this.powerUp,x,y,null);

    }

    public Rectangle getRect() {
        return new Rectangle(x,y,32,32);
    }
    private BufferedImage getImage() {
            try {
                powerUp = read(new File("resources/power_up.png"));

            } catch (IOException e) {
                e.printStackTrace();
            }
            return powerUp;

    }

    public int getX() {
        return this.x;
    }
    public int getY(){
        return this.y;
    }
}
