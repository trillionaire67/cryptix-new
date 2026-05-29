package cryptix.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;

public class Step extends Module{
	private Setting mode;
	private boolean wasCollided;
	public Step() {
		super("Step", 0, Category.MOVEMENT);
		ArrayList<String> modes = new ArrayList<String>(Arrays.asList("Normal", "BlocksMC"));
		Client.instance.settingsManager.addSetting(mode = new Setting("Step Mode", this, "Packet", modes));
	}
	
	@Override
    public void onDisable() {
        mc.thePlayer.stepHeight = 0.5F;
    }
	
	@Override
	public void onRender3D() {
		this.setDisplayName(this.getName() + this.getUppercaseSuffix(mode.getString()));
		mc.thePlayer.stepHeight = 0.5F;
		if(mc.thePlayer.onGround) {
			wasCollided = false;
		}
		if(mode.getString().equalsIgnoreCase("Normal")) {
			mc.thePlayer.stepHeight = 100.0F;
		}else if(mode.getString().equalsIgnoreCase("BlocksMC")) {
			if(mc.thePlayer.isCollidedHorizontally) {
				if(mc.thePlayer.onGround) {
					mc.thePlayer.motionY = 0.42F;
				}
				wasCollided = true;
			}
			if (mc.thePlayer.offGroundTicks == 4 && wasCollided) {
				mc.thePlayer.motionY = -0.09800000190734863;
			}
		}
	}

}
