package cryptix.gui.clickgui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cryptix.Client;
import cryptix.gui.clickgui.element.Element;
import cryptix.gui.clickgui.element.ModuleButton;
import cryptix.gui.clickgui.element.ScriptButton;
import cryptix.gui.clickgui.element.elements.Slider;
import cryptix.gui.clickgui.util.FontUtil;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.script.Script;
import cryptix.utils.RenderCache;
import cryptix.utils.Utils;
import cryptix.utils.render.RenderUtils;

public class ClickGUI extends GuiScreen {
    public static ArrayList<Panel> panels;
    private static ArrayList<Panel> reversedPanels;
    private ModuleButton moduleButton;
    private ScriptButton scriptButton;
    public int alpha;
    private long lastTime = System.currentTimeMillis();
    public long startTime;
    private static final float DURATION = 0.15f;
	
	String searchText = "";
	private boolean searching = false;
	
	private int searchX = 120;
	private int searchY = 10;
	private final int searchWidth = 120;
	private final int searchHeight = 16;

    public ClickGUI() {
        FontUtil.setupFontUtils();
        panels = new ArrayList<>();
        initializePanels();
        reversedPanels = new ArrayList<>(panels);
        Collections.reverse(reversedPanels);
    }

    private void initializePanels() {
        double panelWidth = 90;
        double panelHeight = 15;
        double posX = 10;
        double posY = 10;
        double yIncrement = panelHeight + 10;
        for (Category category : Category.values()) {
            final Category cat = category;
            panels.add(new Panel(formatCategoryTitle(cat), posX, posY, panelWidth, panelHeight, false, this) {
                @Override
                public void setup() {
                    for (Module module : Client.instance.moduleManager.getModules()) {
                        if (module.getCategory() == cat) {
                            Elements.add(new ModuleButton(module, this));
                        }
                    }
                }
            });
            posY += yIncrement;
        }
        panels.add(new Panel("Scripts", posX, posY, panelWidth, panelHeight, false, this) {
            @Override
            public void setup() {
                for (Script script : Client.instance.scriptManager.getScripts()) {
                    scriptElements.add(new ScriptButton(script, this));
                }
            }
        });
    }

