package cryptix.other.command.commands;

import org.lwjgl.input.Keyboard;

import cryptix.Client;
import cryptix.module.Module;
import cryptix.utils.Utils;

public class Bind {
	
	public void onCommand(String[] arg) {
		if(arg.length == 2) {
			Module mod = Client.instance.moduleManager.getModuleByName(arg[0]);
			cryptix.script.Script script = Client.instance.scriptManager.getScript(arg[0]);
			if(mod != null) {
				mod.setKey(Keyboard.getKeyIndex(arg[1].toUpperCase()));
				Utils.sendClientChatMessage("Bound " + mod.getName() + " to " + Keyboard.getKeyName(mod.getKey()));
			}else if(script != null) {
				script.setKey(Keyboard.getKeyIndex(arg[1].toUpperCase()));
				Utils.sendClientChatMessage("Bound " + script.getName() + " to " + Keyboard.getKeyName(script.getKey()));
			}else {
				Utils.sendClientChatMessage("Invalid module or script");
			}
		}
	}

}
