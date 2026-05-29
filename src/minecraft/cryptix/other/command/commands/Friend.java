package cryptix.other.command.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cryptix.other.JsonHandler;
import cryptix.utils.Utils;

public class Friend {
	private File ROOT_DIR = new File("cryptix");
	public void onCommand(String[] arg) {
		if(arg[0].isEmpty()) {
			Utils.sendClientChatMessage("Commands:");
			Utils.sendClientChatMessage(".friend list");
			Utils.sendClientChatMessage(".friend add <name>");
			Utils.sendClientChatMessage(".friend remove <name>");
		}else {
			if (arg[0].toLowerCase().equalsIgnoreCase("list")) {
				Utils.sendClientChatMessage("Friends:");
				for(String name : JsonHandler.friendList) {
					Utils.sendClientChatMessage(name);
				}
			} else if(arg[0].toLowerCase().equalsIgnoreCase("add")) {
            	if (arg.length < 2) {
                    Utils.sendClientChatMessage("Usage: .friend add <name>");
                    return;
                }
            	JsonHandler.addFriend(arg[1]);
			}  else if(arg[0].toLowerCase().equalsIgnoreCase("remove")) {
            	if (arg.length < 2) {
                    Utils.sendClientChatMessage("Usage: .friend remove <name>");
                    return;
                }
            	JsonHandler.removeFriend(arg[1]);
			} 
		}
	}
	
	public boolean isFriend(String name) {
		for(String n : JsonHandler.friendList) {
			if(n.equalsIgnoreCase(name)) return true;
		}
		return false;
	}
}
