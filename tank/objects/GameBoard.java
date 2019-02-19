/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tank.objects;


import tank.ID;
import tank.background.TileLayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import static javax.imageio.ImageIO.read;

public class GameBoard extends Canvas implements Runnable {


    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 680;
    public final String TITLE = "Tank Game";

    public static final int SCREEN_WIDTH_HALF = 640;
    public static final int MINI_SCREEN_WIDTH = SCREEN_WIDTH/3;
    public static final int MINI_SCREEN_HEIGHT = SCREEN_HEIGHT/3;

    public boolean running = false, isPower = false, isShot =false, isAlive = true;
    private BufferedImage world;
    private BufferedImage play1world, play2world,  endworld;
    private Image minimap;
    private Graphics2D buffer, endbuffer, b2;
    private JFrame jf;
    private tank.objects.Tank t1, t2, t3, t4;
    Bullet b;
    public ArrayList<Bullet> bulletArrayList = new ArrayList<>();
    public ArrayList<Wall> w1 = new ArrayList<>();
    private TileLayer layer,endlayer;
    private Thread thread;
    private static ObjectUpdater objectUpdater;
    BufferedImage t1img = null, t2img = null, bulletimg = null, hhimg = null;
    int frames;

    public static void main(String[] args) {


        GameBoard board = new GameBoard();
        board.init();//jframe and initializing
        board.start();//call start
    }

