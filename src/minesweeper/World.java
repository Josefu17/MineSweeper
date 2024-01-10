package minesweeper;

import java.util.Random;

/**
 * Represents the game world in Minesweeper, consisting of a grid of blocks.
 * The world is generated based on specified dimensions and difficulty level.
 */
public class World {

    private Block[][] theWorld;
    private final int ROWS;
    private int mineCount;
    private int toCover;
    private int marksLeft;
    private int minesLeft;

    private final int COLUMNS;

    /**
     * Constructs a Minesweeper world with the specified dimensions and difficulty level.
     *
     * @param x          The number of rows in the world (between 1 and 30, inclusive).
     * @param y          The number of columns in the world (between 1 and 30, inclusive).
     * @param difficulty The difficulty level (1 for medium, 2 for hard).
     * @throws IndexOutOfBoundsException If the dimensions are out of bounds.
     */
    public World(int x, int y, int difficulty) throws IndexOutOfBoundsException {
        if (x > 0 && x <= 30 && y > 0 && y <= 30) {
            ROWS = x;
            COLUMNS = y;
        } else {
            throw new IndexOutOfBoundsException();
        }
        initialize(ROWS, COLUMNS, difficulty);
    }

    /**
     * Initializes the Minesweeper world based on the specified dimensions and difficulty level.
     *
     * @param x          The number of rows in the world.
     * @param y          The number of columns in the world.
     * @param difficulty The difficulty level (1 for medium, 2 for hard).
     *                   difficulty medium -> 25% mines and 1 lifeline, hard -> 40% mines and no lifeline
     */
    private void initialize(int x, int y, int difficulty) {
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
                theWorld[nextX][nextY] = new Block(nextX, nextY, BlockType.MINE);
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
     * Checks if the player has won the game.
     *
     * @return {@code true} if all non-mine blocks are uncovered, indicating a win.
     */
    public boolean won() {
        return toCover == minesLeft;
    }

    /**
     * Prints the current state of the Minesweeper world, including block characters and coordinates.
     */
    public void printWorld() {
        System.out.print("  ");
        for (int i = 0; i < COLUMNS; i++) {
            System.out.print(i + " ");
        }
        for (int i = 0; i < ROWS; i++) {
            System.out.print("\n" + i + " ");
            for (int j = 0; j < COLUMNS; j++) {
                System.out.print(theWorld[i][j].getCharacter());
                System.out.print(" ");
            }
        }
        System.out.println();

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

    /**
     * Gets the current state (block type) of the specified coordinates in the Minesweeper world.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The block type at the specified coordinates.
     */
    protected BlockType getState(int x, int y) {
        return theWorld[x][y].getBlockType();
    }

    /**
     * Gets the current state (block type) of the specified coordinates in the Minesweeper world.
     *
     * @param coordinate The coordinates.
     * @return The block type at the specified coordinates.
     */
    protected BlockType getState(Coordinate coordinate) {
        return theWorld[coordinate.getX()][coordinate.getY()].getBlockType();
    }

    /**
     * Gets the original state (block type) of the specified coordinates in the Minesweeper world.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The original block type at the specified coordinates. That is BLANK for non-mine fields and MINE for
     * mines.
     */
    protected BlockType getOriginalState(int x, int y) {
        return theWorld[x][y].getOriginalType();
    }

    protected BlockType getOriginalState(Coordinate coordinate) {
        return theWorld[coordinate.getX()][coordinate.getY()].getOriginalType();
    }

    /**
     * Modifies the block at the specified coordinates with the given block type.
     *
     * @param coordinate The coordinates of the block to be modified.
     * @param blockType  The new block type to set.
     */
    public void modifyBlock(Coordinate coordinate, BlockType blockType) {
        if (isValid(coordinate)) {
            if (blockType == BlockType.DISCOVERED) {
                theWorld[coordinate.getX()][coordinate.getY()].modify(blockType, getAdjacentMinesCount(coordinate));
            } else {
                theWorld[coordinate.getX()][coordinate.getY()].modify(blockType);
            }
        } else {
            System.out.println("Invalid Point! Nothing is done, continuing...");
        }
    }

    public void modifyBlock(int x, int y, BlockType blockType) {
        modifyBlock(new Coordinate(x, y), blockType);
    }

    public boolean isValid(Coordinate coordinate) {
        return (coordinate.getX() >= 0 && coordinate.getX() < ROWS && coordinate.getY() >= 0 && coordinate.getY() < COLUMNS);
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
     *
     * @param coordinate The coordinate to check for adjacent mines.
     * @return The count of adjacent mines.
     */
    public int getAdjacentMinesCount(Coordinate coordinate) {
        int mines = 0;
        int x = coordinate.getX();
        int y = coordinate.getY();
        if (isValid(x - 1, y - 1) && getOriginalState(x - 1, y - 1) == BlockType.MINE) {
            mines++;
        }
        if (isValid(x - 1, y) && getOriginalState(x - 1, y) == BlockType.MINE) {
            mines++;
        }
        if (isValid(x - 1, y + 1) && getOriginalState(x - 1, y + 1) == BlockType.MINE) {
            mines++;
        }
        if (isValid(x, y - 1) && getOriginalState(x, y - 1) == BlockType.MINE) {
            mines++;
        }
        if (isValid(x, y + 1) && getOriginalState(x, y + 1) == BlockType.MINE) {
            mines++;
        }
        if (isValid(x + 1, y - 1) && getOriginalState(x + 1, y - 1) == BlockType.MINE) {
            mines++;
        }
        if (isValid(x + 1, y) && getOriginalState(x + 1, y) == BlockType.MINE) {
            mines++;
        }
        if (isValid(x + 1, y + 1) && getOriginalState(x + 1, y + 1) == BlockType.MINE) {
            mines++;
        }

        return mines;
    }

    /**
     * Checks the block at the specified coordinate. If the block is a mine or already discovered, nothing happens.
     * Otherwise, if there are adjacent mines, the block is marked as discovered. If there are no adjacent mines,
     * the block is marked as blank and adjacent blocks are recursively expanded.
     *
     * @param coordinate The coordinate of the block to check.
     */
    public void check(Coordinate coordinate) {
        if (getOriginalState(coordinate.getX(), coordinate.getY()) == BlockType.MINE || getState(coordinate) == BlockType.DISCOVERED)
            return;
        int adjacentMines = getAdjacentMinesCount(coordinate);
        decrementToCover();
        if (adjacentMines == 0) {
            modifyBlock(coordinate, BlockType.BLANK);
            expand(coordinate);
        } else {
            modifyBlock(coordinate, BlockType.DISCOVERED);
        }
    }

    public void check(int x, int y) {
        check(new Coordinate(x, y));
    }

    /**
     * Expands the area around the specified coordinate by recursively checking adjacent blocks.
     *
     * @param coordinate The coordinate from which to start the expansion.
     */
    public void expand(Coordinate coordinate) {
        int x = coordinate.getX();
        int y = coordinate.getY();
        if (isValid(x - 1, y - 1) && getState(x - 1, y - 1) == BlockType.UNKNOWN) {
            check(x - 1, y - 1);
        }
        if (isValid(x - 1, y) && getState(x - 1, y) == BlockType.UNKNOWN) {
            check(x - 1, y);
        }
        if (isValid(x - 1, y + 1) && getState(x - 1, y + 1) == BlockType.UNKNOWN) {
            check(x - 1, y + 1);
        }
        if (isValid(x, y - 1) && getState(x, y - 1) == BlockType.UNKNOWN) {
            check(x, y - 1);
        }
        if (isValid(x, y + 1) && getState(x, y + 1) == BlockType.UNKNOWN) {
            check(x, y + 1);
        }
        if (isValid(x + 1, y - 1) && getState(x + 1, y - 1) == BlockType.UNKNOWN) {
            check(x + 1, y - 1);
        }
        if (isValid(x + 1, y) && getState(x + 1, y) == BlockType.UNKNOWN) {
            check(x + 1, y);
        }
        if (isValid(x + 1, y + 1) && getState(x + 1, y + 1) == BlockType.UNKNOWN) {
            check(x + 1, y + 1);
        }
    }


    /**
     * Peeks at the block at the specified coordinates and modifies it if it's a mine. If a mine is found, the block is
     * modified to a discovered mine, and counts for mines left and total blocks to cover are decremented. Returns -1 if a mine
     * is found, otherwise returns 0.
     *
     * @param x The x-coordinate of the block to peek.
     * @param y The y-coordinate of the block to peek.
     * @return -1 if a mine is found, 0 otherwise.
     */
    public int peekAndModifyIfMine(int x, int y) {
        if (getOriginalState(x, y) == BlockType.MINE) {
            modifyBlock(x, y, BlockType.MINE);
            decrementMinesLeft();
            decrementToCover();
            return -1;
        }
        return 0;
    }

    /**
     * Forces an expansion around the specified coordinate if certain conditions are met. If the current state is not
     * discovered or the number of adjacent flags is not equal to the number of adjacent mines, the method returns -1.
     * Otherwise, it expands the area around the coordinate and returns the number of hit mines during the expansion.
     *
     * @param coordinate    The coordinate around which to force the expansion.
     * @param currentState  The current state of the block.
     * @return The number of hit mines during the forced expansion, or -1 if conditions are not met.
     */
    public int forceExpand(Coordinate coordinate, BlockType currentState) {
        if (currentState != BlockType.DISCOVERED || getAdjacentFlags(coordinate) != getAdjacentMinesCount(coordinate)) {
            modifyBlock(coordinate, currentState);
            return -1;
        }
        int x = coordinate.getX();
        int y = coordinate.getY();
        int hitMines = 0;
        if (isValid(x - 1, y - 1) && getState(x - 1, y - 1) == BlockType.UNKNOWN && peekAndModifyIfMine(x - 1, y - 1) == -1) {
            hitMines++;
        } else if (isValid(x - 1, y - 1) && getState(x - 1, y - 1) == BlockType.MARKED && getOriginalState(x - 1, y - 1) == BlockType.BLANK) {
            modifyBlock(x - 1, y - 1, BlockType.UNKNOWN);
        }

        if (isValid(x - 1, y) && getState(x - 1, y) == BlockType.UNKNOWN && peekAndModifyIfMine(x - 1, y) == -1) {
            hitMines++;
        } else if (isValid(x - 1, y) && getState(x - 1, y) == BlockType.MARKED && getOriginalState(x - 1, y) == BlockType.BLANK) {
            modifyBlock(x - 1, y, BlockType.UNKNOWN);
        }

        if (isValid(x - 1, y + 1) && getState(x - 1, y + 1) == BlockType.UNKNOWN && peekAndModifyIfMine(x - 1, y + 1) == -1) {
            hitMines++;
        } else if (isValid(x - 1, y + 1) && getState(x - 1, y + 1) == BlockType.MARKED && getOriginalState(x - 1, y + 1) == BlockType.BLANK) {
            modifyBlock(x - 1, y + 1, BlockType.UNKNOWN);
        }

        if (isValid(x, y - 1) && getState(x, y - 1) == BlockType.UNKNOWN && peekAndModifyIfMine(x, y - 1) == -1) {
            hitMines++;
        } else if (isValid(x, y - 1) && getState(x, y - 1) == BlockType.MARKED && getOriginalState(x, y - 1) == BlockType.BLANK) {
            modifyBlock(x, y - 1, BlockType.UNKNOWN);
        }

        if (isValid(x, y + 1) && getState(x, y + 1) == BlockType.UNKNOWN && peekAndModifyIfMine(x, y + 1) == -1) {
            hitMines++;
        } else if (isValid(x, y + 1) && getState(x, y + 1) == BlockType.MARKED && getOriginalState(x, y + 1) == BlockType.BLANK) {
            modifyBlock(x, y + 1, BlockType.UNKNOWN);
        }

        if (isValid(x + 1, y - 1) && getState(x + 1, y - 1) == BlockType.UNKNOWN && peekAndModifyIfMine(x + 1, y - 1) == -1) {
            hitMines++;
        } else if (isValid(x + 1, y - 1) && getState(x + 1, y - 1) == BlockType.MARKED && getOriginalState(x + 1, y - 1) == BlockType.BLANK) {
            modifyBlock(x + 1, y - 1, BlockType.UNKNOWN);
        }
        if (isValid(x + 1, y) && getState(x + 1, y) == BlockType.UNKNOWN && peekAndModifyIfMine(x + 1, y) == -1) {
            hitMines++;
        } else if (isValid(x + 1, y) && getState(x + 1, y) == BlockType.MARKED && getOriginalState(x + 1, y) == BlockType.BLANK) {
            modifyBlock(x + 1, y, BlockType.UNKNOWN);
        }

        if (isValid(x + 1, y + 1) && getState(x + 1, y + 1) == BlockType.UNKNOWN && peekAndModifyIfMine(x + 1, y + 1) == -1) {
            hitMines++;
        } else if (isValid(x + 1, y + 1) && getState(x + 1, y + 1) == BlockType.MARKED && getOriginalState(x + 1, y + 1) == BlockType.BLANK) {
            modifyBlock(x + 1, y + 1, BlockType.UNKNOWN);
        }

        if (hitMines == 0) {
            expand(coordinate);
        }
        modifyBlock(coordinate, BlockType.DISCOVERED);
        System.out.println("Hit Mines: " + hitMines);
        return hitMines;
    }

    /**
     * Retrieves the count of adjacent flags to the specified coordinate.
     *
     * @param coordinate The coordinate to check for adjacent flags.
     * @return The count of adjacent flags.
     */
    private int getAdjacentFlags(Coordinate coordinate) {
        int flags = 0;
        int x = coordinate.getX();
        int y = coordinate.getY();
        if (isValid(x - 1, y - 1) && getState(x - 1, y - 1) == BlockType.MARKED) {
            flags++;
        }
        if (isValid(x - 1, y) && getState(x - 1, y) == BlockType.MARKED) {
            flags++;
        }
        if (isValid(x - 1, y + 1) && getState(x - 1, y + 1) == BlockType.MARKED) {
            flags++;
        }
        if (isValid(x, y - 1) && getState(x, y - 1) == BlockType.MARKED) {
            flags++;
        }
        if (isValid(x, y + 1) && getState(x, y + 1) == BlockType.MARKED) {
            flags++;
        }
        if (isValid(x + 1, y - 1) && getState(x + 1, y - 1) == BlockType.MARKED) {
            flags++;
        }
        if (isValid(x + 1, y) && getState(x + 1, y) == BlockType.MARKED) {
            flags++;
        }
        if (isValid(x + 1, y + 1) && getState(x + 1, y + 1) == BlockType.MARKED) {
            flags++;
        }

        return flags;
    }

}
