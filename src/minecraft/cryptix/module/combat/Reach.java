package cryptix.module.combat;

import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;

public class Reach extends Module {
	public Setting range = new Setting("Range", this, 3, 3, 6, 1);
	public Setting chance = new Setting("Chance", this, 100, 10, 100, true);
	public Reach() {
		super("Reach", 0, Category.COMBAT);
		this.addSetting(range, chance);
	}

}
