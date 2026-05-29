package cryptix.module.visual;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cryptix.Client;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.RenderCache;
import cryptix.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class BPSCounter extends Module{
	private int offsetX = -200, offsetY, lastMouseX, lastMouseY;
	private boolean isDragging = false;
	public BPSCounter() {
		super("BPSCounter", 0, Category.VISUAL);
	}
	
	@Override
    public void onEnable() {
		offsetX = (int) (-RenderCache.getScaledResolution().getScaledWidth() / 1.05F);
		offsetY = (int) (RenderCache.getScaledResolution().getScaledHeight() / 1.2F);
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
	    int x = screenX + (offsetX / 2);
	    int y = screenY - (offsetY / 2);
	    int height = 15;
	    int width = 0;
	    GlStateManager.disableBlend();
	    switch(Client.instance.moduleManager.hud.font.getString().toLowerCase()) {
		    case "minecraft":
		    	width = mc.fontRendererObj.getStringWidth(String.format("%.2f", getBPS())) + 31;
				RenderUtils.drawRoundedRectangle(x, y, x + width, y + height, 10, 0x80000000);
				GL11.glColor3d(1.0, 1.0, 1.0);
				mc.fontRendererObj.drawString("BPS: " + String.format("%.2f", getBPS()), x + 4, y + 4, -1);
		    	break;
		    case "apple":
		    	width = (int) (Client.instance.apple.getStringWidth(String.format("%.2f", getBPS())) + 31);
				RenderUtils.drawRoundedRectangle(x, y, x + width, y + height, 10, 0x80000000);
				GL11.glColor3d(1.0, 1.0, 1.0);
				Client.instance.apple.drawString("BPS: " + String.format("%.2f", getBPS()), x + 4, y + 4, -1);
		    	break;
		    case "arial":
		    	width = (int) (Client.instance.arial.getStringWidth(String.format("%.2f", getBPS())) + 31);
				RenderUtils.drawRoundedRectangle(x, y, x + width, y + height, 10, 0x80000000);
				GL11.glColor3d(1.0, 1.0, 1.0);
				Client.instance.arial.drawString("BPS: " + String.format("%.2f", getBPS()), x + 4, y + 4, -1);
		    	break;
		    case "product sans":
		    	width = (int) (Client.instance.sans.getStringWidth(String.format("%.2f", getBPS())) + 31);
				RenderUtils.drawRoundedRectangle(x, y, x + width, y + height, 10, 0x80000000);
				GL11.glColor3d(1.0, 1.0, 1.0);
				Client.instance.sans.drawString("BPS: " + String.format("%.2f", getBPS()), x + 4, y + 4, -1);
		    	break;
	    }
	}
	
	private boolean isHovered(int mouseX, int mouseY) {
	    ScaledResolution sr = RenderCache.getScaledResolution();
	    int scaleFactor = sr.getScaleFactor();
	    int screenX = sr.getScaledWidth() / 2;
	    int screenY = sr.getScaledHeight() / 2;
	    int buttonX = screenX + (offsetX / 2);
	    int buttonY = screenY - (offsetY / 2);
	    int width = 0;
	    switch(Client.instance.moduleManager.hud.font.getString().toLowerCase()) {
		    case "minecraft":
		    	width = mc.fontRendererObj.getStringWidth(String.format("%.2f", getBPS())) + 31;
		    	break;
		    case "apple":
		    	width = (int) (Client.instance.apple.getStringWidth(String.format("%.2f", getBPS())) + 31);
		    	break;
		    case "arial":
		    	width = (int) (Client.instance.arial.getStringWidth(String.format("%.2f", getBPS())) + 31);
		    	break;
		    case "product sans":
		    	width = (int) (Client.instance.sans.getStringWidth(String.format("%.2f", getBPS())) + 31);
		    	break;
	    }
	    int buttonHeight = 15;
	    int adjustedMouseY = mc.displayHeight - mouseY;
	    return mouseX >= buttonX * scaleFactor && mouseX <= (buttonX + width) * scaleFactor
	        && adjustedMouseY >= buttonY * scaleFactor && adjustedMouseY <= (buttonY + buttonHeight) * scaleFactor;
	}
	
	private double getBPS() {
		return Math.sqrt((mc.thePlayer.posX - mc.thePlayer.prevPosX) * (mc.thePlayer.posX - mc.thePlayer.prevPosX) + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * (mc.thePlayer.posZ - mc.thePlayer.prevPosZ)) * 20.0D * mc.timer.timerSpeed;
	}

}
