package minesweeper;

/**
 * Represents a block in a Minesweeper game.
 */
public class Block {
    Coordinate coordinate;
    private char character;
    private BlockType blockType;
    private final BlockType originalType;

    /**
     * Constructs a Block with the specified coordinates and initial type.
     *
     * @param x    The x-coordinate of the block.
     * @param y    The y-coordinate of the block.
     * @param type The initial type of the block.
     */
    public Block(int x, int y, BlockType type) {
        coordinate = new Coordinate(x, y);
        character = '-';
        blockType = BlockType.UNKNOWN;
        originalType = type;
    }

    /**
     * Gets the character representation of the block.
     *
     * @return The character representation of the block.
     */
    public char getCharacter() {
        return character;
    }

    /**
     * Modifies the block based on the specified BlockType.
     *
     * @param blockType The BlockType to modify the block to.
     */
    public void modify(BlockType blockType) {
        switch (blockType) {
            case MARKED -> {
                character = '?';
                this.blockType = BlockType.MARKED;
            }
            case MINE -> {
                character = '*';
                this.blockType = BlockType.MINE;
            }
            case BLANK -> {
                character = ' ';
                this.blockType = BlockType.BLANK;
            }
            case IN_PROGRESS -> {
                character = 'X';
                this.blockType = BlockType.IN_PROGRESS;
            }
            case UNKNOWN -> {
                character = '-';
                this.blockType = BlockType.UNKNOWN;
            }
        }
    }

    /**
     * Modifies the block to the specified BlockType and assigns a numeric character
     * for a discovered block.
     *
     * @param blockType The BlockType to modify the block to.
     * @param i         The numeric character for a discovered block.
     */
    public void modify(BlockType blockType, int i) {
        if (blockType == BlockType.DISCOVERED) {
            character = (char) (i + 48);
            this.blockType = BlockType.DISCOVERED;
        }
    }

    /**
     * Gets the current BlockType of the block.
     *
     * @return The current BlockType of the block.
     */
    public BlockType getBlockType() {
        return blockType;
    }

    /**
     * Gets the original BlockType of the block when it was created.
     *
     * @return The original BlockType of the block.
     */
    public BlockType getOriginalType() {
        return originalType;
    }
}
