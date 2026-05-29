package cryptix.module.player;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;

public class ChestStealer extends Module{
	private int delay, startDelay;
	private Setting startDelaySetting, delaySetting, closeChest, custom;
	public ChestStealer() {
		super("ChestStealer", 0, Category.PLAYER);
		Client.instance.settingsManager.addSetting(startDelaySetting = new Setting("Start Delay §aTicks", this, 2, 0, 10, true));
		Client.instance.settingsManager.addSetting(delaySetting = new Setting("Delay §aTicks", this, 2, 0, 10, true));
		Client.instance.settingsManager.addSetting(closeChest = new Setting("Auto Close", this, true));
		Client.instance.settingsManager.addSetting(custom = new Setting("Custom Chest", this, false));
	}
	
	@Override
    public void onEnable() {
		delay = 0;
	}
	
	@Override
	public void onPreMotion() {
		if(mc.thePlayer.openContainer instanceof ContainerChest) {
			startDelay++;
			if(startDelay <= startDelaySetting.getValue()) {
				return;
			}
			if(!custom.getBoolean() && !isChest()) {
				return;
			}
			ContainerChest chestContainer = (ContainerChest) mc.thePlayer.openContainer;
            boolean chestEmpty = true;
            for (int slotIndex = 0; slotIndex < chestContainer.getLowerChestInventory().getSizeInventory(); slotIndex++) {
                Slot slot = chestContainer.getSlot(slotIndex);
                
                if (slot != null && slot.getStack() != null) {
                    chestEmpty = false;
                    if (delay >= delaySetting.getValue()) {
                        mc.playerController.windowClick(chestContainer.windowId, slotIndex, 0, 1, mc.thePlayer);
                        delay = 0;
                    }
                }
            }
            if (chestEmpty) {
            	if(closeChest.getBoolean()) {
            		mc.thePlayer.closeScreen();
            	}
            } else {
                delay++;
            }
		}else {
			startDelay = 0;
		}
	}
	
	private boolean isChest() {
		return ((ContainerChest) mc.thePlayer.openContainer).getLowerChestInventory().getName().equals("Chest") || ((ContainerChest) mc.thePlayer.openContainer).getLowerChestInventory().getName().equals("Large Chest") || ((ContainerChest) mc.thePlayer.openContainer).getLowerChestInventory().getName().equals("Ender Chest");
	}
}