    private String formatCategoryTitle(Category category) {
        String name = category.name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	long now = System.currentTimeMillis();
    	float elapsed = (now - startTime) / 1000.0f;
    	float progress = elapsed / DURATION;
    	if (progress > 1f) progress = 1f;
    	progress = progress * progress * (3f - 2f * progress);
    	alpha = (int)(progress * 150);
        final ScaledResolution sr = RenderCache.getScaledResolution();
        searchX = (sr.getScaledWidth() / 2) - (searchWidth / 2);
        searchY = 10;
        int backColor = (alpha << 24);
        drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), backColor);
        int searchColor = searching ? 0xFFFFFFFF : 0x80FFFFFF;
        RenderUtils.drawRoundedRectangle(searchX,searchY,searchX + searchWidth,searchY + searchHeight,12,0xFF121212);
        RenderUtils.drawOutline(searchX,searchY,searchX + searchWidth,searchY + searchHeight,12,searchColor, searchColor);
        String text = searchText.isEmpty() ? "Search..." : searchText;
        mc.fontRendererObj.drawString(text,searchX + 4,searchY + 4,searchText.isEmpty() ? 0x888888 : 0xFFFFFF);
        for (int i = 0; i < panels.size(); i++) {
            panels.get(i).drawScreen(mouseX, mouseY, partialTicks);
        }
        handleListeningModule();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void handleListeningModule() {
        moduleButton = null;
        scriptButton = null;
        for (int i = 0; i < panels.size(); i++) {
            Panel panel = panels.get(i);
            if (!panel.visible || !panel.extended) continue;
            for (int j = 0; j < panel.Elements.size(); j++) {
                ModuleButton mb = panel.Elements.get(j);
                if (mb.listening) {
                    moduleButton = mb;
                    showListeningOverlay();
                    return;
                }
            }
            for (int j = 0; j < panel.scriptElements.size(); j++) {
                ScriptButton sb = panel.scriptElements.get(j);
                if (sb.listening) {
                    scriptButton = sb;
                    showListeningOverlay();
                    return;
                }
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        int wheel = Mouse.getDWheel();
        if (wheel != 0) {
            for (int i = 0; i < panels.size(); i++) {
                Panel p = panels.get(i);
                if (p.isBoxHovered(mouseX, mouseY)) {
                    p.onScroll(wheel);
                }
            }
        }
    }

    private void showListeningOverlay() {
        drawRect(0, 0, width, height, 0x88101010);
        GL11.glPushMatrix();
        GL11.glTranslatef(width / 2f, height / 2f, 0);
        GL11.glScalef(4f, 4f, 1f);
        FontUtil.drawTotalCenteredStringWithShadow("Listening...", 0, -10, 0xffffffff);
        GL11.glScalef(0.5f, 0.5f, 1f);
        if (moduleButton != null) {
            drawBindText(moduleButton.mod.getName(), moduleButton.mod.getKey());
        } else if (scriptButton != null) {
            drawBindText(scriptButton.script.getName(), scriptButton.script.getKey());
        }
        GL11.glPopMatrix();
    }

    private void drawBindText(String name, int key) {
        String keyName = key > -1 ? Keyboard.getKeyName(key) : "";
        String text = key > -1 ? "Press ESC to unbind " + name + " (" + keyName + ")" : "Press ESC to unbind " + name;
        FontUtil.drawTotalCenteredStringWithShadow(text, 0, 0, 0xffffffff);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    	if (mouseX >= searchX && mouseX <= searchX + searchWidth && mouseY >= searchY && mouseY <= searchY + searchHeight) {
    	    searching = true;
    	    return;
    	}
    	searching = false;
        if (moduleButton != null) return;
        for (int i = 0; i < reversedPanels.size(); i++) {
            Panel panel = reversedPanels.get(i);
            if (!panel.extended || !panel.visible) continue;
            for (int j = 0; j < panel.Elements.size(); j++) {
                ModuleButton button = panel.Elements.get(j);
                if (!button.extended) continue;
                for (int k = 0; k < button.menuelements.size(); k++) {
                    if (button.menuelements.get(k).mouseClicked(mouseX, mouseY, mouseButton)) {
                        return;
                    }
                }
            }
        }
        for (int i = 0; i < reversedPanels.size(); i++) {
            if (reversedPanels.get(i).mouseClicked(mouseX, mouseY, mouseButton)) return;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (moduleButton != null) return;
        for (int i = 0; i < reversedPanels.size(); i++) {
            Panel panel = reversedPanels.get(i);
            if (!panel.extended || !panel.visible) continue;
            for (int j = 0; j < panel.Elements.size(); j++) {
                ModuleButton button = panel.Elements.get(j);
                if (!button.extended) continue;
                for (int k = 0; k < button.menuelements.size(); k++) {
                    button.menuelements.get(k).mouseReleased(mouseX, mouseY, state);
                }
            }
        }
        for (int i = 0; i < reversedPanels.size(); i++) {
            reversedPanels.get(i).mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
    	if (searching) {
    	    if (keyCode == Keyboard.KEY_ESCAPE) {
    	        searching = false;
    	        return;
    	    }
    	    if (keyCode == Keyboard.KEY_BACK) {
    	        if (!searchText.isEmpty()) {
    	            searchText = searchText.substring(0, searchText.length() - 1);
    	        }
    	        return;
    	    }
    	    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
    	        searchText += typedChar;
    	    }
    	    return;
    	}
        for (int i = 0; i < reversedPanels.size(); i++) {
            Panel panel = reversedPanels.get(i);
            if (!panel.visible || !panel.extended) continue;
            for (int j = 0; j < panel.Elements.size(); j++) {
                try {
                    if (panel.Elements.get(j).keyTyped(typedChar, keyCode)) return;
                } catch (IOException ignored) {}
            }
            for (int j = 0; j < panel.scriptElements.size(); j++) {
                try {
                    if (panel.scriptElements.get(j).keyTyped(typedChar, keyCode)) return;
                } catch (IOException ignored) {}
            }
        }
        try {
            super.keyTyped(typedChar, keyCode);
        } catch (IOException ignored) {}
    }

    @Override
    public void onGuiClosed() {
        for (int i = 0; i < reversedPanels.size(); i++) {
            Panel panel = reversedPanels.get(i);
            if (!panel.extended || !panel.visible) continue;
            for (int j = 0; j < panel.Elements.size(); j++) {
                ModuleButton button = panel.Elements.get(j);
                if (!button.extended) continue;
                for (int k = 0; k < button.menuelements.size(); k++) {
                    Element e = button.menuelements.get(k);
                    if (e instanceof Slider) {
                        ((Slider) e).dragging = false;
                    }
                }
            }
        }
    }
}