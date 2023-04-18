package minesweeper;

import javax.swing.text.html.BlockView;

public class World {

    private Block[][] theWorld;
    private final int ROWS;
    private final int COLUMNS;
    /**
     * Generate the world if
     * @param x is a value between 1 and 100 (both inclusive)
     * @param y is a value between 1 and 100
     * throw IndexOutOfBoundsException otherwise
     */
    public World(int x, int y) throws IndexOutOfBoundsException{
        if(x > 0 && x <= 100 && y > 0 && y <= 100){
            ROWS = x;
            COLUMNS = y;
            theWorld = new Block[ROWS][COLUMNS];
            for(int i=0; i<ROWS; i++){
                for(int j=0; j<COLUMNS; j++){
                    theWorld[i][j] = new Block(i, j);
                }
            }
        }else{
            throw new IndexOutOfBoundsException();
        }
    }

    public void printWorld(){
        for(int i=0; i<ROWS; i++){
            for(int j=0; j<COLUMNS; j++){
                System.out.print(theWorld[i][j].getCharacter());
                System.out.print(" ");
            }
            System.out.println();
        }

    }

    public int getRows(){
        return ROWS;
    }

    public int getColumns(){
        return COLUMNS;
    }

    public BlockType getState(int x, int y){
        return theWorld[x][y].getBlockType();
    }

    public void modifyBlock(int x, int y, BlockType blockType){
        if(isValid(x, y)){
            theWorld[x][y].modify(blockType);
        }else{
            System.out.println("Invalid Point! Nothing is done, terminating...");
        }
    }

    public boolean isValid(int x, int y){
        return (x>=0 && x<ROWS && y>=0 && y<COLUMNS);
    }


}
