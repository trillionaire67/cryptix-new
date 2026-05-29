package cryptix.script;

import java.io.File;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import cryptix.Client;
import cryptix.other.JsonHandler;
import cryptix.script.api.*;

public class Script {
	private String name, displayName;
	private int key;
    private final File file;
    private final Globals globals;
    private boolean enabled;
    private long toggleTimeStamp;

    public Script(File file) {
    	key = 0;
        this.file = file;
        this.globals = JsePlatform.standardGlobals();
        this.globals.set("player", new Player(Client.mc.thePlayer));
        this.globals.set("keybinds", new Keybinds());
        this.globals.set("packet", new Packets());
        this.globals.set("render", new Render());
        this.globals.set("client", new cryptix.script.api.Client());
        this.enabled = false;
        this.name = file.getName();
        this.displayName = name.replace(".lua", "");
    }

    public void load() {
        try {
            LuaValue chunk = globals.loadfile(file.getAbsolutePath());
            chunk.call();
        } catch (Exception e) {
            System.out.println("[Lua] Failed to load " + file.getName());
            e.printStackTrace();
        }
    }

    public void enable() {
    	enabled = true;
        call("onEnable");
    }

    public void disable() {
    	enabled = false;
        call("onDisable");
    }
    
    public void toggle() {
    	toggleTimeStamp = System.currentTimeMillis();
    	if(enabled) {
    		disable();
    	}else {
    		enable();
    	}
    }
    
    public void onChatReceive(String message) {
        call("onChatReceive", LuaValue.valueOf(message));
    }
    
    public void onRender3D() {
        call("onRender3D");
    }
    
    public void onRender2D() {
        call("onRender2D");
    }
    
    public void onPreUpdate() {
        call("onPreUpdate");
    }
	
	public void onPreMotion() {
        call("onPreMotion");
    }
	
	public void onPostMotion() {
        call("onPostMotion");
    }
	
	public void onEnable() {
        call("onEnable");
    }
	
	public void onDisable() {
        call("onDisable");
    }

    public void call(String event) {
        LuaValue func = globals.get(event);
        if (!func.isnil()) {
            try {
                func.call();
            } catch (Exception e) {
                System.out.println("[Lua] Error in " + file.getName() + ":" + event);
                e.printStackTrace();
            }
        }
    }
    
    public void call(String event, LuaValue arg) {
        LuaValue func = globals.get(event);
        if (!func.isnil()) {
            try {
                func.call(arg);
            } catch (Exception e) {
                System.out.println("[Lua] Error in " + file.getName() + ":" + event);
                e.printStackTrace();
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return name;
    }
    
    public int getKey() {
		return key;
	}
    
    public void setKey(int key) {
		this.key = key;
		JsonHandler.saveKeybinds();
	}
    
    public long getToggleTimestamp() {
		return toggleTimeStamp;
	}
}