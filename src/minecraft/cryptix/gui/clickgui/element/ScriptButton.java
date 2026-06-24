package cryptix.gui.clickgui.element;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import cryptix.Client;
import cryptix.gui.clickgui.Panel;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.element.elements.CheckBox;
import cryptix.gui.clickgui.element.elements.ModeBox;
import cryptix.gui.clickgui.element.elements.Slider;
import cryptix.gui.clickgui.util.ColorUtil;
import cryptix.gui.clickgui.util.FontUtil;
import cryptix.module.Module;
import cryptix.script.Script;
import cryptix.utils.Utils;
import cryptix.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class ScriptButton {
	public Script script;
	public Panel parent;
	public double x;
	public double y;
	public double width;
	public double height;
	public int alpha, targetAlpha;
	private long lastUpd;
	public boolean listening = false;
	public ScriptButton(Script imod, Panel pl) {
		script = imod;
		height = Client.mc.fontRendererObj.FONT_HEIGHT + 2;
		parent = pl;
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
	    RenderUtils.drawGradientRect(this.x - 2.0, this.y - 1.0, this.x + this.width + 2.0, this.y + 2.0 + this.height + 1.0, script.isEnabled() ? color1 : 0, script.isEnabled() ? color2 : 0);
	    if (isHovered(mouseX, mouseY) && parent.currentHeight > this.height) {
	    	Gui.drawRect(this.x - 2.0, this.y - 1.0, this.x + this.width + 2.0, this.y + 2.0 + this.height + 1.0, 0x55111111);
	    }
	    alpha = (int) Utils.lerp(alpha, script.isEnabled() ? 255 : 0, 1);
	    if(parent.currentHeight > this.height) {
	    	if(Client.instance.moduleManager.clickGUI.font.getBoolean()) {
	    		Client.instance.sans.drawCenteredString(script.getName(), (float) (x + width / 2), (float) (y + height / 1.6) - 4, textcolor);
	    		GlStateManager.disableBlend();
	    	}else {
	    		FontUtil.drawTotalCenteredStringWithShadow(script.getName(), x + width / 2, y + height / 1.6, textcolor);
	    	}
	    }
	}
	
	public boolean keyTyped(char typedChar, int keyCode) throws IOException {
		if (listening) {
			if (keyCode != Keyboard.KEY_ESCAPE) {
				script.setKey(keyCode);
			} else {
				script.setKey(Keyboard.KEY_NONE);
			}
			listening = false;
			return true;
		}
		return false;
	}
	
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (!isHovered(mouseX, mouseY))
			return false;
		if (mouseButton == 0) {
			script.toggle();
		} else if (mouseButton == 2) {
			listening = true;
		}
		return true;
	}
	
	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
}
