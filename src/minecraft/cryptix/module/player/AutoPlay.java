package cryptix.module.player;

import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

public class AutoPlay extends Module{
	private long lastClickTime;
	public AutoPlay() {
		super("AutoPlay", 0, Category.PLAYER);
	}
	
	@Override
	public void onPreUpdate() {
		ItemStack stack = mc.thePlayer.inventory.getStackInSlot(7);
		if (stack != null && stack.getItem() == Items.paper && stack.getDisplayName().equalsIgnoreCase("§b§lPlay Again")) {
			mc.thePlayer.inventory.currentItem = 7;
			if(System.currentTimeMillis() - lastClickTime > 200) { // adds 0.2 second delay to clicking to prevent flags
	        	mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
	        	lastClickTime = System.currentTimeMillis();
	        }
		}
	}

}
