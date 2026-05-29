package cryptix.module.movement;

import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;

public class Timer extends Module{
	private Setting speed = new Setting("Speed", this, 1, 0.05, 5, 2);
	public Timer() {
		super("Timer", 0, Category.MOVEMENT);
		this.addSetting(this.speed);
	}
	
	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1.0f;
	}
	
	@Override
	public void onPreUpdate() {
		mc.timer.timerSpeed = (float) speed.getValue();
		if(!this.isToggled()) {
			mc.timer.timerSpeed = 1.0f;
		}
	}

}
