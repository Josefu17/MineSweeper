package minesweeper;

/**
 * Represents a coordinate with x and y values.
 */
public class Coordinate {
    private final int x;
    private final int y;

    /**
     * Constructs a Coordinate with the specified x and y values.
     *
     * @param x The x-coordinate value.
     * @param y The y-coordinate value.
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x-coordinate value.
     *
     * @return The x-coordinate value.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate value.
     *
     * @return The y-coordinate value.
     */
    public int getY() {
        return y;
    }
}
