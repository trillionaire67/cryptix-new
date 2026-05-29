package cryptix.module.visual;

import cryptix.gambling.slot.SlotMachineGui;
import cryptix.module.Category;
import cryptix.module.Module;

public class Gamble extends Module{

	public Gamble() {
		super("Gamble", 0, Category.VISUAL);
	}
	
	@Override
	public void onEnable() {
		this.toggle();
		this.mc.displayGuiScreen(new SlotMachineGui());
	}

}
