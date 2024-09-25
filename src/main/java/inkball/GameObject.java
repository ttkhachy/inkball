package inkball;

public class GameObject {
    private String type; // e.g., "Hole1", "Spawner"
    private int x;
    private int y;
    private int width;
    private int height;

    public GameObject(String type, int x, int y, int width, int height) {
        this.type = type;
        this.x = x; // x-coordinate of top-left corner
        this.y = y; // y-coordinate of top-left corner
        this.width = width;
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
