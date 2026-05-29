package cryptix.module.visual;

import java.awt.Color;
import java.util.Arrays;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.module.combat.AntiBot;
import cryptix.utils.render.EspUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;

public class PlayerESP extends Module{
	private Setting colorred, colorgreen, colorblue, teamColor, d3d, d2d, bar, box, alan;
	private static final int[] TEAM_COLORS = {
		    0x000000,
		    0x0000AA,
		    0x00AA00,
		    0x00AAAA,
		    0xAA0000,
		    0xAA00AA,
		    0xFFAA00,
		    0xAAAAAA,
		    0x555555,
		    0x5555FF,
		    0x55FF55,
		    0x55FFFF,
		    0xFF5555,
		    0xFF55FF,
		    0xFFFF55,
		    0xFFFFFF
		};
	public PlayerESP() {
		super("PlayerESP", 0, Category.VISUAL);
		Client.instance.settingsManager.addSetting(colorred = new Setting("Red", this, 255, 0, 255, false));
		Client.instance.settingsManager.addSetting(colorgreen = new Setting("Green", this, 255, 0, 255, false));
		Client.instance.settingsManager.addSetting(colorblue = new Setting("Blue", this, 255, 0, 255, false));
		Client.instance.settingsManager.addSetting(teamColor = new Setting("Team Color", this, false));
		Client.instance.settingsManager.addSetting(d3d = new Setting("3D", this, false));
		Client.instance.settingsManager.addSetting(d2d = new Setting("2D", this, false));
		Client.instance.settingsManager.addSetting(bar = new Setting("Health bar", this, "Normal", Arrays.asList("Disabled", "Normal", "Myau")));
		Client.instance.settingsManager.addSetting(box = new Setting("Box", this, false));
		Client.instance.settingsManager.addSetting(alan = new Setting("Alan WOOD", this, false));
	}
	
	@Override
	public void onRender3D() {
	    final boolean draw3d  = d3d.getBoolean();
	    final boolean draw2d  = d2d.getBoolean();
	    final boolean drawBar = bar.getString().equalsIgnoreCase("Normal");
	    final boolean drawBarMyau = bar.getString().equalsIgnoreCase("Myau");
	    final boolean drawBox = box.getBoolean();
	    final boolean drawAlan = alan.getBoolean();
	    final boolean useTeamColor = teamColor.getBoolean();
	    if (!draw3d && !draw2d && !drawBar && !drawBox && !drawAlan && !drawBarMyau) return;
	    final int staticColor = useTeamColor ? 0 :
	            ((int) colorred.getValue() << 16) |
	            ((int) colorgreen.getValue() << 8) |
	            (int) colorblue.getValue();

	    final int color = staticColor;
	    if (draw3d)  EspUtils.drawPlayersESP(mc.theWorld.playerEntities, 0, color);
	    if (draw2d)  EspUtils.drawPlayersESP(mc.theWorld.playerEntities, 1, color);
	    if (drawBar) EspUtils.drawPlayersESP(mc.theWorld.playerEntities, 2, color);
	    if (drawBox) EspUtils.drawPlayersESP(mc.theWorld.playerEntities, 3, color);
	    if (drawAlan)EspUtils.drawPlayersESP(mc.theWorld.playerEntities, 4, color);
	    if (drawBarMyau)EspUtils.drawPlayersESP(mc.theWorld.playerEntities, 5, color);
	}
	
	private int getTeamColor(EntityPlayer player) {
	    ScorePlayerTeam team = (ScorePlayerTeam) player.getTeam();
	    if (team != null) {
	        String prefix = team.getColorPrefix();
	        if (prefix != null && prefix.length() >= 2 && prefix.charAt(0) == '§') {
	            char c = prefix.charAt(1);
	            int index = "0123456789abcdef".indexOf(c);
	            if (index != -1) {
	                return TEAM_COLORS[index];
	            }
	        }
	    }
	    return 0xFFFFFF;
	}
	
	private int getHealthColor(EntityPlayer player) {
	    float hp = player.getHealth() / player.getMaxHealth();
	    if (hp < 0.2f) return 0xFF0000;
	    if (hp < 0.6f) return 0xFFFF00;
	    return 0x00FF00;
	}

}
