package cryptix.other;

import java.util.Comparator;

import cryptix.Client;
import cryptix.module.Module;
import cryptix.module.visual.HUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Compare implements Comparator<Module> {

    private final FontRenderer fr = Client.mc.fontRendererObj;

    @Override
    public int compare(Module m1, Module m2) {
        final HUD hud = Client.instance.moduleManager.hud;
        final boolean lower = hud.lowercase.getBoolean();
        final String font = hud.font.getString();
        String n1 = lower ? m1.getDisplayName().toLowerCase() : m1.getDisplayName();
        String n2 = lower ? m2.getDisplayName().toLowerCase() : m2.getDisplayName();
        float w1, w2;
        switch (font) {
            case "apple":
                w1 = (float) Client.instance.apple.getStringWidth(n1);
                w2 = (float) Client.instance.apple.getStringWidth(n2);
                break;
            case "arial":
                w1 = (float) Client.instance.arial.getStringWidth(n1);
                w2 = (float) Client.instance.arial.getStringWidth(n2);
                break;
            case "product sans":
                w1 = (float) Client.instance.sans.getStringWidth(n1);
                w2 = (float) Client.instance.sans.getStringWidth(n2);
                break;
            default:
                w1 = fr.getStringWidth(n1);
                w2 = fr.getStringWidth(n2);
        }
        return Float.compare(w1, w2);
    }
}