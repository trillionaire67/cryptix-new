package cryptix.module.combat;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class AutoRod extends Module{
	private Setting movefix = new Setting("Movefix", this, false);
	private Setting delay = new Setting("Delay", this, 5, 2, 10, true);
	private EntityPlayer target;
	private int lastSlot = -1, delayTick;
	public boolean blocking;
	public AutoRod() {
		super("AutoRod", 0, Category.COMBAT);
		this.addSetting(movefix, delay);
	}
	
	@Override
	public void onPreUpdate() {
		if (movefix.getBoolean()) {
            Client.movefix = true;
        }
		target = getTarget();
		delayTick++;
		blocking = false;
		if(target != null) {
			int rodSlot = -1;
			for (int i = 0; i < 9; i++) {
			    ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
			    if (stack != null && stack.getItem() instanceof ItemFishingRod) {
			        rodSlot = i;
			        break;
			    }
			}
			if (rodSlot != -1 && delayTick >= delay.getValue()) {
				delayTick = 0;
				lastSlot = mc.thePlayer.inventory.currentItem;
			    mc.thePlayer.inventory.currentItem = rodSlot;
			}else {
				reset();
			}
		}else {
			reset();
		}
	}
	
	@Override
	public void onPreMotion() {
		if(target != null && lastSlot != -1) {
			float[] rotations = getRotations(target);
			mc.thePlayer.rotationYawHead = rotations[0];
			mc.thePlayer.rotationPitchHead = rotations[1];
		}
		if(blocking) {
			lastSlot = -1;
		}
	}
	
	private EntityPlayer getTarget() {
		EntityPlayer target = null;
		double range = 6.0;
		double closest = range;

		for (EntityPlayer player : mc.theWorld.playerEntities) {
		    if (player == mc.thePlayer) continue;
		    if (player.isDead || player.getHealth() <= 0) continue;

		    double dist = mc.thePlayer.getDistanceToEntity(player);
		    if (dist < closest) {
		        closest = dist;
		        target = player;
		    }
		}
		return target;
	}
	
	private float[] getRotations(Entity entity) {
	    double diffX = entity.posX - mc.thePlayer.posX;
	    double diffY = (entity.posY + entity.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
	    double diffZ = entity.posZ - mc.thePlayer.posZ;

	    double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

	    float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F);
	    float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, dist)));

	    return new float[]{yaw, pitch};
	}
	
	private void reset() {
		if(lastSlot != -1) {
			sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
			blocking = true;
			mc.thePlayer.inventory.currentItem = lastSlot;
		}
	}

}
