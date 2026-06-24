package cryptix.module.movement;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import cryptix.Client;
import cryptix.gui.clickgui.ClickGUI;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.BooleanSetting;
import cryptix.gui.clickgui.settings.ModeSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.other.event.Event;
import cryptix.other.event.events.PacketSendEvent;
import cryptix.utils.BlinkUtils;
import cryptix.utils.Utils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0EPacketClickWindow;

public class InvMove extends Module{
	private BooleanSetting clickgui = new BooleanSetting("ClickGUI", this, false);
	private ModeSetting mode = new ModeSetting("Mode", this, "Standard", Arrays.asList("Standard", "Hypixel"));
	private BooleanSetting rotation = new BooleanSetting("Allow Rotating", this, false);
	private int ticks;
	private boolean stopMoving;
	public InvMove() {
		super("InvMove", 0, Category.MOVEMENT);
		this.addSetting(mode, clickgui, rotation);
	}
	
	@Override
	public void onPreUpdate() {
		this.setDisplayName(this.getName() + this.getUppercaseSuffix(mode.getString()));
		ticks++;
		stopMoving = ticks < 10 || !(mc.currentScreen instanceof GuiInventory);
		if(mc.currentScreen == null) return;
		if(mc.currentScreen instanceof GuiContainer || mc.currentScreen instanceof GuiInventory || clickgui.getBoolean() && mc.currentScreen instanceof ClickGUI) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
	        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
	        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
	        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
	        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
		}
	}
	
	@Override
	public void onPreMotion() {
		if(mc.currentScreen == null) return;
		if(mc.currentScreen instanceof GuiContainer || mc.currentScreen instanceof GuiInventory || clickgui.getBoolean() && mc.currentScreen instanceof ClickGUI) {
			if (rotation.getBoolean()) {
	            float rotationSpeed = 4.0F;
	            if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
	                mc.thePlayer.rotationYaw -= rotationSpeed;
	            }
	            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
	                mc.thePlayer.rotationYaw += rotationSpeed;
	            }
	            if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
	                mc.thePlayer.rotationPitch -= rotationSpeed;
	            }
	            if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
	                mc.thePlayer.rotationPitch += rotationSpeed;
	            }
	            mc.thePlayer.rotationPitch = Math.max(-90.0F,Math.min(90.0F, mc.thePlayer.rotationPitch));
	        }
		}
	}
	
	@Override
	public void onPreInput() {
		if(mc.currentScreen == null) return;
		if(stopMoving && mode.getString().equalsIgnoreCase("Hypixel")) {
			mc.thePlayer.movementInput.moveForward = 0;
			mc.thePlayer.movementInput.moveStrafe = 0;
		}
		mc.thePlayer.setSprinting(false);
		mc.thePlayer.sprintToggleTimer = 0;
	}

	@Override
	public void onEvent(Event event) {
		if(event instanceof PacketSendEvent) {
			PacketSendEvent e = (PacketSendEvent) event;
			if(e.getPacket() instanceof C0EPacketClickWindow) {
				ticks = 0;
				if(mode.getString().equalsIgnoreCase("Hypixel")) {
					stopMoving = true;
				}
			}
		}
	}

}
