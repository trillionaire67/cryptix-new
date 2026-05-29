package cryptix.module.player;

import java.util.ArrayList;
import java.util.Arrays;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;

public class SafeWalk extends Module{
	private boolean sneaked;
	private int timer;
	public Setting mode;
	public Setting groundOnly, pitchCheck, directionCheck, delay, delay2;
	private String[] modes = new String[] {"Normal", "Sneak"};
	public SafeWalk() {
		super("Safewalk", 0, Category.PLAYER);
		Client.instance.settingsManager.addSetting(mode = new Setting("Mode", this, "Sneak", new ArrayList<String>(Arrays.asList(modes))));
		Client.instance.settingsManager.addSetting(delay = new Setting("Min Sneak Delay", this, 100, 0, 1000, true));
		Client.instance.settingsManager.addSetting(delay2 =new Setting("Max Sneak Delay", this, 300, 0, 2000, true));
		Client.instance.settingsManager.addSetting(groundOnly = new Setting("Ground Only", this, true));
		Client.instance.settingsManager.addSetting(pitchCheck = new Setting("Pitch Check", this, false));
		Client.instance.settingsManager.addSetting(directionCheck = new Setting("Direction Check", this, false));
	}
	
	@Override
	public void onPreMotion() {
		timer = timer > 0 ? --timer : 0;
		if((directionCheck.getBoolean() && mc.gameSettings.keyBindForward.isKeyDown()) || (pitchCheck.getBoolean() && mc.thePlayer.rotationPitch < 70)) {
			if(sneaked) {
				reset();
			}
			return;
		}
		if(groundOnly.getBoolean() || mc.thePlayer.onGround) {
			if(mode.getString().equalsIgnoreCase("Sneak")) {
				int y = mc.thePlayer.posY % 1.0f == 0 ? 1 : 0;
				BlockPos bp = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
				if(mc.thePlayer.onGround && mc.theWorld.isAirBlock(bp) && timer == 0) {
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
					sneaked = true;
				}else if(sneaked) {
					reset();
				}
			}
		}else if(sneaked) {
			reset();
		}
	}
	
	private void reset() {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
		sneaked = false;
		int time = (int) delay.getValue();
		int time2 = (int) delay2.getValue();
		if(time >= time2) {
			time = time2;
			time2++;
		}
		timer = Utils.randomInt(time,time2) / 150;
	}

}