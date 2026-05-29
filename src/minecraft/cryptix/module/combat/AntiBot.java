package cryptix.module.combat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mojang.authlib.GameProfile;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class AntiBot extends Module {
    private Setting tab;
    private Setting delay;
    private Map<String, Integer> playerTimers = new HashMap<>();

    public AntiBot() {
        super("AntiBot", 0, Category.COMBAT);
        this.addSetting(tab = new Setting("Tablist", this, false));
        this.addSetting(delay = new Setting("Delay", this, 20, 1, 100, true));
    }

    @Override
    public void onPreUpdate() {
        if (!tab.getBoolean()) return;
        if(mc.thePlayer.getGameProfile() == null) return;
        List<String> infos = Client.mc.getNetHandler().getPlayerInfoMap().stream().map(NetworkPlayerInfo::getGameProfile).map(GameProfile::getName).collect(Collectors.toList());
        for (String name : infos) {
            playerTimers.put(name, playerTimers.getOrDefault(name, 0) + 1);
        }
        playerTimers.keySet().removeIf(name -> infos.stream()
                .noneMatch(info -> info.equalsIgnoreCase(name)));
    }

    public static boolean isBot(Entity e) {
        if (!Client.instance.moduleManager.antibot.isToggled()) return false;
        if (!(e instanceof EntityPlayer)) return true;
        if (e == Client.mc.thePlayer) return true;

        if (Client.instance.moduleManager.antibot.tab.getBoolean()) {
            String name = e.getName();
            int ticks = Client.instance.moduleManager.antibot.playerTimers.getOrDefault(name, 0);
            return ticks < Client.instance.moduleManager.antibot.delay.getValue();
        }

        return false;
    }
}