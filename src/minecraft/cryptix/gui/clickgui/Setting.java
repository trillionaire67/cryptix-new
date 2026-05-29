package cryptix.gui.clickgui;

import java.util.ArrayList;
import java.util.Collection;

import cryptix.module.Module;

public class Setting {
    private String name;
    private Module parent;
    private String mode;
    
    private String sval;
    private ArrayList<String> options;
    
    private boolean bval;
    
    private double dval;
    private double min;
    private double max;
    private boolean onlyint = false;
    
    private boolean visible;
    public static boolean visible2;
    private int decimalPlaces = 1;

    public Setting(String name, Module parent, String sval, ArrayList<String> options) {
        this.name = name;
        this.parent = parent;
        this.sval = sval;
        this.options = options;
        this.mode = "ModeBox";
        this.visible = true;
    }
    
    public Setting(String name, Module parent, String sval, Collection<String> options) {
        this.name = name;
        this.parent = parent;
        this.sval = sval;
        this.options = new ArrayList<String>(options);
        this.mode = "ModeBox";
        this.visible = true;
    }
    
    public Setting(String name, Module parent, boolean bval) {
        this.name = name;
        this.parent = parent;
        this.bval = bval;
        this.mode = "CheckBox";
        this.visible = true;
    }
    
    public Setting(String name, Module parent, double dval, double min, double max, boolean onlyint) {
        this.name = name;
        this.parent = parent;
        this.dval = dval;
        this.min = min;
        this.max = max;
        this.onlyint = onlyint;
        this.decimalPlaces = 2;
        this.mode = "Slider";
        this.visible = true;
    }
    
    public Setting(String name, Module parent, double dval, double min, double max, int decimalPlaces) {
        this.name = name;
        this.parent = parent;
        this.dval = dval;
        this.min = min;
        this.max = max;
        this.decimalPlaces = decimalPlaces;
        this.mode = "Slider";
        this.visible2 = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getName() {
        return name;
    }

    public Module getParentMod() {
        return parent;
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

    public boolean getBoolean() {
        return this.bval;
    }

    public void setBoolean(boolean in) {
        this.bval = in;
    }

    public double getValue() {
        if (this.onlyint) {
            this.dval = (int) dval;
        } else {
            double scale = Math.pow(10, this.decimalPlaces);
            this.dval = Math.round(this.dval * scale) / scale;
        }
        return this.dval;
    }

    public void setValue(double in) {
        this.dval = in;
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public boolean isModeBox() {
        return this.mode.equalsIgnoreCase("ModeBox");
    }

    public boolean isCheckBox() {
        return this.mode.equalsIgnoreCase("CheckBox");
    }

    public boolean isSlider() {
        return this.mode.equalsIgnoreCase("Slider");
    }

    public boolean onlyInt() {
        return this.onlyint;
    }
}
