package cryptix.module.player;

import java.util.Arrays;

import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.ModeSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.other.event.Event;
import cryptix.other.event.events.PacketReceiveEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;

public class AutoPlay extends Module{
	private ModeSetting mode = new ModeSetting("Mode", this, "Hypixel", Arrays.asList("Hypixel", "BlocksMC"));
	private long lastClickTime;
	public AutoPlay() {
		super("AutoPlay", 0, Category.PLAYER);
		this.addSetting(mode);
	}
	
	@Override
	public void onPreUpdate() {
		ItemStack stack = mc.thePlayer.inventory.getStackInSlot(7);
		if (stack != null && stack.getItem() == Items.paper) {
			if(mode.getString().equalsIgnoreCase("BlocksMC") && stack.getDisplayName().equalsIgnoreCase("§b§lPlay Again")) {
				mc.thePlayer.inventory.currentItem = 7;
				if(System.currentTimeMillis() - lastClickTime > 200) { // adds 0.2 second delay to clicking to prevent flags
		        	mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
		        	lastClickTime = System.currentTimeMillis();
		        }
			}
		}
	}
	
	@Override
	public void onEvent(Event e) {
		if(e instanceof PacketReceiveEvent) {
			PacketReceiveEvent event = (PacketReceiveEvent) e;
			Packet<?> packet = event.getPacket();
	        if (packet instanceof S02PacketChat) {
	            S02PacketChat chat = ((S02PacketChat) packet);
	            if (mode.getString().equalsIgnoreCase("Hypixel")) {
	                if (chat.isChat()) return;
	                if (chat.getChatComponent().getFormattedText().contains("play again?")) {
	                    for (IChatComponent iChatComponent : chat.getChatComponent().getSiblings()) {
	                        for (String value : iChatComponent.toString().split("'")) {
	                            if (value.startsWith("/play") && !value.contains(".")) {
	                                sendPacket(new C01PacketChatMessage(value));
	                                break;
	                            }
	                        }
	                    }
	                }
	            }
	        }
		}
	}

}
