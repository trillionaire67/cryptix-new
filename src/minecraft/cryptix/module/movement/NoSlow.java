package cryptix.module.movement;

import java.util.Arrays;
import java.util.List;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.BlinkUtils;
import cryptix.utils.MovementUtils;
import cryptix.utils.Utils;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlow extends Module {
    public Setting mode;
    private int tick;
    private boolean blinking, rotated;

    public NoSlow() {
        super("NoSlow", 0, Category.MOVEMENT);
        List<String> modes = Arrays.asList("Vanilla", "Post", "Alpha", "Beta", "NoGround");
        Client.instance.settingsManager.addSetting(mode = new Setting("Mode", this, "Vanilla", modes));
    }

    @Override
    public void onPreMotion() {
        this.setDisplayName(this.getName() + this.getUppercaseSuffix(mode.getString()));
        rotated = false;
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

            case "alpha":
                break;
                
            case "beta":
            	if(!(mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.onGround)) {
            		mc.thePlayer.rotationYawHead += 45;
            		rotated = true;
            	}
                break;

            case "noground":
                Client.instance.moduleManager.noFall.spoof = false;
                break;
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