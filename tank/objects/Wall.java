package tank.objects;

import tank.ID;
import tank.background.TileLayer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static javax.imageio.ImageIO.read;

public class Wall extends GameObject {
    TileLayer layer;
    private BufferedImage img, breakableWallImg = null, wallImg = null;
    private boolean isBreakable = false;
    ObjectUpdater objectUpdater;
    private ArrayList<String> wallLife;

    public Wall(int x, int y, ID Wall, boolean b, ObjectUpdater objectUpdater) {
        super(x, y, Wall);
        this.objectUpdater = objectUpdater;
        this.isBreakable = b;
        if (isBreakable){
            isBreakable =true;
            wallLife = new ArrayList<String>(Arrays.asList("Strong", "Weaker", "Dead"));
        }else{
            isBreakable=false;
        }
        img = getImage(isBreakable);
    }

    public void tick() {


    }

    public void render(Graphics g) {

            g.drawImage(this.img, x, y, null);
    }

    public Rectangle getRect() {
        return new Rectangle(x,y,32,32);
    }
    public ArrayList<String> getWallLife() {
        return this.wallLife;
    }


    private BufferedImage getImage(boolean isBreakable) {
        if (isBreakable == false){
            try {
                wallImg = read(new File("resources/wall.png"));

            } catch (IOException e) {
                e.printStackTrace();
            }
            return wallImg;
        }else{
            try {
                breakableWallImg = read(new File("resources/brick_wall.png"));

            } catch (IOException e) {
                e.printStackTrace();
            }
            return breakableWallImg;
        }
    }


}