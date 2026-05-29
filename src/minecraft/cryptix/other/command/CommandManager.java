package cryptix.other.command;

import cryptix.Client;
import cryptix.gambling.slot.SlotMachineGui;
import cryptix.other.command.commands.*;
import cryptix.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;

public class CommandManager {
	public Friend friend;
	public Config config;
	public Script script;
	public Bind bind;
	public CommandManager() {
		friend = new Friend();
		config = new Config();
		script = new Script();
		bind = new Bind();
	}
	
	public void onChatMessage(String m) {
		m = m.toLowerCase();
		if(m.startsWith(".config")) {
			String[] args = m.substring(".config".length()).trim().split("\\s+");
			config.onCommand(args);
		}
		if(m.startsWith(".script")) {
			String[] args = m.substring(".script".length()).trim().split("\\s+");
			script.onCommand(args);
		}
		if(m.startsWith(".bind")) {
			String[] args = m.substring(".bind".length()).trim().split("\\s+");
			bind.onCommand(args);
		}
		if(m.startsWith(".friend")) {
			String[] args = m.substring(".friend".length()).trim().split("\\s+");
			friend.onCommand(args);
		}
		if(m.startsWith(".ign")) {
			String ign = Client.mc.getSession().getUsername();

		    ChatComponentText chat = new ChatComponentText("IGN: §r" + ign + " §7[§eClick to copy§7]");
		    ChatStyle style = new ChatStyle();
		    style.setChatClickEvent(new ClickEvent(Action.RUN_COMMAND, ".copyign " + ign));
		    chat.setChatStyle(style);

		    Client.mc.thePlayer.addChatMessage(chat);
		}
		if (m.startsWith(".copyign")) {
		    String ignToCopy = m.substring(".copyign".length()).trim();
		    GuiScreen.setClipboardString(ignToCopy);
		    Utils.sendClientChatMessage("Copied IGN: §r" + ignToCopy);
		}
	}
}
