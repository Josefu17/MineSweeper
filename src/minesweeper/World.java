package minesweeper;

import java.util.Random;

public class World {

    private Block[][] theWorld;
    private final int ROWS;
    private int mineCount;
    private int toCover;
    private int marksLeft;
    private int minesLeft;

    private final int COLUMNS;
    /**
     * Generate the world if
     * @param x is a value between 1 and 100 (both inclusive)
     * @param y is a value between 1 and 100
     * throw IndexOutOfBoundsException otherwise
     */
    public World(int x, int y, int difficulty) throws IndexOutOfBoundsException{
        if(x > 0 && x <= 100 && y > 0 && y <= 100){
            ROWS = x;
            COLUMNS = y;
        }else{
            throw new IndexOutOfBoundsException();
        }
        initialize(ROWS, COLUMNS, difficulty);

    }

    public boolean won(){
        return toCover == minesLeft;
    }
    public void printWorld(){
        System.out.print("  ");
        for(int i=0; i<COLUMNS; i++){
            System.out.print(i + " ");
        }
        for(int i=0; i<ROWS; i++){
            System.out.print("\n" + i + " ");
            for(int j=0; j<COLUMNS; j++){
                System.out.print(theWorld[i][j].getCharacter());
                System.out.print(" ");
            }
        }
        System.out.println();

    }

    private void initialize(int x, int y, int difficulty){
        theWorld = new Block[ROWS][COLUMNS];
        for(int i=0; i<ROWS; i++)
            theWorld[i] = new Block[COLUMNS];

        int minesToPlant = difficulty == 2 ? 2*(x*y)/5 : (x*y)/4;
        mineCount = minesToPlant;
        minesLeft = mineCount;
        toCover = x * y;
        marksLeft = mineCount;

        Random random = new Random();
        while(minesToPlant > 0){
            int nextX = random.nextInt(ROWS);
            int nextY = random.nextInt(COLUMNS);
            if(theWorld[nextX][nextY] == null){
                theWorld[nextX][nextY] = new Block(nextX, nextY, BlockType.MINE);
                minesToPlant--;
            }
        }
        for(int i=0; i<ROWS; i++){
            for(int j=0; j<COLUMNS; j++){
                if(theWorld[i][j] == null){
                    theWorld[i][j] = new Block(i,j,BlockType.BLANK);
                }
            }
        }
    }

    public int getMineCount() {
        return mineCount;
    }

    public int getRows(){
        return ROWS;
    }

    public int getColumns(){
        return COLUMNS;
    }

    protected BlockType getState(int x, int y){
        return theWorld[x][y].getBlockType();
    }

    protected BlockType getState(Coordinate coordinate){
        return theWorld[coordinate.getX()][coordinate.getY()].getBlockType();
    }

    protected BlockType getOriginalState(int x, int y){
        return theWorld[x][y].getOriginalType();
    }

    protected BlockType getOriginalState(Coordinate coordinate){
        return theWorld[coordinate.getX()][coordinate.getY()].getOriginalType();
    }

    public void modifyBlock(Coordinate coordinate, BlockType blockType){
        if(isValid(coordinate)){
            if(blockType == BlockType.DISCOVERED){
                theWorld[coordinate.getX()][coordinate.getY()].modify(blockType, getAdjacentMinesCount(coordinate));
            }else{
                theWorld[coordinate.getX()][coordinate.getY()].modify(blockType);
            }
        }else{
            System.out.println("Invalid Point! Nothing is done, terminating...");
        }
    }

    public void modifyBlock(int x, int y, BlockType blockType){
        modifyBlock(new Coordinate(x, y), blockType);
    }

    public boolean isValid(Coordinate coordinate){
        return (coordinate.getX()>=0 && coordinate.getX()<ROWS && coordinate.getY()>=0 && coordinate.getY()<COLUMNS);
    }

    public boolean isValid(int x, int y){
        return (x>=0 && x<ROWS && y>=0 && y<COLUMNS);
    }

    public int getMarksLeft(){return marksLeft;}
    public int getToCover(){return toCover;}
    public void decrementMarksLeft(){marksLeft--;}
    public void incrementMarksLeft(){marksLeft++;}
    public void decrementToCover(){toCover--;}
    public void incrementToCover(){toCover++;}
    public void decrementMinesLeft(){minesLeft--;}
    public void incrementMinesLeft(){minesLeft++;}

    public int getAdjacentMinesCount(Coordinate coordinate){
        int mines = 0;
        int x = coordinate.getX();
        int y = coordinate.getY();
        if(isValid(x-1, y-1) && getOriginalState(x-1, y-1) == BlockType.MINE){mines++;}
        if(isValid(x-1, y) && getOriginalState(x-1, y) == BlockType.MINE){mines++;}
        if(isValid(x-1, y+1) && getOriginalState(x-1, y+1) == BlockType.MINE){mines++;}
        if(isValid(x, y-1) && getOriginalState(x, y-1) == BlockType.MINE){mines++;}
        if(isValid(x, y+1) && getOriginalState(x, y+1) == BlockType.MINE){mines++;}
        if(isValid(x+1, y-1) && getOriginalState(x+1, y-1) == BlockType.MINE){mines++;}
        if(isValid(x+1, y) && getOriginalState(x+1, y) == BlockType.MINE){mines++;}
        if(isValid(x+1, y+1) && getOriginalState(x+1, y+1) == BlockType.MINE){mines++;}

        return mines;
    }

