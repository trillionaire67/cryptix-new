package cryptix.other.command.commands;

import org.lwjgl.input.Keyboard;

import cryptix.Client;
import cryptix.module.Module;
import cryptix.utils.Utils;

public class Toggle {
	public void onCommand(String[] arg) {
		if(arg.length == 1) {
			Module mod = Client.instance.moduleManager.getModuleByName(arg[0]);
			cryptix.script.Script script = Client.instance.scriptManager.getScript(arg[0]);
			if(mod != null) {
				mod.toggle();
				Utils.sendClientChatMessage("Toggled " + mod.getName());
			}else if(script != null) {
				script.toggle();
				Utils.sendClientChatMessage("Toggled " + script.getName());
			}else {
				Utils.sendClientChatMessage("Invalid module or script");
			}
		}else {
			Utils.sendClientChatMessage("Usage: .toggle <module>");
		}
	}
}
