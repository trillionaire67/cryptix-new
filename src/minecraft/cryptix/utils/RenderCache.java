package cryptix.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class RenderCache {
	private static final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
	
	public static ScaledResolution getScaledResolution() {
		sr.update(Minecraft.getMinecraft());
		return sr;
	}
	
}
