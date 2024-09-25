package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32; // 8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64; // this was initially 0? changed it to 6 after doing maths
    public static int WIDTH = 576; // CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; // BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH / CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static int CURRENT_LEVEL = 1; // needs to be set by the actual config file tho

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;

    public static Random random = new Random();

    private Tile[][] board;
    private static HashMap<String, PImage> allSprites = new HashMap<>();
    public static HashMap<String, PImage> levelFileSymbolSprites = new HashMap<>();
    protected HashMap<String, String> ballColours = new HashMap<>();

    private String[][] levelFileArray;
    private List<GameObject> gameObjects = new ArrayList<>();
    private ArrayList<Ball> allBalls = new ArrayList<>();
    private ArrayList<Wall> allWalls = new ArrayList<>();
    // Feel free to add any additional methods or attributes you want. Please put
    // classes in different files.

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    public PImage getSprite(String s) {
        PImage result = allSprites.get(s);
        if (result == null) {
            result = loadImage(
                    this.getClass().getResource(s + ".png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));

        }
        allSprites.put(s, result);
        return result;
        /*
         * try {
         * result = loadImage(URLDecoder.decode(this.getClass().getResource(s
         * +".png").getPath(), StandardCharsets.UTF_8.name()));
         * catch (UnsupportedEncodingException e) {
         * throw new RuntimeException(e);
         * }
         */
    }

    public Tile[][] getBoard() {
        return this.board;
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player
     * and map elements.
     */
    @Override
    public void setup() {
        frameRate(FPS);
        // See PApplet javadoc:
        loadJSONObject(configPath);
        // the image is loaded from relative path: "src/main/resources/inkball/..."

        String[] sprites = { "entrypoint", "tile" }; // idk why spritesheet is for ??
        for (int i = 0; i < sprites.length; i++) {
            getSprite(sprites[i]);
        }

        String[] SpriteFirstWords = { "ball", "hole", "wall" };
        for (int i = 0; i < SpriteFirstWords.length; i++) {
            for (int k = 0; k < 5; k++) {
                getSprite(SpriteFirstWords[i] + String.valueOf(k));
            }
        }

        declarelevelFileSymbolSprites();

        levelFileArray = GetLevelFileSymbol();

        parseLevelFile();

        setBallColoursMap();
        for (String s : ballColours.keySet()) {
            System.out.println(s);
        }
        for (String s : ballColours.values()) {
            System.out.println(s);
        }

        // this.board = new Tile[(HEIGHT - TOPBAR) / CELLSIZE][WIDTH / CELLSIZE];

        // for (int i = 0; i < this.board.length; i++) {
        // for (int k = 0; k < this.board[i].length; k++) {
        // this.board[i][k] = new Tile(k, i);
        // }
        // }

    }

    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed(KeyEvent event) {

    }

    /**
     * Receive key released signal from the keyboard.
     */
    @Override
    public void keyReleased() {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // create a new player-drawn line object
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // add line segments to player-drawn line object if left mouse button is held

        // remove player-drawn line object if right mouse button is held
        // and mouse position collides with the line
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {
        for (int i = 0; i < board.length; i++) {
            for (int k = 0; k < board[i].length; k++) {
                if (board[i][k] != null) {
                    board[i][k].draw(this);
                }
            }
        }
        for (GameObject obj : gameObjects) {
            drawGameObject(obj);
        }

        for (Ball ball : allBalls) {
            ball.draw(this);

            for (Wall wall : allWalls) {
                ball.checkCollisionWithWall(wall);
            }
        }
        // ----------------------------------
        // display Board for current level:
        // ----------------------------------
        // TODO

        // ----------------------------------
        // display score
        // ----------------------------------
        // TODO

        // ----------------------------------
        // ----------------------------------
        // display game end message

    }

    public String[][] GetLevelFileSymbol() {
        String[][] levelFileArray = new String[18][18];
        String currentLevelFile = "level" + String.valueOf(CURRENT_LEVEL) + ".txt";
        int row = 0;

        try {
            File file = new File(currentLevelFile);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine() && row < 18) {
                String line = scanner.nextLine();
                for (int i = 0; i < line.length() && i < 18; i++) {
                    char currentChar = line.charAt(i);
                    if (currentChar == 'H' || currentChar == 'B') {
                        levelFileArray[row][i] = "" + line.charAt(i) + line.charAt(i + 1);
                        levelFileArray[row][i + 1] = " ";
                        i++;
                    } else {
                        levelFileArray[row][i] = String.valueOf(currentChar);
                    }

                }
                row++;
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return levelFileArray;
    }

    public void parseLevelFile() {
        int numRows = levelFileArray.length;
        int numCols = levelFileArray[0].length;
        board = new Tile[numRows][numCols];

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                String symbol = levelFileArray[row][col];

                if (symbol == null) { // been marked as occupied in markOccupiedTiles
                    continue;
                }

                if (symbol.startsWith("H")) {
                    // could add a function that does this since it both if statements are doing the
                    // same thing?
                    int holeType = Integer.parseInt(symbol.substring(1));
                    GameObject hole = new GameObject("Hole" + holeType, col, row, 2, 2);
                    gameObjects.add(hole);

                    markOccupiedTiles(levelFileArray, row, col, 2, 2);
                } else if (symbol.startsWith("S")) {
                    GameObject spawner = new GameObject("Spawner", col, row, 1, 1);
                    gameObjects.add(spawner);
                } else if (symbol.startsWith("B")) {
                    System.out.println("symbol " + symbol);
                    System.out.println(this.ballColours.get(symbol));
                    Ball ball = new Ball(symbol, col, row, this.ballColours.get(symbol));
                    this.allBalls.add(ball);
                    board[row][col] = new Tile(col, row); // even though a ball is supposed to start there, we still
                                                          // want a tile to be initialised because the ball is going to
                                                          // move away
                    board[row][col].levelFileSymbol = " "; // setting the row as a space so a
                    // tile is drawn
                } else if (symbol.matches("[X1234]")) {
                    board[row][col] = new Wall(col, row, symbol);
                    this.allWalls.add(new Wall(col, row, symbol));
                    board[row][col].levelFileSymbol = symbol;
                } else {
                    board[row][col] = new Tile(col, row);
                    board[row][col].levelFileSymbol = symbol;
                }

            }
        }

    }

    public void markOccupiedTiles(String[][] levelFileArray, int startRow, int startCol, int width, int height) {
        for (int y = startRow; y < startRow + height; y++) { // not fully convinced about the values here??
            for (int x = startCol; x < startCol + width; x++) {
                if (y < levelFileArray.length && x < levelFileArray[y].length) {
                    levelFileArray[y][x] = null; // Mark as occupied
                }
            }
        }
    }

    public static void declarelevelFileSymbolSprites() {
        String[] wallSymbols = { "X", "1", "2", "3", "4" };

        // walls
        for (int i = 0; i < wallSymbols.length; i++) {
            levelFileSymbolSprites.put(wallSymbols[i], allSprites.get("wall" + String.valueOf(i)));
        }

        // holes
        for (int i = 0; i <= 4; i++) {
            levelFileSymbolSprites.put("H" + i, allSprites.get("hole" + String.valueOf(i)));
        }

        // balls
        for (int i = 0; i <= 4; i++) {
            levelFileSymbolSprites.put("B" + i, allSprites.get("ball" + String.valueOf(i)));
        }

        // spawner
        levelFileSymbolSprites.put("S", allSprites.get("entrypoint"));

    }

    private void drawGameObject(GameObject obj) {
        PImage sprite = null;

        if (obj.getType().startsWith("Hole")) {
            String holeType = obj.getType().substring(4);
            sprite = levelFileSymbolSprites.get("H" + holeType);
        } else if (obj.getType().equals("Spawner")) {
            sprite = levelFileSymbolSprites.get("S");
        }

        if (sprite != null) {
            int x = obj.getX();
            int y = obj.getY();
            // int widthPixels = obj.getWidth() * CELLSIZE;
            // int heightPixels = obj.getHeight() * CELLHEIGHT;
            image(sprite, x * CELLSIZE, y * CELLSIZE + TOPBAR);
        }
    }

    public void setBallColoursMap() {
        ballColours.put("B0", "grey");
        ballColours.put("B1", "orange");
        ballColours.put("B2", "blue");
        ballColours.put("B3", "green");
        ballColours.put("B4", "yellow");
    }

    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}
