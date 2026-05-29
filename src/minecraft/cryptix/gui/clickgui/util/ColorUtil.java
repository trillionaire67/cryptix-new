package cryptix.gui.clickgui.util;

import java.awt.Color;

import cryptix.Client;

public class ColorUtil {
	
	public static int getClickGUIColor() {
        int r = (int) Client.instance.settingsManager.getSettingByName("Color1 Red").getValue();
        int g = (int) Client.instance.settingsManager.getSettingByName("Color1 Green").getValue();
        int b = (int) Client.instance.settingsManager.getSettingByName("Color1 Blue").getValue();
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public static int getClickGUIColor2() {
        int r = (int) Client.instance.settingsManager.getSettingByName("Color2 Red").getValue();
        int g = (int) Client.instance.settingsManager.getSettingByName("Color2 Green").getValue();
        int b = (int) Client.instance.settingsManager.getSettingByName("Color2 Blue").getValue();
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
