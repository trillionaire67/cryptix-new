package cryptix.module.movement;

import java.util.Arrays;
import java.util.List;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.ModeSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.other.event.Event;
import cryptix.other.event.events.RotationEvent;
import cryptix.utils.BlinkUtils;
import cryptix.utils.MovementUtils;
import cryptix.utils.RotationUtils;
import cryptix.utils.Utils;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlow extends Module {
    public ModeSetting mode = new ModeSetting("Mode", this, "Vanilla", Arrays.asList("Vanilla", "Post", "Alpha", "Beta", "Gamma", "NoGround"));
    private int tick;
    private boolean blinking, rotated;

    public NoSlow() {
        super("NoSlow", 0, Category.MOVEMENT);
        this.addSetting(mode);
    }

    @Override
    public void onPreMotion() {
        this.setDisplayName(this.getName() + this.getUppercaseSuffix(mode.getString()));
        if (!mc.thePlayer.isUsingItem()) {
            tick = 0;
            if(blinking){
            	BlinkUtils.stopBlink();
            	blinking = false;
            }
            return;
        }
        String currentMode = mode.getString().toLowerCase();
        boolean holdingSword = Utils.holdingSword();
        switch (currentMode) {
            case "post":
                if (holdingSword) {
                    sendPacket(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.UP));
                }
                break; //retarded objdbgjsdbfsj
            case "gamma":
            	if(tick == 1 && !Utils.holdingSword()) {
            		sendPacket(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                    sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 0, mc.thePlayer.getHeldItem(), 0.0f, 0.0f, 0.0f));
                }
                break;
            case "alpha":
                break;
            case "beta":
                break;

            case "noground":
                Client.instance.moduleManager.noFall.spoof = false;
                break;
        }
        tick++;
    }
    
    @Override
    public void onEvent(Event e) {
    	if(e instanceof RotationEvent) {
    		rotated = false;
    		if (!mc.thePlayer.isUsingItem()) {
    			return;
    		}
    		if(mode.getString().equalsIgnoreCase("Beta")) {
    			((RotationEvent) e).setYaw(RotationUtils.getMovementYaw() + 180);
            	if(!(mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.onGround)) {
            		((RotationEvent) e).setYaw(((RotationEvent) e).getYaw() - 45);
            		rotated = true;
            	}
    		}
    	}
    }
    
    @Override
    public void onPreUpdate() {
    	if (mc.thePlayer.isUsingItem() && mode.getString().equalsIgnoreCase("Beta")) {
    		Client.movefix = true;
    	}
    }

    @Override
    public void onPostMotion() {
        if (mc.thePlayer.isUsingItem() && "Post".equalsIgnoreCase(mode.getString()) && Utils.holdingSword()) {
            sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
        }
    }
    
    @Override
    public void onSprint() {
    	if("Beta".equalsIgnoreCase(mode.getString())) {
    		if(mc.thePlayer.movementInput.moveForward < 0.05) return;
            if(mc.thePlayer.isCollidedHorizontally) return;
            mc.thePlayer.setSprinting(true);
    	}
    	if("Gamma".equalsIgnoreCase(mode.getString())) {
            mc.thePlayer.setSprinting(true);
    	}
    }

    @Override
    public void onPreInput() {
        if (mc.thePlayer.isUsingItem() && MovementUtils.isMoving() && ("Alpha".equalsIgnoreCase(mode.getString()) || "Beta".equalsIgnoreCase(mode.getString()))) {
            mc.thePlayer.movementInput.moveForward *= 0.2f;
            mc.thePlayer.movementInput.moveStrafe *= 0.2f;
            mc.gameSettings.keyBindSprint.pressed = true;
        }
        if(rotated) {
        	mc.thePlayer.movementInput.jump = false;
        }
    }

    @Override
    public void onDisable() {
        tick = 0;
    }
}