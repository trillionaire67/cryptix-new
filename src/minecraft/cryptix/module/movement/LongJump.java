package cryptix.module.movement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.other.event.Event;
import cryptix.other.event.events.PacketReceiveEvent;
import cryptix.utils.MovementUtils;
import cryptix.utils.RotationUtils;
import cryptix.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.BlockPos;

public class LongJump extends Module{
	private boolean fart, send, delaying;
	private int tic, lastSlot = -1, dela, fakeY;
	private List<Packet> packets = new ArrayList<>();
	private Setting mode = new Setting("Mode", this, "Standard", Arrays.asList("Standard", "High"));
	private Setting spoofY = new Setting("Spoof Y", this, false);
	public LongJump() {
		super("LongJump", 0, Category.MOVEMENT);
		this.addSetting(mode, spoofY);
	}
	
	@Override
	public void onDisable() {
		release();
		mc.timer.timerSpeed = 1F;
	}
	
	@Override
	public void onEnable() {
		send = false;
		fart = false;
		tic = 0;
		lastSlot = -1;
		if(getBall() == -1) {
			Utils.sendClientChatMessage("Couldnt find Fireball");
			this.toggle();
		}
		fakeY = (int) mc.thePlayer.posY;
		dela = 0;
	}
	
	@Override
	public void onPreMotion() {
		if((mc.thePlayer.posY >= fakeY || delaying) && spoofY.getBoolean()) {
			mc.thePlayer.posY = fakeY;
		}
		int ballSlot = getBall();
		tic++;
		if(ballSlot != -1 && mc.thePlayer.onGround) {
			if(tic <= 5 && !fart) {
				if(tic == 1) {
					lastSlot = mc.thePlayer.inventory.currentItem;
	                mc.thePlayer.inventory.currentItem = ballSlot;
				}
				if(tic == 2) {
					if(!mode.getString().equalsIgnoreCase("High")) {
						delaying = true;
					}
					mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
					this.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
					send = true;
				}
				if(tic == 5) {
	    			mc.thePlayer.inventory.currentItem = lastSlot;
	    		}
			}
			if(tic == 10 && !fart) {
				this.toggle();
				Utils.sendClientChatMessage("Fireball timed out");
			}
		}
		if(tic > 10 && mc.thePlayer.onGround) {
			this.toggle();
		}
		rotate();
	}
	
	@Override
	public void onPreUpdate() {
		Client.movefix = true;
	}
	
	@Override
	public void onPostMotion() {
		if(fart) {
			if(mc.thePlayer.offGroundTicks >= 11) {
				release();
			}
		}
	}
	
	@Override
	public void onEvent(Event event) {
		if(event instanceof PacketReceiveEvent) {
			PacketReceiveEvent e = (PacketReceiveEvent) event;
			if (e.getPacket() instanceof S12PacketEntityVelocity) {
	    		if(send) {
		    		fart = true;
		    		send = false;
		    		if(!mode.getString().equalsIgnoreCase("High")) {
		    			delaying = true;
		    		}else {
		    			MovementUtils.strafe(1.8);
		    		}
	    		}
	    	}
			if(delaying) {
				e.setCancelled(true);
				packets.add(e.getPacket());
			}
		}
	}
	
	@Override
    public void onPreInput() {
		if(tic == 4) {
			mc.thePlayer.movementInput.jump = true;
		}
	}
	
	public void release() {
        delaying = false;
        synchronized(packets) {
	        for(Packet packet : packets) {
	        	if(packet == null) continue; 
	        	if(this.mc.getNetHandler() == null) continue;
	        	packet.processPacket(mc.getNetHandler());
	        }
	        packets.clear();
        }
    }
	
	private int getBall() {
	    for (int i = 0; i < 9; i++) {
	        ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
	        if (itemStack != null && itemStack.getItem() instanceof ItemFireball) {
	            return i;
	        }
	    }
	    return -1;
	}
	
	private void rotate() {
		mc.thePlayer.rotationPitchHead = 90;
		mc.thePlayer.rotationYawHead = RotationUtils.getMovementYaw() + (tic == 4 ? 180 : 135);
	}

}
