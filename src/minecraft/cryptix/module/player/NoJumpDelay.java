package cryptix.module.player;

import cryptix.module.Category;
import cryptix.module.Module;

public class NoJumpDelay extends Module{

	public NoJumpDelay() {
		super("NoJumpDelay", 0, Category.PLAYER);
	}
	
	@Override
	public void onPreUpdate() {
		mc.thePlayer.jumpTicks = 0;
	}

}
