package minesweeper;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MineSweeper {
    private World world;
    private int livesLeft;
    private boolean won = false;
    private boolean lost = false;
    boolean worldGenerated = false;


    public void start(){
        System.out.println("Hello! Welcome to the MineSweeper by Josefu17");

        initializeGame();

        run();
        boolean running = true;
        while(running){
            if(result()){
                initializeGame();
                run();
            }else{
                if(won)System.out.println("Goodbye champ! See you next time.");
                else if(lost)System.out.println("Goodbye! See you next time.");
                running = false;
            }
        }
    }
    private void run(){
        if(!worldGenerated)
            return;
        Scanner scanner = new Scanner(System.in);
        while(!(won || lost)){
            if(world.won()){
                won = true;
                break;
            }
            Coordinate coordinate;
            try{
                if(world.getToCover() == 0 && !lost){
                    won = true;
                    break;
                }
                coordinate = nextLocation(scanner);
                if(coordinate == null)
                    continue;
                nextAction(coordinate);

            }catch(InputMismatchException exc){
                System.out.println(exc.getMessage());
            }
        }

    }



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

    private static int getInput(String message, Scanner scanner) {
        while (true) {
            try {
                System.out.println(message);
                int input = scanner.nextInt();
                if(input == -1 || (input > 0 && input < 31)){
                    return input;
                }else{ throw new InputMismatchException(); }
            } catch (InputMismatchException exc) {
                System.out.println("Invalid input. Please enter a valid number or type in \"-1\" to quit");
                scanner = new Scanner(System.in);
            }
        }
    }

    private void nextAction(Coordinate coordinate){
        int x = coordinate.getX(),y = coordinate.getY();
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
     * @return the newly generated coordinate to process in next move, or NULL if coordinate isn't valid
     */
    private Coordinate nextLocation(Scanner scanner){
        world.printWorld();
        int inputX, inputY;
        System.out.println("Total mines: " + world.getMineCount() + ", Marks Left: " + world.getMarksLeft());
        System.out.println("Enter Location please: ");
        System.out.println("x: (0 - " + (world.getRows()-1) + ")");
        inputX = scanner.nextInt();
        System.out.println("y: (0 - " + (world.getColumns()-1) +")");
        inputY = scanner.nextInt();
        Coordinate currentCoordinate = new Coordinate(inputX, inputY);
        if(!world.isValid(currentCoordinate)){
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



    private boolean result() {
        if(won){
            world.printWorld();
            System.out.println("You won!! Congrats!!!\n" +
                    "Press 1 to play again or any other button to quit");
        }else if(lost){
            world.printWorld();
            System.out.println("You lost! Game over... Good luck next time : ). Press 1 to play again or any other button to quit");
        }
        Scanner scanner = new Scanner(System.in);
        int input;
        try{
            input = scanner.nextInt();
        }catch(InputMismatchException exc){
            System.out.println("Input mismatch! Terminating the program.");
            return false;
        }
        return input == 1;
    }


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


    private BlockType getBlockState(int x, int y){
        return world.getState(x, y);
    }

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
