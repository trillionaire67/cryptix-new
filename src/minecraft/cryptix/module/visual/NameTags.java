package cryptix.module.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.module.combat.AntiBot;
import cryptix.utils.FrustumUtils;
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
    	//long startTime = System.nanoTime();
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
        StringBuilder sb = new StringBuilder();
        for (EntityPlayer player : mc.theWorld.playerEntities) {
        	AxisAlignedBB bb = player.getEntityBoundingBox();
            if (player == null || player == mc.thePlayer || player.isDead || !FrustumUtils.isVisible(bb)) {
                continue;
            }
            double x = interpolate(player.posX, player.lastTickPosX) - viewerX;
            double y = interpolate(player.posY, player.lastTickPosY) - viewerY;
            double z = interpolate(player.posZ, player.lastTickPosZ) - viewerZ;
            double ex = x - player.posX;
            double ey = y - player.posY;
            double ez = z - player.posZ;
            double expand = 0.1;
            double minX = bb.minX + ex - expand;
            double minY = bb.minY + ey - expand;
            double minZ = bb.minZ + ez - expand;
            double maxX = bb.maxX + ex + expand;
            double maxY = bb.maxY + ey + expand;
            double maxZ = bb.maxZ + ez + expand;
            double[] coords = RenderUtils.worldToScreen((minX + maxX) / 2.0,maxY + 0.2,(minZ + maxZ) / 2.0, sr);
            if (coords == null) {
                continue;
            }
            float screenX = (float) coords[0];
            float screenY = (float) coords[1];
            sb.setLength(0);
            sb.append(player.getDisplayName().getFormattedText());
            if (distance) {
                double dx = mc.thePlayer.posX - player.posX;
                double dy = mc.thePlayer.posY - player.posY;
                double dz = mc.thePlayer.posZ - player.posZ;
                int dist = (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
                sb.append(" §7[").append(dist).append("m§7]");
            }
            String text = sb.toString();
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
        //System.out.println("time: " + (System.nanoTime() - startTime));
    }

    private double interpolate(double current, double old) {
        return old + (current - old) * mc.timer.renderPartialTicks;
    }
}