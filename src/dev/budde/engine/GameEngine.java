package dev.budde.engine;

import Game.TestGame;

public class GameEngine implements Runnable {

    private final Thread gameLoopThread;

    public static final int TARGET_FPS = 144;
    public static final int TARGET_UPS = 30;

    private final Timer timer;
    private final IGameLogic gameLogic;
    private final Window window;
    private final MouseInput mouseInput;

    public GameEngine(String title, int width, int height, boolean vSync, IGameLogic gameLogic) throws Exception {
        this.gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        this.gameLogic = gameLogic;
        window = new Window(title, width, height, vSync);
        timer = new Timer();
        mouseInput = new MouseInput();
    }

    protected void init() throws Exception {
        window.init();
        timer.init();
        mouseInput.init(window);
        gameLogic.init(window);
    }

    public void start() {

        // Fix for MacOs since it doesn't support another thread
        String osName = System.getProperty("os.name");
        if (osName.contains("Mac")) {
            gameLoopThread.run();
        } else {
            gameLoopThread.start();
        }
    }

    @Override
    public void run() {
        try {
            init();
            loop();
        } catch (Exception excp) {
            excp.printStackTrace();
        } finally {
            cleanup();
        }
    }

    protected void loop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if (!window.isvSync()) {
                sync();
            }
        }
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            }
        }

    }

    protected void input() {
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }

    protected void update(float interval) {
        gameLogic.update(interval, mouseInput);
    }

    protected void render() {
        gameLogic.render(window);
        window.update();
    }

    private void cleanup() {
        gameLogic.cleanup();
    }
}
