package minesweeper;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The MineSweeper class represents the main controller for the Minesweeper game.
 * It manages the game state, user input, and interactions with the World.
 */
public class MineSweeper {
    private World world;
    private int livesLeft;
    private boolean won = false;
    private boolean lost = false;
    boolean worldGenerated = false;

    /**
     * Starts the Minesweeper game, initializing the game and entering the main game loop.
     */
    public void start() {
        System.out.println("Hello! Welcome to the MineSweeper by Josefu17");
        boolean running = true;
        do{
            if (result()) {
                initializeGame();
                run();
            } else {
                if (won) System.out.println("Goodbye champ! See you next time.");
                else if (lost) System.out.println("Goodbye! See you next time.");
                running = false;
            }
        }while(running);
    }

    /**
     * Runs the main game loop, processing user input and updating the game state.
     */
    private void run() {
        if (!worldGenerated)
            return;
        Scanner scanner = new Scanner(System.in);
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
     * Initializes the Minesweeper game by prompting the user for input
     * to set up the game world and difficulty.

     * This method initializes game-related variables, such as whether the player
     * has won or lost, and whether the game world has been generated. It prompts
     * the user for the number of rows, columns, and difficulty level to create
     * a new game world. The user can choose to quit the game at any point by
     * entering "-1".

     * The method continues to prompt the user until a valid game world is generated
     * or the user chooses to quit.
     */
    private void initializeGame(){
        won = false;
        lost = false;
        worldGenerated = false;

        Scanner scanner = new Scanner(System.in);
        int inputX;
        int inputY;
        int inputDiff;
        while(!worldGenerated){
            inputX = getInput("Number of Rows (1-30, -1 for quit):", scanner);
            if (inputX == -1) {
                System.out.println("Terminating the program, goodbye and see you next time dear gamer.");
                return;
            }

            inputY = getInput("Number of Columns: (1 - 30, -1 for quit)", scanner);
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
     * Retrieves input from the user, ensuring it is a valid integer within a specified range.
     * This method prompts the user with the provided message and expects an integer input.
     * It continues to prompt the user until a valid integer within the specified range
     * (1 to 30, inclusive, or -1 to quit) is entered. If the user enters an invalid input,
     * an InputMismatchException is thrown, and the user is prompted to enter a valid number
     * or type "-1" to quit.
     *
     * @param message The message prompt for the user.
     * @param scanner The Scanner object for user input.
     * @return The user input as an integer within the specified range or -1 to quit.
     * @throws InputMismatchException If the user enters an invalid input other than -1.
     */
    private static int getInput(String message, Scanner scanner) {
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
                scanner = new Scanner(System.in);
            }
        }
    }

    /**
     * Performs the next action based on the user's input for a given coordinate in the Minesweeper game.
     * <p>
     * This method is responsible for handling user input and executing actions such as checking, marking,
     * un-marking, or auto-expanding a block on the Minesweeper game board. The user is prompted with the
     * available actions depending on the current state of the selected block.
     * </p>
     *
     * @param coordinate The coordinate for which the user is performing the action.
     */
    private void nextAction(Coordinate coordinate){
        int x = coordinate.getX(), y = coordinate.getY();
        BlockType currentState = world.getState(coordinate.getX(), coordinate.getY());
        world.modifyBlock(coordinate, BlockType.IN_PROGRESS);
        world.printWorld();
        System.out.println("Location: (" + x + "," + y + ")");
        System.out.println("Current State: " + currentState.toString().toLowerCase());
        Scanner scanner = new Scanner(System.in);
        int input;
        try{
            if(currentState == BlockType.DISCOVERED){
                System.out.println("Your Action: ");
                System.out.println("1- Auto-Expand\n2- Go Back");
                input = scanner.nextInt();
                if(input == 1){
                    int minesHit = world.forceExpand(coordinate, currentState);
                    if(minesHit < 0){
                        System.out.println("Invalid Expansion!");
                    }
                    else if(minesHit > livesLeft){
                        lost = true;
                    }else if(minesHit > 0){
                        mightyTouch(coordinate);
                    }
                }
            }else{
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
                    default ->
                            world.modifyBlock(coordinate, currentState);
                }
            }
        }catch(Exception exc){
            System.out.println(exc.getMessage());
            world.modifyBlock(coordinate, currentState);
        }

    }

    private void mark(Coordinate coordinate){
        world.decrementMarksLeft();
        world.decrementToCover();
        world.modifyBlock(coordinate, BlockType.MARKED);
    }
    private void unmark(Coordinate coordinate){
        world.incrementMarksLeft();
        world.incrementToCover();
        world.modifyBlock(coordinate, BlockType.UNKNOWN);
    }

