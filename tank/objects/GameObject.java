package tank.objects;

import tank.ID;

import java.awt.*;

public abstract class GameObject {
    protected int x, y, vx=0, vy=0;
    protected ID id;

    public GameObject(int x, int y, ID id){
        this.x = x;
        this.y = y;
        this.id = id;

    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract Rectangle getRect();

}
