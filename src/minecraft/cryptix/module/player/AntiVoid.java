package cryptix.module.player;

import java.util.Arrays;

import org.lwjgl.input.Mouse;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.BlinkUtils;
import cryptix.utils.Utils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class AntiVoid extends Module{
	public BlockPos lastSafePos;
	private Setting mode, distance;
	public boolean blink, b1, lastInVoid;
	public AntiVoid() {
		super("AntiVoid", 0, Category.PLAYER);
        Client.instance.settingsManager.addSetting(mode = new Setting("Mode", this, "Normal", Arrays.asList("Normal", "BlocksMC", "Hypixel")));
        Client.instance.settingsManager.addSetting(distance = new Setting("Distance", this, 5.0, 1.0, 10.0, true));
	}
	
	@Override
	public void onPreMotion() {
		this.setDisplayName(this.getName() + this.getUppercaseSuffix(mode.getString()));
		boolean overVoid = Utils.overVoid();
		if(mode.getString().equalsIgnoreCase("Normal")) {
			BlockPos bp = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY - 1.0, mc.thePlayer.posZ);
	        if ((double)mc.thePlayer.fallDistance > distance.getValue() && overVoid && this.lastSafePos != null) {
	            mc.thePlayer.setPosition(this.lastSafePos.getX(), this.lastSafePos.getY(), this.lastSafePos.getZ());
	        }
	        if(!overVoid && mc.thePlayer.onGround) {
	        	this.lastSafePos = mc.thePlayer.getPosition();
	        }
		}
		if(mode.getString().equalsIgnoreCase("BlocksMC")) {
			BlockPos bp = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY - 1.0, mc.thePlayer.posZ);
	        if ((double)mc.thePlayer.fallDistance > distance.getValue() && overVoid && this.lastSafePos != null && !Client.instance.moduleManager.longjump.isToggled()) {
	        	mc.thePlayer.motionY = -0.09800000190734863;
	        	b1 = true;
	        	return;
	        }
	        if(mc.thePlayer.onGround) {
	        	this.lastSafePos = mc.thePlayer.getPosition();
	        }
	        if(b1) {
	        	mc.thePlayer.motionY = -0.09800000190734863;
	        	this.lastSafePos = null;
	        	b1 = false;
	        }
		}
		if(mode.getString().equalsIgnoreCase("Hypixel")) {
			if(b1) {
				mc.timer.timerSpeed = 1.0f;
				b1 = false;
			}
			BlockPos bp = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY - 1.0, mc.thePlayer.posZ);
	        if ((double)mc.thePlayer.fallDistance > distance.getValue() && overVoid && this.lastSafePos != null) {
	            b1 = true;
	            mc.timer.timerSpeed = 0.7f;
	            BlinkUtils.packets.offerFirst(new C03PacketPlayer.C04PacketPlayerPosition(this.lastSafePos.getX(), this.lastSafePos.getY() - 10, this.lastSafePos.getZ(), false));
	            BlinkUtils.stopBlink();
	            this.lastSafePos = null;
	            blink = false;
	        }
	        if((!overVoid || Client.instance.moduleManager.scaffold.isToggled() || Mouse.isButtonDown(1)) && blink) {
	        	BlinkUtils.stopBlink();
	        	blink = false;
	        }
	        if(overVoid && !lastInVoid) {
	        	if(mc.thePlayer.onGround) {
		        	this.lastSafePos = new BlockPos(mc.thePlayer.prevPosX, mc.thePlayer.prevPosY, mc.thePlayer.prevPosZ);
		        	BlinkUtils.startBlink();
		        	blink = true;
	        	}else {
	        		this.lastSafePos = null;
	        	}
	        }
	        lastInVoid = overVoid;
		}
	}

}
