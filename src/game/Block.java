package game;

import dev.budde.engine.*;
import dev.budde.engine.graph.*;

public class Block extends GameItem {
	public Block(Mesh mesh) {
		super(mesh);
	}
	
	public Block(Texture texture, float x, float y, float z) {
		this(init(texture));
		this.setPosition(x, y, z);
	}
	
	private static Mesh init(Texture texture) {
   		return null;
	}
}
