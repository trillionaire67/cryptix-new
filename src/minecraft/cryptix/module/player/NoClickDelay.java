package cryptix.module.player;

import cryptix.module.Category;
import cryptix.module.Module;

public class NoClickDelay extends Module{

	public NoClickDelay() {
		super("NoClickDelay", 0, Category.PLAYER);
	}
	
	@Override
	public void onPreUpdate() {
		mc.leftClickCounter = 0;
	}

}
