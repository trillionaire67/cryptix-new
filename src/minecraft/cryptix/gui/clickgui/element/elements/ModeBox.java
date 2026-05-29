package cryptix.gui.clickgui.element.elements;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.element.Element;
import cryptix.gui.clickgui.element.ModuleButton;
import cryptix.gui.clickgui.util.ColorUtil;
import cryptix.gui.clickgui.util.FontUtil;
import cryptix.utils.Utils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class ModeBox extends Element {
	private int alpha;
	private long alphaTime;
	public ModeBox(ModuleButton parent1, Setting setting1) {
		parent = parent1;
		setting = setting1;
		super.setup();
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if(this.y > parent.parent.y + parent.parent.height + parent.parent.currentHeight - parent.parent.currentScroll || this.y < parent.parent.y) {
			alpha = 255;
			return;
		}
		Gui.drawRect(x - 2, y, x + 88, y + height, 0xFF1A1A1A);
		String mode = Client.instance.settingsManager.getSettingByName(parent.mod, settingName).getString();
		mode = mode.substring(0, 1).toUpperCase() + mode.substring(1);
		int color = (alpha << 24) | (255 << 16) | (255 << 8) | 255;;
		if(Client.instance.moduleManager.clickGUI.font.getBoolean()) {
			Client.instance.sans12.drawString(settingName + ": " + mode, (x + 3 / 2), (float) ((y + 4)), color);
		}else {
			GlStateManager.pushMatrix();
			GL11.glScaled(0.85, 0.85, 0.85);
			FontUtil.drawString(settingName + ": " + mode, (x + 3 / 2) / 0.85, (y + 3) / 0.85, color);
			GlStateManager.popMatrix();
		}

		Gui.drawRect(x - 2, y + 13, x + 88, y + 14, 0xFF131313);
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0) {
			if (isButtonHovered(mouseX, mouseY)) {
	            List<String> options = setting.getOptions();
	            String currentVal = Client.instance.settingsManager.getSettingByName(parent.mod, settingName).getString();
	            int currentIndex = -1;
	            for (int i = 0; i < options.size(); i++) {
	                if (options.get(i).equalsIgnoreCase(currentVal)) {
	                    currentIndex = i;
	                    break;
	                }
	            }
	            if (currentIndex != -1) {
	                int nextIndex = (currentIndex + 1) % options.size();
	                Client.instance.settingsManager.getSettingByName(parent.mod, settingName).setString(options.get(nextIndex).toLowerCase());
	            } else {
	            	Client.instance.settingsManager.getSettingByName(parent.mod, settingName).setString(options.get(0).toLowerCase());
	            }
	            return true;
	        }
		}
		
		if (mouseButton == 1) {
			if (isButtonHovered(mouseX, mouseY)) {
	            List<String> options = setting.getOptions();
	            String currentVal = Client.instance.settingsManager.getSettingByName(parent.mod, settingName).getString();
	            int currentIndex = -1;
	            for (int i = 0; i < options.size(); i++) {
	                if (options.get(i).equalsIgnoreCase(currentVal)) {
	                    currentIndex = i;
	                    break;
	                }
	            }
	            if (currentIndex != -1) {
	                int nextIndex = currentIndex - 1 != -1 ? (currentIndex - 1) % options.size() : options.size() - 1;
	                Client.instance.settingsManager.getSettingByName(parent.mod, settingName).setString(options.get(nextIndex).toLowerCase());
	            } else {
	            	Client.instance.settingsManager.getSettingByName(parent.mod, settingName).setString(options.get(0).toLowerCase());
	            }
	            return true;
	        }
		}

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public boolean isButtonHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 15;
	}
}
