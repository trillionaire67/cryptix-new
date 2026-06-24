package cryptix.gui.clickgui.util;

import java.awt.Color;

import cryptix.Client;

public class ColorUtil {
	
	public static int getClickGUIColor() {
        int r = (int) Client.instance.moduleManager.clickGUI.color1red.getValue();
        int g = (int) Client.instance.moduleManager.clickGUI.color1green.getValue();
        int b = (int) Client.instance.moduleManager.clickGUI.color1blue.getValue();
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public static int getClickGUIColor2() {
    	int r = (int) Client.instance.moduleManager.clickGUI.color2red.getValue();
        int g = (int) Client.instance.moduleManager.clickGUI.color2green.getValue();
        int b = (int) Client.instance.moduleManager.clickGUI.color2blue.getValue();
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
