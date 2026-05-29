package cryptix.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.BlinkUtils;
import cryptix.utils.MovementUtils;
import cryptix.utils.RotationUtils;
import cryptix.utils.Utils;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class Speed extends Module{
	private float groundY;
	private Setting mode = new Setting("Mode", this, "Vanilla", Arrays.asList("Vanilla", "NCP", "Vulcan", "Vulcan New", "Timer"));
	private Setting rotate = new Setting("Rotate", this, false);
	public Speed() {
		super("Speed", 0, Category.MOVEMENT);
		this.addSetting(mode, rotate);
	}
	
	@Override
	public void onDisable() {
		mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
		groundY = 0;
		mc.timer.timerSpeed = 1.0f;
		BlinkUtils.stopBlink();
	}
	
	@Override
	public void onPreMotion() {
		if(rotate.getBoolean()) {
			if(!mc.thePlayer.onGround) {
				mc.thePlayer.rotationYawHead = RotationUtils.getMovementYaw() + 225;
			}else {
				mc.thePlayer.rotationYawHead = RotationUtils.getMovementYaw() + 180;
			}
		}
	}
	
	@Override
	public void onPreUpdate() {
		if(rotate.getBoolean()) {
			Client.movefix = true;
		}
		this.setDisplayName(this.getName() + this.getUppercaseSuffix(this.mode.getString()));
		switch(mode.getString().toLowerCase()) {
			case "vanilla":
				if(mc.thePlayer.onGround) {
					mc.gameSettings.keyBindJump.pressed = true;
				}else {
					mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
				}
				break;
			case "timer":
				if(mc.thePlayer.onGround) {
					mc.gameSettings.keyBindJump.pressed = true;
				}else {
					mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
				}
				switch(mc.thePlayer.offGroundTicks) {
				case 0:
					BlinkUtils.stopBlink();
					break;
				case 7:
					BlinkUtils.startBlink();
					mc.timer.timerSpeed = 2.0f;
					break;
				case 8:
					mc.timer.timerSpeed = 1.0f;
					break;
				}
				break;
			case "vulcan":
				if(mc.thePlayer.onGround) {
					if(!mc.gameSettings.keyBindJump.isKeyDown()) {
						mc.thePlayer.jump();
					}
					MovementUtils.strafe(0.483);
				}
				switch(mc.thePlayer.offGroundTicks) {
					case 1:
						mc.thePlayer.motionX *= 1.01;
						mc.thePlayer.motionZ *= 1.01;
						MovementUtils.strafe();
					case 2:
						break;
					case 5:
						mc.thePlayer.motionY -= 0.20;
						break;
					case 6:
						mc.thePlayer.motionY -= 0.10;
						break;
					case 8:
						MovementUtils.strafe();
						break;
				}
				if(MovementUtils.getSpeed() < 0.222) {
					MovementUtils.strafe(0.222 + Math.random() * 0.001);
				}
				break;
			case "vulcan new":
				if(mc.thePlayer.onGround) {
					if(!mc.gameSettings.keyBindJump.isKeyDown()) {
						mc.thePlayer.jump();
					}
					if(MovementUtils.getSpeed() < 0.4) {
						MovementUtils.strafe(0.4 - Math.random() * 0.001);
					} else if(MovementUtils.getSpeed() > 0.49) {
						MovementUtils.strafe(0.48 - Math.random() * 0.001);
					}
				}
				if(mc.thePlayer.hurtTime > 0) return;
				if(MovementUtils.getSpeed() < 0.222) {
					MovementUtils.strafe(0.222 + Math.random() * 0.001);
				}
				break;
			case "ncp":
				switch(mc.thePlayer.offGroundTicks) {
					case 0:
						mc.thePlayer.motionY = 0.42F;
						MovementUtils.strafe(0.49);
						break;
					case 1:
						MovementUtils.strafe(0.34);
						break;
					case 3:
						mc.thePlayer.motionY -= 0.05;
						MovementUtils.strafe(0.32);
						break;
					case 5:
						mc.thePlayer.motionY -= 0.17;
						MovementUtils.strafe(0.3);
						break;
				}
				break;
		}
	}
	
	private float moveDirection(float rawYaw) {
	    float yaw = (rawYaw % 360.0f + 360.0f) % 360.0f > 180.0f ? (rawYaw % 360.0f + 360.0f) % 360.0f - 360.0f : (rawYaw % 360.0f + 360.0f) % 360.0f;
	    float forward = 1.0f;
	    if (mc.thePlayer.moveForward < 0.0f) {
	        yaw += 180.0f;
	    }
	    if (mc.thePlayer.moveForward < 0.0f) {
	        forward = -0.5f;
	    }
	    if (mc.thePlayer.moveForward > 0.0f) {
	        forward = 0.5f;
	    }
	    if (mc.thePlayer.moveStrafing > 0.0f) {
	        yaw -= 90.0f * forward;
	    }
	    if (mc.thePlayer.moveStrafing < 0.0f) {
	        yaw += 90.0f * forward;
	    }
	    return yaw;
	}

	private float strafeDirection() {
	    float yaw = (float) Math.toDegrees(Math.atan2(-mc.thePlayer.motionX, mc.thePlayer.motionZ));
	    if (yaw < 0.0f) {
	        yaw += 360.0f;
	    }
	    return yaw;
	}

}
