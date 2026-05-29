package cryptix.module.player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.BlinkUtils;
import cryptix.utils.RotationUtils;
import cryptix.utils.Utils;
import cryptix.utils.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

public class BedNuker extends Module{
	public BlockPos bedPos, lastPos, surroundingPos, surroundingLastPos, spawnPos;
	private IBlockState bedBlock;
	private Setting range, delay, rotate, render, surrounding, whitelist, speed, dig, movefix, ka, bmc;
	public double breakProgress, smoothProgress;
	private int delayTick, alpha, backAlpha, progTick, lastSlot, tick;
	private boolean start, surroundingBroken, rotation;
	public boolean rotating, check, swapped, teleport;
	private long offsetTime;
	private float[] rotations;
	public BedNuker() {
		super("BedNuker", 0, Category.PLAYER);
		Client.instance.settingsManager.addSetting(range = new Setting("Range", this, 3, 3, 8, false));
		Client.instance.settingsManager.addSetting(delay = new Setting("Break Delay", this, 100, 0, 500, true));
		Client.instance.settingsManager.addSetting(speed = new Setting("Break Speed", this, 1, 1, 2, 1));
		Client.instance.settingsManager.addSetting(surrounding = new Setting("Surroundings", this, false));
		Client.instance.settingsManager.addSetting(rotate = new Setting("Only S/S Rotate", this, false));
		Client.instance.settingsManager.addSetting(render = new Setting("Render Progress", this, "Bar", Arrays.asList("None", "Bar", "Block", "Adjust")));
		Client.instance.settingsManager.addSetting(whitelist = new Setting("Whitelist", this, true));
		Client.instance.settingsManager.addSetting(dig = new Setting("Ignore Slowdown", this, true));
		Client.instance.settingsManager.addSetting(movefix = new Setting("Movefix", this, true));
		Client.instance.settingsManager.addSetting(ka = new Setting("Allow KillAura", this, true));
		Client.instance.settingsManager.addSetting(bmc = new Setting("BlocksMC", this, true));
	}
	
	@Override
	public void onDisable() {
		reset();
	}
	
