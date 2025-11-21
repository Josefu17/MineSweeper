package minesweeper;

import java.util.Random;

import static minesweeper.BlockType.DISCOVERED;
import static minesweeper.BlockType.MINE;

/**
 * Manages the grid state, mine placement, and adjacency logic.
 */
public class World
{

    private Block[][] theWorld;
    private final int ROWS;
    private int mineCount;
    private int toCover;
    private int marksLeft;
    private int minesLeft;

    private final int COLUMNS;

    public World(int x, int y, int difficulty) throws IndexOutOfBoundsException
    {
        if (x > 0 && x <= 30 && y > 0 && y <= 30) {
            ROWS = x;
            COLUMNS = y;
        } else {
            throw new IndexOutOfBoundsException("Dimensions might be 30x30 at max. Current: " + x + "X" + y);
        }
        initialize(ROWS, COLUMNS, difficulty);
    }

    /**
     * Populates the grid with blocks and randomly places mines.
     */
    private void initialize(int x, int y, int difficulty)
    {
        theWorld = new Block[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++)
            theWorld[i] = new Block[COLUMNS];

        int minesToPlant = difficulty == 2 ? 2 * (x * y) / 5 : (x * y) / 4;
        mineCount = minesToPlant;
        minesLeft = mineCount;
        toCover = x * y;
        marksLeft = mineCount;

        Random random = new Random();
        while (minesToPlant > 0) {
            int nextX = random.nextInt(ROWS);
            int nextY = random.nextInt(COLUMNS);
            if (theWorld[nextX][nextY] == null) {
                theWorld[nextX][nextY] = new Block(nextX, nextY, MINE);
                minesToPlant--;
            }
        }
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (theWorld[i][j] == null) {
                    theWorld[i][j] = new Block(i, j, BlockType.BLANK);
                }
            }
        }
    }


    /**
     * Returns true if all non-mine blocks have been uncovered.
     */
    public boolean won()
    {
        return toCover == minesLeft;
    }

    /**
     * Returns a string representation of the current board state.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        // Add Column headers
        sb.append("  ");
        for (int i = 0; i < COLUMNS; i++) {
            sb.append(i).append(" ");
        }

        // Add Rows
        for (int i = 0; i < ROWS; i++) {
            sb.append("\n").append(i).append(" ");
            for (int j = 0; j < COLUMNS; j++) {
                sb.append(theWorld[i][j].getCharacter()).append(" ");
            }
        }
        return sb.toString();
    }

    public int getMineCount() {
        return mineCount;
    }

    public int getRows() {
        return ROWS;
    }

    public int getColumns() {
        return COLUMNS;
    }

    protected BlockType getState(int x, int y) {
        return theWorld[x][y].getBlockType();
    }

    protected BlockType getState(Coordinate coordinate) {
        return theWorld[coordinate.x()][coordinate.y()].getBlockType();
    }

    /**
     * Gets the original state (block type) of the specified coordinates in the Minesweeper world.
     */
    protected BlockType getOriginalState(int x, int y) {
        return theWorld[x][y].getOriginalType();
    }

    protected BlockType getOriginalState(Coordinate coordinate) {
        return theWorld[coordinate.x()][coordinate.y()].getOriginalType();
    }

    public void modifyBlock(Coordinate coordinate, BlockType blockType)
    {
        if (isOutOfBounds(coordinate)) {
            System.out.println("Invalid Point! Nothing is done, continuing...");
            return;
        }

        if (blockType == DISCOVERED) {
            theWorld[coordinate.x()][coordinate.y()].modify(blockType, getAdjacentMinesCount(coordinate));
        } else {
            theWorld[coordinate.x()][coordinate.y()].modify(blockType);
        }
    }

    public boolean isOutOfBounds(Coordinate coordinate) {
        return !isValid(coordinate.x(), coordinate.y());
    }

    public boolean isValid(int x, int y) {
        return (x >= 0 && x < ROWS && y >= 0 && y < COLUMNS);
    }

    public int getMarksLeft() {
        return marksLeft;
    }

    public int getToCover() {
        return toCover;
    }

    public void decrementMarksLeft() {
        marksLeft--;
    }

    public void incrementMarksLeft() {
        marksLeft++;
    }

    public void decrementToCover() {
        toCover--;
    }

    public void incrementToCover() {
        toCover++;
    }

    public void decrementMinesLeft() {
        minesLeft--;
    }

    public void incrementMinesLeft() {
        minesLeft++;
    }


    /**
     * Returns the number of adjacent mines to the given coordinate.
     */
    public int getAdjacentMinesCount(Coordinate coordinate)
    {
        int mines = 0;

        // Iterate from -1 to +1 on both X and Y axes
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                // Skip the center point (0,0) because that is the block itself
                if (dx == 0 && dy == 0) continue;

                int checkX = coordinate.x() + dx;
                int checkY = coordinate.y() + dy;

                // Check validity and if it is a mine
                if (isValid(checkX, checkY) && getOriginalState(checkX, checkY) == MINE) {
                    mines++;
                }
            }
        }
        return mines;
    }

    /**
     * Recursively reveals safe areas (Flood Fill).
     */
    public void check(Coordinate coordinate)
    {
        if (getOriginalState(coordinate) == MINE || getState(coordinate) == DISCOVERED) {
            return;
        }

        int adjacentMines = getAdjacentMinesCount(coordinate);
        decrementToCover();
        if (adjacentMines == 0) {
            modifyBlock(coordinate, BlockType.BLANK);
            expand(coordinate);
        } else {
            modifyBlock(coordinate, DISCOVERED);
        }
    }

    public void check(int x, int y)
    {
        check(new Coordinate(x, y));
    }

    /**
     * Triggers checks on all valid neighbors.
     */
    public void expand(Coordinate coordinate) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int nx = coordinate.x() + dx;
                int ny = coordinate.y() + dy;
                if (isValid(nx, ny) && getState(nx, ny) == BlockType.UNKNOWN) {
                    check(nx, ny);
                }
            }
        }
    }

    /**
     * Helper to peek at a block and reveal it if it's a mine.
     */
    public int peekAndModifyIfMine(int x, int y) {
        if (theWorld[x][y].getOriginalType() == BlockType.MINE) {
            modifyBlock(new Coordinate(x, y), BlockType.MINE);
            decrementMinesLeft();
            decrementToCover();
            return -1;
        }
        return 0;
    }

    /**
     * Auto-expands neighbors if the number of flags matches adjacent mines (Chord).
     */
    public int forceExpand(Coordinate coordinate, BlockType currentState) {
        if (currentState != BlockType.DISCOVERED || getNumberOfAdjacentFlags(coordinate) != getAdjacentMinesCount(coordinate)) {
            modifyBlock(coordinate, currentState);
            return -1;
        }

        int hitMines = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                int nx = coordinate.x() + dx;
                int ny = coordinate.y() + dy;

                if (isValid(nx, ny)) {
                    BlockType state = getState(nx, ny);
                    if (state == BlockType.UNKNOWN) {
                        if (peekAndModifyIfMine(nx, ny) == -1) hitMines++;
                        else check(nx, ny); // Safe to check since we peeked for mines
                    } else if (state == BlockType.MARKED && theWorld[nx][ny].getOriginalType() == BlockType.BLANK) {
                        // Incorrectly marked safe spot
                        modifyBlock(new Coordinate(nx, ny), BlockType.UNKNOWN);
                    }
                }
            }
        }

        modifyBlock(coordinate, BlockType.DISCOVERED);
        System.out.println("Hit Mines: " + hitMines);
        return hitMines;
    }

    private int getNumberOfAdjacentFlags(Coordinate coordinate) {
        int flags = 0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue; // block itself

                int nx = coordinate.x() + dx;
                int ny = coordinate.y() + dy;
                if (isValid(nx, ny) && getState(nx, ny) == BlockType.MARKED) {
                    flags++;
                }
            }
        }
        return flags;
    }

}
