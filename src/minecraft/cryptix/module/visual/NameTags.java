package cryptix.module.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.BooleanSetting;
import cryptix.gui.clickgui.settings.DoubleSetting;
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

	private DoubleSetting opacity = new DoubleSetting("Opacity", this, 100, 0, 255, true);
	private DoubleSetting scale = new DoubleSetting("Scale", this, 1, 0.5, 5, false);
    private BooleanSetting background = new BooleanSetting("Background", this, true);
    private BooleanSetting showDistance = new BooleanSetting("Show Distance", this, false);
    private BooleanSetting textShadow = new BooleanSetting("Text Shadow", this, false);
    private BooleanSetting round = new BooleanSetting("Round", this, false);
    private BooleanSetting font = new BooleanSetting("Font", this, false);

    public NameTags() {
        super("NameTags", 0, Category.VISUAL);
        this.addSetting(opacity, scale, background, showDistance, textShadow, round, font);
    }

    @Override
    public void onRender2D() {
    	boolean distance = showDistance.getBoolean();
    	boolean back = background.getBoolean();
    	boolean rounded = round.getBoolean();
    	boolean customFont = font.getBoolean();
    	boolean shadow = textShadow.getBoolean();
    	double alpha = opacity.getValue();
    	double viewerX = mc.getRenderManager().viewerPosX;
    	double viewerY = mc.getRenderManager().viewerPosY;
    	double viewerZ = mc.getRenderManager().viewerPosZ;
    	float tagScale = (float) scale.getValue();
    	int scale = RenderCache.getScaledResolution().getScaleFactor();
    	GlStateManager.pushMatrix();
        GlStateManager.scale(tagScale, tagScale, 1);
        StringBuilder sb = new StringBuilder();
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player == null || player == mc.thePlayer || player.isDead) {
                continue;
            }
            AxisAlignedBB bb = player.getEntityBoundingBox();
            if(!FrustumUtils.isVisible(bb)) {
            	continue;
            }
            double x = interpolate(player.posX, player.lastTickPosX) - viewerX;
            double y = interpolate(player.posY, player.lastTickPosY) - viewerY;
            double z = interpolate(player.posZ, player.lastTickPosZ) - viewerZ;
            double[] coords = RenderUtils.worldToScreen(x,y + player.height + 0.3,z, scale);
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
            	int bg = ((int) alpha << 24);
                if (rounded) {
                    RenderUtils.drawRoundedRectangle(xPos - 4,yPos - 3,xPos + width + 4,yPos + height + 2,4,bg);
                } else {
                    Gui.drawRect(xPos - 4,yPos - 3,xPos + width + 4,yPos + height + 2,bg);
                }
            }
            final int white = 0xFFFFFFFF;
            if(!customFont) {
            	mc.fontRendererObj.drawString(text,xPos,yPos,white,shadow);
            }else {
            	Client.instance.sans.drawString(text,xPos,yPos,white);
            	GlStateManager.disableBlend();
            }
        }
        GlStateManager.popMatrix();
    }

    private double interpolate(double current, double old) {
        return old + (current - old) * mc.timer.renderPartialTicks;
    }
}