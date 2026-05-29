package cryptix.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.BlinkUtils;
import cryptix.utils.MovementUtils;
import cryptix.utils.Utils;
import net.minecraft.client.settings.KeyBinding;

public class Sprint extends Module{
	private Setting mode;
	private boolean blinked;
	public Sprint() {
		super("Sprint", 0, Category.MOVEMENT);
		ArrayList<String> modes = new ArrayList<String>();
		Client.instance.settingsManager.addSetting(mode = new Setting("Mode", this, "Legit", new ArrayList<String>(Arrays.asList("Legit", "Omni", "Omni Hypixel"))));
	}
	
	@Override
	public void onPreMotion() {
		this.setDisplayName(this.getName() + this.getUppercaseSuffix(mode.getString()));
		if(!Client.instance.moduleManager.getModuleByName("Scaffold").isToggled()) {
			if(mode.getString().equalsIgnoreCase("Legit")) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
			}
			if(MovementUtils.isMoving() && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking() && mode.getString().equalsIgnoreCase("Omni")) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
				if(!mc.gameSettings.keyBindForward.isKeyDown() && Client.instance.moduleManager.killAura.target == null) {
					mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw - 180;
					if(mc.gameSettings.keyBindRight.isKeyDown()) {
						mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw + 90;
					}
					if(mc.gameSettings.keyBindLeft.isKeyDown()) {
						mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw - 90;
					}
				}
			}
			if(MovementUtils.isMoving() && mode.getString().equalsIgnoreCase("Omni Hypixel") && mc.thePlayer.ticksExisted % 2 == 0 && Client.instance.moduleManager.killAura.target == null) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
				if(!mc.gameSettings.keyBindForward.isKeyDown()) {
					BlinkUtils.startBlink();
					blinked = true;
					mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw - 180;
					if(mc.gameSettings.keyBindRight.isKeyDown()) {
						mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw + 90;
					}
					if(mc.gameSettings.keyBindLeft.isKeyDown()) {
						mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw - 90;
					}
				}
			}else {
				if(blinked) {
					BlinkUtils.stopBlink();
				}
				blinked = false;
			}
		}
	}
	
	@Override
	public void onPreUpdate() {
		if(mode.getString().equalsIgnoreCase("Omni") && MovementUtils.isMoving() && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking() && !mc.gameSettings.keyBindForward.isKeyDown() && Client.instance.moduleManager.killAura.target == null) {
			Client.movefix = true;
		}
		if(!mc.gameSettings.keyBindForward.isKeyDown() && MovementUtils.isMoving() && mode.getString().equalsIgnoreCase("Omni Hypixel")) {
			Client.movefix = true;
		}
	}
	
	@Override
	public void onPostMotion() {
		if(MovementUtils.isMoving() && mode.getString().equalsIgnoreCase("Omni Hypixel") && mc.thePlayer.ticksExisted % 2 == 0 && !mc.gameSettings.keyBindForward.isKeyDown() && Client.instance.moduleManager.killAura.target == null) {
			mc.thePlayer.fakeYaw = mc.thePlayer.rotationYaw;
		}
	}
}
