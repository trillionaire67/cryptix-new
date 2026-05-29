package cryptix.other.event.events;

import cryptix.Client;
import cryptix.module.Module;
import cryptix.other.event.Event;
import cryptix.utils.Utils;
import net.minecraft.network.Packet;

public class PacketReceiveEvent extends Event {
	private Packet packet;
	
	public PacketReceiveEvent innit(Packet<?> packet) {
        this.packet = packet;
        this.cancelled = false;
		return this;
    }

	public Packet getPacket() {
		return packet;
	}
	
	public void setPacket(Packet packet) {
		this.packet = packet;
	}
}
