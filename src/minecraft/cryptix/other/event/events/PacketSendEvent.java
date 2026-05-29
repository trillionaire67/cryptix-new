package cryptix.other.event.events;

import cryptix.other.event.Event;
import net.minecraft.network.Packet;

public class PacketSendEvent extends Event{
	private Packet packet;
	
	public PacketSendEvent innit(Packet<?> packet) {
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
