package cryptix.other.event;

import cryptix.other.event.events.PacketReceiveEvent;
import cryptix.other.event.events.PacketSendEvent;
import cryptix.other.event.events.RotationEvent;

public class EventManager {
	public static PacketSendEvent PACKET_SEND_EVENT = new PacketSendEvent();
	public static PacketReceiveEvent PACKET_RECEIVE_EVENT = new PacketReceiveEvent();
	public static RotationEvent ROTATION_EVENT = new RotationEvent();
}
