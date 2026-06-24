package cryptix.module.combat;

import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.module.Category;
import cryptix.module.Module;

public class Reach extends Module {
	public DoubleSetting range = new DoubleSetting("Range", this, 3, 3, 6, 1);
	public DoubleSetting chance = new DoubleSetting("Chance", this, 100, 10, 100, true);
	public Reach() {
		super("Reach", 0, Category.COMBAT);
		this.addSetting(range, chance);
	}

}
