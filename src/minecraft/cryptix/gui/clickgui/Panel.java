package cryptix.gui.clickgui;

import java.awt.Color;
import java.util.ArrayList;

import cryptix.Client;
import cryptix.gui.clickgui.element.Element;
import cryptix.gui.clickgui.element.ModuleButton;
import cryptix.gui.clickgui.element.ScriptButton;
import cryptix.gui.clickgui.util.FontUtil;
import cryptix.utils.Utils;
import cryptix.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;


public class Panel {
	public String title;
	public double x;
	public double y;
	private double x2;
	private double y2;
	private long lastUpdateTime;
	public double width;
	public double height;
	public boolean dragging;
	public boolean extended = false;
	public boolean visible;
	public boolean wasExtended = false;
	public double currentHeight = 0;
	public ArrayList<ModuleButton> Elements = new ArrayList<>();
	public ArrayList<ScriptButton> scriptElements = new ArrayList<>();
	public ClickGUI clickgui;
	public double scrollY = 0, currentScroll = 0;
	private double maxScroll = 0;

	public Panel(String ititle, double ix, double iy, double iwidth, double iheight, boolean iextended, ClickGUI parent) {
		this.title = ititle;
		this.x = ix;
		this.y = iy;
		this.width = iwidth;
		this.height = iheight;
		this.extended = iextended;
		this.dragging = false;
		this.visible = true;
		this.clickgui = parent;
		setup();
	}

	public void setup() {}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (this.dragging) {
			x = x2 + mouseX;
			y = y2 + mouseY;
		}
		float x = (float) this.x;
		float y = (float) this.y;
		float width = (float) this.width;
		float height = (float) this.height;
		double startY = y + height + 1 - currentScroll;
		long currentTime = System.currentTimeMillis();
		float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
		lastUpdateTime = currentTime;

		float smoothTime = 0.1f;
		float alpha = 1.0f - (float)Math.exp(-deltaTime / smoothTime);
		if (this.extended && (!Elements.isEmpty() || !scriptElements.isEmpty())) {
		    int epanelcolor = 0xFF232424;
		    double totalHeight = 0;
		    for (ModuleButton et : Elements) {
		        totalHeight += et.height + 3.8 + et.settHeight;
		    }
		    for (ScriptButton et : scriptElements) {
		        totalHeight += et.height + 3.8;
		    }
		    currentHeight = Utils.lerp((float) currentHeight, (float) totalHeight, alpha);

		    double visibleHeight = 300 - this.height;
		    if(totalHeight < 300) scrollY = 0;
		    currentHeight = Math.min(currentHeight, 300 + currentScroll);
		    if (currentHeight < totalHeight) {
		        if (currentHeight > totalHeight) {
		            currentHeight = totalHeight;
		        }
		    } else if (currentHeight > totalHeight) {
		        if (currentHeight < 0) {
		            currentHeight = 0;
		        }
		    }
		    RenderUtils.startRoundedRectangle();
		    RenderUtils.drawRoundedRectangleNoRender(x, y, x + width, (float) (startY + currentHeight + 6), Client.instance.moduleManager.clickGUI.square.getBoolean() ? 0 : 12, 0xFF1A1A1A);
		    RenderUtils.drawRoundedRectangleNoRender(x, y - 2, x + width, y + height, Client.instance.moduleManager.clickGUI.square.getBoolean() ? 0 : 12, 0xFF121212);
		    RenderUtils.stopRoundedRectangle();
		    for (ModuleButton et : Elements) {
		        int etheight = (int) et.height;
		        int startYI = (int) startY;
		        et.x = x + 2;
		        et.y = startY;
		        et.width = width - 4;
		        double offset = 0;
		        if(et.extended && et.menuelements != null && !et.menuelements.isEmpty()) {
	                for (Element element : et.menuelements) {
	                    element.offset = offset;
	                    element.update();
	                    element.drawScreen(mouseX, mouseY, partialTicks);
	                    offset += element.height;
	                }
		        }
		        et.drawScreen(mouseX, mouseY, partialTicks);
		        startY += et.height + 4 + et.settHeight;
		    }
		    for (ScriptButton et : scriptElements) {
		        int etheight = (int) et.height;
		        int startYI = (int) startY;
		        et.x = x + 2;
		        et.y = startY;
		        et.width = width - 4;
		        double offset = 0;
		        et.drawScreen(mouseX, mouseY, partialTicks);
		        startY += et.height + 4;
		    }
		}else {
			currentHeight = Utils.lerp((float) currentHeight, 0.0f, alpha);
			RenderUtils.startRoundedRectangle();
			RenderUtils.drawRoundedRectangleNoRender(x, y, x + width, (float) (startY + currentHeight + 1), Client.instance.moduleManager.clickGUI.square.getBoolean() ? 0 : 12, 0xFF1A1A1A);
			RenderUtils.drawRoundedRectangleNoRender(x, y - 2, x + width, y + height + 2, Client.instance.moduleManager.clickGUI.square.getBoolean() ? 0 : 12, 0xFF121212);
			RenderUtils.stopRoundedRectangle();
		}
		if(Client.instance.moduleManager.clickGUI.font.getBoolean()) {
			Client.instance.sans.drawStringWithShadow(title, x + (width / 2) - (FontUtil.getStringWidth(title) / 2), (int) (y + (height / 2) - (FontUtil.getFontHeight() / 2)), -1);
			GlStateManager.disableBlend();
		}else {
			FontUtil.drawStringWithShadow(title, x + (width / 2) - (FontUtil.getStringWidth(title) / 2), (int) (y + (height / 2) - (FontUtil.getFontHeight() / 2)), -1);
		}
		currentScroll = Utils.lerp((float) currentScroll, (float) scrollY, alpha);
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (!this.visible) {
			return false;
		}
		if (mouseButton == 0 && isHovered(mouseX, mouseY)) {
			x2 = this.x - mouseX;
			y2 = this.y - mouseY;
			dragging = true;
			return true;
		} else if (mouseButton == 1 && isHovered(mouseX, mouseY)) {
			extended = !extended;
			return true;
		} else if (extended) {
			for (ModuleButton et : Elements) {
				if (et.mouseClicked(mouseX, mouseY, mouseButton)) {
					return true;
				}
			}
			for (ScriptButton et : scriptElements) {
				if (et.mouseClicked(mouseX, mouseY, mouseButton)) {
					return true;
				}
			}
		}
		return false;
	}

	public void mouseReleased(int mouseX, int mouseY, int state) {
		if (!this.visible) {
			return;
		}
		if (state == 0) {
			this.dragging = false;
		}
	}

	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public boolean isBoxHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height + currentHeight;
	}
	
	public void onScroll(int wheel) {
	    updateMaxScroll();
	    if (wheel > 0) {
	        scrollY -= 15;
	    } else if (wheel < 0) {
	        scrollY += 15;
	    }
	    scrollY = Math.max(0, Math.min(scrollY, maxScroll));
	}

	private void updateMaxScroll() {
		double totalHeight = 0;
		for (ModuleButton et : Elements) {
	        totalHeight += et.height + 3.8 + et.settHeight;
	    }
	    double visibleHeight = 300 - this.height;
	    maxScroll = Math.max(0, totalHeight - visibleHeight);
	}
}
