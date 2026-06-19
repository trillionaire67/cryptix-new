package cryptix.module.visual;

import java.awt.Color;
import java.util.Arrays;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL11;

import cryptix.Client;
import cryptix.font.CustomFontRenderer;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.RenderCache;
import cryptix.utils.Utils;
import cryptix.utils.render.RenderUtils;
import cryptix.utils.render.StencilUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TargetHUD extends Module{
	private final ResourceLocation GLOW_TEXTURE1 = new ResourceLocation("cryptix/glow/targethud_1.png");
	private final ResourceLocation GLOW_TEXTURE2 = new ResourceLocation("cryptix/glow/targethud_2.png");
	private final ResourceLocation GLOW_TEXTURE3 = new ResourceLocation("cryptix/glow/targethud_3.png");
	private EntityLivingBase target = null, lastTarget = null;
	private float smoothBarWidth;
	private Setting opacity, mode, font;
	private int alpha;
	private int offsetX, offsetY;
	private boolean isDragging = false;
	private long offsetTime;
	private int lastMouseX, lastMouseY;
	private int background = (100 << 24) | (0 << 16) | (0 << 8) | 0;
	private int color1 = (200 << 24) | (246 << 16) | (174 << 8) | 90;
	private int color2 = (200 << 24) | (183 << 16) | (211 << 8) | 82;
	private int color3 = (200 << 24) | (227 << 16) | (103 << 8) | 103;
	private int color4 = (200 << 24) | (182 << 16) | (182 << 8) | 84;
	private int color5 = (200 << 24) | (1   << 16) | (1   << 8) | 1;
	private int color6 = (200 << 24) | (199 << 16) | (19  << 8) | 19;
	private int color7 = (255 << 24) | (182 << 16) | (182 << 8) | 84;
	public TargetHUD() {
		super("TargetHUD", 0, Category.VISUAL);
		Client.instance.settingsManager.addSetting(opacity = new Setting("Opacity", this, 100, 0, 155, true));
		Client.instance.settingsManager.addSetting(mode = new Setting("Mode", this, "Modern", Arrays.asList("Modern", "Old Modern", "Novoline", "Zeroday", "Raven")));
		Client.instance.settingsManager.addSetting(font = new Setting("Font", this, "Minecraft", Arrays.asList("Minecraft", "Apple", "Arial", "Product Sans")));
	}
	
	@Override
	public void onPreMotion() {
	    EntityLivingBase kaTarget = Client.instance.moduleManager.killAura.target;
	    if (kaTarget != null) {
	        alpha = (int) Utils.lerp(alpha, 255, 0.5F);
	        target = kaTarget;
	    } else if(mc.currentScreen instanceof GuiChat){
	    	alpha = (int) Utils.lerp(alpha, 255, 0.5F);
	        target = mc.thePlayer;
	    }else {
	    	alpha = (int) Utils.lerp(alpha, 0, 0.5F);
	        target = null;
	    }
	}
	
	@Override
	public void onRender2D() {
		int adjustedAlpha = Math.max(1, alpha);
		int finalAlpha = Math.min(255, (int) (opacity.getValue() * adjustedAlpha / 255.0));
		if(target != null || finalAlpha > 1 && lastTarget != null) {
			if(mode.getString().equalsIgnoreCase("Zeroday")) {
				renderZeroday(target != null ? target : lastTarget);
			}else if(mode.getString().equalsIgnoreCase("Raven")) {
				renderRaven(target != null ? target : lastTarget);
			}else{
				render(target != null ? target : lastTarget);
			}
		}
	}
	
	private void handleDragging(int x, int y, int width, int height) {
	    ScaledResolution sr = RenderCache.getScaledResolution();
	    int factor = sr.getScaleFactor();

	    int mouseX = Mouse.getX() / factor;
	    int mouseY = (mc.displayHeight - Mouse.getY()) / factor;

	    if (Mouse.isButtonDown(0)) {
	        if (!isDragging && mc.currentScreen instanceof GuiChat &&
	            isHovered(Mouse.getX(), Mouse.getY(), x, y, width, height, factor)) {
	            isDragging = true;
	        }

	        if (isDragging) {
	            offsetX += mouseX - lastMouseX;
	            offsetY += mouseY - lastMouseY;
	        }
	    } else {
	        isDragging = false;
	    }

	    lastMouseX = mouseX;
	    lastMouseY = mouseY;
	}
	
	private void render(EntityLivingBase target) {
	    if (target == null) return;
	    boolean modern = mode.getString().equalsIgnoreCase("Modern") || mode.getString().equalsIgnoreCase("Old Modern");
	    ScaledResolution sr = RenderCache.getScaledResolution();
	    String name = target.getDisplayName().getFormattedText();
	    String fontName = font.getString().toLowerCase();
	    int textWidth;
	    if ("apple".equals(fontName)) textWidth = (int) Client.instance.apple.getStringWidth(name) + 5;
	    else if ("arial".equals(fontName)) textWidth = (int) Client.instance.arial.getStringWidth(name) + 5;
	    else if ("product sans".equals(fontName)) textWidth = (int) Client.instance.sans.getStringWidth(name) + 5;
	    else textWidth = mc.fontRendererObj.getStringWidth(name);
	    int height = modern ? 40 : 32;
	    int width = 37 + textWidth;
	    int screenX = sr.getScaledWidth() / 2;
	    int screenY = sr.getScaledHeight() / 2;
	    int x = screenX + offsetX;
	    int y = screenY + offsetY;
	    handleDragging(x, y, width, height);
	    float health = target.getHealth();
	    float barWidth = width * (health / target.getMaxHealth());
	    if (smoothBarWidth == 0 || lastTarget != target) smoothBarWidth = barWidth;
	    long currentTime = System.currentTimeMillis();
	    float deltaTime = (currentTime - offsetTime) / 1000f;
	    offsetTime = currentTime;
	    smoothBarWidth = Utils.lerp(smoothBarWidth, barWidth, 1f - (float)Math.exp(-deltaTime / 0.15f));
	    HUD hud = (HUD) Client.instance.moduleManager.hud;
	    int adjustedAlpha = Math.max(1, alpha);
	    int finalAlpha = Math.min(255, (int)(opacity.getValue() * adjustedAlpha / 255.0));
	    int color1 = (adjustedAlpha << 24) | ((int)hud.color1red.getValue() << 16) | ((int)hud.color1green.getValue() << 8) | (int)hud.color1blue.getValue();
	    int color2 = (adjustedAlpha << 24) | ((int)hud.color2red.getValue() << 16) | ((int)hud.color2green.getValue() << 8) | (int)hud.color2blue.getValue();
	    int color3 = (adjustedAlpha << 24) | (255 << 16) | (70 << 8) | 70;
	    int backClr = modern ? (finalAlpha << 24) : ((adjustedAlpha << 24) | (60 << 16) | (60 << 8) | 60);
	    RenderUtils.drawRoundedRectangle(x, y, x + width, y + height, modern ? 10 : 0, backClr);
	    RenderUtils.drawRoundedGradientRect(
	        x + (modern ? 3 : 32),
	        y + height - (modern ? 9 : 17),
	        x + (modern ? smoothBarWidth - 3 : smoothBarWidth / 1.5f + 32),
	        y + height - (modern ? 3 : 5),
	        modern ? 5 : 0,
	        modern ? color1 : color3,
	        modern ? color1 : color3,
	        modern ? color2 : color3,
	        modern ? color2 : color3
	    );
	    if (mode.getString().equalsIgnoreCase("Modern")) {
	    	drawOutline(x, y, height, width, color1, color2);
	    }
	    if(mode.getString().equalsIgnoreCase("Old Modern")) {
	    	RenderUtils.drawOutline(x, y, x + width, y + height, 10.0f, color1, color2);
	    }
	    float scale = "minecraft".equalsIgnoreCase(fontName) ? 0.9f : 1.0f;
	    int nameColor = (adjustedAlpha << 24) | (255 << 16) | (255 << 8) | 255;
	    float scaledX = (x + 33) / scale;
	    float scaledY = (y + 4) / scale;
	    if ("apple".equals(fontName)) {
	        Client.instance.apple.drawString(name, scaledX, scaledY, nameColor);
	        if (!modern) Client.instance.apple.drawString((int) health * 5 + "%", scaledX, y + height - 14, nameColor);
	    } else if ("arial".equals(fontName)) {
	        Client.instance.arial.drawString(name, scaledX, scaledY, nameColor);
	        if (!modern) Client.instance.arial.drawString((int) health * 5 + "%", scaledX, y + height - 14, nameColor);
	    } else if ("product sans".equals(fontName)) {
	        Client.instance.sans.drawString(name, scaledX, scaledY, nameColor);
	        if (!modern) Client.instance.sans.drawString((int) health * 5 + "%", scaledX, y + height - 14, nameColor);
	    } else {
	        GlStateManager.pushMatrix();
	        GL11.glScaled(scale, scale, scale);
	        GL11.glEnable(GL11.GL_BLEND);
	        mc.fontRendererObj.drawStringWithShadow(name, scaledX, scaledY, nameColor);
	        if (!modern) mc.fontRendererObj.drawStringWithShadow((int) health * 5 + "%", scaledX, (y + height - 14) / scale, nameColor);
	        GlStateManager.popMatrix();
	    }
	    float playerHealth = mc.thePlayer.getHealth();
	    float diff = playerHealth - health;
	    boolean positive = diff >= 0;
	    if (!positive) diff = -diff;
	    int intPart = (int) diff;
	    int decimal = (int) ((diff - intPart) * 10.0f + 0.5f);
	    String text;
	    if (positive) {
	        text = "+" + intPart + "." + decimal;
	    } else {
	        text = "-" + intPart + "." + decimal;
	    }
	    int dmgColor = health > playerHealth ? (adjustedAlpha << 24) | (255 << 16) | (80 << 8) | 80
	                                          : (adjustedAlpha << 24) | (0 << 16) | (255 << 8) | 0;
	    if ("apple".equals(fontName)) {
	        Client.instance.apple.drawString("", x + (modern ? 32 : width - 12), y + (modern ? 20 : 4), nameColor);
	        if (modern) Client.instance.apple.drawString(text, x + width - 5 - Client.instance.apple.getStringWidth(text), y + (modern ? 20 : 4), dmgColor);
	    } else if ("arial".equals(fontName)) {
	        Client.instance.arial.drawString("", x + (modern ? 32 : width - 12), y + (modern ? 20 : 4), nameColor);
	        if (modern) Client.instance.arial.drawString(text, x + width - 5 - Client.instance.arial.getStringWidth(text), y + (modern ? 20 : 4), dmgColor);
	    } else if ("product sans".equals(fontName)) {
	        Client.instance.sans.drawString("", x + (modern ? 32 : width - 12), y + (modern ? 20 : 4), nameColor);
	        if (modern) Client.instance.sans.drawString(text, x + width - 5 - Client.instance.sans.getStringWidth(text), y + (modern ? 20 : 4), dmgColor);
	    } else {
	        GL11.glEnable(GL11.GL_BLEND);
	        mc.fontRendererObj.drawStringWithShadow("", x + (modern ? 32 : width - 12), y + (modern ? 20 : 4), nameColor);
	        if (modern) mc.fontRendererObj.drawStringWithShadow(text, x + width - 5 - mc.fontRendererObj.getStringWidth(text), y + (modern ? 20 : 4), dmgColor);
	    }
	    drawPlayerHead(x + 3, y + 3, 16, target, 25, modern);
	    if (this.target != null) lastTarget = this.target;
	    GlStateManager.disableBlend();
	}
	
	private void renderRaven(EntityLivingBase target) {
	    if (target == null) return;
	    ScaledResolution sr = RenderCache.getScaledResolution();
	    int screenX = sr.getScaledWidth() >> 1;
	    int screenY = sr.getScaledHeight() >> 1;
	    float health = target.getHealth();
	    final String name = target.getDisplayName().getFormattedText();
	    final String healthColor;
	    if (health > 15f) healthColor = "§a";
	    else if (health > 10f) healthColor = "§e";
	    else if (health > 5f) healthColor = "§6";
	    else healthColor = "§c";
	    final String status = mc.thePlayer.getHealth() > health ? " W" : " L";
	    float maxHealth = target.getMaxHealth();
	    float healthPercentage = health / maxHealth;
	    int x = screenX + offsetX;
	    int y = screenY + offsetY;
	    int moduleOpacity = (int) opacity.getValue();
	    int adjustedAlpha = (moduleOpacity < alpha) ? moduleOpacity : alpha;
	    int r, g, b;
	    if (health > 15f) { r = 0; g = 255; b = 0; }
	    else if (health > 10f) { r = 255; g = 255; b = 0; }
	    else if (health > 5f) { r = 255; g = 215; b = 0; }
	    else { r = 255; g = 0; b = 0; }
	    int adjustedAlpha2 = (alpha < 255) ? alpha : 255;
	    int healthColorInt = (adjustedAlpha2 << 24) | (r << 16) | (g << 8) | b;
	    int backColor = (adjustedAlpha << 24) | (205 << 16) | (205 << 8) | 205;
	    int whiteColor = (adjustedAlpha2 << 24) | 0xFFFFFF;
	    CustomFontRenderer renderer;
	    switch (this.font.getString().toLowerCase()) { case "apple": renderer = Client.instance.apple; break; case "arial": renderer = Client.instance.arial; break; case "product sans": renderer = Client.instance.sans; break; default: renderer = null; break; }
	    float roundedHealth = (int)(health * 10f) / 10f;
	    String displayText = "§f" + name + healthColor + " " + roundedHealth + status;
	    int textWidth;
	    if (renderer != null) {
	        textWidth = (int) renderer.getStringWidth(displayText);
	    } else {
	        textWidth = mc.fontRendererObj.getStringWidth(displayText);
	    }
	    int width = textWidth + 12;
	    int height = 35;
	    handleDragging(x, y, width, height);
	    RenderUtils.drawRoundedRectangle(x, y, x + width, y + height, 8, backColor);
	    RenderUtils.drawRoundedRectangle(x + 5,y + height - 13,x - 1 + (int)((width - 4) * healthPercentage),y + height - 8,4.5f,healthColorInt);
	    if (renderer != null) {
	        renderer.drawString(displayText, x + 6, y + 7, whiteColor);
	    } else {
	        GlStateManager.enableBlend();
	        mc.fontRendererObj.drawString(displayText, x + 6, y + 7, whiteColor);
	    }
	}
	
	private void drawPlayerHead(int x, int y, int width, EntityLivingBase player, int scale, boolean modern) {
	    NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(player.getUniqueID());
	    if (playerInfo == null) return;
	    int adjustedAlpha = Math.max(1, alpha);
	    StencilUtil.prepareStencilWrite();
	    RenderUtils.drawRoundedRectangle(x, y, x + scale, y + scale, modern ? 8 : 0, -1);
	    StencilUtil.restrictToStencil(1);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glColor4f(1, 1, 1, adjustedAlpha / 255.0F);
	    mc.getTextureManager().bindTexture(playerInfo.getLocationSkin());
	    Gui.drawScaledCustomSizeModalRect(x, y, 8F, 8F, 8, 8, scale, scale, 64F, 64F);
	    GL11.glDisable(GL11.GL_BLEND);
	    StencilUtil.clearStencil();
	    GL11.glColor4f(1, 1, 1, 1);
	}
	
	private void drawOutline(int x, int y, int height, int width, int color1, int color2) {
		GL11.glEnable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_DEPTH_TEST);
	    GL11.glDepthMask(false);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    GL11.glAlphaFunc(GL11.GL_GREATER, 0.01f);
	    float a1 = (color1 >> 24 & 0xFF) / 255.0F;
	    float r1 = (color1 >> 16 & 0xFF) / 255.0F;
	    float g1 = (color1 >> 8 & 0xFF) / 255.0F;
	    float b1 = (color1 & 0xFF) / 255.0F;
	    float a2 = (color2 >> 24 & 0xFF) / 255.0F;
	    float r2 = (color2 >> 16 & 0xFF) / 255.0F;
	    float g2 = (color2 >> 8 & 0xFF) / 255.0F;
	    float b2 = (color2 & 0xFF) / 255.0F;
	    float r = (r1 + r2) / 2f;
	    float g = (g1 + g2) / 2f;
	    float b = (b1 + b2) / 2f;
	    float a = (a1 + a2) / 2f;
	    GL11.glColor4f(r, g, b, a);
	    mc.getTextureManager().bindTexture(GLOW_TEXTURE1);
	    Gui.drawModalRectWithCustomSizedTexture(x -18, y - 16, 0.0f, 0.0f, 25, height + 32, 25, height +32);
	    mc.getTextureManager().bindTexture(GLOW_TEXTURE2);
	    Gui.drawModalRectWithCustomSizedTexture(x +7, y - 16, 0.0f, 0.0f, width - 15, height + 32, width - 15, height +32);
	    mc.getTextureManager().bindTexture(GLOW_TEXTURE3);
	    Gui.drawModalRectWithCustomSizedTexture(x + width - 8, y - 15.7f, 0.0f, 0.0f, 26, height + 32, 26, height +32);
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glEnable(GL11.GL_DEPTH_TEST);
	    GL11.glDepthMask(true);
	}
	
	private void renderZeroday(EntityLivingBase target) {
		ScaledResolution sr = RenderCache.getScaledResolution();
        int screenX = sr.getScaledWidth() / 2;
        int screenY = sr.getScaledHeight() / 2;
        int width = 160;
        int height = 60;
        int x = screenX + offsetX;
	    int y = screenY + offsetY;
	    handleDragging(x, y, width, height);
        float circleRadius = 7f;
        float circleLineWidth = 2.1f;
        float healthPercentage = target.getHealth() / target.getMaxHealth();
        float health = (float) Math.round(target.getHealth() * 10.0f) / 10.0f;
        RenderUtils.drawRoundedRectangle(x, y, x + width, y + height, 2, background);
        Gui.drawRect(x, y + height - 2, x + width, y + height, color5);
        RenderUtils.drawGradientRect(x, y + height - 2, x + (int) (width * healthPercentage), y + height,
            color6,
            color7);
        GuiInventory.drawEntityOnScreen(x + 22, y + 54, 26, -target.rotationYaw, target.rotationPitch, target);
        Client.instance.arial.drawStringWithShadow(target.getName(), x + 44, y + 8, -1);
        Client.instance.arial.drawStringWithShadow(target.getHealth() < mc.thePlayer.getHealth() ? "Winning" : "Losing",
            x + 44, y + 44, -1);
        Client.instance.arial.drawStringWithShadow(String.valueOf(Math.round(health - 0.5)), x + 44.5f, y + 26,
            -1);
        Client.instance.arial.drawStringWithShadow(String.valueOf(Math.round(mc.thePlayer.getDistanceToEntity(target) - 0.5)),
            x + 67.2f, y + 26, -1);
        Client.instance.arial.drawStringWithShadow("69ms", x + width - 28, y + 4, -1);
        drawLoadingCircle(x + 50, y + 29, circleRadius, color1, 0.5f, circleLineWidth);
        drawLoadingCircle(x + 50, y + 29, circleRadius, color2, 1.5f, circleLineWidth);
        drawLoadingCircle(x + 70, y + 29, circleRadius, color3, 1.5f, circleLineWidth);
        drawLoadingCircle(x + 70, y + 29, circleRadius, color4, 0.5f, circleLineWidth);
        GlStateManager.disableBlend();
        GL11.glColor4f(1,1,1,1);
        if (this.target != null) {
            lastTarget = this.target;
        }
    }

    private void drawLoadingCircle(int x, int y, float radius, int color, float speed, float lineWidth) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(lineWidth);
        float time = System.currentTimeMillis() % 2000 / 1000f * speed;
        float angle = (float) (time * Math.PI * 2);
        GL11.glColor4f((float) (color >> 16 & 255) / 255.0F,(float) (color >> 8 & 255) / 255.0F,(float) (color & 255) / 255.0F,(float) (color >> 24 & 255) / 255.0F);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 30; i++) {
            float a = angle + (float) i / 30 * (float) Math.PI * 2;
            GL11.glVertex2f(
                x + (float) MathHelper.cos(a) * radius,
                y + (float) MathHelper.sin(a) * radius
            );
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
	
    private boolean isHovered(int mouseX, int mouseY, int x, int y, int width, int height, int scaleFactor) {
        int scaledMouseX = mouseX / scaleFactor;
        int scaledMouseY = (mc.displayHeight - mouseY) / scaleFactor;

        return scaledMouseX >= x &&
               scaledMouseX <= x + width &&
               scaledMouseY >= y &&
               scaledMouseY <= y + height;
    }
}