    /**
     *
     * @return the newly generated coordinate to process in next move, or NULL if coordinate isn't valid or is already
     * cleared (blank field)
     */
    private Coordinate nextLocation(Scanner scanner){
        world.printWorld();
        int inputX, inputY;
        System.out.println("Total mines: " + world.getMineCount() + ", Marks Left: " + world.getMarksLeft());
        System.out.println("Enter Location please: (-1, -1) to end the game");
        System.out.println("x: (0 - " + (world.getRows()-1) + ")");
        inputX = scanner.nextInt();
        System.out.println("y: (0 - " + (world.getColumns()-1) +")");
        inputY = scanner.nextInt();
        Coordinate currentCoordinate = new Coordinate(inputX, inputY);
        if(inputX == -1 && inputY == -1){
            lost = true;
            return null;
        }
        else if(!world.isValid(currentCoordinate)){
            System.out.println("Invalid Coordinates, please make sure you enter a location that is within (0,0) and ("
                    + (world.getRows()-1) + "," + (world.getColumns()-1) + ")");
            return null;
        }
        else if(getBlockState(inputX, inputY) == BlockType.MINE || getBlockState(inputX, inputY) == BlockType.BLANK){
            System.out.println("Location is already cleared, please try another location!");
            return null;
        }
        return currentCoordinate;
    }


    /**
     * Displays the result of the game and prompts the user for the next action.
     * <p>
     * This method prints the game board and notifies the user if they have won or lost the game.
     * The user is then prompted to either play again by pressing 1 or quit by pressing any other button.
     * </p>
     *
     * @return {@code true} if the user chooses to play again or if the world is being generated for the first time,
     * {@code false} otherwise.
     */
    private boolean result() {
        if(!worldGenerated)
            return true;
        if(won){
            world.printWorld();
            System.out.println("You won!! Congrats!!!\n" +
                    "Press 1 to play again or any other button to quit");
        }else if(lost){
            world.printWorld();
            System.out.println("You lost! Game over... Good luck next time : ). Press 1 to play again or any other button to quit");
        }
        Scanner scanner = new Scanner(System.in);
        String input;
        try{
            input = scanner.next();
        }catch(InputMismatchException exc){
            System.out.println("Input mismatch! Terminating the program.");
            return false;
        }
        return input.equals("1");
    }

    /**
     * Checks the block at the specified coordinate in the Minesweeper game.
     * <p>
     * This method checks the block at the given coordinate and performs actions based on the block's original state.
     * If the block contains a mine, it decrements the mine count, updates the game state, and checks if the user
     * has additional lives to continue. If the block does not contain a mine, it calls the {@code check} method
     * to reveal neighboring blocks.
     * </p>
     *
     * @param currentCoordinate The coordinate of the block to be checked.
     */
    private void checkBlock(Coordinate currentCoordinate) {
        if(world.getOriginalState(currentCoordinate) == BlockType.MINE){
            world.decrementMinesLeft();world.decrementToCover();
            world.modifyBlock(currentCoordinate, BlockType.MINE);
            if(livesLeft == 0 || !mightyTouch(currentCoordinate)) {
                lost = true;
            }
        }else{
            world.check(currentCoordinate);
        }
    }

    /**
     * Retrieves the current state of the block at the specified coordinates
     * <p>
     * This method returns the current state (e.g., UNKNOWN, MINE, MARKED, etc.) of the block located
     * at the specified coordinates in the Minesweeper game world.
     * </p>
     *
     * @param x The x-coordinate of the block.
     * @param y The y-coordinate of the block.
     * @return The {@code BlockType} representing the current state of the block at the given coordinates.
     */
    private BlockType getBlockState(int x, int y){
        return world.getState(x, y);
    }

    /**
     * Handles the special case of hitting a mine and receiving a second chance in the Minesweeper game.
     * <p>
     * This method prints a message indicating that the player has hit a mine but is granted a second chance. This only
     * happens if the player has chosen the "medium" difficulty and only happens once. It is also added as a kind of
     * easter-egg : )
     * The player is prompted to either continue playing or quit the game. If the player chooses to continue
     * playing, the game state is updated, marks are decremented, and the player's lives are set to zero.
     * </p>
     *
     * @param currentCoordinate The coordinate of the block that triggered the mighty touch.
     * @return {@code true} if the player chooses to continue playing, {@code false} if the player decides to quit.
     */
    private boolean mightyTouch(Coordinate currentCoordinate){
        world.printWorld();
        System.out.println("You hit a mine... BUT!! You've been blessed by the creator's mighty touch, therefore you get a 2nd chance!");
        System.out.println("1- Continue playing\n2- Quit");
        Scanner scanner = new Scanner(System.in);
        if(scanner.nextInt() == 2){
            lost = true;
            return false;
        }else{
            if(world.getToCover() == 1){
                won = true;
            }
            world.modifyBlock(currentCoordinate, BlockType.MINE);
            world.decrementMarksLeft();
            livesLeft = 0;
            return true;
        }
    }

}
