package cryptix.module.combat;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.gui.clickgui.settings.ModeSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.other.event.Event;
import cryptix.other.event.events.PacketSendEvent;
import cryptix.utils.Utils;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class SprintReset extends Module{
	private ModeSetting mode = new ModeSetting("Mode", this, "WTap/Legit", Arrays.asList("WTap/Legit", "Packet"));
	private DoubleSetting delaySetting = new DoubleSetting("Delay", this, 5, 1, 10, true);
	private int tick, delay;
	public SprintReset() {
		super("SprintReset", 0, Category.COMBAT);
		this.addSetting(this.mode, this.delaySetting);
	}
	
	@Override
	public void onPreMotion() {
		delay++;
		if(!mc.thePlayer.isSprinting()) {
			delay = 0;
		}
		if(mode.getString().equalsIgnoreCase("WTap/Legit")) {
			switch(tick) {
				case 1:
					mc.gameSettings.keyBindForward.pressed = false;
					tick++;
					break;
				case 2:
					mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
					tick = 0;
			}
		}
	}
	
	@Override
	public void onPostInput() {
		if(mode.getString().equalsIgnoreCase("Packet")) {
			if(tick < 2) {
				mc.thePlayer.setSprinting(false);
				tick++;
			}
		}
	}
	
	@Override
	public void onEvent(Event event) {
		if(event instanceof PacketSendEvent) {
			PacketSendEvent e = (PacketSendEvent) event;
			if(e.getPacket() instanceof C02PacketUseEntity) {
				if (((C02PacketUseEntity) e.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK && mc.thePlayer.isSprinting()) {
					attack();
				}
			}
		}
	}
	
	public void attack() {
		if(delay >= delaySetting.getValue()) {
			tick = 1;
			delay = 0;
		}
	}

}
