package inkball;

import processing.core.PConstants;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

public class Tile {

    private int x;
    private int y;
    private boolean isSpawner;
    private boolean isHole;

    public String levelFileSymbol;

    public Tile(int x, int y) {
        this.x = x; // should i just times this by the app cell size and make my life easier??
        this.y = y;
    }

    public void draw(App app) {
        if (this.levelFileSymbol == null) {
            return;
        }
        PImage tile = app.getSprite("tile");
        if (!this.getLevelFileSymbol().equals(" ")) {
            tile = App.levelFileSymbolSprites.get(this.levelFileSymbol);
        }
        app.image(tile, x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
    }

    public String getLevelFileSymbol() {
        return this.levelFileSymbol;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getXCellSize() {
        return this.x * App.CELLSIZE;
    }

    public int getYCellSize() {
        return this.y * App.CELLSIZE + App.TOPBAR;
    }

    public int getXOtherEnd() {
        return this.x * App.CELLSIZE + App.CELLSIZE;
    }

    public int getYOtherEnd() {
        return this.y * App.CELLSIZE + App.TOPBAR + App.CELLSIZE;
    }
}
