package cryptix.module.visual;

import java.util.ArrayList;
import java.util.Arrays;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;

public class Animations extends Module{
	public Setting mode = new Setting("Mode", this, "None",Arrays.asList("None", "1.7", "Exhibition", "Spin", "Swing", "Slide"));
	public Setting speed = new Setting("Swing Speed", this, 0, -20, 4 ,true);
	public Setting scale = new Setting("Scale", this, 1, 0.1, 2 ,1);
	public Setting x = new Setting("X", this, 1, -1, 2 ,1);
	public Setting y = new Setting("Y", this, 1, -1, 2 ,1);
	public Setting z = new Setting("Z", this, 1, -1, 2 ,1);
	public Animations() {
		super("Animations", 0, Category.VISUAL);
		this.addSetting(mode, speed, scale, x, y, z);
	}

}
