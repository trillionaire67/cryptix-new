package cryptix.module.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.module.combat.AntiBot;
import cryptix.utils.RenderCache;
import cryptix.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

public class NameTags extends Module {

    private Setting background, opacity, scale, showDistance, textShadow, round, font;

    public NameTags() {
        super("NameTags", 0, Category.VISUAL);

        Client.instance.settingsManager.addSetting(opacity = new Setting("Opacity", this, 100, 0, 255, true));
        Client.instance.settingsManager.addSetting(scale = new Setting("Scale", this, 1, 0.5, 5, false));
        Client.instance.settingsManager.addSetting(background = new Setting("Background", this, true));
        Client.instance.settingsManager.addSetting(showDistance = new Setting("Show Distance", this, false));
        Client.instance.settingsManager.addSetting(textShadow = new Setting("Text Shadow", this, false));
        Client.instance.settingsManager.addSetting(round = new Setting("Round", this, false));
        Client.instance.settingsManager.addSetting(font = new Setting("Font", this, false));
    }

    @Override
    public void onRender2D() {
    	long startTime = System.nanoTime();
    	boolean distance = showDistance.getBoolean();
    	boolean back = background.getBoolean();
    	boolean rounded = round.getBoolean();
    	boolean customFont = font.getBoolean();
    	double viewerX = mc.getRenderManager().viewerPosX;
    	double viewerY = mc.getRenderManager().viewerPosY;
    	double viewerZ = mc.getRenderManager().viewerPosZ;
    	float tagScale = (float) scale.getValue();
    	ScaledResolution sr = RenderCache.getScaledResolution();
    	GlStateManager.pushMatrix();
        GlStateManager.scale(tagScale, tagScale, 1);
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player == null || player == mc.thePlayer || player.isDead) {
                continue;
            }
            double x = interpolate(player.posX, player.lastTickPosX) - viewerX;
            double y = interpolate(player.posY, player.lastTickPosY) - viewerY;
            double z = interpolate(player.posZ, player.lastTickPosZ) - viewerZ;
            double ex = x - player.posX;
            double ey = y - player.posY;
            double ez = z - player.posZ;
            AxisAlignedBB bb = player.getEntityBoundingBox().offset(ex, ey, ez).expand(0.1, 0.1, 0.1);
            double[] coords = RenderUtils.worldToScreen((bb.minX + bb.maxX) / 2.0,bb.maxY + 0.2,(bb.minZ + bb.maxZ) / 2.0, sr);
            if (coords == null) {
                continue;
            }
            float screenX = (float) coords[0];
            float screenY = (float) coords[1];
            String text = player.getDisplayName().getFormattedText();
            if (distance) {
                text += " §7[" + (int) mc.thePlayer.getDistanceToEntity(player) + "m§7]";
            }
            int width = customFont ? (int)Client.instance.sans.getStringWidth(text) : mc.fontRendererObj.getStringWidth(text);
            int height = customFont ? Client.instance.sans.getHeight() : mc.fontRendererObj.FONT_HEIGHT;
            float xPos = (screenX / tagScale) - (width / 2f);
            float yPos = screenY / tagScale - height;
            if (back) {
            	int bg = ((int) opacity.getValue() << 24);
                if (rounded) {
                    RenderUtils.drawRoundedRectangle(xPos - 4,yPos - 3,xPos + width + 4,yPos + height + 2,4,bg);
                } else {
                    Gui.drawRect(xPos - 4,yPos - 3,xPos + width + 4,yPos + height + 2,bg);
                }
            }
            if(!customFont) {
            	mc.fontRendererObj.drawString(text,xPos,yPos,Color.WHITE.getRGB(),textShadow.getBoolean());
            }else {
            	Client.instance.sans.drawString(text,xPos,yPos,Color.WHITE.getRGB());
            	GlStateManager.disableBlend();
            }
        }
        GlStateManager.popMatrix();
        System.out.println("time: " + (System.nanoTime() - startTime));
    }

    private double interpolate(double current, double old) {
        return old + (current - old) * mc.timer.renderPartialTicks;
    }
}