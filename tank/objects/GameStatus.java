package tank.objects;

import tank.ID;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GameStatus extends GameObject {
    BufferedImage img;
    Tank t;
    ArrayList<Integer> healtharray;

    public GameStatus (int x, int y, ID GameStatus, Tank t, BufferedImage img){
        super(x, y,GameStatus );
        this.img = img;
        this.t = t;
        healtharray = new ArrayList<>(10);

    }
    public void tick() {

    }

    public void render(Graphics g) {

        g.drawImage(this.img, x, y, null);
    }

    public Rectangle getRect() {
        return null;
    }

    public void removeHealth(){

        this.healtharray.remove(healtharray.size()-1);
    }

    public ArrayList<Integer> getHealthArray() {
        return this.healtharray;
    }
}
