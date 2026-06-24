package cryptix.module.movement;

import java.awt.Color;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.BooleanSetting;
import cryptix.gui.clickgui.settings.ModeSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.other.event.Event;
import cryptix.other.event.events.PacketReceiveEvent;
import cryptix.utils.BlinkUtils;
import cryptix.utils.MovementUtils;
import cryptix.utils.RotationUtils;
import cryptix.utils.Utils;
import cryptix.utils.render.RenderUtils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Fly extends Module {
    public int i;
    private double floatPos;
    private boolean hasShot = false;
    private int chargeTicks = 0, lastSlot;
    private float silentPitch = 0.0f;
    
    public ModeSetting mode = new ModeSetting("Mode", this, "Vanilla", Arrays.asList("Vanilla", "BlocksMC", "Experimental", "Verus", "Vulcan", "OldNCP"));
    private BooleanSetting jump = new BooleanSetting("Jump on Edge", this, false);

    public Fly() {
        super("Fly", 0, Category.MOVEMENT);
        this.addSetting(mode, jump);
    }
    
    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        chargeTicks = 0;
        BlinkUtils.stopBlink();
    }
    
    @Override
    public void onEnable() {
        i = 0;
        floatPos = mc.thePlayer.posY;
        hasShot = false;
        chargeTicks = 0;
        silentPitch = -90f;
        if(mode.getString().equalsIgnoreCase("Experimental")) {
            Utils.setMotion(0);
        }
        
        if(!mc.thePlayer.onGround && mode.getString().equalsIgnoreCase("BlocksMC")) {
            Utils.sendClientChatMessage("Start on ground");
        }
        mc.timer.timerSpeed = 1.0f;
    }
    
    @Override
    public void onPreMotion() {
        switch(mode.getString().toLowerCase()) {
            case "vanilla":
            	i++;
            	if(mc.thePlayer.motionY < 0) {
            		mc.thePlayer.motionY = 0;
            	}
                break;
            case "oldncp":
            	if(mc.thePlayer.onGround) {
            		mc.thePlayer.jump();
            		MovementUtils.strafe(0.5);
            	}else {
            		mc.thePlayer.motionY = 0;
            		i++;
            		mc.timer.timerSpeed = 1.05f;
            		switch(i) {
            		case 1:
            			mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-12D, mc.thePlayer.posZ);
            			break;
            		case 2:
            			mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0E-12D, mc.thePlayer.posZ);
            			break;
            		case 3:
            			mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-12D, mc.thePlayer.posZ);
            			i = 0;
            			break;
            		}
            		MovementUtils.strafe(0.26);
            	}
                break;
			case "blocksmc":
				if(!this.isToggled()) return;
				if(i == 1 && mc.thePlayer.onGround) {
					MovementUtils.strafe(0);
					this.toggle();
					return;
				}   
				if (mc.thePlayer.onGround && (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock() == Blocks.air || !jump.getBoolean())) {
				    mc.thePlayer.motionY = 0.424F;
				    MovementUtils.strafe(mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.68 : 0.485);
				    hasShot = true;
				} else {
					if(Utils.getBlocks() != -1) {
						if(hasShot) {
							sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getPosition(),EnumFacing.DOWN.getIndex(),mc.thePlayer.inventory.getStackInSlot(Utils.getBlocks()),0.5F,1F,0.5F));
						}
					}else {
						this.toggle();
						Utils.sendClientChatMessage("you need blocks in your hotbar or longjump will fail");
					}
					if (mc.thePlayer.offGroundTicks == 7 && i == 0) {
						MovementUtils.strafe(mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.62 : 0.46);
				        i = 1;
					}
					if (mc.thePlayer.offGroundTicks > 8 && i == 1) {
				       mc.thePlayer.motionX *= 0.99;
				       mc.thePlayer.motionZ *= 0.99;
				    }
					if((mc.thePlayer.offGroundTicks == 11 || mc.thePlayer.offGroundTicks == 12) && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ)).getBlock() != Blocks.air) {
						mc.thePlayer.motionY += 0.075;
					}else if(mc.thePlayer.motionY < 0) {
						mc.thePlayer.motionY += 0.028;
					}
				}
				break;
            case "verus":
            	if(mc.thePlayer.offGroundTicks % (mc.gameSettings.keyBindJump.isKeyDown() ? 10 : 20) == 0) {
            		mc.thePlayer.jump();
            	}else if(mc.thePlayer.motionY < -0.0784000015258789) {
            		mc.thePlayer.motionY = -0.0784000015258789;
            	}
            	MovementUtils.strafe(0.4, false);
            	break;
            case "vulcan":
                i++;
            	break;
        }
    }
    
    @Override
	public void onEvent(Event event) {
		if(event instanceof PacketReceiveEvent) {
			PacketReceiveEvent e = (PacketReceiveEvent) event;
	    	if(e.getPacket() instanceof S08PacketPlayerPosLook) {
	    		//e.setCancelled(true);
	    		if(chargeTicks == 0) {
		    		chargeTicks++;
	    		}
	    	}
		}
    }
    
    @Override
    public void onRender2D() {
    	
    }
}
