package cryptix.script;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cryptix.Client;
import cryptix.gui.clickgui.Panel;
import cryptix.gui.clickgui.element.ScriptButton;
import net.minecraft.client.Minecraft;

public class ScriptManager {
	private File ROOT_DIR = new File("cryptix");
    private File scriptFolder = new File(ROOT_DIR, "script");
    private final List<Script> scripts = new ArrayList<>();

    public void loadScripts() {
        File dir = scriptFolder;
        if (!dir.exists()) dir.mkdirs();
        scripts.clear();
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".lua")) {
                Script script = new Script(file);
                script.load();
                scripts.add(script);
            }
        }
        if(Client.instance.clickGui != null) {
	        for(Panel panel : Client.instance.clickGui.panels) {
	        	if(panel.title.equalsIgnoreCase("scripts")) {
	        		panel.scriptElements.clear();
	        		for (Script script : scripts) {
	                    panel.scriptElements.add(new ScriptButton(script, panel));
	                }
	        	}
	        }
        }
    }

    public void enableScript(String name) {
        for (Script script : scripts) {
            if (script.getName().equalsIgnoreCase(name)) {
                script.enable();
                return;
            }
        }
        System.out.println("Script not found");
    }

    public void disableScript(String name) {
        for (Script script : scripts) {
            if (script.getName().equalsIgnoreCase(name)) {
                script.disable();
                return;
            }
        }
        System.out.println("Script not found");
    }
    
    public void onKey(int key) {
    	for (Script script : scripts) {
            if (script.getKey() == key) {
                script.toggle();
            }
        }
    }
    
    public Script getScript(String name) {
    	for(Script script : scripts) {
    		if(script.getName().replace(".lua", "").equalsIgnoreCase(name)) {
    			return script;
    		}
    	}
		return null;
    }

    public List<Script> getScripts() {
        return scripts;
    }
}