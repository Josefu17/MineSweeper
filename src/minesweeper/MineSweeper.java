package minesweeper;

import java.util.Random;

public class MineSweeper {
    World world;


    private void initializeWorld(int x, int y){
        world = new World(x, y);
        int minesToPlant = x*y / 4;
        Random random = new Random();
        while(minesToPlant > 0){
            int nextX = random.nextInt(world.getRows());
            int nextY = random.nextInt(world.getColumns());
            if(world.getState(nextX, nextY) != BlockType.MINE){
                world.modifyBlock(nextX, nextY, BlockType.MINE);
                minesToPlant--;
            }
        }
    }

    public void run(){
        initializeWorld(10, 10);
        world.printWorld();
    }
}
