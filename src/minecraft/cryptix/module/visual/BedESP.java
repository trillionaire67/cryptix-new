package cryptix.module.visual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.module.ModuleManager;
import cryptix.module.player.BedNuker;
import cryptix.utils.render.EspUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.optifine.BlockPosM;

public class BedESP extends Module{
	public List<BlockPos> beds = new ArrayList<>();
	private List<BlockPos> obsidians = new ArrayList<>();
	private Setting range, showObby, red, green, blue, bednuker, updateTime, mode;
	public BedESP() {
		super("BedESP", 0, Category.VISUAL);
		Client.instance.settingsManager.addSetting(mode = new Setting("Mode", this, "Outline", Arrays.asList("Outline", "Box")));
		Client.instance.settingsManager.addSetting(bednuker = new Setting("Only BedNuker", this, false));
		Client.instance.settingsManager.addSetting(showObby = new Setting("Show Obsidian", this, false));
		Client.instance.settingsManager.addSetting(range = new Setting("Range", this, 20.0, 10.0, 50.0, true));
		Client.instance.settingsManager.addSetting(red = new Setting("Red", this, 255, 0.0, 255.0, true));
		Client.instance.settingsManager.addSetting(green = new Setting("Green", this, 255, 0.0, 255.0, true));
		Client.instance.settingsManager.addSetting(blue = new Setting("Blue", this, 255, 0.0, 255.0, true));
		Client.instance.settingsManager.addSetting(updateTime = new Setting("Update Time", this, 10, 5.0, 20, true));
	}
	
	@Override
	public void onPreUpdate() {
	    if (mc.thePlayer.ticksExisted % updateTime.getValue() != 0 || bednuker.getBoolean()) return;
	    beds.clear();
	    obsidians.clear();
	    final int rangeVal = (int) range.getValue();
	    final int rSq = rangeVal * rangeVal;

	    final double pxD = mc.thePlayer.posX;
	    final double pyD = mc.thePlayer.posY;
	    final double pzD = mc.thePlayer.posZ;
	    final int px = (int) pxD;
	    final int py = (int) pyD;
	    final int pz = (int) pzD;
	    final boolean obby = showObby.getBoolean();
	    final World world = mc.theWorld;
	    final int playerChunkX = px >> 4;
	    final int playerChunkZ = pz >> 4;
	    final int chunkRadius = (rangeVal >> 4) + 1;
	    final int minY = Math.max(0, py - 10);
	    final int maxY = Math.min(255, py + 10);
	    for (int cx = -chunkRadius; cx <= chunkRadius; cx++) {
	        final int chunkX = playerChunkX + cx;
	        final int baseX = chunkX << 4;
	        final int dxChunk = cx << 4;
	        final int dxChunkSq = dxChunk * dxChunk;
	        for (int cz = -chunkRadius; cz <= chunkRadius; cz++) {
	            final int dzChunk = cz << 4;
	            if (dxChunkSq + dzChunk * dzChunk > rSq) continue;
	            final int chunkZ = playerChunkZ + cz;
	            final int baseZ = chunkZ << 4;
	            final Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
	            if (!chunk.isLoaded()) continue;
	            for (int y = minY; y <= maxY; y++) {
	                final int yBase = y * 0;
	                for (int x = 0; x < 16; x++) {
	                    final int worldX = baseX + x;
	                    final int dx2 = worldX - px;
	                    final int dx2Sq = dx2 * dx2;
	                    for (int z = 0; z < 16; z++) {
	                        final int worldZ = baseZ + z;
	                        final int dz2 = worldZ - pz;
	                        final int distSq = dx2Sq + dz2 * dz2;
	                        if (distSq > rSq) continue;
	                        final Block block = chunk.getBlock(x, y, z);
	                        if (block == Blocks.bed) {
	                            beds.add(new BlockPos(worldX, y, worldZ));
	                        } else if (obby && block instanceof BlockObsidian) {
	                            obsidians.add(new BlockPos(worldX, y, worldZ));
	                        }
	                    }
	                }
	            }
	        }
	    }
	}

	@Override
	public void onRender3D() {
	    if (bednuker.getBoolean()) {
	        BlockPos bedPos = Client.instance.moduleManager.bedNuker.bedPos;
	        if (bedPos != null) {
	            EspUtils.drawBedESP(bedPos, (float) red.getValue(), (float) green.getValue(), (float) blue.getValue(), mode.getString().equalsIgnoreCase("Box"));
	        }
	    } else {
	        EspUtils.drawBedsESP(beds, (float) red.getValue(), (float) green.getValue(), (float) blue.getValue(), mode.getString().equalsIgnoreCase("Box"));

	        if (showObby.getBoolean()) {
	        	EspUtils.drawObby(obsidians, 1.0f, 0.0f, 1.0f);
	        }
	    }
	}

}
