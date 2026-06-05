package cryptix.utils;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public class BlinkUtils {
	private static final Minecraft mc = Minecraft.getMinecraft();
	public static Deque<Packet<?>> packets = new ConcurrentLinkedDeque<>();
	public static boolean blinking = false;
	
	public static void startBlink() {
		if(mc.getCurrentServerData() == null) return;
		blinking = true;
	}
	
	public static boolean isBlinking() {
		return blinking;
	}
	
	public static void stopBlink() {
	    blinking = false;
	    if(mc.theWorld == null) return;
	    for (Packet<?> packet : packets) {
	    	mc.getNetHandler().getNetworkManager().sendPacket(packet, null);
	    }
	    packets.clear();
	}
	
	public static void releaseOne() {
	    if(mc.theWorld == null) return;
	    
	    blinking = false;
	    for (Packet<?> packet : packets) {
	    	packets.remove(packet);
            mc.getNetHandler().getNetworkManager().sendPacket(packet, null);
	        if (packet instanceof C03PacketPlayer || packet instanceof C03PacketPlayer.C04PacketPlayerPosition || packet instanceof C03PacketPlayer.C05PacketPlayerLook || packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
	            break;
	        }
	    }
	    blinking = true;
	}
	
	public static void stopBlinkNoC03() {
	    blinking = false;
	    if(mc.theWorld == null) return;
	    for (Packet<?> packet : packets) {
	    	if(packet instanceof C03PacketPlayer) continue;
	        mc.thePlayer.sendQueue.addToSendQueue(packet);
	    }
	    packets.clear();
	}
	
	public static class DelayedPacket {
        public final Packet packet;
        public final long timestamp;

        public DelayedPacket(Packet packet, long timestamp) {
            this.packet = packet;
            this.timestamp = timestamp;
        }
    }
}