	private void reset() {
		BlockPos pos = surroundingPos != null ? surroundingPos : bedPos;
		if(breakProgress > 0 && pos != null) {
			EnumFacing facing = getFacingFromYaw(mc.thePlayer.rotationYawHead, mc.thePlayer.rotationPitchHead).getOpposite();
			mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, pos, facing));
		}
		breakProgress = 0;
		smoothProgress = 0;
		if(lastSlot != -1) {
			mc.thePlayer.inventory.currentItem = lastSlot;
			lastSlot = -1;
		}
		bedPos = null;
		surroundingPos = null;
		rotating = false;
		surroundingBroken = false;
		rotations = null;
		rotation = false;
	}
	
	@Override
	public void onEnable() {
		reset();
	}
	@Override
	public void onPreMotion() {
		if(rotations != null && rotating) {
			mc.thePlayer.rotationYawHead = rotations[0];
			mc.thePlayer.rotationPitchHead = rotations[1];
			rotations = null;
			rotating = false;
		}
		if((bedPos != null || surroundingPos != null)&& smoothProgress > 0.1) {
			alpha = (int) Utils.lerp(alpha, 255, 0.5f);
			backAlpha = (int) Utils.lerp(backAlpha, 100, 0.5f);
		}else {
			alpha = (int) Utils.lerp(alpha, 0, 0.5f);
			backAlpha = (int) Utils.lerp(backAlpha, 0, 0.5f);
		}
	}
	@Override
	public void onPreUpdate() {
		rotating = false;
		delayTick++;
		tick++;
		if(mc.thePlayer.isUsingItem() || Client.instance.moduleManager.scaffold.isToggled()|| (Client.instance.moduleManager.killAura.target != null && Client.instance.moduleManager.killAura.oldTarget != null && !ka.getBoolean()) || BlinkUtils.isBlinking()&& !ka.getBoolean() || mc.currentScreen != null) {
			reset();
			return;
		}
		if(tick < 10) {
			if(tick == 1 && lastPos != null && !rotate.getBoolean()) {
				rotate(lastPos);
			}
			reset();
			return;
		}
		bedPos = findBed();
		if(bedPos != null) { 
			bedBlock = mc.theWorld.getBlockState(bedPos);
		}else {
			bedPos = findBed();
		}
		if(bedPos != null && mc.thePlayer.getDistance(bedPos.getX() + 0.5, bedPos.getY() + 0.5, bedPos.getZ() + 0.5) > range.getValue()) {
			reset();
			return;
		}
		if(surrounding.getBoolean() && bedPos != null && getSurrounding(bedPos) != null && !isOpen(bedPos) && delayTick > delay.getValue() / 30 && !surroundingBroken) {
			surroundingPos = getSurrounding(bedPos);
			if (!rotation && rotations == null) {
		        rotate(surroundingPos);
		        rotation = true;
		        return;
		    }
			breakBlock(surroundingPos);
			return;
		}else {
			surroundingPos = null;
		}
		if(bedPos != null && lastPos != null && mc.theWorld.getBlockState(lastPos) != bedBlock) {
			breakProgress = 0;
			surroundingBroken = false;
		}
		if(surroundingPos != null && surroundingLastPos != null && mc.theWorld.getBlockState(surroundingLastPos) != mc.theWorld.getBlockState(surroundingPos)) {
			breakProgress = 0;
			surroundingBroken = false;
		}
		lastPos = bedPos;
		rotating = false;
		if(bedPos != null && delayTick > delay.getValue() / 30) {
			if (!rotation && rotations == null) {
		        rotate(bedPos);
		        rotation = true;
		        return;
		    }
			breakBlock(bedPos);
		}else {
			breakProgress = 0;
		}
	}
	
	@Override
	public void onRender2D() {
		long currentTime = System.currentTimeMillis();
	    float deltaTime = (currentTime - offsetTime) / 1000.0f;
	    offsetTime = currentTime;

	    float smoothTime = 0.075f;
	    float alphaa = 1.0f - (float)Math.exp(-deltaTime / smoothTime);
		smoothProgress = Utils.lerp((float)smoothProgress, (float)breakProgress, alphaa);
		if(smoothProgress > 1.0) {
			smoothProgress = 1.0;
		}
		if(render.getString().equalsIgnoreCase("Bar") && (bedPos != null || surroundingPos != null || alpha > 0 || backAlpha > 0)) {
			Module hud = Client.instance.moduleManager.hud;
			Setting c1r = Client.instance.moduleManager.hud.color1red, c1g = Client.instance.moduleManager.hud.color1green, c1b = Client.instance.moduleManager.hud.color1blue;
			Setting c2r = Client.instance.moduleManager.hud.color2red, c2g = Client.instance.moduleManager.hud.color2green, c2b = Client.instance.moduleManager.hud.color2blue;
			int color1 = (alpha << 24) | ((int)c1r.getValue() << 16) | ((int)c1g.getValue() << 8) | (int)c1b.getValue();
		    int color2 = (alpha << 24) | ((int)c2r.getValue() << 16) | ((int)c2g.getValue() << 8) | (int)c2b.getValue();
		    int bg     = (backAlpha << 24);
			RenderUtils.drawProgressBar(smoothProgress, color1, color2, bg);
		}
	}
	
	@Override
	public void onRender3D() {
	    final RenderManager rm = mc.getRenderManager();
	    if (bedPos != null && render.getString().equalsIgnoreCase("Block")) {
	        double bx = (surroundingPos == null ? bedPos.getX() : surroundingPos.getX()) - rm.viewerPosX;
	        double by = (surroundingPos == null ? bedPos.getY() : surroundingPos.getY()) - rm.viewerPosY;
	        double bz = (surroundingPos == null ? bedPos.getZ() : surroundingPos.getZ()) - rm.viewerPosZ;
	        AxisAlignedBB box = new AxisAlignedBB(bx, by, bz,bx + 1.0,by + smoothProgress,bz + 1.0);
	        int color = Client.instance.moduleManager.hud.getColorInt(0, 1f);
	        float r = ((color >> 16) & 255) * 0.003921569f;
	        float g = ((color >> 8) & 255) * 0.003921569f;
	        float b = (color & 255) * 0.003921569f;
	        GlStateManager.pushMatrix();
	        GL11.glColor4f(r, g, b, 0.25f);
	        RenderUtils.drawFilledBox(box);
	        GL11.glColor4f(1f, 1f, 1f, 1f);
	        GlStateManager.popMatrix();
	    }
	    if (bedPos != null && render.getString().equalsIgnoreCase("Adjust")) {
	        double bx = (surroundingPos == null ? bedPos.getX() : surroundingPos.getX()) - rm.viewerPosX;
	        double by = (surroundingPos == null ? bedPos.getY() : surroundingPos.getY()) - rm.viewerPosY;
	        double bz = (surroundingPos == null ? bedPos.getZ() : surroundingPos.getZ()) - rm.viewerPosZ;
	        double height = (surroundingPos == null ? 0.5625 : 1.0);
	        AxisAlignedBB box = new AxisAlignedBB(bx, by, bz,bx + 1.0,by + height,bz + 1.0);
	        int color = getProgressColor(breakProgress);
	        float r = ((color >> 16) & 255) * 0.003921569f;
	        float g = ((color >> 8) & 255) * 0.003921569f;
	        float b = (color & 255) * 0.003921569f;
	        GlStateManager.pushMatrix();
	        GL11.glColor4f(r, g, b, 0.25f);
	        RenderUtils.drawFilledBox(box);
	        GL11.glColor4f(1f, 1f, 1f, 1f);
	        GlStateManager.popMatrix();
	    }
	}
	
	public static int getProgressColor(double progress) {
	    progress = Math.max(0.0, Math.min(1.0, progress));
	    int r, g;
	    if (progress < 0.5) {
	        double t = progress * 2.0;
	        r = 255;
	        g = (int) (255.0 * t);
	    } else {
	        double t = (progress - 0.5) * 2.0;
	        r = (int) (255.0 * (1.0 - t));
	        g = 255;
	    }
	    return (255 << 24) | (r << 16) | (g << 8);
	}
	
	private void breakBlock(BlockPos pos) {
		if(pos == null) return;
		int tool = Utils.getTool(mc.theWorld.getBlockState(pos).getBlock());
		if(tool != -1 && mc.thePlayer.inventory.currentItem != tool) {
			if(breakProgress == 0) {
				lastSlot = mc.thePlayer.inventory.currentItem;
			}
			mc.thePlayer.inventory.currentItem = tool;
		}
		double prog = mc.theWorld.getBlockState(pos).getBlock().getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, pos, dig.getBoolean()) * speed.getValue();
		if(rotate.getBoolean() && breakProgress == 0 || !rotate.getBoolean()) {
			rotate(pos);
		}
		if(breakProgress >= 0) {
			mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), pos, (int) (breakProgress * 10 - 1));
			if(!rotate.getBoolean()) {
					mc.thePlayer.swingItem();
			}
		}
		if(breakProgress == 0) {
			if(Client.instance.moduleManager.killAura.blocking) {
				return;
			}else {
				start = false;
				mc.thePlayer.swingItem();
				EnumFacing facing = getFacingFromYaw(mc.thePlayer.rotationYawHead, mc.thePlayer.rotationPitchHead).getOpposite();
				mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, facing));
			}
		}
		if(breakProgress + prog >= 1.0) {
			rotate(pos);
		}
		if(breakProgress >= 1.0 && !Client.instance.moduleManager.killAura.blocking) {
			if(rotate.getBoolean()) {
				rotate(pos);
			}
			mc.thePlayer.swingItem();
			if(bmc.getBoolean() && pos == bedPos) {
				MovingObjectPosition raycast = RotationUtils.rayCast(range.getValue(), mc.thePlayer.rotationYawHead, mc.thePlayer.rotationPitchHead, 0, 0);
				MovingObjectPosition raycast2 = RotationUtils.rayCast(range.getValue(), mc.thePlayer.rotationYawHead + 5, mc.thePlayer.rotationPitchHead, 0, 0);
				MovingObjectPosition raycast3 = RotationUtils.rayCast(range.getValue(), mc.thePlayer.rotationYawHead - 5, mc.thePlayer.rotationPitchHead, 0, 0);
				MovingObjectPosition raycast4 = RotationUtils.rayCast(range.getValue(), mc.thePlayer.rotationYawHead, mc.thePlayer.rotationPitchHead + 5, 0, 0);
				MovingObjectPosition raycast5 = RotationUtils.rayCast(range.getValue(), mc.thePlayer.rotationYawHead, mc.thePlayer.rotationPitchHead - 5, 0, 0);
				if(raycast == null || mc.theWorld.getBlockState(raycast.getBlockPos()).getBlock() != Blocks.bed) {
					return;
				}
				if(raycast2 == null || mc.theWorld.getBlockState(raycast2.getBlockPos()).getBlock() != Blocks.bed) {
					return;
				}
				if(raycast3 == null || mc.theWorld.getBlockState(raycast3.getBlockPos()).getBlock() != Blocks.bed) {
					return;
				}
				if(raycast4 == null || mc.theWorld.getBlockState(raycast4.getBlockPos()).getBlock() != Blocks.bed) {
					return;
				}
				if(raycast5 == null || mc.theWorld.getBlockState(raycast5.getBlockPos()).getBlock() != Blocks.bed) {
					return;
				}
			}
			start = true;
			EnumFacing facing = getFacingFromYaw(mc.thePlayer.rotationYawHead, mc.thePlayer.rotationPitchHead).getOpposite();
			mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing));
			mc.playerController.onPlayerDestroyBlock(pos, EnumFacing.UP);
			mc.theWorld.setBlockState(pos, Blocks.air.getDefaultState(), 50);
			breakProgress = 0;
			delayTick = 0;
			if(pos == bedPos) {
				tick = 0;
			}
			if(pos == surroundingPos && isOpen(bedPos)) {
				surroundingBroken = true;
			}else {
				surroundingBroken = false;
			}
			if(lastSlot != -1) {
				mc.thePlayer.inventory.currentItem = lastSlot;
				lastSlot = -1;
			}
			if(Client.instance.moduleManager.bedesp.beds.contains(pos)) {
				Client.instance.moduleManager.bedesp.beds.remove(pos);
				List<BlockPos> toRemove = new ArrayList<>();
				for (EnumFacing side : EnumFacing.HORIZONTALS) {
			        BlockPos block = pos.offset(side);
			        if (mc.theWorld.getBlockState(block).getBlock() instanceof BlockBed) {
			            if (Client.instance.moduleManager.bedesp.beds.contains(block)) {
			                toRemove.add(block);
			            }
			        }
			    }
				Client.instance.moduleManager.bedesp.beds.removeAll(toRemove);
			}
		}
		
		if(breakProgress + prog >= 1.0) {
			rotating = true;
		}
		if(!start) {
			breakProgress += prog;
			breakProgress = Math.min(1, breakProgress); // prevents progress from showing above 100
		}
	}
	
	private EnumFacing getFacingFromYaw(float yaw, float pitch) {
	    yaw = (yaw % 360 + 360) % 360;

	    if (pitch <= -60) return EnumFacing.UP;
	    if (pitch >= 60) return EnumFacing.DOWN;
	    if (yaw >= 315 || yaw < 45) return EnumFacing.SOUTH;
	    if (yaw < 135) return EnumFacing.WEST;
	    if (yaw < 225) return EnumFacing.NORTH;
	    return EnumFacing.EAST;
	}
	
	private BlockPos findBed() {
		if (lastPos != null && mc.theWorld.getBlockState(lastPos).getBlock() == Blocks.bed) {
			return lastPos;
		}
		BlockPos pos = new BlockPos(0,0,0);
	    for (int x = (int) -range.getValue(); x <= range.getValue(); x++) {
	        for (int y = (int) -range.getValue(); y <= range.getValue(); y++) {
	            for (int z = (int) -range.getValue(); z <= range.getValue(); z++) {
	                pos.setPosition(
	                    mc.thePlayer.posX + x,
	                    mc.thePlayer.posY + y,
	                    mc.thePlayer.posZ + z
	                );
	                if (mc.theWorld.getBlockState(pos).getBlock() == Blocks.bed) {
	                	if(spawnPos == null) return pos;
	                	if (mc.thePlayer.getDistance(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()) >25 || !whitelist.getBoolean()) {
	                		return pos;
	                	}
	                }
	            }
	        }
	    }
	    return null;
	}
	
	private BlockPos getSurrounding(BlockPos pos) {
	    if (bmc.getBoolean()) {
	        BlockPos current = pos;
	        while (true) {
	            BlockPos next = current.offset(EnumFacing.UP);
	            if (mc.theWorld.getBlockState(next).getBlock() != Blocks.air) {
	                current = next;
	            } else {
	                return current.equals(pos) ? null : current;
	            }
	        }
	    }
	    BlockPos block = pos.offset(EnumFacing.UP);
	    if (mc.theWorld.getBlockState(block).getBlock() != Blocks.air) {
	        return block;
	    }
	    return null;
	}
	
	private boolean isOpen(BlockPos pos) {
		for(EnumFacing facing : EnumFacing.VALUES) {
			BlockPos block = pos.offset(facing);
			if (mc.theWorld.getBlockState(block).getBlock() == Blocks.air) {
	            return true;
	        }
		}
        return false;
	}
	
	private void rotate(BlockPos bp) {
		if(bp != null) {
			if(movefix.getBoolean()) {
				Client.movefix = true;
			}
			rotating = true;
			float[] rots = RotationUtils.getRotationsBlock(bp);
			
			rotations = rots;
			Client.instance.moduleManager.killAura.rotTick = -2;
		}
	}

}
