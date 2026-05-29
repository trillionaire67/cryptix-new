package cryptix;
 
import java.awt.Font;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import java.util.List;
 
import org.lwjgl.opengl.Display;

import cryptix.altmanager.SessionChanger;
import cryptix.font.CustomFontRenderer;
import cryptix.gui.clickgui.ClickGUI;
import cryptix.gui.clickgui.SettingsManager;
import cryptix.module.Module;
import cryptix.module.ModuleManager;
import cryptix.module.player.BedNuker;
import cryptix.other.JsonHandler;
import cryptix.other.ScaffoldBlockCount;
import cryptix.other.command.CommandManager;
import cryptix.script.Script;
import cryptix.script.ScriptManager;
import cryptix.utils.BlinkUtils;
import cryptix.utils.FrustumUtils;
import cryptix.utils.MovementUtils;
import cryptix.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.Packet;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
 
public class Client {
	public static long frameTime;
	public static String apikey = "";
	public boolean altFinder = false;
	public static ServerData lastServerData;
    public boolean blink;
	public static boolean movefix;
    public static Minecraft mc = Minecraft.getMinecraft();
    public ScaffoldBlockCount scaffold;
    public ModuleManager moduleManager;
    public SettingsManager settingsManager;
    public CommandManager commandManager;
    public ScriptManager scriptManager;
    public CustomFontRenderer arial, apple, sans, appleBig, sans12;
    public ClickGUI clickGui;
    public static Client instance = new Client();
    private String authorizedUsername = null;
    private static String enteredUsername = "";
    private static final Map<String, Font> fontCache = new HashMap<>();
    public void start() {
        settingsManager = new SettingsManager();
        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        scriptManager = new ScriptManager();
    	JsonHandler.start();
        clickGui = new ClickGUI();
        scaffold = new ScaffoldBlockCount(moduleManager.scaffold);
        arial = new CustomFontRenderer(getFont("arial.ttf", 20), true, true);
        apple = new CustomFontRenderer(getFont("apple.ttf", 20), true, true);
        appleBig = new CustomFontRenderer(getFont("apple.ttf", 42), true, true);
        sans = new CustomFontRenderer(getFont("sans.ttf", 20), true, true);
        sans12 = new CustomFontRenderer(getFont("sans.ttf", 15), true, true);
        SessionChanger.username = mc.getSession().getUsername();
        Display.setTitle("Cryptix | 5.6");
    }
 
    public void stop() {
        JsonHandler.stop();
    }
 
    private Font getFont(String location, int size) {
        String cacheKey = location + ":" + size;
        Font font = fontCache.get(cacheKey);
        
        if (font != null) {
            return font;
        }
        
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                .getResource(new ResourceLocation("cryptix/font/" + location)).getInputStream();
            Font baseFont = Font.createFont(0, is);
            font = baseFont.deriveFont(Font.PLAIN, size);
            fontCache.put(cacheKey, font);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }
 
    public static void onPreUpdate() {
    	if(mc.thePlayer.ticksExisted % 10 == 0) {
    		instance.moduleManager.hud.updateMods();
    	}
    	if(mc.getCurrentServerData() != null) {
    		lastServerData = mc.getCurrentServerData();
    	}
    	if(!instance.moduleManager.killAura.isToggled()) {
        	instance.moduleManager.killAura.reset();
        }
    	for (Module mod : instance.moduleManager.getModules()) {
    		if(!mod.isToggled()) continue;
    	    if(mod == instance.moduleManager.killAura) continue;
    	    mod.onPreUpdate();
    	}
        if(instance.moduleManager.killAura.isToggled()) {
        	instance.moduleManager.killAura.onPreUpdate();
        }
        BedNuker nuker = instance.moduleManager.bedNuker;
        if(nuker.check && nuker.teleport) {
        	nuker.spawnPos = mc.thePlayer.getPosition();
        	nuker.check = false;
        	nuker.teleport = false;
			Utils.sendClientChatMessage("whitelist");
			return;
		}else if(nuker.teleport) {
			nuker.teleport = false;
		}
        for(Script script : instance.scriptManager.getScripts()) {
        	if(!script.isEnabled()) continue;
        	script.onPreUpdate();
        }
    }
 
    public static void onMotionEvent(int type) {
    	if (type == 0) {
        	instance.scaffold.onUpdate();
        }
    	if(instance.moduleManager.speed.isToggled() && type == 0) {
        	instance.moduleManager.speed.onPreMotion();
        }
    	if(instance.moduleManager.killAura.isToggled() && type == 0) {
        	instance.moduleManager.killAura.onPreMotion();
        }
    	for (Module mod : instance.moduleManager.getModules()) {
    		if(!mod.isToggled()) continue;
            if (type == 0 && mod != instance.moduleManager.killAura && mod != instance.moduleManager.speed) {
                mod.onPreMotion();
            } else if (type == 1) {
                mod.onPostMotion();
            } else if (type == 2) {
                mod.onSprint();
            }
        }
        for(Script script : instance.scriptManager.getScripts()) {
        	if(!script.isEnabled()) continue;
        	if (type == 0)script.onPreMotion();
        	if (type == 1)script.onPostMotion();
        }
    }
 
    public static void onInputEvent(int type) {
    	if(movefix && type == 0) {
    		float forward = mc.thePlayer.movementInput.moveForward;
            float strafe = mc.thePlayer.movementInput.moveStrafe;
            final double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MovementUtils.getDirection(mc.thePlayer.fixedRotationYaw, forward, strafe)));

            if (forward == 0 && strafe == 0) {
                return;
            }
            float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;
            for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
                for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                    if (predictedStrafe == 0 && predictedForward == 0) continue;
                    final double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MovementUtils.getDirection(mc.thePlayer.rotationYaw, predictedForward, predictedStrafe)));
                    final double difference = Math.abs(angle - predictedAngle);

                    if (difference < closestDifference) {
                        closestDifference = (float) difference;
                        closestForward = predictedForward;
                        closestStrafe = predictedStrafe;
                    }
                }
            }
            mc.thePlayer.movementInput.moveForward = closestForward;
            mc.thePlayer.movementInput.moveStrafe = closestStrafe;
            if (mc.thePlayer.movementInput.sneak) {
            	mc.thePlayer.movementInput.moveStrafe = (float)((double)mc.thePlayer.movementInput.moveStrafe * 0.3D);
            	mc.thePlayer.movementInput.moveForward = (float)((double)mc.thePlayer.movementInput.moveForward * 0.3D);
            }
    	}
    	for (Module mod : instance.moduleManager.getModules()) {
    		if(!mod.isToggled()) continue;
    		if(type == 0) mod.onPreInput();
    		if(type == 1) mod.onPostInput();
        }
    }
 
    public static void onRender(int type) {
    	long startTime = System.nanoTime();
    	for (Module mod : instance.moduleManager.getModules()) {
    		if(!mod.isToggled()) continue;
            if (type == 0) mod.onRender2D();
            if (type == 1) {
            	FrustumUtils.update(mc.getRenderManager().viewerPosX,mc.getRenderManager().viewerPosY,mc.getRenderManager().viewerPosZ);
            	mod.onRender3D();
            	frameTime = startTime;
            }
        }
        if (type == 0) {
        	instance.scaffold.onRender();
        }
        for(Script script : instance.scriptManager.getScripts()) {
        	if(!script.isEnabled()) continue;
        	if (type == 0) script.onRender2D();
        	if (type == 1) script.onRender3D();
        }
    }
  
    
}