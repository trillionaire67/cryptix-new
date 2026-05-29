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
	private int tic, lastSlot = -1, dela;
	private List<Packet> packets = new ArrayList<>();
	private Setting mode = new Setting("Mode", this, "Standard", Arrays.asList("Standard", "High"));
	public LongJump() {
		super("LongJump", 0, Category.MOVEMENT);
		this.addSetting(mode);
	}
	
	@Override
	public void onDisable() {
		release();
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
		dela = 0;
	}
	
	@Override
	public void onPreMotion() {
		int ballSlot = getBall();
		tic++;
		if(ballSlot != -1 && mc.thePlayer.onGround) {
			if(tic <= 3 && !fart) {
				if(tic == 1) {
					lastSlot = mc.thePlayer.inventory.currentItem;
	                mc.thePlayer.inventory.currentItem = ballSlot;
				}
				if(tic == 2) {
					delaying = true;
					mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
					this.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
					send = true;
				}
				if(tic == 3) {
	    			mc.thePlayer.inventory.currentItem = lastSlot;
	    		}
			}
			if(!fart) {
				rotate();
			}
			if(tic == 10 && !fart) {
				this.toggle();
				Utils.sendClientChatMessage("Fireball timed out");
			}
		}
	}
	
	@Override
	public void onPostMotion() {
		if(fart) {
			if(mc.thePlayer.offGroundTicks >= 11) {
				release();
				this.toggle();
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
		    		delaying = true;
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
	        	mc.addScheduledTask(() -> packet.processPacket(mc.getNetHandler()));
	        }
        }
        packets.clear();
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
	}

}
