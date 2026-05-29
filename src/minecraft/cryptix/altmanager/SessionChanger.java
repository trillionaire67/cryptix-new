package cryptix.altmanager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cryptix.altmanager.microsoft.MicrosoftOAuthTranslation;
import cryptix.other.JsonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class SessionChanger {
	public static String username = null;
	private static SessionChanger instance;
	private final Minecraft mc = Minecraft.getMinecraft();
	public long timeSinceFail;
	
	public void loginCracked(String name) {
	    java.util.UUID uuid = java.util.UUID.nameUUIDFromBytes((name).getBytes(java.nio.charset.StandardCharsets.UTF_8));
	    mc.setSession(new Session(
	        name,
	        uuid.toString(),
	        "0",
	        "legacy"
	    ));
	}
	
	public void loginWithRefreshToken(String refreshToken) {
	    new Thread(() -> {
	        AltManagerGui.status = "§6Logging in with OAuth...";
	        MicrosoftOAuthTranslation.LoginData loginData = MicrosoftOAuthTranslation.login(refreshToken);
	        
	        if (loginData.isGood()) {
	            setSessionWithData(loginData);
	            AltManagerGui.status = "§aLogged in as " + loginData.username;
	        } else {
	            System.out.println("OAuth login failed");
	            timeSinceFail = System.currentTimeMillis();
	            AltManagerGui.status = "§cOAuth login failed";
	        }
	    }).start();
	}
	
	public void setSessionWithData(MicrosoftOAuthTranslation.LoginData loginData) {
	    mc.setSession(new Session(loginData.username, loginData.uuid, loginData.mcToken, "mojang"));
	    username = loginData.username;
	    System.out.println("OAuth login successful: " + loginData.username);
	}
	
	public void loginWithToken(String token, AltManagerGui parent, boolean add) {
        new Thread(() -> {
            try {
                String[] playerInfo = getProfileInfo(token);
                Session newSession = new Session(playerInfo[0], playerInfo[1], token, "mojang");
                mc.setSession(newSession);
                AltManagerGui.status = "§aLogged in as " + newSession.getUsername();
                if(add) {
	                Alt alt = new Alt(null,null,playerInfo[0],false);
	                alt.setToken(token);
	                AltManagerGui.alts.add(alt);
                }
                this.mc.displayGuiScreen(parent);
                JsonHandler.saveAlts();
            }catch (Exception e) {
            	timeSinceFail = System.currentTimeMillis();
            	AltManagerGui.status = "§cFailed login";
            	this.mc.displayGuiScreen(parent);
                e.printStackTrace();
            }
        }).start();
    }
	
	private String[] getProfileInfo(String token) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("https://api.minecraftservices.com/minecraft/profile");
            request.setHeader("Authorization", "Bearer " + token);
            try (CloseableHttpResponse response = client.execute(request)) {
                String jsonString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                JsonParser parser = new JsonParser();
                JsonObject json = parser.parse(jsonString).getAsJsonObject();
                String username = json.get("name").getAsString();
                String uuid = json.get("id").getAsString();
                return new String[]{username, uuid};
            }
        }
    }
	
	public static SessionChanger instance() {
		if (instance == null) {
			instance = new SessionChanger();
		}

		return instance;
	}
}