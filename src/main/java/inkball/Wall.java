package inkball;

public class Wall extends Tile implements Collidable<Wall> {
    private String wallColour;

    public Wall(int x, int y, String colour) {
        super(x, y);
        this.wallColour = setWallColour(colour);
    }

    public String getWallColour() {
        return this.wallColour;
    }

    public String setWallColour(String fileSymbol) {
        if (fileSymbol.equals("1")) {
            return "orange";
        } else if (fileSymbol.equals("2")) {
            return "blue";
        } else if (fileSymbol.equals("3")) {
            return "green";
        } else if (fileSymbol.equals("4")) {
            return "yellow";
        } else {
            return null;
        }

    }

    public int[][] getWallSegments() {
        int[][] wallSegments = { { this.getXCellSize(), this.getYCellSize() },
                { this.getXCellSize() + App.CELLSIZE, this.getYCellSize() },
                { this.getXCellSize() + App.CELLSIZE, this.getYCellSize() + App.CELLSIZE },
                { this.getXCellSize(), this.getYCellSize() + App.CELLSIZE } };
        return wallSegments;
    }
}
