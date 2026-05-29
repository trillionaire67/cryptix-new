package cryptix.utils;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class BadPacketsHandler {
	private static int lastSlot = -1;
	
	public static boolean handle(Packet packet) {
		if(packet instanceof C09PacketHeldItemChange) {
			C09PacketHeldItemChange ev = (C09PacketHeldItemChange) packet;
			if(ev.getSlotId() == lastSlot) {
				return false;
			}
			lastSlot = ev.getSlotId();
		}
		return true;
	}
}
