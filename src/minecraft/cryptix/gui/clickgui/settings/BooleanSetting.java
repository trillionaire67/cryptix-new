package cryptix.gui.clickgui.settings;

import cryptix.gui.clickgui.Setting;
import cryptix.module.Module;

public class BooleanSetting extends Setting{
	private boolean bval;
	
	public BooleanSetting(String name, Module parent, boolean bval) {
        super(name, parent);
        this.bval = bval;
        this.mode = "CheckBox";
        settings++;
    }

	public boolean getBoolean() {
        return this.bval;
    }

    public void setBoolean(boolean in) {
        this.bval = in;
    }
}
