package cryptix.altmanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import cryptix.Client;
import cryptix.altmanager.gui.CrackedLoginGui;
import cryptix.altmanager.gui.MicrosoftLoginGui;
import cryptix.altmanager.gui.NiceAltGui;
import cryptix.altmanager.microsoft.MicrosoftOAuthTranslation;
import cryptix.gui.MainMenu;
import cryptix.other.JsonHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class AltManagerGui extends GuiScreen {
    private MainMenu parent;
    private NiceAltGui nicealtgui;
    private CrackedLoginGui crackedgui = new CrackedLoginGui(this);
    private MicrosoftLoginGui tokengui = new MicrosoftLoginGui(this);
    private GuiButton loginButton, loginButton2, oauthButton, backButton, niceAltButton;
    public static List<Alt> alts = new ArrayList<>();
    public static String status = "§aIdle";

    public AltManagerGui(MainMenu parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int baseY = this.height - 60;
        this.buttonList.clear();
        this.loginButton = new GuiButton(0, centerX - 160, baseY, 100, 20, "Add Cracked");
        this.loginButton2 = new GuiButton(1, centerX - 50, baseY, 100, 20, "Token Login");
        this.oauthButton = new GuiButton(3, centerX + 60, baseY, 100, 20, "OAuth Login");
        this.backButton = new GuiButton(2, centerX - 100, baseY + 30, 200, 20, "Back");
        this.niceAltButton = new GuiButton(4, this.width - 90, 10, 80, 20, "NiceAlt");
        this.buttonList.add(loginButton);
        this.buttonList.add(loginButton2);
        this.buttonList.add(oauthButton);
        this.buttonList.add(backButton);
        this.buttonList.add(niceAltButton);
        int offsetX = 0;
        int offsetY = 0;
        int count = 0;
        for (int i = 0; i < alts.size(); i++) {
            Alt alt = alts.get(i);
            if(count > 3) {
                offsetY -= 40;
                offsetX = 0;
                count = 0;
            }
            this.buttonList.add(new GuiButton(100 + i, centerX - 220 + (offsetX * 110), 50 - offsetY, 80, 20, alt.getName()));
            this.buttonList.add(new GuiButton(1000 + i, centerX - 220 + (offsetX * 110) + 85, 50 - offsetY, 20, 20, "X"));
            count++; 
            offsetX++;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        if(System.currentTimeMillis() - SessionChanger.instance().timeSinceFail < 3000 && SessionChanger.instance().timeSinceFail != 0) {
        	status = "§cFailed login";
        }else {
        	if(status.equalsIgnoreCase("§cFailed login")) {
	        	SessionChanger.instance().timeSinceFail = 0;
	        	status = "§aIdle";
        	}
        }
        drawCenteredString(this.fontRendererObj, "Alt Manager", this.width / 2, 20, 0xFFFFFF);
        this.fontRendererObj.drawString("Current Alt: §a" + mc.getSession().getUsername(), 5, 5, 0xAAAAAA);
        this.fontRendererObj.drawString("Status: " + status, 5, 20, 0xAAAAAA);;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(crackedgui);
        } else if (button.id == 1) {
            this.mc.displayGuiScreen(tokengui);
        } else if (button.id == 2) {
            JsonHandler.saveAlts();
            this.mc.displayGuiScreen(parent);
        } else if (button.id == 3) {
            startOAuthLogin();
        } else if (button.id == 4) {
        	if(nicealtgui == null) {
        		nicealtgui = new NiceAltGui(this, Client.apikey);
        	}
        	nicealtgui.loading = false;
        	this.mc.displayGuiScreen(nicealtgui);
        } else if (button.id >= 1000) {
            int index = button.id - 1000;
            if (index >= 0 && index < alts.size()) {
                alts.remove(index);
                JsonHandler.saveAlts();
                this.initGui();
            }
        } else if (button.id >= 100) {
            Alt alt = alts.get(button.id - 100);
            if (alt.isCracked()) {
                SessionChanger.instance().loginCracked(alt.getName());
            } else {
                if (alt.hasRefreshToken()) {
                    SessionChanger.instance().loginWithRefreshToken(alt.getRefreshToken());
                } else if (alt.hasToken()) {
                    SessionChanger.instance().loginWithToken(alt.getToken(), this, false);
                }
            }
        }
    }
    
    private void startOAuthLogin() {
        status = "§6Opening browser...";
        MicrosoftOAuthTranslation.getRefreshToken(refreshToken -> {
            if (refreshToken != null && !refreshToken.isEmpty()) {
                status = "§6Logging in...";
                MicrosoftOAuthTranslation.LoginData loginData = MicrosoftOAuthTranslation.login(refreshToken);
                
                if (loginData.isGood()) {
                    SessionChanger.instance().setSessionWithData(loginData);
                    
                    Alt alt = new Alt(loginData.username, "", loginData.username, false);
                    alt.setRefreshToken(refreshToken);
                    alt.setUuid(loginData.uuid);
                    alts.add(alt);
                    JsonHandler.saveAlts();
                    mc.displayGuiScreen(this);
                    status = "§aLogged in as " + loginData.username;
                } else {
                    status = "§cOAuth login failed";
                }
            } else {
                status = "§cOAuth cancelled";
            }
        });
    }
}