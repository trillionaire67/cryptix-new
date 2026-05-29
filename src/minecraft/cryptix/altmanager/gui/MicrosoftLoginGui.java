package cryptix.altmanager.gui;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cryptix.altmanager.Alt;
import cryptix.altmanager.AltManagerGui;
import cryptix.altmanager.SessionChanger;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.Session;

public class MicrosoftLoginGui extends GuiScreen{
	private AltManagerGui parent;
	private GuiButton loginButton, backButton;
	private GuiTextField tokenField;
	
	public MicrosoftLoginGui(AltManagerGui parent) {
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
        this.tokenField = new GuiTextField(0, this.fontRendererObj, centerX - (fieldWidth / 2), baseY, fieldWidth, fieldHeight);
        this.tokenField.setMaxStringLength(32767);
        this.loginButton = new GuiButton(0, centerX - (buttonWidth / 2), baseY + fieldHeight + 10, buttonWidth, buttonHeight, "Login");
        this.backButton = new GuiButton(1, centerX - (buttonWidth / 2), baseY + fieldHeight + 40, buttonWidth, buttonHeight, "Back");
        
        this.buttonList.add(loginButton);
        this.buttonList.add(backButton);
    }
	
	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawCenteredString(this.fontRendererObj, "Token Login", this.width / 2, 20, 0xFFFFFF);
        this.tokenField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
	
	@Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
        	SessionChanger.instance().loginWithToken(tokenField.getText(), parent, true);
        } else if (button.id == 1) {
            this.mc.displayGuiScreen(parent);
        }
        super.actionPerformed(button);
    }
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.tokenField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.tokenField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
