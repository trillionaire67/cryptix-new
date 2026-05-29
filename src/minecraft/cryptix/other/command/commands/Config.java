package cryptix.other.command.commands;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import cryptix.other.JsonHandler;
import cryptix.utils.Utils;

public class Config {
	private File ROOT_DIR = new File("cryptix");
    private File config = new File(ROOT_DIR, "config");
	public void onCommand(String[] arg) {
		if(arg[0].isEmpty()) {
			Utils.sendClientChatMessage("Commands:");
			Utils.sendClientChatMessage(".config list");
			Utils.sendClientChatMessage(".config folder");
			Utils.sendClientChatMessage(".config load <configname>");
			Utils.sendClientChatMessage(".config save <configname>");
		}else {
			if (arg[0].toLowerCase().equalsIgnoreCase("folder")) {
                try {
                    Desktop.getDesktop().open(config);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(arg[0].toLowerCase().equalsIgnoreCase("load")) {
            	if (arg.length < 2) {
                    Utils.sendClientChatMessage("Usage: .config load <configname>");
                    return;
                }
            	File configs = new File(config, String.valueOf(arg[1]) + ".json");
                if (configs.exists()) {
                    JsonHandler.loadMods(arg[1]);
                    Utils.sendClientChatMessage("Loaded config: " + arg[1]);
                } else {
                    Utils.sendClientChatMessage("Couldnt find config file: " + arg[1]);
                }
			} else if(arg[0].toLowerCase().equalsIgnoreCase("save")) {
				if (arg.length < 2) {
	                Utils.sendClientChatMessage("Usage: .config save <configname>");
	                return;
	            }
				JsonHandler.saveMods(arg[1]);
                Utils.sendClientChatMessage("Saved config as: " + arg[1]);
			} else if (arg[0].toLowerCase().equalsIgnoreCase("list")) {
                File[] files = config.listFiles((dir, name) -> name.endsWith(".json"));
                if (files != null && files.length > 0) {
                    Utils.sendClientChatMessage("Available config files:");
                    Arrays.stream(files)
                          .forEach(file -> Utils.sendClientChatMessage(file.getName()));
                } else {
                    Utils.sendClientChatMessage("No config files found.");
                }
            } else {
                Utils.sendClientChatMessage("Command not found");
            }
		}
	}
}
