package cryptix.module.player;

import java.util.Arrays;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.ModeSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.MovementUtils;
import cryptix.utils.Utils;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.BlockPos;

public class Jesus extends Module{
	private int waterTick;
	private ModeSetting mode = new ModeSetting("Mode", this, "BlocksMC", Arrays.asList("BlocksMC", "Vulcan"));
	public Jesus() {
		super("Jesus", 0, Category.PLAYER);
		this.addSetting(mode);
	}
	
	@Override
	public void onPreMotion() {
		if(!mc.gameSettings.keyBindJump.isKeyDown()) {
			if(mode.getString().equalsIgnoreCase("Vulcan")) {
				if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock() instanceof BlockLiquid){
					mc.thePlayer.motionY = 0.42F;
					waterTick++;
				}
				if(waterTick >= 10) {
					mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.5, mc.thePlayer.posZ);
					waterTick = 0;
				}
				if(mc.thePlayer.onGround) {
					waterTick = 0;
				}
			}
			if(mode.getString().equalsIgnoreCase("BlocksMC")) {
				if(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)).getBlock() instanceof BlockLiquid) {
					mc.thePlayer.motionY = -0.015F;
					MovementUtils.strafe(0.135);
					waterTick++;
				}
				if(waterTick >= 2) {
					mc.thePlayer.motionY = 0.046318803010859555;
					MovementUtils.strafe(0.155);
					waterTick = 0;
				}
				if(mc.thePlayer.isInWater()) {
					mc.thePlayer.motionY = 0.15F;
				}
			}
		}
	}

}
