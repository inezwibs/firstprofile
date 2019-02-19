package tank.objects;

import java.awt.*;
import java.util.LinkedList;

public class ObjectUpdater {
    LinkedList<GameObject> obj = new LinkedList<>();

    public void tick(){
        for (int i = 0; i<obj.size(); i++){
            GameObject tempobj = obj.get(i);

            tempobj.tick();

        }
    }
    public void render(Graphics g){
        for (int i = 0; i<obj.size(); i++){
            GameObject tempobj = obj.get(i);
            tempobj.render(g);
        }
    }
    public void addObject(GameObject tempobj){
        obj.add(tempobj);

    }
    public void removeObject(){
        obj.remove();
    }

    public LinkedList<GameObject> getObj() {
        return obj;
    }

}
