package cryptix.gui.clickgui.element.elements;

import java.awt.Color;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.BooleanSetting;
import cryptix.gui.clickgui.element.Element;
import cryptix.gui.clickgui.element.ModuleButton;
import cryptix.gui.clickgui.util.ColorUtil;
import cryptix.gui.clickgui.util.FontUtil;
import cryptix.utils.Utils;
import cryptix.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;


public class CheckBox extends Element {
	private int alpha;
	private long lastUpdateTime;
	public CheckBox(ModuleButton parent1, Setting setting1) {
		parent = parent1;
		setting = setting1;
		super.setup();
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if(this.y > parent.parent.y + parent.parent.height + parent.parent.currentHeight - parent.parent.currentScroll || this.y < parent.parent.y) return;
		long currentTime = System.currentTimeMillis();
		float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
		lastUpdateTime = currentTime;

		float smoothTime = 0.1f;
		float lerpAlpha = 1.0f - (float)Math.exp(-deltaTime / smoothTime);
		alpha = (int) Utils.lerp(alpha, ((BooleanSetting)setting).getBoolean() ? 255 : 0, lerpAlpha);
		int color = ColorUtil.getClickGUIColor();
		
		Gui.drawRect(x - 2, y, x + 88, y + height, 0x00000000);
		if(Client.instance.moduleManager.clickGUI.font.getBoolean()) {
			Client.instance.sans12.drawString(settingName, x + 14, (float) (y + 5.5), 0xffffffff);
			GlStateManager.disableBlend();
		}else {
			FontUtil.drawString(settingName, x + 14, y + FontUtil.getFontHeight() / 2 - 0.5, 0xffffffff);
		}
		RenderUtils.drawRoundedRectangle((float) x + 1,(float) y + 2, (float)x + 12, (float)y + 13, 4, 0xff101010);
		if(((BooleanSetting)setting).getBoolean()) {
			RenderUtils.drawRoundedRectangle((float) x + 1,(float) y + 2, (float)x + 12, (float)y + 13, 4, color);
		}
		if (isCheckHovered(mouseX, mouseY)) {
			RenderUtils.drawRoundedRectangle((float)x + 1, (float)y + 2, (float)x + 12, (float)y + 13,4,((BooleanSetting)setting).getBoolean() ? 0x35111111 : 0x20232323);
		}
		RenderUtils.drawCheckmark((int)x + 4, (int) y + 4, 7, 0xff101010);
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0 && isCheckHovered(mouseX, mouseY)) {
			((BooleanSetting)setting).setBoolean(!((BooleanSetting)setting).getBoolean());
			return true;
		}
		
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public boolean isCheckHovered(int mouseX, int mouseY) {
		return mouseX >= x + 1 && mouseX <= x + 12 && mouseY >= y + 2 && mouseY <= y + 13;
	}
}
