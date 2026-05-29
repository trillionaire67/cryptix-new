package cryptix.altmanager.gui;

import java.io.IOException;
import java.util.Random;

import cryptix.altmanager.Alt;
import cryptix.altmanager.AltManagerGui;
import cryptix.altmanager.SessionChanger;
import cryptix.other.JsonHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class CrackedLoginGui extends GuiScreen {
    private AltManagerGui parent;
    private GuiButton loginButton, backButton, randomButton;
    private GuiTextField usernameField;
    private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
    private final int USERNAME_LENGTH = 10;

    public CrackedLoginGui(AltManagerGui parent) {
        this.parent = parent;
    }
    
    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int fieldWidth = 150;
        int fieldHeight = 20;
        int buttonWidth = 150;
        int buttonHeight = 20;
        int baseY = this.height / 2 - 20;
        
        this.buttonList.clear();
        this.usernameField = new GuiTextField(0, this.fontRendererObj, centerX - (fieldWidth / 2), baseY, fieldWidth, fieldHeight);
        this.usernameField.setMaxStringLength(14);
        
        this.loginButton = new GuiButton(0, centerX - (buttonWidth / 2), baseY + fieldHeight + 35, buttonWidth / 2, buttonHeight, "Login Cracked");
        this.backButton = new GuiButton(1, centerX, baseY + fieldHeight + 35, buttonWidth / 2, buttonHeight, "Back");
        this.randomButton = new GuiButton(2, centerX - (buttonWidth / 2), baseY + fieldHeight + 8, buttonWidth, buttonHeight, "Generate Random");
        
        this.buttonList.add(loginButton);
        this.buttonList.add(backButton);
        this.buttonList.add(randomButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawCenteredString(this.fontRendererObj, "Cracked Login", this.width / 2, 20, 0xFFFFFF);
        this.usernameField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
        	if(!usernameField.getText().equals("")) {
        		parent.alts.add(new Alt(null, null, usernameField.getText(), true));
        		SessionChanger.instance().loginCracked(usernameField.getText());
        		JsonHandler.saveAlts();
        		this.mc.displayGuiScreen(parent);
        	}
        } else if (button.id == 1) {
            this.mc.displayGuiScreen(parent);
        } else if (button.id == 2) {
            usernameField.setText(generateRandomUsername());
        }
    }
    
    private String generateRandomUsername() {
        Random random = new Random();
        StringBuilder username = new StringBuilder();
        for (int i = 0; i < USERNAME_LENGTH; i++) {
            username.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return username.toString();
    }
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.usernameField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.usernameField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }


}
