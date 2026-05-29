package cryptix.module.player;

import java.util.Arrays;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.BlinkUtils;
import cryptix.utils.RenderCache;
import net.minecraft.client.gui.ScaledResolution;

public class Blink extends Module{
	private Setting mode = new Setting("Mode", this, "Normal", Arrays.asList("Normal", "Hypixel"));
	private int ticks;
	
	public Blink() {
		super("Blink", 0, Category.PLAYER);
		this.addSetting(mode);
	}
	
	@Override
	public void onEnable() {
		BlinkUtils.startBlink();
		ticks = 0;
	}
	
	@Override
	public void onDisable() {
		BlinkUtils.stopBlink();
	}
	
	@Override
	public void onPreUpdate() {
		if(!BlinkUtils.isBlinking()) {
			this.toggle();
			return;
		}
		if(mode.getString().equalsIgnoreCase("Hypixel")) {
	        ticks++;
	        if(ticks >= 15) {
	            BlinkUtils.releaseOne();
	        }
	    }
	}
	
	@Override
	public void onRender2D() {
		ScaledResolution sr = RenderCache.getScaledResolution();
		int x = sr.getScaledWidth() / 2 + 5;
		int y = sr.getScaledHeight() / 2 + 5;
		mc.fontRendererObj.drawStringWithShadow("§fBlinking: §a" + BlinkUtils.packets.size(), x, y, -1);
	}
}
