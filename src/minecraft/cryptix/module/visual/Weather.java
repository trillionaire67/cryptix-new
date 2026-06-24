package cryptix.module.visual;

import java.util.Arrays;

import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.gui.clickgui.settings.ModeSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.other.event.Event;
import cryptix.other.event.events.PacketReceiveEvent;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.world.storage.WorldInfo;

public class Weather extends Module{
	private ModeSetting weather = new ModeSetting("Weather", this, "Clear", Arrays.asList("Clear", "Rain"));
	private DoubleSetting time = new DoubleSetting("Time", this, 1000, 1, 24000, 1);
	public Weather() {
		super("Weather", 0, Category.VISUAL);
		this.addSetting(weather, time);
	}
	
	@Override
	public void onDisable() {
		WorldInfo info = mc.theWorld.getWorldInfo();
		mc.theWorld.setRainStrength(0F);
        mc.theWorld.setThunderStrength(0F);
        info.setRaining(false);
        info.setThundering(false);
	}
	
	@Override
	public void onRender3D() {
		mc.theWorld.setWorldTime((int)time.getValue());
	}
	
	@Override
    public void onPreUpdate() {
        WorldInfo info = mc.theWorld.getWorldInfo();
        if (weather.getString().equalsIgnoreCase("Clear")) {
            mc.theWorld.setRainStrength(0F);
            mc.theWorld.setThunderStrength(0F);
            info.setRaining(false);
            info.setThundering(false);
        } 
        else if (weather.getString().equalsIgnoreCase("Rain")) {
            mc.theWorld.setRainStrength(1F);
            info.setRainTime(Integer.MAX_VALUE);
            info.setRaining(true);
            info.setThundering(false);
        }
    }
	
	@Override
	public void onEvent(Event event) {
		if(event instanceof PacketReceiveEvent) {
			PacketReceiveEvent e = (PacketReceiveEvent) event;
			if (e.getPacket() instanceof S03PacketTimeUpdate) {
				e.setCancelled(true);
			}
		}
	}

}
