package cryptix.module.visual;

import cryptix.module.Category;
import cryptix.module.Module;

public class FullBright extends Module{
	private float oldGamma;
	public FullBright() {
		super("FullBright", 0, Category.VISUAL);
	}
	
	public void onDisable() {
		mc.gameSettings.gammaSetting = oldGamma;
	}
	
	public void onEnable() {
		oldGamma = mc.gameSettings.gammaSetting;
	}
	
	public void onPreUpdate() {
		mc.gameSettings.gammaSetting = 10;
	}

}
