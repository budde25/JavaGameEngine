package game;

import dev.budde.engine.GameEngine;
import dev.budde.engine.IGameLogic;

public class Main {

    public static void main(String[] args) {
        try {
            boolean vSync = true;
            IGameLogic gameLogic = new TestGame();
            GameEngine gameEng = new GameEngine("Ethan's Engine",
                    600, 480, vSync, gameLogic);
            gameEng.start();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }

}