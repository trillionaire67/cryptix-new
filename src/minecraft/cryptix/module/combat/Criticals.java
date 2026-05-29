package cryptix.module.combat;

import cryptix.Client;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.MovementUtils;

public class Criticals extends Module{
	private int tick;
	public Criticals() {
		super("Criticals", 0, Category.COMBAT);
	}
	
	@Override
	public void onPreMotion() {
		if(mc.thePlayer.hurtTime > 0) tick = 10;
		if(tick > 0 && mc.thePlayer.onGround && mc.thePlayer.posY % 1 == 0) {
			mc.thePlayer.motionY = 0.027;
		}
		tick--;
	}

}
