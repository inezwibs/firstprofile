package tank.background;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;


    public  class TileLayer {
        private static final String DELIMITERS = " ";

        private int[][] map;
        private BufferedImage tileSheet;
        public int x, y,width,height;
        private int findIndex;
        static ArrayList<ArrayList<Integer>> tempLayout;
        //constructor to take in width and height and initialize the map
        private TileLayer(int width, int height) {
            this.width = width;
            this.height = height;
            map = new int[height][width];
        }


        public static TileLayer FromFile(String fileName) {

            TileLayer layer = null;

            tempLayout = getTempLayout(fileName);

             int tlw = tempLayout.get(0).size();
                int tlh = tempLayout.size();

                layer = new TileLayer(tlw, tlh);

                for (int y = 0; y < tlh; y++) {
                    for (int x = 0; x < tlw; x++) {
                        //this layer's map
                        layer.map[y][x] = tempLayout.get(y).get(x);
                        //we can access private "layer" because this is still an object of this class
                    }
                }

                layer.tileSheet = layer.LoadTileSheet("resources/Tile.png");

                return layer;

            }

        public BufferedImage LoadTileSheet(String fileName) {

            BufferedImage img = null;

            try {

                img = ImageIO.read(new File(fileName));
            } catch (IOException e) {
                System.out.print("Could not load image!");
            }
            return img;
        }

        public void DrawLayer(Graphics g) {
            //traverse down by row
            for (int y = 0; y < map.length; y++) {
                //for row 0, i.e. y length, go to the end of the row
                for (int x = 0; x < map[y].length; x++) {
                    int index = map[y][x];

                    int yOffset = 0;

                    if (index > (tileSheet.getWidth() / Engine.TILE_WIDTH) - 1) {

                        yOffset++;

                        index = index - (tileSheet.getWidth() / Engine.TILE_WIDTH);
                    }

                    g.drawImage(tileSheet, x*Engine.TILE_WIDTH,y*Engine.TILE_WIDTH,
                            ((x*Engine.TILE_WIDTH)+Engine.TILE_WIDTH),((y*Engine.TILE_HEIGHT)+Engine.TILE_HEIGHT),
                            index * Engine.TILE_WIDTH, yOffset * Engine.TILE_HEIGHT,
                            ((index*Engine.TILE_WIDTH)+Engine.TILE_WIDTH),((yOffset*Engine.TILE_HEIGHT)+Engine.TILE_HEIGHT), null);
                }
            }
        }//end DrawLayer


        //}

        public int getWidth(){
            return this.width;
        }
        public int getHeight(){
            return height;
        }

        public static int getTileID( int y, int x){

            int tileID = 0;
            ArrayList<Integer> temprow = new ArrayList<>();
            //gets me the array list of row y
                temprow = getTempLayout("resources/map.txt").get(y);

                //j=0
                tileID = temprow.get(x);

            return tileID;
        }
        public TileLayer loadLayer(){
            return FromFile("resources/map.txt");

        }

        public static ArrayList<ArrayList<Integer>> getTempLayout(String fileName){
            ArrayList<ArrayList<Integer>> layout = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                //buffered reader will be created using the file name
                //create a string that will read the matrix line by line
                String currentLine;
                String[] values;
                while ((currentLine = br.readLine()) != null) {

                    ArrayList<Integer> row = new ArrayList<>();

                    if (!currentLine.isEmpty()) {
                        values = currentLine.split(DELIMITERS);


                        for (String s : values) {

                            int id = Integer.parseInt(s);
                            row.add(id);
                        }
                        layout.add(row);
                    }
                }
            } catch (IOException e) {
                System.out.printf("Input failed %s ",e );
            }
            return layout;

        }
        public static void setTempLayout(int val, int x, int y){
            //get the row y
            ArrayList<Integer> row = getTempLayout("resources/map.txt").get(y+1);
                if (row.get(x+1)!=0){
                    row.set(x+1,val);
                }
        }


        public void setWidth(int i ){
            this.x = i;
        }
        public void setHeight (int i ){
            this.y = i;
        }

    }
