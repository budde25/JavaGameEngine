package dev.budde.engine;

public interface IGameLogic {

    void init(Window window) throws Exception;

    void input(Window window, MouseInput input);

    void update(float interval, MouseInput input);

    void render(Window window);

    void cleanup();

}