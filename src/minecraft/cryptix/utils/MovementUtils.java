package cryptix.utils;

import org.lwjgl.input.Keyboard;

import cryptix.Client;
import cryptix.other.event.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class MovementUtils {
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	public static boolean isMoving() {
		if(mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) {
			return true;
		}
		return false;
	}
	
	public static double getSpeed() {
		return getSpeed(mc.thePlayer);
	}
	
	public static double getSpeed(Entity e) {
		return Math.sqrt(e.motionX * e.motionX + e.motionZ * e.motionZ);
	}
	
	public static void strafe() {
		strafe(getSpeed());
	}
	
	public static void strafe(double speed) {
		if(isMoving()) {
	        mc.thePlayer.motionZ = Math.cos(getDirectionRadians()) * speed;
	        mc.thePlayer.motionX = -Math.sin(getDirectionRadians()) * speed;
		}
    }
	public static void strafe(double speed, float maxChange) {
	    if (isMoving()) {
	        double targetZ = Math.cos(getDirectionRadians()) * speed;
	        double targetX = -Math.sin(getDirectionRadians()) * speed;
	        double deltaZ = targetZ - mc.thePlayer.motionZ;
	        double deltaX = targetX - mc.thePlayer.motionX;
	        deltaZ = Math.max(-maxChange, Math.min(maxChange, deltaZ));
	        deltaX = Math.max(-maxChange, Math.min(maxChange, deltaX));
	        mc.thePlayer.motionZ += deltaZ;
	        mc.thePlayer.motionX += deltaX;
	    }
	}
	
	public static void strafe(double speed, boolean moving) {
		if(!moving || isMoving()) {
	        mc.thePlayer.motionZ = Math.cos(getDirectionRadians()) * speed;
	        mc.thePlayer.motionX = -Math.sin(getDirectionRadians()) * speed;
		}
    }
	
	private static double getDirectionRadians() {
	    float yaw = Client.movefix ? mc.thePlayer.fixedRotationYaw : mc.thePlayer.rotationYaw;
	    float forward = mc.thePlayer.moveForward;
	    float strafe = mc.thePlayer.moveStrafing;

	    double angle = yaw;

	    if (forward != 0) {
	        if (strafe > 0) {
	            angle += (forward > 0) ? -45 : 45;
	        } else if (strafe < 0) {
	            angle += (forward > 0) ? 45 : -45;
	        }
	        strafe = 0;
	        angle += (forward < 0) ? 180 : 0;
	    } else {
	        if (strafe > 0) {
	            angle -= 90;
	        } else if (strafe < 0) {
	            angle += 90;
	        }
	    }

	    return Math.toRadians(angle);
	}

	public static float getDirection(float yaw, float forward, float strafe) {
		if (forward == 0 && strafe == 0) {
            return (float) Math.toRadians(yaw);
        }
        double rad = Math.toRadians(yaw);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);
        double x = forward * cos - strafe * sin;
        double z = forward * sin + strafe * cos;
        return (float) Math.atan2(z, x);
	}
	
	public static void applyFriction() {
		if(mc.thePlayer.isPotionActive(Potion.moveSpeed) ? MovementUtils.getSpeed() < 0.367 : MovementUtils.getSpeed() < 0.255) { // bmc friction shit
			strafe(mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.367 : 0.255);
		}
	}
	
	public static void disableMovement() {
        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getPosition(),EnumFacing.DOWN.getIndex(),mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem),0,0,0));
    }
	
	public static void enableMovement() {
        mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
        mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
        mc.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
        mc.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
        
        mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
        
        mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
        
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
	}
}
