package cryptix.module.visual;

import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.module.Category;
import cryptix.module.Module;

public class Scoreboard extends Module{
	public DoubleSetting height = new DoubleSetting("Height", this, 0, -40, 20, true);
	public DoubleSetting round = new DoubleSetting("Round", this, 0, 0, 10, true);
	public Scoreboard() {
		super("Scoreboard", 0, Category.VISUAL);
		this.addSetting(height, round);
	}

}
