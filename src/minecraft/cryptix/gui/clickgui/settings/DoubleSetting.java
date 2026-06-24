package cryptix.gui.clickgui.settings;

import cryptix.gui.clickgui.Setting;
import cryptix.module.Module;

public class DoubleSetting extends Setting{
	private double dval;
    private double min;
    private double max;
    private int decimalPlaces = 1;
    
	public DoubleSetting(String name, Module parent, double dval, double min, double max, boolean onlyint) {
        super(name, parent);
        this.dval = dval;
        this.min = min;
        this.max = max;
        this.decimalPlaces = onlyint ? 0 : 2;
        this.mode = "Slider";
        settings++;
    }
    
    public DoubleSetting(String name, Module parent, double dval, double min, double max, int decimalPlaces) {
    	super(name, parent);
        this.dval = dval;
        this.min = min;
        this.max = max;
        this.decimalPlaces = decimalPlaces;
        this.mode = "Slider";
        settings++;
    }
    
    public double getValue() {
        double scale = Math.pow(10, this.decimalPlaces);
        this.dval = Math.round(this.dval * scale) / scale;
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
}