    @Override
    public void run() {
        Thread me = Thread.currentThread();

        long startTime = System.nanoTime();
        final double tickAmount = 60.0;
        double ns = 1000000000 / tickAmount;
        double delta = 0;
        frames = 0;
        long timer = System.currentTimeMillis();

        while (thread == me && running) {
            long currentTime = System.nanoTime();
            delta += (currentTime - timer) / ns;
            startTime = currentTime;
            if (delta >= 1) {
                tick();//to update items
                delta--;
            }
            render();//to draw them
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frames = 0;

            }


        }
        stop();

    }

    private synchronized void start() {
        //preventing thread interference and memory consistency errors
        //to avoid suspension of game indefinitely
        if (running){
            return;
        }//leave so thread doesn't restart
        running = true;
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    public synchronized void stop() {
        /*
         if (!running){
            return;
        }
         */

        running = false;

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    private void render() {
        if (running){

            BufferStrategy bs = this.getBufferStrategy();
            if (bs == null) {
                this.createBufferStrategy(3);
                return;
            }
            Graphics g = bs.getDrawGraphics();
            Graphics2D g2 = (Graphics2D) g;
            //--------------
            buffer = world.createGraphics();
            this.layer.DrawLayer(buffer);//
            this.objectUpdater.render(buffer);

            drawDisplay(buffer, world);
            //g2.drawImage(world, 0, 0, null);
            g2.drawImage(play1world,0,0,null);
            g2.drawImage(play2world, GameBoard.SCREEN_WIDTH_HALF-10 ,0,null);
            g2.drawImage(minimap, GameBoard.SCREEN_WIDTH_HALF-200 ,0,null);

            //lives count
            Font stringFont = new Font("Georgia", Font.PLAIN, 18);
            g.setFont(stringFont);
            g.setColor(Color.lightGray);
            g.drawString("Player 1 Heart Health Bar: " + t1.getHeartHealth(), 5 ,60);
            g.setColor(Color.lightGray);
            g.drawString("Player 1 Lives: " + t1.getLives(), 5,80);
            g.setColor(Color.lightGray);
            g.drawString("Player 1 Power Up: " + t1.getPowerbar(), 5,100);
            g.setColor(Color.lightGray);
            g.drawString("Player 2 Heart Health Bar: " + t2.getHeartHealth(), getWidth()-350 ,60);
            g.setColor(Color.lightGray);
            g.drawString("Player 2 Lives: " + t2.getLives(), getWidth()-350,80);
            g.setColor(Color.lightGray);
            g.drawString("Player 2 Power Up: " + t2.getPowerbar(), getWidth()-350,100);

            //--------------
            g.dispose();
            g2.dispose();
            bs.show();

        }else if (!running){

            BufferStrategy endbs = this.getBufferStrategy();
            if (endbs == null) {
                this.createBufferStrategy(3);
                return;
            }
            Graphics g = endbs.getDrawGraphics();
            Graphics2D g2 = (Graphics2D) g;
            //--------------
            endbuffer = world.createGraphics();
            this.endlayer.DrawLayer(endbuffer);//

            g2.drawImage(world, 0, 0, null);

            if (t1.getLives() ==0 ){
                g.drawString ("Player 1 has no more lives. Game Over. Player 1 lives = " +t1.getLives(), 5, 100 );
            }else if (t2.getLives()==0){
                g.drawString ("Player 2 has no more lives. Game Over. Player 1 Wins! Player 2 lives = " +t1.getLives(), getWidth()-350,100 );
            }
            Font stringFont = new Font("Georgia", Font.PLAIN, 36);
            g.setFont(stringFont);
            g.setColor(Color.lightGray);
            g.drawString("GAME OVER" , 300 ,300);
            System.out.println("drawing endworld message");
            g.dispose();
            g2.dispose();
            endbs.show();
        }

    }

    private void tick() {
        if (t1.getLives() !=0 && t2.getLives()!=0 && running){

            objectUpdater.tick();

        }
    }

    private void init() {
        this.jf = new JFrame(TITLE);
        //set up frame
        this.jf.setSize(this.SCREEN_WIDTH, this.SCREEN_HEIGHT);
        this.jf.setLayout(new BorderLayout());
        //add game to frame
        this.jf.add(this);
        this.jf.setResizable(false);
        jf.setLocationRelativeTo(null);
        //close on exit
        this.jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.jf.setVisible(true);

        this.world = new BufferedImage(GameBoard.SCREEN_WIDTH, GameBoard.SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        this.endworld = new BufferedImage(GameBoard.SCREEN_WIDTH, GameBoard.SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        //download images
        try {
            BufferedImage tmp;
            System.out.println(System.getProperty("user.dir"));

            t1img = read(new File("resources/tank1.png"));
            t2img = read(new File("resources/tank2.png"));
            bulletimg = read (new File("resources/heart_bullet.png"));
            hhimg = read(new File ( "resources/heart_health.png"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        //map layer
        layer = TileLayer.FromFile("resources/map2.txt");
        endlayer = TileLayer.FromFile("resources/map2.txt");
        //initialize objects updater
        objectUpdater = new ObjectUpdater();
        //tank
        t1 = new Tank(200, 200, ID.Tank1, 0, 0, 0, t1img, layer, this, objectUpdater);
        t2 = new Tank(SCREEN_WIDTH_HALF + 200, 200, ID.Tank2, 0, 0, 0, t2img, layer, this, objectUpdater);

        objectUpdater.addObject(t1);
        objectUpdater.addObject(t2);

        TankControl tc1 = new TankControl(t1, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_E, KeyEvent.VK_C, KeyEvent.VK_S, KeyEvent.VK_F, KeyEvent.VK_SPACE);
        //draw board and play items on it
        ArrayList<ArrayList<Integer>> boardmatrix = TileLayer.getTempLayout("resources/map.txt");
         int w = boardmatrix.get(0).size();
         int h = boardmatrix.size();
                for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                int pixel = TileLayer.getTileID(j, i);
                switch(pixel) {
                    //for walls boolean isBreakable = false
                    case 1:
                        objectUpdater.addObject(new Wall(i*32, j*32, ID.Wall, false, objectUpdater));
                        break;
                    case 2:
                        objectUpdater.addObject(new Wall(i*32, j*32, ID.BreakableWall, true, objectUpdater));
                        break;
                    case 3:
                        objectUpdater.addObject(new PowerUp(i*32, j*32, ID.PowerUp, objectUpdater));
                    case 4:
                        objectUpdater.addObject(new PowerUp(i*32, j*32, ID.PowerUp, objectUpdater));
                        break;
                    case 5:
                        objectUpdater.addObject((new Wall(i*32,j*32,ID.BreakableWall,true,objectUpdater)));
                }
            }
        }
        /* */

        //healthbars
        int healtht1=10;
        int heartdisplay1 = t1.getHeartHealth();
        for (int i =0; i< heartdisplay1; i++) {
            objectUpdater.addObject(new GameStatus(healtht1, 10,ID.GameStatus,t1, hhimg));
            healtht1 += 32;
        }
        int heartdisplay2 = t2.getHeartHealth();
        int healtht2 = getWidth()-350;
        for (int i =0; i< heartdisplay2; i++) {
            objectUpdater.addObject(new GameStatus(healtht2, 10,ID.GameStatus,t2, hhimg));
            healtht2 += 32;
        }


        this.jf.addKeyListener(tc1);
        this.jf.addKeyListener(tc2);

  }//end init
    public int getFrames(){
        return this.frames;
    }

    public void loadTankBullets(Tank tank, int x, int y, boolean isPower,ObjectUpdater objectUpdater) {

                this.bulletArrayList.add(new Bullet(this, tank, x, y, ID.Bullet, isPower, objectUpdater));
   }
    public void drawUpdateBullets(ArrayList<Bullet> barray, Graphics2D buffer){
        Iterator<Bullet> it = barray.iterator();
        Bullet tempBul;
        while (it.hasNext()) {
            tempBul = it.next();
            // remove player bullets when they're out of frame
            if (tempBul.getBullety() <= 40 || tempBul.getBullety() >= GameBoard.SCREEN_HEIGHT - 128 || tempBul.getBulletx() <= 20 || tempBul.getX() >= GameBoard.SCREEN_WIDTH - 88 ) {
                it.remove();
            }else {
                tempBul.tick();

                tempBul.render(buffer);

            }
        }

    }
    public void drawDisplay(Graphics2D buf, BufferedImage wor){
            if (running){
            //draw bullets
            drawUpdateBullets(this.bulletArrayList,buf);
            //draw views

            int xt1, yt1, xt2, yt2;

            xt1 = t1.getX()-180 ;//200 to 642 is outside the x raster
            xt2 = t2.getX()-400; //t2 starts at 840, at getx-200 it is at the edge
            yt1 = t1.getY();
            yt2 = t2.getY();
        /*
        t1 = 200, 200,
        t2 = 640 + 200, 200,
        * */

            int viewW = wor.getWidth()/2;
            int viewH = wor.getHeight();

            int newx1 = viewHelper(xt1);
            int newx2 = viewHelper(xt2);

            play1world = wor.getSubimage(newx1, 0, viewW, viewH);
            play2world = wor.getSubimage(newx2, 0, viewW, viewH);
            minimap  = wor.getScaledInstance(GameBoard.MINI_SCREEN_WIDTH, GameBoard.MINI_SCREEN_HEIGHT, BufferedImage.SCALE_FAST);

            }else{
                stop();
            }
        }

        private int viewHelper(int x){


        if (x <=20 ){
        x = 20;
        }else if (x >=640){
        x = 640;
        }
        return x;
        }

}//end board

