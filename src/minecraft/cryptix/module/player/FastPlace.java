package cryptix.module.player;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.Utils;
import net.minecraft.item.ItemBlock;

public class FastPlace extends Module{
	private short ticks;
	private Setting delay, blocksOnly;
	public FastPlace() {
		super("FastPlace", 0, Category.PLAYER);
		Client.instance.settingsManager.addSetting(delay = new Setting("Delay", this, 1, 0, 3, true));
		Client.instance.settingsManager.addSetting(blocksOnly = new Setting("Blocks Only", this, true));
	}
	
	@Override
	public void onPreMotion() {
		if(blocksOnly.getBoolean() && !Utils.holdingBlock()) {
			return;
		}
		if((int)delay.getValue() == 0) {
			mc.rightClickDelayTimer = 0;
		}else {
			if(ticks >= delay.getValue()) {
				mc.rightClickDelayTimer = 0;
				ticks = 0;
			}
			ticks++;
		}
	}

}
