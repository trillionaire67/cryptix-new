package cryptix.gui;
 
import java.awt.Color;

import cryptix.Client;
import cryptix.altmanager.AltManagerGui;
import cryptix.other.JsonHandler;
import cryptix.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
 
public class MainMenu extends GuiScreen {
	public static MainMenu instance = new MainMenu();
    private static Minecraft mc = Minecraft.getMinecraft();
    private AltManagerGui altmanagergui= new AltManagerGui(this);
    private static final int TITLE_COLOR = (255 << 24) | (20 << 16) | (20 << 8) | 20;
    private static final ResourceLocation BG = new ResourceLocation("cryptix/main1.png");
    private static final String[] BUTTONS = {
        "Singleplayer", "Multiplayer", "AltManager", "Settings", "Quit"
    };
    
    private double textWidth;
    
    public MainMenu() {
    	textWidth = Client.instance.apple.getStringWidth(Client.name) * 3;
    }
 
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        mc.getTextureManager().bindTexture(BG);
        this.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, this.width, this.height, this.width, this.height);
        String title = Client.name;
        double textHeight = Client.instance.apple.getFontHeight() * 3;
        float x = (float) (width / 2f - textWidth / 2f);
        float y = (float) (height / 3f - textHeight / 2f);
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.5, 1.5, 1.5);
        Client.instance.appleBig.drawString(title, (x - 2) / 1.5, (float) (y / 1.5), TITLE_COLOR);
        GlStateManager.popMatrix();
        float startY = height / 3.4f + 50;
        for (int i = 0; i < BUTTONS.length; i++) {
            float btnX = (width - 100) / 2f;
            float btnY = startY + i * 30;
            boolean hovered = mouseX >= btnX && mouseY >= btnY && mouseX < btnX + 100 && mouseY < btnY + 20;
            RenderUtils.drawRoundedRectangle(btnX, btnY, btnX + 100, btnY + 20, 15, 0xAA000000);
            this.drawCenteredString(mc.fontRendererObj,BUTTONS[i],btnX + 100 / 2f,btnY + (20 - mc.fontRendererObj.FONT_HEIGHT) / 2f,hovered ? 0x4F0381 : -1);
        }
        GlStateManager.color(1, 1, 1, 1);
    }
 
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        float startY = height / 3.4f + 50;
 
        for (int i = 0; i < BUTTONS.length; i++) {
            float x = (width - 100) / 2f;
            float y = startY + i * 30;
 
            if (mouseX >= x && mouseY >= y && mouseX < x + 100 && mouseY < y + 20) {
                switch (BUTTONS[i]) {
                    case "Singleplayer":
                        mc.displayGuiScreen(new GuiSelectWorld(this));
                        break;
                    case "Multiplayer":
                        mc.displayGuiScreen(new GuiMultiplayer(this));
                        break;
                    case "AltManager":
                    	JsonHandler.loadAlts();
                    	this.mc.displayGuiScreen(altmanagergui);
                        break;
                    case "Settings":
                        mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                        break;
                    case "Quit":
                        mc.shutdown();
                        break;
                }
            }
        }
    }
 
    @Override
    public void onGuiClosed() {
    }
}