package cryptix.module.visual;

import java.awt.Color;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.module.combat.AntiBot;
import cryptix.utils.RenderCache;
import cryptix.utils.render.EspUtils;
import cryptix.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;

public class PlayerESP extends Module{
	private Setting colorred, colorgreen, colorblue, teamColor, d3d, d2d, bar, box, alan;
	public PlayerESP() {
		super("PlayerESP", 0, Category.VISUAL);
		Client.instance.settingsManager.addSetting(colorred = new Setting("Red", this, 255, 0, 255, false));
		Client.instance.settingsManager.addSetting(colorgreen = new Setting("Green", this, 255, 0, 255, false));
		Client.instance.settingsManager.addSetting(colorblue = new Setting("Blue", this, 255, 0, 255, false));
		Client.instance.settingsManager.addSetting(teamColor = new Setting("Theme Color", this, false));
		Client.instance.settingsManager.addSetting(d3d = new Setting("3D", this, false));
		Client.instance.settingsManager.addSetting(d2d = new Setting("2D", this, false));
		Client.instance.settingsManager.addSetting(bar = new Setting("Health bar", this, "Normal", Arrays.asList("Disabled", "Normal", "Myau")));
		Client.instance.settingsManager.addSetting(box = new Setting("Box", this, false));
		Client.instance.settingsManager.addSetting(alan = new Setting("Alan WOOD", this, false));
	}
	
	@Override
	public void onRender3D() {
	    final boolean draw3d  = d3d.getBoolean();
	    final boolean drawBar = bar.getString().equalsIgnoreCase("Normal");
	    final boolean drawBarMyau = bar.getString().equalsIgnoreCase("Myau");
	    final boolean drawBox = box.getBoolean();
	    final boolean drawAlan = alan.getBoolean();
	    final boolean useTeamColor = teamColor.getBoolean();
	    if (!draw3d && !drawBar && !drawBox && !drawAlan && !drawBarMyau) return;
	    final int staticColor = useTeamColor ? Client.instance.moduleManager.hud.getColorInt(0, 1) :
	            ((int) colorred.getValue() << 16) |
	            ((int) colorgreen.getValue() << 8) |
	            (int) colorblue.getValue();

	    final int color = staticColor;
	    if (draw3d)  EspUtils.drawPlayersESP(mc.theWorld.playerEntities, 0, color);
	    if (drawBar) EspUtils.drawPlayersESP(mc.theWorld.playerEntities, 2, color);
	    if (drawBox) EspUtils.drawPlayersESP(mc.theWorld.playerEntities, 3, color);
	    if (drawAlan)EspUtils.drawPlayersESP(mc.theWorld.playerEntities, 4, color);
	    if (drawBarMyau)EspUtils.drawPlayersESP(mc.theWorld.playerEntities, 5, color);
	}
	
	private int getHealthColor(EntityPlayer player) {
	    float hp = player.getHealth() / player.getMaxHealth();
	    if (hp < 0.2f) return 0xFF0000;
	    if (hp < 0.6f) return 0xFFFF00;
	    return 0x00FF00;
	}
	
	@Override
	public void onRender2D() {
	    if (!d2d.getBoolean() || mc.theWorld == null || mc.thePlayer == null)
	        return;
	    int color = teamColor.getBoolean() ? Client.instance.moduleManager.hud.getColorInt(0, 1) : (0xFF000000 | ((int) colorred.getValue() << 16) | ((int) colorgreen.getValue() << 8) | (int) colorblue.getValue());
	    float a = (color >> 24 & 255) / 255.0F;
	    float r = (color >> 16 & 255) / 255.0F;
	    float g = (color >> 8 & 255) / 255.0F;
	    float b = (color & 255) / 255.0F;
	    ScaledResolution sr = RenderCache.getScaledResolution();
	    int scale = sr.getScaleFactor();
	    Tessellator tess = Tessellator.getInstance();
	    WorldRenderer wr = tess.getWorldRenderer();
	    GlStateManager.enableBlend();
	    GlStateManager.disableTexture2D();
	    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	    GlStateManager.color(r, g, b, a);
	    wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
	    for (EntityPlayer player : mc.theWorld.playerEntities) {
	        if (AntiBot.isBot(player))
	            continue;
	        if (player.isDead || player.getHealth() <= 0)
	            continue;
	        double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
	        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY;
	        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;
	        double width = player.width / 2.0;
	        double height = player.height + 0.2;
	        double w = player.width / 2.0;
	        double h = player.height + 0.2;
	        double maxX = -Double.MAX_VALUE;
	        double maxY = -Double.MAX_VALUE;
	        double minX = Double.MAX_VALUE;
	        double minY = Double.MAX_VALUE;
	        boolean visible = false;
	        for (int i = 0; i < 8; i++) {
	            double sx = x + ((i & 1) == 0 ? -w : w);
	            double sy = y + ((i & 2) == 0 ? 0 : h);
	            double sz = z + ((i & 4) == 0 ? -w : w);
	            double[] s = RenderUtils.worldToScreen(sx, sy, sz, scale);
	            if (s == null) continue;
	            float px = (float) s[0];
	            float py = (float) s[1];
	            if (px < minX) minX = px;
	            if (py < minY) minY = py;
	            if (px > maxX) maxX = px;
	            if (py > maxY) maxY = py;
	        }
	        if (minX == Float.MAX_VALUE)
	            continue;
	        draw2DBox(minX, minY, maxX, maxY, color);
	    }
	    tess.draw();
	    GlStateManager.enableTexture2D();
	    GlStateManager.disableBlend();
	}
	
	private void draw2DBox(double left, double top, double right, double bottom, int color) {
	    if (left < right) {
	        double i = left;
	        left = right;
	        right = i;
	    }
	    if (top < bottom) {
	        double j = top;
	        top = bottom;
	        bottom = j;
	    }
	    Tessellator tess = Tessellator.getInstance();
	    WorldRenderer wr = tess.getWorldRenderer();
	    wr.pos(left, top, 0).endVertex();
	    wr.pos(right, top, 0).endVertex();
	    wr.pos(right, top + 1, 0).endVertex();
	    wr.pos(left, top + 1, 0).endVertex();
	    wr.pos(left, bottom, 0).endVertex();
	    wr.pos(right, bottom, 0).endVertex();
	    wr.pos(right, bottom + 1, 0).endVertex();
	    wr.pos(left, bottom + 1, 0).endVertex();
	    wr.pos(left, top, 0).endVertex();
	    wr.pos(left + 1, top, 0).endVertex();
	    wr.pos(left + 1, bottom, 0).endVertex();
	    wr.pos(left, bottom, 0).endVertex();
	    wr.pos(right, top, 0).endVertex();
	    wr.pos(right + 1, top, 0).endVertex();
	    wr.pos(right + 1, bottom, 0).endVertex();
	    wr.pos(right, bottom, 0).endVertex();
	}
}
