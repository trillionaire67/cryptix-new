package cryptix.module.visual;

import java.awt.Color;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cryptix.Client;
import cryptix.font.CustomFontRenderer;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.BooleanSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.RenderCache;
import cryptix.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class SessionInfo extends Module{
	private long startTime;
	public int kills, offsetX = -200, offsetY, lastMouseX, lastMouseY;
	private boolean isDragging = false;
	private BooleanSetting glow = new BooleanSetting("Glow", this, false);
	public SessionInfo() {
		super("SessionInfo", 0, Category.VISUAL);
		this.addSetting(glow);
	}
	
	@Override
    public void onEnable() {
		ScaledResolution sr = RenderCache.getScaledResolution();
		offsetX = (int) (-sr.getScaledWidth() / 1.05F);
		offsetY = (int) (sr.getScaledHeight() / 1.3F);
        startTime = System.currentTimeMillis();
        kills = 0;
    }
	
	@Override
	public void onRender2D() {
		ScaledResolution sr = RenderCache.getScaledResolution();
	    int screenX = sr.getScaledWidth() / 2;
	    int screenY = sr.getScaledHeight() / 2;
		if (Mouse.isButtonDown(0) && mc.currentScreen instanceof GuiChat && isHovered(Mouse.getX(), Mouse.getY())) {
	        if (!isDragging) {
	            isDragging = true;
	        }
	    } else {
	        if (isDragging) {
	            isDragging = false;
	        }
	    }

	    if (isDragging) {
	        int mouseX = Mouse.getX();
	        int mouseY = Mouse.getY();
	        offsetX += mouseX - lastMouseX;
	        offsetY += mouseY - lastMouseY;
	    }

	    lastMouseX = Mouse.getX();
	    lastMouseY = Mouse.getY();
	    int clr = Client.instance.moduleManager.hud.getColorInt(0, 1);
	    int x = screenX + (offsetX / 2);
	    int y = screenY - (offsetY / 2);
	    String username = mc.getSession().getUsername();
	    String nameStr = "Name: " + username;
	    String timeStr = "Time: " + getTime();
	    String killsStr = "Kills: " + kills;
	    CustomFontRenderer font;

		switch (Client.instance.moduleManager.hud.font.getString().toLowerCase()) {
		    case "apple":
		        font = Client.instance.apple;
		        break;
		    case "arial":
		        font = Client.instance.arial;
		        break;
		    case "product sans":
		        font = Client.instance.sans;
		        break;
		    default:
		        font = null;
		        break;
		}
	    int textWidth;
	    if(font == null) {
	    	textWidth = Math.max(Math.max(mc.fontRendererObj.getStringWidth(nameStr),
                    mc.fontRendererObj.getStringWidth(timeStr)),
                    mc.fontRendererObj.getStringWidth(killsStr));
	    }else {
	    	textWidth = (int) Math.max(Math.max(font.getStringWidth(nameStr),
                    font.getStringWidth(timeStr)),
	    			font.getStringWidth(killsStr));
	    }
	    int width = 10 + textWidth;
	    int height = 47;
	    RenderUtils.drawRoundedRectangle(x, y, x + width, y + height, 10, 0x80000000);
	    if (glow.getBoolean()) {
	        
	    }
	    GL11.glColor3d(1, 1, 1);
	    if(font == null) {
	    	mc.fontRendererObj.drawString(nameStr, x + 5, y + 5, clr);
            mc.fontRendererObj.drawString(timeStr, x + 5, y + 20, clr);
            mc.fontRendererObj.drawString(killsStr, x + 5, y + 35, clr);
	    }else {
	    	font.drawString(nameStr, x + 5, y + 5, clr);
	    	font.drawString(timeStr, x + 5, y + 20, clr);
	    	font.drawString(killsStr, x + 5, y + 35, clr);
	    }
	}
	
	private String getTime() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        long hours = (elapsedTime / (1000 * 60 * 60)) % 24;
        long minutes = (elapsedTime / (1000 * 60)) % 60;
        long seconds = (elapsedTime / 1000) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
	
	private boolean isHovered(int mouseX, int mouseY) {
		ScaledResolution sr = RenderCache.getScaledResolution();
	    int scaleFactor = sr.getScaleFactor();
	    int screenX = sr.getScaledWidth() / 2;
	    int screenY = sr.getScaledHeight() / 2;
	    int buttonX = screenX + (offsetX / 2);
	    int buttonY = screenY - (offsetY / 2);
	    int buttonWidth = 40 + mc.fontRendererObj.getStringWidth(mc.getSession().getUsername());
	    int buttonHeight = 47;
	    int adjustedMouseY = mc.displayHeight - mouseY;
	    return mouseX >= buttonX * scaleFactor && mouseX <= (buttonX + buttonWidth) * scaleFactor
	        && adjustedMouseY >= buttonY * scaleFactor && adjustedMouseY <= (buttonY + buttonHeight) * scaleFactor;
	}

}