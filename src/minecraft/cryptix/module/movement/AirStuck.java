package cryptix.module.movement;

import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.other.event.Event;
import cryptix.other.event.events.PacketSendEvent;
import net.minecraft.network.play.client.C03PacketPlayer;

public class AirStuck extends Module{
	private Setting rotate = new Setting("Allow Rotating", this, false);
	private int ticks;
	private double x,y,z;
	private float yaw, pitch;
	public AirStuck() {
		super("AirStuck", 0, Category.MOVEMENT);
		this.addSetting(rotate);
	}
	
	@Override
	public void onEnable() {
		ticks = 0;
		x = mc.thePlayer.motionX;
		y = mc.thePlayer.motionY;
		z = mc.thePlayer.motionZ;
		yaw = mc.thePlayer.rotationYaw;
		pitch = mc.thePlayer.rotationPitch;
	}
	
	@Override
	public void onRender2D() {
		if(!rotate.getBoolean()) {
			mc.thePlayer.rotationYaw = yaw;
			mc.thePlayer.rotationPitch = pitch;
		}
	}
	
	@Override
	public void onPreMotion() {
		ticks++;
		if(ticks >= 15) {
        	ticks = 0;
        	x = mc.thePlayer.motionX;
    		y = mc.thePlayer.motionY;
    		z = mc.thePlayer.motionZ;
        	return;
        }
		mc.thePlayer.motionX = x;
		mc.thePlayer.motionY = y;
		mc.thePlayer.motionZ = z;
		mc.thePlayer.setPosition(mc.thePlayer.lastTickPosX, mc.thePlayer.lastTickPosY, mc.thePlayer.lastTickPosZ);
	}
	
	@Override
	public void onEvent(Event event) {
		if(event instanceof PacketSendEvent) {
			PacketSendEvent e = (PacketSendEvent) event;
			if (e.getPacket() instanceof C03PacketPlayer && ticks != 0) {
	            e.setCancelled(true);
	        }
		}
	}

}
