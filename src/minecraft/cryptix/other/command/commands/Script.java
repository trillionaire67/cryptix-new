package cryptix.other.command.commands;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import cryptix.Client;
import cryptix.other.JsonHandler;
import cryptix.utils.Utils;

public class Script {
	private File ROOT_DIR = new File("cryptix");
    private File script = new File(ROOT_DIR, "script");
	public void onCommand(String[] arg) {
		if(arg[0].isEmpty()) {
			Utils.sendClientChatMessage("Commands:");
			Utils.sendClientChatMessage(".script list");
			Utils.sendClientChatMessage(".script folder");
			Utils.sendClientChatMessage(".script load");
			Utils.sendClientChatMessage(".script enable <scriptname>");
			Utils.sendClientChatMessage(".script disable <scriptname>");
		}else {
			if (arg[0].toLowerCase().equalsIgnoreCase("folder")) {
                try {
                    Desktop.getDesktop().open(script);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
			} else if(arg[0].toLowerCase().equalsIgnoreCase("load")) {
				Client.instance.scriptManager.loadScripts();
				Utils.sendClientChatMessage("Loaded scripts");
            } else if(arg[0].toLowerCase().equalsIgnoreCase("enable")) {
            	if (arg.length < 2) {
                    Utils.sendClientChatMessage("Usage: .script enable <scriptname>");
                    return;
                }
            	Client.instance.scriptManager.enableScript(arg[1]);
            	Utils.sendClientChatMessage("Enabled script: " + arg[1]);
			} else if(arg[0].toLowerCase().equalsIgnoreCase("disable")) {
				if (arg.length < 2) {
	                Utils.sendClientChatMessage("Usage: .script disable <scriptname>");
	                return;
	            }
				Client.instance.scriptManager.disableScript(arg[1]);
                Utils.sendClientChatMessage("Disabled script: " + arg[1]);
			} else if (arg[0].toLowerCase().equalsIgnoreCase("list")) {
                File[] files = script.listFiles((dir, name) -> name.endsWith(".lua"));
                if (files != null && files.length > 0) {
                    Utils.sendClientChatMessage("Available script files:");
                    Arrays.stream(files)
                          .forEach(file -> Utils.sendClientChatMessage(file.getName()));
                } else {
                    Utils.sendClientChatMessage("No script files found.");
                }
            } else {
                Utils.sendClientChatMessage("Command not found");
            }
		}
	}
}
