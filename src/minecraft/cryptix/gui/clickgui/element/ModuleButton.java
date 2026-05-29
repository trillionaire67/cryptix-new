package cryptix.gui.clickgui.element;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatComponentText;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cryptix.Client;
import cryptix.gui.clickgui.Panel;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.element.elements.CheckBox;
import cryptix.gui.clickgui.element.elements.ModeBox;
import cryptix.gui.clickgui.element.elements.Slider;
import cryptix.gui.clickgui.util.ColorUtil;
import cryptix.gui.clickgui.util.FontUtil;
import cryptix.module.Module;
import cryptix.utils.Utils;
import cryptix.utils.render.RenderUtils;

public class ModuleButton {
	public Module mod;
	public ArrayList<Element> menuelements;
	public Panel parent;
	public double x;
	public double y;
	public double width;
	public double height;
	public double settHeight;
	public double allSettHeight;
	public double currentHeight;
	public boolean extended = false;
	public boolean listening = false;
	public int alpha, targetAlpha;
	private long lastUpd;
	public ModuleButton(Module imod, Panel pl) {
		mod = imod;
		height = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 2;
		parent = pl;
		menuelements = new ArrayList<>();
		if (Client.instance.settingsManager.getSettingsByMod(imod) != null)
			for (Setting s : Client.instance.settingsManager.getSettingsByMod(imod)) {
				if (s.isCheckBox()) {
					menuelements.add(new CheckBox(this, s));
				} else if (s.isSlider()) {
					menuelements.add(new Slider(this, s));
				} else if (s.isModeBox()) {
					menuelements.add(new ModeBox(this, s));
				}
			}

	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
	    int color1 = ColorUtil.getClickGUIColor();
	    int color2 = ColorUtil.getClickGUIColor2();
	    int textcolor = -1;
	    long currentTime = System.currentTimeMillis();
	    float deltaTime = (currentTime - lastUpd) / 1000.0f;
	    lastUpd = currentTime;

	    float smoothTime = 0.1f;
	    float lerpAlpha = 1.0f - (float)Math.exp(-deltaTime / smoothTime);
	    targetAlpha = (int) Utils.lerp(targetAlpha, 255, lerpAlpha);
	    if(this.y > parent.y + parent.height + parent.currentHeight - parent.currentScroll || this.y < parent.y) {
	    	targetAlpha = 0;
	    	alpha = 0;
	    	return;
	    }
	    RenderUtils.drawGradientRect(this.x - 2.0, this.y - 1.0, this.x + this.width + 2.0, this.y + 2.0 + this.height + 1.0, !mod.isToggled() ? 0 : color1, !mod.isToggled() ? 0 : color2);

	    if (isHovered(mouseX, mouseY) && parent.currentHeight > this.height) {
	    	Gui.drawRect(this.x - 2.0, this.y - 1.0, this.x + this.width + 2.0, this.y + 2.0 + this.height + 1.0, 0x55111111);
	    }

	    alpha = (int) Utils.lerp(alpha, mod.isToggled() ? 255 : 0, 1);
	    
	    if(parent.currentHeight > this.height) {
	    	if(Client.instance.moduleManager.clickGUI.font.getBoolean()) {
	    		Client.instance.sans.drawCenteredString(mod.getName(), (float) (x + width / 2), (float) (y + height / 1.6) - 4, textcolor);
	    		GlStateManager.disableBlend();
	    	}else {
	    		FontUtil.drawTotalCenteredStringWithShadow(mod.getName(), x + width / 2, y + height / 1.6, textcolor);
	    	}
	    }

	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (!isHovered(mouseX, mouseY))
			return false;

		if (mouseButton == 0) {
			mod.toggle();
			
		} else if (mouseButton == 1) {
			if (menuelements != null && menuelements.size() > 0) {
				this.extended = !this.extended;
			}
			if(!this.extended) {
				this.settHeight -= findSettings();
			}else {
				this.settHeight += findSettings();
			}
		} else if (mouseButton == 2) {
			listening = true;
		}
		return true;
	}
	
	public double findSettings() {
		this.allSettHeight = 0;
		if (Client.instance.settingsManager.getSettingsByMod(mod) != null) {
			for (Setting s : Client.instance.settingsManager.getSettingsByMod(mod)) {
				if (s.isSlider()) {
					allSettHeight += 17;
				} else if (s.isCheckBox()) {
					allSettHeight += 15;
				} else if (s.isModeBox()) {
					allSettHeight += 15;
				}
			}
		}else {
			return 0;
		}
		return allSettHeight;
	}

	public boolean keyTyped(char typedChar, int keyCode) throws IOException {
		if (listening) {
			if (keyCode != Keyboard.KEY_ESCAPE) {
				mod.setKey(keyCode);
			} else {
				mod.setKey(Keyboard.KEY_NONE);
			}
			listening = false;
			return true;
		}
		return false;
	}

	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

}
