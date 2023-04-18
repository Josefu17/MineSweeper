package minesweeper;
public class Block {
    private int x;
    private int y;
    char character;
    BlockType blockType;
    public Block(int x, int y){
        this.x = x;
        this.y = y;
        this.character = '-';
        this.blockType = BlockType.UNKNOWN;
    }

    public char getCharacter(){
        return character;
    }

    public void modify(BlockType blockType1){
        switch (blockType1){
            case MARKED: {
                character = '?';
                blockType = BlockType.MARKED;
                break;
            } case MINE: {
                character = '*';
                blockType = BlockType.MINE;
                break;
            } case BLANK: {
                character = ' ';
                blockType = BlockType.BLANK;
            }

        }
    }

    public BlockType getBlockType() {
        return blockType;
    }
}

enum BlockType{
    UNKNOWN,
    MINE,
    MARKED,
    BLANK,
    DISCOVERED
}
