package cryptix.module.movement;

import java.util.Arrays;
import java.util.Random;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.ModeSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.MovementUtils;
import cryptix.utils.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class GroundSpeed extends Module{
	private ModeSetting mode = new ModeSetting("Mode", this, "Hypixel", Arrays.asList("Hypixel", "BlocksMC", "Strafe", "Vulcan"));
	private int offStairTick;
	private boolean isCollided, grounded;
	public GroundSpeed() {
		super("GroundSpeed", 0, Category.MOVEMENT);
		this.addSetting(mode);
	}
	
	@Override
	public void onEnable() {
		if(mc.thePlayer.onGround) {
			grounded = true;
		}else {
			grounded = false;
		}
	}
	
	@Override
	public void onPreMotion() {
		this.setDisplayName(this.getName() + this.getUppercaseSuffix(mode.getString()));
		if(mode.getString().equalsIgnoreCase("Strafe")) {
			if(MovementUtils.getSpeed() < 0.15315988037089012 && mc.thePlayer.onGround && !mc.thePlayer.isUsingItem() && !mc.thePlayer.isSneaking()) {
				MovementUtils.strafe(0.15315988037089012);
			}
		}
		if(mode.getString().equalsIgnoreCase("BlocksMC")) {
			if(mc.thePlayer.onGroundTicks > 0) {
				mc.thePlayer.motionY = 0.0522;
				MovementUtils.strafe(0.28);
			}else {
				MovementUtils.strafe(0.45);
			}
		}
		if(mc.thePlayer.onGround) {
			if(mode.getString().equalsIgnoreCase("Hypixel")) {
				MovementUtils.strafe(0.2);
				if(mc.thePlayer.onGround) {
					mc.thePlayer.motionY = 0.27;
				}
			}
			if(mode.getString().equalsIgnoreCase("Vulcan")) {
				isCollided = false;
				BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
				BlockPos[] checkPositions = new BlockPos[] {
					    playerPos.add(1, 0, 0),
					    playerPos.add(-1, 0, 0),
					    playerPos.add(0, 0, 1),
					    playerPos.add(0, 0, -1),
					    playerPos.add(1, 0, 1),
					    playerPos.add(1, 0, -1),
					    playerPos.add(-1, 0, 1),
					    playerPos.add(-1, 0, -1),
					    playerPos.add(1, 1, 0),
					    playerPos.add(-1, 1, 0),
					    playerPos.add(0, 1, 1),
					    playerPos.add(0, 1, -1),
					};

				for (BlockPos pos : checkPositions) {
					IBlockState state = mc.theWorld.getBlockState(pos);
					if (state.getBlock() != Blocks.air) {
						isCollided = true;
						break;
					}
				}
				double yMod = Math.round((mc.thePlayer.posY % 1) * 100);
				if (offStairTick > 3 && !isCollided) {
					if (mc.thePlayer.onGroundTicks > 0 && Math.round((mc.thePlayer.posY % 1) * 100) == 0) {
				    	mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42F, mc.thePlayer.posZ);
				    }
				    if (yMod == 0 || yMod == 42) {
				        MovementUtils.strafe(0.42);
				    } else {
				        offStairTick = 0;
				    }
				} else {
				    MovementUtils.strafe(0.10);
				}
				if (yMod == 50 || isCollided) {
				    offStairTick = 0;
				}
				offStairTick++;
			}
			
		}
	}

}
