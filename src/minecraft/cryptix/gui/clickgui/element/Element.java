package cryptix.gui.clickgui.element;

import cryptix.gui.clickgui.ClickGUI;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.BooleanSetting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.gui.clickgui.settings.ModeSetting;
import cryptix.gui.clickgui.util.FontUtil;

public class Element {
    public ClickGUI clickgui;
    public ModuleButton parent;
    public Setting setting;
    public double offset;
    public double x;
	public double y;
	public double width;
    public static double height;
    public String settingName;
    public boolean startExtend = true;

    public void setup() {
        clickgui = parent.parent.clickgui;
    }

    public void update() {
        x = parent.x;
        y = parent.y + offset + 15;
        width = parent.width;
        height = 15;
        
        settingName = setting.getName();

        if (setting instanceof BooleanSetting) {
            adjustWidth(FontUtil.getStringWidth(settingName), 13);
        } else if (setting instanceof ModeSetting) {
            adjustWidth(getLongestStringWidth(((ModeSetting)setting).getOptions().toArray(new String[0])), 0);
        } else if (setting instanceof DoubleSetting) {
            height = 17;
            String displayMax = formatDouble(((DoubleSetting)setting).getMax());
            adjustWidth(FontUtil.getStringWidth(settingName) + FontUtil.getStringWidth(displayMax) + 4, 0);
        }
    }

    private int getLongestStringWidth(String[] options) {
        int longest = FontUtil.getStringWidth(settingName);
        for (String option : options) {
            longest = Math.max(longest, FontUtil.getStringWidth(option));
        }
        return longest;
    }

    private void adjustWidth(double textWidth, double padding) {
        double textX = x + width - textWidth;
        if (textX < x + padding) {
            width += (x + padding) - textX + 1;
        }
    }

    private String formatDouble(double value) {
        return String.valueOf(Math.round(value * 100D) / 100D);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //rendering goes here
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        return isHovered(mouseX, mouseY);
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        //mouse release goes here
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
