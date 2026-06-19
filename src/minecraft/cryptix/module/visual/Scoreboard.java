package cryptix.module.visual;

import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;

public class Scoreboard extends Module{
	public Setting height = new Setting("Height", this, 0, -40, 20, true);
	public Setting round = new Setting("Round", this, 0, 0, 10, true);
	public Scoreboard() {
		super("Scoreboard", 0, Category.VISUAL);
		this.addSetting(height, round);
	}

}
