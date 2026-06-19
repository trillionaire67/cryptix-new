package cryptix.module;

import java.util.ArrayList;
import java.util.List;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.other.JsonHandler;
import cryptix.other.event.Event;
import cryptix.script.Script;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.network.Packet;
import net.minecraft.util.ResourceLocation;

public abstract class Module {
	private static ResourceLocation iSound = new ResourceLocation("gui.button.press");
	protected static final Minecraft mc = Minecraft.getMinecraft();
	private String name, displayName, displayNameLower;
	private int key;
	private final Category category;
	private boolean toggled;
	private long toggleTimeStamp;
	private String cachedSuffix;
	public boolean markedForRemoval = false;
	public boolean markedForAdd = false;
	public Module(String name, int key, Category category) {
		this.name = name;
		this.key = key;
		this.category = category;
		this.toggled = false;
		setDisplayName(name);
	}
	
	public String getDisplayName() {
        return this.displayName == null ? this.name : this.displayName;
    }

	public void setDisplayName(String displayName){
	    this.displayName = displayName;
	    this.displayNameLower = displayName.toLowerCase();
	}
	
	public String getDisplayNameLower() {
	    return this.displayNameLower;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
	    List<Module> oldKeyList = Client.instance.moduleManager.keyMap.get(this.key);
	    if (oldKeyList != null) {
	        oldKeyList.remove(this);
	        if (oldKeyList.isEmpty()) {
	            Client.instance.moduleManager.keyMap.remove(this.key);
	        }
	    }
	    this.key = key;
	    List<Module> list = Client.instance.moduleManager.keyMap.get(key);
	    if (list == null) {
	        list = new ArrayList<>(2);
	        Client.instance.moduleManager.keyMap.put(key, list);
	    }
	    list.add(this);
	    JsonHandler.saveKeybinds();
	}

	public boolean isToggled() {
		return toggled;
	}

	public void setToggled(boolean toggled) {
	    if (this.toggled == toggled) return;

	    this.toggled = toggled;
	    Client.instance.moduleManager.hud.needsSort = true;
	    if (toggled) {
	    	Client.instance.moduleManager.hud.mods2.add(this);
	    	this.onEnable();
	    } else {
	    	Client.instance.moduleManager.hud.mods2.remove(this);
	    	this.onDisable();
	    }
	}
	
	public Category getCategory() {
		return category;
	}
	
	public long getToggleTimestamp() {
		return toggleTimeStamp;
	}
	
	public void toggle() {
	    toggleTimeStamp = System.currentTimeMillis();

	    if (mc.theWorld != null && !this.name.equals("ClickGUI") && Client.instance.moduleManager.clickGUI.sound.getBoolean()) {
	        mc.getSoundHandler().playSound(PositionedSoundRecord.create(iSound, 1.0f));
	    }
	    setToggled(!toggled);
	}
	
	
	public void onDisable() {}
	public void onEnable() {}
	public void onPreUpdate() {}
	public void onPreMotion() {}
	public void onSprint() {}
	public void onPostMotion() {}
	public void onPreInput() {}
	public void onPostInput() {}
	public void onRender2D() {}
	public void onRender3D() {}
	
	public void addSetting(Setting... setting) {
		Client.instance.settingsManager.addSettings(setting);
    }
	
	public void sendPacket(Packet p) {
		mc.thePlayer.sendQueue.addToSendQueue(p);
	}
	
	public String getUppercaseSuffix(String name) {
	    return  "§7 " + Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	public void onEvent(Event e) {
		
	}
	
}