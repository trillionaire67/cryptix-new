package cryptix.module.player;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.BooleanSetting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.Utils;
import net.minecraft.item.ItemBlock;

public class FastPlace extends Module{
	private short ticks;
	private DoubleSetting delay = new DoubleSetting("Delay", this, 1, 0, 3, true);
	private BooleanSetting blocksOnly = new BooleanSetting("Blocks Only", this, true);
	public FastPlace() {
		super("FastPlace", 0, Category.PLAYER);
		this.addSetting(this.delay, this.blocksOnly);
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
