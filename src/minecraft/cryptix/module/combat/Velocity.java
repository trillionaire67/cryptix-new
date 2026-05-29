package cryptix.module.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.other.event.Event;
import cryptix.other.event.events.PacketReceiveEvent;
import cryptix.utils.BlinkUtils;
import cryptix.utils.MovementUtils;
import cryptix.utils.Utils;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class Velocity extends Module{
	private Setting mode, jump, chance, kb;
	private int tick, delayTick;
	public boolean delaying;
	public int ticks;
	private KillAura aura;
	public List<Packet> packets = new ArrayList<>();
	public boolean velocity,dely;
	private Vec3 position;
	public Velocity() {
		super("Velocity", 0, Category.COMBAT);
		Client.instance.settingsManager.addSetting(chance = new Setting("Chance", this, 100, 10, 100, true));
		Client.instance.settingsManager.addSetting(jump = new Setting("Jump Reset", this, true));
		Client.instance.settingsManager.addSetting(kb = new Setting("Delay with Large KB", this, true));
		Client.instance.settingsManager.addSetting(mode = new Setting("Mode", this, "None", Arrays.asList("None", "Delay", "BlocksMC", "Vulcan", "Reduce")));
	}
	
	@Override
	public void onEnable() {
		aura = Client.instance.moduleManager.killAura;
	}
	
	@Override
	public void onPreUpdate() {

	}
	
	@Override
	public void onPostMotion() {
		tick++;
		if(ticks >= 0 && position != null && mode.getString().equalsIgnoreCase("Vulcan")) {
			ticks++;
			if(ticks < 2) return;
			position = null;
			mc.thePlayer.motionX *= -0.8;
			mc.thePlayer.motionZ *= -0.8;
		}
		if(delaying) {
			delayTick++;
		}
		ticks++;
		if(mode.getString().equalsIgnoreCase("Vulcan")) return;
		if((mc.thePlayer.onGround || delayTick >= 14) && delaying) {
			release();
		}
	}
	
	@Override
	public void onPreInput() {
		if(tick == 2) {
			mc.thePlayer.movementInput.jump = true;
		}
	}
	
	@Override
	public void onEvent(Event event) {
		if(event instanceof PacketReceiveEvent) {
			PacketReceiveEvent e = (PacketReceiveEvent) event;
			synchronized(packets) {
				if(delaying) {
					if(e.getPacket() instanceof S21PacketChunkData || mc.thePlayer.ticksExisted < 20 || e.getPacket() instanceof S40PacketDisconnect) {
						release();
						return;
					}
					if(e.getPacket() instanceof S08PacketPlayerPosLook) delayTick = 20;
					
					if(mode.getString().equalsIgnoreCase("BlocksMC")) {
						if(e.getPacket() instanceof S32PacketConfirmTransaction || e.getPacket() instanceof S12PacketEntityVelocity) {
							packets.add(e.getPacket());
							e.setCancelled(true);
						}
						return;
					}
					packets.add(e.getPacket());
					e.setCancelled(true);
					return;
				}
			}
			if(e.getPacket() instanceof S19PacketEntityStatus) { // this prevents the delay from starting when the anticheat sends a velocity packet (made by chatgpt)
	            final S19PacketEntityStatus packet = (S19PacketEntityStatus) e.getPacket();
	            if(packet.getEntity(mc.theWorld) != mc.thePlayer || packet.getOpCode() != 2) {
	                return;
	            }
	            dely = true;
	        }
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				if(mc.thePlayer == null) return;
				if(mc.thePlayer.ticksExisted < 20) return;
				if(!shouldApply()) {
			        return;
			    }
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				if(packet.getEntityID() == mc.thePlayer.getEntityId() && !mode.getString().equalsIgnoreCase("BlocksMC")) {
					Entity target = aura.target;
					double x = packet.motionX;
	                double y = packet.motionY;
	                double z = packet.motionZ;
	                double a = Math.sqrt(x * x + y * y + z * z);
					if(mode.getString().equalsIgnoreCase("Delay") && !mc.thePlayer.onGround && (a > 5000 || !kb.getBoolean())) {
						packets.add(packet);
						if(!e.isCancelled()) {
							e.setCancelled(true);
							delaying = true;
						}
						return;
					}
				}
				if(mode.getString().equalsIgnoreCase("BlocksMC")) {
					final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;
		            if (wrapper.getEntityID() == mc.thePlayer.getEntityId()) {
		            	packets.add(packet);
		            	if(!e.isCancelled()) {
							e.setCancelled(true);
							delaying = true;
						}
		                dely = false;
		                return;
		            }
				}
				if((mode.getString().equalsIgnoreCase("BlocksMC"))) {
					final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;
		            if (wrapper.getEntityID() == mc.thePlayer.getEntityId()) {
		                dely = false;
		            }
				}
				if(mode.getString().equalsIgnoreCase("Vulcan") && dely) {
					if(MovementUtils.getSpeed() < 0.25) {
						ticks = 0;
						position = mc.thePlayer.getPositionVector();
						dely = false;
					}else {
						e.setCancelled(true);
					}
				}
				if(packet.getEntityID() == mc.thePlayer.getEntityId() && mc.thePlayer.onGround && packet.getMotionY() > 0 && jump.getBoolean()) {
					tick = 2;
				}
			}
		}
	}
	
	private boolean shouldApply() {
	    return Math.random() * 100 <= chance.getValue();
	}
	
	public void release() {
        delaying = false;
        boolean bad = false;
        synchronized(packets) {
	        for(Packet packet : packets) {
	        	if(packet == null) continue; 
	        	if(this.mc.getNetHandler() == null) continue;
	        	mc.addScheduledTask(() -> packet.processPacket(mc.getNetHandler()));
	        }
        }
        if(mc.thePlayer.onGround && jump.getBoolean()) {
        	tick = 2;
        }
        if(mode.getString().equalsIgnoreCase("BlocksMC")) {
        	velocity = true;
        }
        delayTick = 0;
        packets.clear();
    }

}
