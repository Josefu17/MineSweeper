package minesweeper;

/*
Enum to define different possible block types

 */
/**
 * Enumeration representing different states of blocks in a Minesweeper game.
 */
public enum BlockType {
    UNKNOWN,    // Default state for unexplored blocks.
    MINE,       // Represents a block containing a mine.
    MARKED,     // Indicates a flagged or marked block by the player.
    BLANK,      // Represents an empty block with no adjacent mines.
    DISCOVERED, // Indicates a successfully revealed block.
    IN_PROGRESS // Represents a block currently being investigated.
}
