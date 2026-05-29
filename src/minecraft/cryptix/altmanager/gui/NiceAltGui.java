package cryptix.altmanager.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cryptix.altmanager.AltManagerGui;
import cryptix.altmanager.SessionChanger;
import cryptix.other.JsonHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;

public class NiceAltGui extends GuiScreen{
	private String key;
	private String username;
	private int balance;
	private long lastStockRefresh, lastBalanceRefresh;
	private AltManagerGui parent;
	private GuiButton backButton, tokenButton, token2Button, token3Button, token4Button, genButton;
	private GuiTextField keyField;
	private int unbannedStock = 0;
	private int hqUnbannedStock = 0;
	private int bannedStock = 0;
	private int hqBannedStock = 0;
	public boolean loading = true;
	public NiceAltGui(AltManagerGui parent, String key) {
        this.parent = parent;
        this.key = key;
        JsonHandler.loadKey();
    }
	
	@Override
    public void initGui() {
		updateBalance();
		int centerX = this.width / 2;
        int fieldWidth = 150;
        int fieldHeight = 20;
        int buttonWidth = 150;
        int buttonHeight = 20;
        int baseY = this.height / 2 - 20;
        keyField = new GuiTextField(0,this.fontRendererObj,centerX - 75,60,150,20);
        keyField.setMaxStringLength(64);
        keyField.setFocused(false);
        keyField.setText(key != null ? key : "");
		this.buttonList.clear();
		this.backButton = new GuiButton(1, centerX - (buttonWidth / 2), baseY * 2 + 10, buttonWidth, buttonHeight, "Back");
		this.tokenButton = new GuiButton(2, this.width - 135, 180, 100, buttonHeight, "Unbanned");
		this.token2Button = new GuiButton(3, this.width - 135, 200, 100, buttonHeight, "Unbanned (+8)");
		this.token3Button = new GuiButton(4, this.width - 135, 220, 100, buttonHeight, "Unbanned (Ranked)");
		this.token4Button = new GuiButton(5, this.width - 135, 240, 100, buttonHeight, "Banned");
		this.genButton = new GuiButton(6, 5, 90, 100, buttonHeight, "Generate Unbanned");
		this.buttonList.add(backButton);
		this.buttonList.add(tokenButton);
		this.buttonList.add(token2Button);
		this.buttonList.add(token3Button);
		this.buttonList.add(token4Button);
		this.buttonList.add(genButton);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
	    keyField.textboxKeyTyped(typedChar, keyCode);
	    this.key = keyField.getText();
	    JsonHandler.NICEALTS_KEY = key;
	    JsonHandler.saveKey();
	    updateBalance();
	    super.keyTyped(typedChar, keyCode);
	}
	
	@Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.keyField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
	
	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawString(this.fontRendererObj, "§7Balance: " + "§a" + balance, 5, 17, -1);
        this.drawString(this.fontRendererObj, "§7Username: " + "§a" + username, 5, 5, -1);
        this.drawString(this.fontRendererObj, "§cThis only works if you have a subscription", 5, 75, -1);
        drawCenteredString(this.fontRendererObj, "Nice Alt", this.width / 2, 20, 0xFFFFFF);
        GlStateManager.pushMatrix();
        GlStateManager.scale(2, 2, 2);
        this.drawString(this.fontRendererObj, "Stock", (this.width - 135) / 2, 20 / 2, -1);
        this.drawString(this.fontRendererObj, "Generator", 5 / 2, 50 / 2, -1);
        this.drawString(this.fontRendererObj, "Purchase", (this.width - 135) / 2, 150 / 2, -1);
        GlStateManager.popMatrix();
        this.drawString(this.fontRendererObj, "§7- Unbanned: " + "§a" + unbannedStock, this.width - 135, 50, -1);
        this.drawString(this.fontRendererObj, "§7- Unbanned (+8): " + "§a" + hqUnbannedStock, this.width - 135, 65, -1);
        this.drawString(this.fontRendererObj, "§7- Unbanned (Ranked): " + "§a" + bannedStock, this.width - 135, 80, -1);
        this.drawString(this.fontRendererObj, "§7- Banned: " + "§a" + hqBannedStock, this.width - 135, 95, -1);
        this.drawString(this.fontRendererObj, "API Key:", keyField.xPosition, keyField.yPosition - 10, 0xFFFFFF);
        keyField.drawTextBox();
        long now = System.currentTimeMillis();
        if(now - lastStockRefresh > 5000 && !key.isEmpty()) {
        	lastStockRefresh = now;
        	getStockData();
        }
        if(key.isEmpty()) {
        	keyField.setText(JsonHandler.NICEALTS_KEY);
        	key = JsonHandler.NICEALTS_KEY;
        }
        if(username == null) {
        	updateBalance();
        }
        if(unbannedStock == 0 || loading || balance == 0 || key.isEmpty()) {
			tokenButton.enabled = false;
			genButton.enabled = false;
		}else {
			tokenButton.enabled = true;
			genButton.enabled = true;
		}
		if(hqUnbannedStock == 0 || loading || balance == 0 || key.isEmpty()) {
			token2Button.enabled = false;
		}else {
			token2Button.enabled = true;
		}
		if(bannedStock == 0 || loading || balance == 0 || key.isEmpty()) {
			token3Button.enabled = false;
		}else {
			token3Button.enabled = true;
		}
		if(hqBannedStock == 0 || loading || balance == 0 || key.isEmpty()) {
			token4Button.enabled = false;
		}else {
			token4Button.enabled = true;
		}
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
	
