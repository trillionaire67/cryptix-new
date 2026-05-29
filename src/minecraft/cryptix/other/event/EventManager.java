package cryptix.other.event;

import cryptix.other.event.events.PacketReceiveEvent;
import cryptix.other.event.events.PacketSendEvent;

public class EventManager {
	public static PacketSendEvent PACKET_SEND_EVENT = new PacketSendEvent();
	public static PacketReceiveEvent PACKET_RECEIVE_EVENT = new PacketReceiveEvent();
}
