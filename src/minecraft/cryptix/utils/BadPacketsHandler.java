package cryptix.utils;

import cryptix.Client;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class BadPacketsHandler {
	private static int lastSlot = -1;
	private static boolean blocking;
	
	public static boolean handle(Packet packet) {
		if(packet instanceof C09PacketHeldItemChange) {
			C09PacketHeldItemChange ev = (C09PacketHeldItemChange) packet;
			if(ev.getSlotId() == lastSlot && Client.mc.thePlayer.ticksExisted > 2) {
				return false;
			}
			lastSlot = ev.getSlotId();
		}
		if(packet instanceof C08PacketPlayerBlockPlacement) {
			C08PacketPlayerBlockPlacement ev = (C08PacketPlayerBlockPlacement) packet;
			if(ev.getPosition().getX() == -1 && ev.getPosition().getY() == -1 && ev.getPosition().getZ() == -1 && ev.getPlacedBlockDirection() == 255) {
				blocking = true;
			}
		}
		if(packet instanceof C07PacketPlayerDigging) {
			C07PacketPlayerDigging ev = (C07PacketPlayerDigging) packet;
			if(ev.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
				blocking = false;
			}
		}
		return true;
	}

	public static int getLastSlot() {
		return lastSlot;
	}

	public static boolean isBlocking() {
		return blocking;
	}
	
}
