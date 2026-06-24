package cryptix.module.visual;

import org.lwjgl.input.Keyboard;

import cryptix.Client;
import cryptix.gambling.slot.SlotMachineGui;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.BooleanSetting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.Utils;

public class ClickGUI extends Module{
	public BooleanSetting sound = new BooleanSetting("Sound", this, true);
	public BooleanSetting square = new BooleanSetting("Square", this, false);
	public BooleanSetting font = new BooleanSetting("Font", this, false);
	public DoubleSetting color1red = new DoubleSetting("Color1 Red", this, 120, 0, 255, true);
	public DoubleSetting color1green = new DoubleSetting("Color1 Green", this, 120, 0, 255, true);
	public DoubleSetting color1blue = new DoubleSetting("Color1 Blue", this, 120, 0, 255, true);
	public DoubleSetting color2red = new DoubleSetting("Color2 Red", this, 120, 0, 255, true);
	public DoubleSetting color2green = new DoubleSetting("Color2 Green", this, 120, 0, 255, true);
	public DoubleSetting color2blue = new DoubleSetting("Color2 Blue", this, 120, 0, 255, true);
	public DoubleSetting alphaSetting = new DoubleSetting("Alpha", this, 255, 0, 255, true);
	public ClickGUI() {
		super("ClickGUI", Keyboard.KEY_RSHIFT, Category.VISUAL);
		this.addSetting(sound, square, font, color1red, color1green, color1blue, color2red, color2green, color2blue, alphaSetting);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		Client.instance.clickGui.alpha = 0;
		Client.instance.clickGui.startTime = System.currentTimeMillis();
		mc.displayGuiScreen(Client.instance.clickGui);
		toggle();
	}

}
