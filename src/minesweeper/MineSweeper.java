package minesweeper;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Main controller for the Minesweeper game loop and user input.
 */
public class MineSweeper
{
    private World world;

    private final Scanner scanner;

    private int livesLeft;
    private boolean won = false;
    private boolean lost = false;
    boolean worldGenerated = false;

    public MineSweeper()
    {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Entry point for the application.
     */
    public void start()
    {
        System.out.println("Hello! Welcome to the MineSweeper by Josefu17");
        boolean running = true;
        do {
            if (result()) {
                initializeGame();
                run();
            } else {
                if (won) System.out.println("Goodbye champ! See you next time.");
                else if (lost) System.out.println("Goodbye! See you next time.");
                running = false;
            }
        } while (running);
    }

    /**
     * Executes the active gameplay loop until the game is won or lost.
     */
    private void run()
    {
        if (!worldGenerated)
            return;
        while (!(won || lost)) {
            if (world.won()) {
                won = true;
                break;
            }
            Coordinate coordinate;
            try {
                if (world.getToCover() == 0 && !lost) {
                    won = true;
                    break;
                }
                coordinate = nextLocation(scanner);
                if (coordinate == null)
                    continue;
                nextAction(coordinate);

            } catch (InputMismatchException exc) {
                System.out.println(exc.getMessage());
            }
        }
    }


    /**
     * Prompts user for grid dimensions and difficulty to initialize the World.
     */
    private void initializeGame()
    {
        won = false;
        lost = false;
        worldGenerated = false;

        int inputX;
        int inputY;
        int inputDiff;
        while (!worldGenerated) {
            inputX = getValidatedInput("Number of Rows (1-30, -1 for quit):", scanner);
            if (inputX == -1) {
                System.out.println("Terminating the program, goodbye and see you next time dear gamer.");
                return;
            }

            inputY = getValidatedInput("Number of Columns: (1 - 30, -1 for quit)", scanner);
            if (inputY == -1) {
                System.out.println("Terminating the program, goodbye and see you next time dear gamer.");
                return;
            }

            System.out.println("Please choose Difficulty: \n 1- Medium \n 2- Hard");
            inputDiff = scanner.nextInt();
            if (inputDiff != 1 && inputDiff != 2) {
                System.out.println("Invalid Difficulty option, please try again.");
                continue;
            }

            world = new World(inputX, inputY, inputDiff);
            livesLeft = inputDiff == 2 ? 0 : 1;

            System.out.println("Good Luck!");
            worldGenerated = true;
        }
    }

    /**
     * Validates that user input is an integer within the accepted range (1-30).
     */
    private static int getValidatedInput(String message, Scanner scanner)
    {
        while (true) {
            try {
                System.out.println(message);
                int input = scanner.nextInt();
                if (input == -1 || (input > 0 && input < 31)) {
                    return input;
                } else {
                    throw new InputMismatchException();
                }
            } catch (InputMismatchException exc) {
                System.out.println("Invalid input. Please enter a valid number or type in \"-1\" to quit");
            }
        }
    }

    /**
     * Processes the user's choice (Check, Mark, Unmark, Expand) for a specific block.
     */
    private void nextAction(Coordinate coordinate)
    {
        int x = coordinate.x(), y = coordinate.y();
        BlockType currentState = world.getState(coordinate.x(), coordinate.y());
        world.modifyBlock(coordinate, BlockType.IN_PROGRESS);

        System.out.println(world.toString());

        System.out.println("Location: (" + x + "," + y + ")");
        System.out.println("Current State: " + currentState.toString().toLowerCase());
        int input;
        try {
            if (currentState == BlockType.DISCOVERED) {
                System.out.println("Your Action: ");
                System.out.println("1- Auto-Expand\n2- Go Back");
                input = scanner.nextInt();
                if (input == 1) {
                    int minesHit = world.forceExpand(coordinate, currentState);
                    if (minesHit < 0) {
                        System.out.println("Invalid Expansion!");
                    } else if (minesHit > livesLeft) {
                        lost = true;
                    } else if (minesHit > 0) {
                        mightyTouch(coordinate);
                    }
                }
            } else {
                System.out.println("Your Action: ");
                System.out.println("1- Check\n2- Mark\n3- Unmark\n4- Go Back");
                input = scanner.nextInt();
                switch (input) {
                    case 1 -> {
                        if (currentState == BlockType.MARKED) {
                            System.out.println("Location given is currently marked, you first need to unmark it in" +
                                    "order to be able to check it.");
                            System.out.println("1- Unmark\n2- Cancel");
                            input = scanner.nextInt();
                            if (input == 1) {
                                unmark(coordinate);
                            } else if (input == 2) {
                                world.modifyBlock(coordinate, BlockType.MARKED);
                            } else {
                                System.out.println("Invalid input, canceling the process anyway. (i hate you)");
                                world.modifyBlock(coordinate, BlockType.MARKED);
                            }
                        } else {
                            checkBlock(coordinate);
                        }
                    }
                    case 2 -> {
                        if (currentState != BlockType.UNKNOWN) {
                            System.out.println("Location not markable!");
                            world.modifyBlock(coordinate, currentState);
                        } else if (world.getMarksLeft() <= 0) {
                            System.out.println("You don't have any marks left, you first need to unmark some block(s)");
                            world.modifyBlock(coordinate, currentState);
                        } else {
                            if (world.getOriginalState(coordinate) == BlockType.MINE) {
                                world.decrementMinesLeft();
                            }
                            mark(coordinate);
                        }
                    }
                    case 3 -> {
                        if (currentState != BlockType.MARKED) {
                            System.out.println("Given block needs to be marked in order to be unmarkable!");
                            world.modifyBlock(coordinate, currentState);
                        } else {
                            if (world.getOriginalState(coordinate) == BlockType.MINE) {
                                world.incrementMinesLeft();
                            }
                            unmark(coordinate);
                        }
                    }
                    default -> world.modifyBlock(coordinate, currentState);
                }
            }
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
            world.modifyBlock(coordinate, currentState);
        }

    }

    private void mark(Coordinate coordinate)
    {
        world.decrementMarksLeft();
        world.decrementToCover();
        world.modifyBlock(coordinate, BlockType.MARKED);
    }

    private void unmark(Coordinate coordinate)
    {
        world.incrementMarksLeft();
        world.incrementToCover();
        world.modifyBlock(coordinate, BlockType.UNKNOWN);
    }

    /**
     * Prompts the user for the next coordinate to interact with.
     */
    private Coordinate nextLocation(Scanner scanner)
    {
        System.out.println(world.toString());

        int inputX, inputY;
        System.out.println("Total mines: " + world.getMineCount() + ", Marks Left: " + world.getMarksLeft());
        System.out.println("Enter Location please: (-1, -1) to end the game");
        System.out.println("x: (0 - " + (world.getRows() - 1) + ")");
        inputX = scanner.nextInt();
        System.out.println("y: (0 - " + (world.getColumns() - 1) + ")");
        inputY = scanner.nextInt();
        Coordinate currentCoordinate = new Coordinate(inputX, inputY);
        if (inputX == -1 && inputY == -1) {
            lost = true;
            return null;
        } else if (world.isOutOfBounds(currentCoordinate)) {
            System.out.println("Invalid Coordinates, please make sure you enter a location that is within (0,0) and ("
                    + (world.getRows() - 1) + "," + (world.getColumns() - 1) + ")");
            return null;
        } else if (getBlockState(inputX, inputY) == BlockType.MINE || getBlockState(inputX, inputY) == BlockType.BLANK) {
            System.out.println("Location is already cleared, please try another location!");
            return null;
        }
        return currentCoordinate;
    }


    /**
     * Displays Game Over or Victory screens and checks if user wants to replay.
     */
    private boolean result()
    {
        if (!worldGenerated)
            return true;
        if (won) {
            System.out.println(world.toString());
            System.out.println("You won!! Congrats!!!\n" +
                    "Press 1 to play again or any other button to quit");
        } else if (lost) {
            System.out.println(world.toString());
            System.out.println("You lost! Game over... Good luck next time : ). Press 1 to play again or any other button to quit");
        }
        String input;
        try {
            input = scanner.next();
        } catch (InputMismatchException exc) {
            System.out.println("Input mismatch! Terminating the program.");
            return false;
        }
        return input.equals("1");
    }

    /**
     * Reveals a block and handles consequences (Game Over or Recursive Reveal).
     */
    private void checkBlock(Coordinate currentCoordinate)
    {
        if (world.getOriginalState(currentCoordinate) == BlockType.MINE) {
            world.decrementMinesLeft();
            world.decrementToCover();
            world.modifyBlock(currentCoordinate, BlockType.MINE);
            if (livesLeft == 0 || !mightyTouch(currentCoordinate)) {
                lost = true;
            }
        } else {
            world.check(currentCoordinate);
        }
    }

    /**
     * Retrieves the current state of the block at the specified coordinates
     */
    private BlockType getBlockState(int x, int y)
    {
        return world.getState(x, y);
    }

    /**
     * Activates the 'Second Chance' mechanic on Medium difficulty.
     */
    private boolean mightyTouch(Coordinate currentCoordinate)
    {
        System.out.println(world.toString());

        System.out.println("You hit a mine... BUT!! You've been blessed by the creator's mighty touch, therefore you get a 2nd chance!");
        System.out.println("1- Continue playing\n2- Quit");
        if (scanner.nextInt() == 2) {
            lost = true;
            return false;
        } else {
            if (world.getToCover() == 1) {
                won = true;
            }
            world.modifyBlock(currentCoordinate, BlockType.MINE);
            world.decrementMarksLeft();
            livesLeft = 0;
            return true;
        }
    }

}
