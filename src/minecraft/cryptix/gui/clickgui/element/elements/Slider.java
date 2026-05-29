package cryptix.gui.clickgui.element.elements;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.element.Element;
import cryptix.gui.clickgui.element.ModuleButton;
import cryptix.gui.clickgui.util.ColorUtil;
import cryptix.gui.clickgui.util.FontUtil;
import cryptix.utils.Utils;
import cryptix.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;


public class Slider extends Element {
	public boolean dragging;
	private float currentPercent = 0;
	private long lastUpdateTime;

	public Slider(ModuleButton parent1, Setting setting1) {
		parent = parent1;
		setting = setting1;
		dragging = false;
		currentPercent = (float) ((setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin()));
		super.setup();
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if(this.y > parent.parent.y + parent.parent.height + parent.parent.currentHeight - parent.parent.currentScroll || this.y < parent.parent.y) return;
		int color1 = ColorUtil.getClickGUIColor();
	    int color2 = ColorUtil.getClickGUIColor2();
		String displayval = "" + Math.round(setting.getValue() * 100D)/ 100D;
		boolean hoveredORdragged = isSliderHovered(mouseX, mouseY) || dragging;
		float targetPercent = (float) ((setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin()));
		long currentTime = System.currentTimeMillis();
		float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
		lastUpdateTime = currentTime;
		float smoothTime = 0.1f;
		float lerpAlpha = 1.0f - (float)Math.exp(-deltaTime / smoothTime);
		currentPercent = Utils.lerp(currentPercent, targetPercent, lerpAlpha);
		
		Gui.drawRect(x - 2, y, x + 88, y + height, 0xFF1A1A1A);
		
		if(Client.instance.moduleManager.clickGUI.font.getBoolean()) {
			Client.instance.sans12.drawString(settingName + ":", (x + 1), (float) (y + 3), -1);
			Client.instance.sans12.drawString(displayval, (x + Client.instance.sans12.getStringWidth(settingName) + 6), (float) (y + 3), -1);
		}else {
			GlStateManager.pushMatrix();
			GL11.glScaled(0.8, 0.8, 0.8);
			FontUtil.drawString(settingName + ":", (x + 1) / 0.8, (y + 2) / 0.8, -1);
			FontUtil.drawString(displayval, (x + (FontUtil.getStringWidth(settingName) * 0.8) + 6) / 0.8, y / 0.8 + 2.5, -1);
			GlStateManager.popMatrix();
		}
		int c = 0xff121212;
		int color = 0xFF969696;
		RenderUtils.drawRoundedRectangle((float)x, (float) y + 12, (float) x + 86, (float) y + 15, 3, c);
        RenderUtils.drawRoundedGradientRect((float)x, (float) y + 12, (float) (x + (currentPercent * 85)), (float) y + 15, 3, color1, color1, color2, color2);
        RenderUtils.drawCircle((float)x + (currentPercent * 85), (float)y + 13.5F, 2.5F, 1, color2);
        RenderUtils.drawFilledCircle((float)x + (currentPercent * 85), (float) y + 13.5F, 1.85F, 0xff121212);

		if (this.dragging) {
		    double diff = setting.getMax() - setting.getMin();
		    float clamped = MathHelper.clamp_float((float) ((mouseX - x) / 84f), 0f, 1f);
		    double val = setting.getMin() + clamped * diff;
		    setting.setValue(val);
		}

	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0 && isSliderHovered(mouseX, mouseY)) {
			this.dragging = true;
			return true;
		}
		
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public void mouseReleased(int mouseX, int mouseY, int state) {
		this.dragging = false;
	}

	public boolean isSliderHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y + 11 && mouseY <= y + 14;
	}
}