	@Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            this.mc.displayGuiScreen(parent);
        }
        if (button.id == 4) {
        	loading = true;
        	buy(token -> {
        	    if (token != null) {
        	        SessionChanger.instance().loginWithToken(token, parent, true);
        	    }
        	}, "3");
        }
        if (button.id == 3) {
        	loading = true;
        	buy(token -> {
        	    if (token != null) {
        	        SessionChanger.instance().loginWithToken(token, parent, true);
        	    }
        	}, "2");
        }
        if (button.id == 2) {
        	loading = true;
        	buy(token -> {
        	    if (token != null) {
        	        SessionChanger.instance().loginWithToken(token, parent, true);
        	        System.out.println("token: " + token);
        	    }else {
        	    	System.out.println("invalid token");
        	    }
        	}, "1");
        }
        if (button.id == 5) {
        	loading = true;
        	buy(token -> {
        	    if (token != null) {
        	        SessionChanger.instance().loginWithToken(token, parent, true);
        	        System.out.println("token: " + token);
        	    }else {
        	    	System.out.println("invalid token");
        	    }
        	}, "6");
        }
        if (button.id == 6) {
            loading = true;

            generate(token -> {
                if (token != null) {
                    System.out.println("generated token: " + token);
                    SessionChanger.instance().loginWithToken(token, parent, true);
                } else {
                    System.out.println("generate failed");
                }
            }, "Unbanned");
        }
        super.actionPerformed(button);
    }
	
	private void updateBalance() {
		long now = System.currentTimeMillis();
		if(now - lastBalanceRefresh > 10000 && !key.isEmpty()) {
			getBalance();
			lastBalanceRefresh = now;
		}
	}
	
	private void getStockData() {
	    new Thread(() -> {
	        try {
	            URL url = new URL("https://app.nicealts.com/public/stock");
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("GET");
	            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
	            conn.setRequestProperty("Accept", "application/json");
	            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            StringBuilder sb = new StringBuilder();
	            String line;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line);
	            }
	            reader.close();
	            String response = sb.toString();
	            System.out.println("STOCK RESPONSE: " + response);
	            JsonObject root = new JsonParser().parse(response).getAsJsonObject();
	            JsonObject stock = root.getAsJsonObject("stock");
	            unbannedStock = 0;
	            hqUnbannedStock = 0;
	            bannedStock = 0;
	            hqBannedStock = 0;
	            if (stock.has("1")) unbannedStock = stock.get("1").getAsInt();
	            if (stock.has("2")) hqUnbannedStock = stock.get("2").getAsInt();
	            if (stock.has("3")) bannedStock = stock.get("3").getAsInt();
	            if (stock.has("6")) hqBannedStock = stock.get("6").getAsInt();

	        } catch (Exception e) {
	            unbannedStock = 0;
	            hqUnbannedStock = 0;
	            bannedStock = 0;
	            hqBannedStock = 0;
	            e.printStackTrace();
	        }
	    }).start();
	}
	
	private void buy(java.util.function.Consumer<String> callback, String productId) {
		new Thread(() -> {
	        try {
	            URL url = new URL("https://app.nicealts.com/api/purchase");
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("POST");
	            conn.setDoOutput(true);
	            conn.setConnectTimeout(5000);
	            conn.setReadTimeout(8000);
	            conn.setRequestProperty("Content-Type", "application/json");
	            conn.setRequestProperty("Accept", "application/json");
	            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
	            String jsonInput = "{\"api_key\":\"" + key + "\",\"product_id\":\"" + productId + "\"}";
	            try (java.io.OutputStream os = conn.getOutputStream()) {
	                os.write(jsonInput.getBytes("utf-8"));
	            }
	            int status = conn.getResponseCode();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream(), "utf-8"));
	            StringBuilder sb = new StringBuilder();
	            String line;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line.trim());
	            }
	            reader.close();
	            String response = sb.toString();
	            System.out.println("PURCHASE STATUS: " + status);
	            System.out.println("PURCHASE RESPONSE: " + response);
	            if (!response.startsWith("{")) {
	                callback.accept(null);
	                return;
	            }
	            String token = extractMcToken(response);
	            callback.accept(token);
	        } catch (Exception e) {
	            e.printStackTrace();
	            callback.accept(null);
	        }
	    }).start();
	}
	
	private void getBalance() {
	    new Thread(() -> {
	        try {
	            URL url = new URL("https://app.nicealts.com/api/balance");
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("POST");
	            conn.setDoOutput(true);
	            conn.setRequestProperty("Content-Type", "application/json");
	            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
	            String jsonInput = "{\"api_key\":\"" + key + "\"}";
	            try (java.io.OutputStream os = conn.getOutputStream()) {
	                byte[] input = jsonInput.getBytes("utf-8");
	                os.write(input, 0, input.length);
	            }
	            int status = conn.getResponseCode();
	            BufferedReader reader;
	            if (status >= 200 && status < 300) {
	                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
	            } else {
	                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
	            }
	            StringBuilder sb = new StringBuilder();
	            String line;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line.trim());
	            }
	            reader.close();
	            String response = sb.toString();
	            System.out.println("BALANCE RESPONSE: " + response);
	            JsonObject obj = new JsonParser().parse(response).getAsJsonObject();

	            if (obj.has("balance")) {
	                balance = obj.get("balance").getAsInt();
	            } else {
	                balance = 0;
	            }
	            if (obj.has("username")) {
	                username = obj.get("username").getAsString();
	            }
	        } catch (Exception e) {
	            balance = 0;
	            e.printStackTrace();
	        }
	    }).start();
	}
	
	private void generate(java.util.function.Consumer<String> callback, String category) {
	    new Thread(() -> {
	        try {
	            URL url = new URL("https://app.nicealts.com/api/generate");
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("POST");
	            conn.setDoOutput(true);
	            conn.setConnectTimeout(5000);
	            conn.setReadTimeout(8000);
	            conn.setRequestProperty("Content-Type", "application/json");
	            conn.setRequestProperty("Accept", "application/json");
	            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
	            String jsonInput = "{\"api_key\":\"" + key + "\",\"category\":\"" + category + "\"}";
	            try (java.io.OutputStream os = conn.getOutputStream()) {
	                os.write(jsonInput.getBytes("utf-8"));
	            }
	            int status = conn.getResponseCode();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream(),"utf-8"));
	            StringBuilder sb = new StringBuilder();
	            String line;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line.trim());
	            }
	            reader.close();
	            String response = sb.toString();
	            System.out.println("GENERATE STATUS: " + status);
	            System.out.println("GENERATE RESPONSE: " + response);
	            if (!response.startsWith("{")) {
	                callback.accept(null);
	                return;
	            }
	            String token = extractMcToken2(response);
	            callback.accept(token);
	        } catch (Exception e) {
	            e.printStackTrace();
	            callback.accept(null);
	        }
	    }).start();
	}
	
	private String extractMcToken(String response) {
		System.out.println(response);
	    int index = response.indexOf("mctoken:");
	    int start = index + 8;
	    if (index == -1) {
	    	index = response.indexOf("Accesstoken:");
	    	start = index + 12;
	    	if (index == -1) {
	    		return response;
	    	}
	    }
	    int end = response.indexOf("|", start);
	    if (end == -1) {
	        return response.substring(start).trim();
	    }
	    return response.substring(start, end).trim();
	}
	
	private String extractMcToken2(String response) {
	    try {
	        JsonObject obj = new JsonParser().parse(response).getAsJsonObject();

	        if (obj.has("token")) {
	            return obj.get("token").getAsString();
	        }

	        return null;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

}
