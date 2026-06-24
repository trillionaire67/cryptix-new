package cryptix.module.player;

import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.BooleanSetting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.other.event.Event;
import cryptix.other.event.events.PacketReceiveEvent;
import cryptix.utils.RotationUtils;
import cryptix.utils.Utils;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.util.BlockPos;

import java.util.concurrent.ThreadLocalRandom;

public class AutoFish extends Module{
	private DoubleSetting minStartDelay = new DoubleSetting("Min Start Delay", this, 10, 0, 30, true);
	private DoubleSetting maxStartDelay = new DoubleSetting("Max Start Delay", this, 10, 0, 30, true);
	private DoubleSetting minFishDelay = new DoubleSetting("Min Fish Delay", this, 2, 0, 5, 1);
	private DoubleSetting maxFishDelay = new DoubleSetting("Max Fish Delay", this, 2, 0, 5, 1);
	private BooleanSetting rotate = new BooleanSetting("Rotate", this, false);
	private boolean fish, active, rotating;
	private int fishingTicks, catchTicks, nextCastDelay, nextFishDelay, rotationDelay;
	public AutoFish() {
		super("AutoFish", 0, Category.PLAYER);
		this.addSetting(minStartDelay, maxStartDelay, minFishDelay, maxFishDelay, rotate);
	}
	
	@Override
	public void onDisable() {
		reset();
	}
	
	@Override
	public void onPreUpdate() {
		if(!(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemFishingRod)) {
			reset();
			return;
		}
		if(getWater(5) == null && active) {
			mc.rightClickMouse();
			reset();
			return;
		}
		fishingTicks++;
		if(fish) {
			catchTicks++;
			if(catchTicks >= nextFishDelay) {
				mc.rightClickMouse();
				nextCastDelay = fixValues(minStartDelay, maxStartDelay);
				reset();
			}
		}else if(!active && mc.thePlayer.fishEntity == null && fishingTicks >= nextCastDelay) {
			startFishing();
		}
	}
	
	@Override
	public void onEvent(Event event) {
		if(event instanceof PacketReceiveEvent) {
			PacketReceiveEvent e = (PacketReceiveEvent) event;
			Packet packet = e.getPacket();
			if (packet instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity p = (S12PacketEntityVelocity) packet;
				if (mc.thePlayer.fishEntity != null && p.getEntityID() == mc.thePlayer.fishEntity.getEntityId() && active && fishingTicks > 50 + nextCastDelay) {
			    	fish = true;
			    	int minFish = (int) minFishDelay.getValue();
			    	int maxFish = (int) maxFishDelay.getValue();
			        if (maxFish < minFish) {
			            int temp = minFish;
			            minFish = maxFish;
			            maxFish = temp;
			        }
			        nextFishDelay = fixValues(minFishDelay, maxFishDelay);
			        catchTicks = 0;
			    }
			}
		}
	}
	
	private void startFishing() {
	    BlockPos water = getWater(5);
	    if (water == null) return;

	    if (rotate.getBoolean()) {
	        float[] rot = RotationUtils.getRotationsBlock(water);
	        mc.thePlayer.rotationYaw = rot[0];
	        mc.thePlayer.rotationPitch = rot[1];
	    } else {
	        if (mc.objectMouseOver == null || mc.objectMouseOver.getBlockPos() == null) return;
	        BlockPos target = mc.objectMouseOver.getBlockPos();
	        if (!mc.theWorld.getBlockState(target).getBlock().getMaterial().isLiquid()) return;
	    }

	    if (++rotationDelay > 10) {
	        mc.rightClickMouse();
	        active = true;
	        Utils.sendClientChatMessage("AutoFish started fishing");
	    }
	}
	
	private void reset() {
		fish = false;
		active = false;
		fishingTicks = 0;
		rotationDelay = 0;
	}
	
	private int fixValues(DoubleSetting minSetting, DoubleSetting maxSetting) {
	    int min = (int) minSetting.getValue();
	    int max = (int) maxSetting.getValue();
	    if (max < min) {
	        int temp = min;
	        min = max;
	        max = temp;
	    }
	    return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
	
	private BlockPos getWater(int radius) {
	    BlockPos nearestWater = null;
	    double nearestDistance = Double.MAX_VALUE;
	    BlockPos playerPos = mc.thePlayer.getPosition();

	    for (int x = -radius; x <= radius; x++) {
	        for (int y = -radius; y <= radius; y++) {
	            for (int z = -radius; z <= radius; z++) {
	                BlockPos pos = playerPos.add(x, y, z);
	                if (mc.theWorld.getBlockState(pos).getBlock().getMaterial().isLiquid()) {
	                    double distance = mc.thePlayer.getDistanceSq(pos);
	                    if (distance < nearestDistance) {
	                        nearestDistance = distance;
	                        nearestWater = pos;
	                    }
	                }
	            }
	        }
	    }

	    return nearestWater;
	}

}
