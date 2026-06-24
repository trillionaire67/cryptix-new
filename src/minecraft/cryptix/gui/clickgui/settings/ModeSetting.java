package cryptix.gui.clickgui.settings;

import java.util.ArrayList;
import java.util.Collection;

import cryptix.gui.clickgui.Setting;
import cryptix.module.Module;

public class ModeSetting extends Setting{
	private String sval;
    private ArrayList<String> options;
	
	public ModeSetting(String name, Module parent, String sval, Collection<String> options) {
		super(name, parent);
        this.sval = sval;
        this.options = new ArrayList<String>(options);
        this.mode = "ModeBox";
        settings++;
    }
	
	public String getString() {
        return this.sval;
    }

    public void setString(String in) {
        this.sval = in;
    }

    public ArrayList<String> getOptions() {
        return this.options;
    }

}
