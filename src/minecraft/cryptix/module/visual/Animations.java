package cryptix.module.visual;

import java.util.ArrayList;
import java.util.Arrays;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.gui.clickgui.settings.ModeSetting;
import cryptix.module.Category;
import cryptix.module.Module;

public class Animations extends Module{
	public ModeSetting mode = new ModeSetting("Mode", this, "None",Arrays.asList("None", "1.7", "Exhibition", "Spin", "Swing", "Slide"));
	public DoubleSetting speed = new DoubleSetting("Swing Speed", this, 0, -20, 4 ,true);
	public DoubleSetting scale = new DoubleSetting("Scale", this, 1, 0.1, 2 ,1);
	public DoubleSetting x = new DoubleSetting("X", this, 1, -1, 2 ,1);
	public DoubleSetting y = new DoubleSetting("Y", this, 1, -1, 2 ,1);
	public DoubleSetting z = new DoubleSetting("Z", this, 1, -1, 2 ,1);
	public Animations() {
		super("Animations", 0, Category.VISUAL);
		this.addSetting(mode, speed, scale, x, y, z);
	}

}
