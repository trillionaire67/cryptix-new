package cryptix.module.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.ModeSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.MovementUtils;
import cryptix.utils.Utils;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class NoFall extends Module{
	private ModeSetting mode = new ModeSetting("Mode", this, "Packet", Arrays.asList("Packet", "Clip", "Timer", "Ground", "NoGround"));
	private float fallDist;
	public boolean spoof, timer, blinking;
	public NoFall() {
		super("NoFall", 0, Category.PLAYER);
		this.addSetting(mode);
	}
	
	@Override
	public void onPreMotion() {
		this.setDisplayName(this.getName() + this.getUppercaseSuffix(mode.getString()));
		BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 10, mc.thePlayer.posZ);
		if(mode.getString().equalsIgnoreCase("Packet")) {
            if (mc.thePlayer.fallDistance > 3.5 && !Utils.overVoid() && Client.instance.moduleManager.killAura.target == null) {
            	spoof = true;
            	mc.thePlayer.fallDistance = 0;
            	mc.timer.timerSpeed = 0.5F;
            	timer = true;
            	fallDist++;
            }else if(timer) {
            	mc.timer.timerSpeed = 1.0F;
            	timer = false;
            }
		}
		if(mode.getString().equalsIgnoreCase("Clip")) {
			if (mc.thePlayer.fallDistance > 3.5 && !Utils.overVoid()) {
				timer = true;
			}else if(timer && mc.thePlayer.onGround) {
				mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ);
				mc.timer.timerSpeed = 1.0F;
				timer = false;
			}
		}
		if(mode.getString().equalsIgnoreCase("Ground")) {
			spoof = true;
		}
		if(mode.getString().equalsIgnoreCase("NoGround")) {
			spoof = false;
		}
	}

}