    public int check(Coordinate coordinate){
        if(getOriginalState(coordinate.getX(), coordinate.getY()) == BlockType.MINE)
            return -1;
        else if(getState(coordinate) == BlockType.DISCOVERED)
            return 0;
        int adjacentMines = getAdjacentMinesCount(coordinate);
        decrementToCover();
        if(adjacentMines == 0){
            modifyBlock(coordinate, BlockType.BLANK);
            expand(coordinate);
        }else{
            modifyBlock(coordinate, BlockType.DISCOVERED);
        }
        return 0;
    }

    public int check(int x, int y){
        return check(new Coordinate(x, y));
    }
    public void expand(Coordinate coordinate){
        int x = coordinate.getX();
        int y = coordinate.getY();
        if(isValid(x-1, y-1) && getState(x-1, y-1) == BlockType.UNKNOWN){check(x-1, y-1);}
        if(isValid(x-1, y) && getState(x-1, y) == BlockType.UNKNOWN) {check(x-1, y);}
        if(isValid(x-1, y+1) && getState(x-1, y+1) == BlockType.UNKNOWN){check(x-1, y+1);}
        if(isValid(x, y-1) && getState(x, y-1) == BlockType.UNKNOWN){check(x, y-1);}
        if(isValid(x, y+1) && getState(x, y+1) == BlockType.UNKNOWN){check(x, y+1);}
        if(isValid(x+1, y-1) && getState(x+1, y-1) == BlockType.UNKNOWN){check(x+1, y-1);}
        if(isValid(x+1, y) && getState(x+1, y) == BlockType.UNKNOWN){check(x+1, y);}
        if(isValid(x+1, y+1) && getState(x+1, y+1) == BlockType.UNKNOWN){check(x+1, y+1);}
    }


    public int peekAndModifyIfMine(int x, int y){
        if(getOriginalState(x, y) == BlockType.MINE){
            modifyBlock(x, y, BlockType.MINE);
            decrementMinesLeft();
            decrementToCover();
            return -1;
        }
        return 0;
    }

    public int forceExpand(Coordinate coordinate, BlockType currentState){
        if(currentState != BlockType.DISCOVERED || getAdjacentFlags(coordinate) != getAdjacentMinesCount(coordinate)){
            modifyBlock(coordinate, currentState);
            return -1;
        }
        int x = coordinate.getX();
        int y = coordinate.getY();
        int hitMines = 0;
        if(isValid(x-1, y-1) && getState(x-1, y-1) == BlockType.UNKNOWN && peekAndModifyIfMine(x-1, y-1) == -1){
            hitMines++;
        }else if(isValid(x-1, y-1) && getState(x-1, y-1) == BlockType.MARKED && getOriginalState(x-1, y-1) == BlockType.BLANK){
            modifyBlock(x-1, y-1, BlockType.UNKNOWN);
        }

        if(isValid(x-1, y) && getState(x-1, y) == BlockType.UNKNOWN && peekAndModifyIfMine(x-1, y) == -1) {
            hitMines++;
        }else if(isValid(x-1, y) && getState(x-1, y) == BlockType.MARKED && getOriginalState(x-1, y) == BlockType.BLANK){
            modifyBlock(x-1, y, BlockType.UNKNOWN);
        }

        if(isValid(x-1, y+1) && getState(x-1, y+1) == BlockType.UNKNOWN && peekAndModifyIfMine(x-1, y+1) == -1){
                hitMines++;
        }else if(isValid(x-1, y+1) && getState(x-1, y+1) == BlockType.MARKED && getOriginalState(x-1, y+1) == BlockType.BLANK){
            modifyBlock(x-1, y+1, BlockType.UNKNOWN);
        }

        if(isValid(x, y-1) && getState(x, y-1) == BlockType.UNKNOWN && peekAndModifyIfMine(x, y-1) == -1){
            hitMines++;
        }else if(isValid(x, y-1) && getState(x, y-1) == BlockType.MARKED && getOriginalState(x, y-1) == BlockType.BLANK){
            modifyBlock(x, y-1, BlockType.UNKNOWN);
        }

        if(isValid(x, y+1) && getState(x, y+1) == BlockType.UNKNOWN && peekAndModifyIfMine(x, y+1) == -1){
                hitMines++;
        }else if(isValid(x, y+1) && getState(x, y+1) == BlockType.MARKED && getOriginalState(x, y+1) == BlockType.BLANK){
            modifyBlock(x, y+1, BlockType.UNKNOWN);
        }

        if(isValid(x+1, y-1) && getState(x+1, y-1) == BlockType.UNKNOWN && peekAndModifyIfMine(x+1, y-1) == -1){
            hitMines++;
        }else if(isValid(x+1, y-1) && getState(x+1, y-1) == BlockType.MARKED && getOriginalState(x+1, y-1) == BlockType.BLANK){
            modifyBlock(x+1, y-1, BlockType.UNKNOWN);
        }
        if(isValid(x+1, y) && getState(x+1, y) == BlockType.UNKNOWN && peekAndModifyIfMine(x+1, y) == -1){
            hitMines++;
        }else if(isValid(x+1, y) && getState(x+1, y) == BlockType.MARKED && getOriginalState(x+1, y) == BlockType.BLANK){
            modifyBlock(x+1, y, BlockType.UNKNOWN);
        }

        if(isValid(x+1, y+1) && getState(x+1, y+1) == BlockType.UNKNOWN && peekAndModifyIfMine(x+1, y+1) == -1){
            hitMines++;
        }else if(isValid(x+1, y+1) && getState(x+1, y+1) == BlockType.MARKED && getOriginalState(x+1, y+1) == BlockType.BLANK){
            modifyBlock(x+1, y+1, BlockType.UNKNOWN);
        }

        if (hitMines == 0) {
            expand(coordinate);
        }
        modifyBlock(coordinate, BlockType.DISCOVERED);
        System.out.println("Hit Mines: " + hitMines);
        return hitMines;
    }

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
