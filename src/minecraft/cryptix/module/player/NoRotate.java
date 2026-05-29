package cryptix.module.player;

import cryptix.Client;
import cryptix.module.Category;
import cryptix.module.Module;

public class NoRotate extends Module{
	public float yaw, pitch;
	public boolean received;
	public NoRotate() {
		super("NoRotate", 0, Category.PLAYER);
	}
	
	@Override
	public void onPreMotion() {
		if(received && yaw != 0 && pitch != 0) {
			mc.thePlayer.rotationYaw = yaw;
			mc.thePlayer.rotationPitch = pitch;
			pitch = 0;
			yaw = 0;
			received = false;
		}
	}

}
