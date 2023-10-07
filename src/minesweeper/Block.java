package minesweeper;
public class Block {
    Coordinate coordinate;
    private char character;
    private BlockType blockType;

    private final BlockType originalType;

    public Block(int x, int y, BlockType type){
        coordinate = new Coordinate(x, y);
        character = '-';
        blockType = BlockType.UNKNOWN;
        originalType = type;
    }

    public char getCharacter(){
        return character;
    }

    public void modify(BlockType blockType1){
        switch (blockType1) {
            case MARKED -> {
                character = '?';
                blockType = BlockType.MARKED;
            }
            case MINE -> {
                character = '*';
                blockType = BlockType.MINE;
            }
            case BLANK -> {
                character = ' ';
                blockType = BlockType.BLANK;
            }
            case IN_PROGRESS -> {
                character = 'X';
                blockType = BlockType.IN_PROGRESS;
            }
            case UNKNOWN -> {
                character = '-';
                blockType = BlockType.UNKNOWN;
            }
        }
    }

    public void modify(BlockType blocktype, int i){
        if(blocktype == BlockType.DISCOVERED){
            character = (char)(i+48);
            this.blockType = BlockType.DISCOVERED;
        }
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public BlockType getOriginalType() {
        return originalType;
    }


}

