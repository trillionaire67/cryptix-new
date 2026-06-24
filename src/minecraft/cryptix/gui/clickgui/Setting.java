package cryptix.gui.clickgui;

import java.util.ArrayList;
import java.util.Collection;

import cryptix.module.Module;

public class Setting {
	public static int settings = 0;
    protected String name;
    protected Module parent;
    protected String mode;
    
    public Setting(String name, Module parent) {
    	this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public Module getParentMod() {
        return parent;
    }
}
