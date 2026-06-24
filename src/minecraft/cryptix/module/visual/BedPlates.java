package cryptix.module.visual;

import java.util.ArrayList;
import java.util.List;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.render.EspUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.optifine.BlockPosM;

public class BedPlates extends Module {

    public final List<BlockPos> beds = new ArrayList<>(128);

    private final DoubleSetting range = new DoubleSetting("Range", this, 20.0, 10.0, 50.0, true);
    private final DoubleSetting updateTime = new DoubleSetting("Update Time", this, 10, 5.0, 20, true);

    public BedPlates() {
        super("BedPlates", 0, Category.VISUAL);
        this.addSetting(range, updateTime);
    }

    @Override
    public void onPreUpdate() {
        int update = (int) updateTime.getValue();
        if (mc.thePlayer.ticksExisted % update != 0) return;
        beds.clear();
        final int rangeVal = (int) range.getValue();
        final int rSq = rangeVal * rangeVal;
        final int px = (int) mc.thePlayer.posX;
        final int py = (int) mc.thePlayer.posY;
        final int pz = (int) mc.thePlayer.posZ;
        final int playerChunkX = px >> 4;
        final int playerChunkZ = pz >> 4;
        final int chunkRadius = (rangeVal >> 4) + 1;
        final int minY = Math.max(0, py - 6);
        final int maxY = Math.min(255, py + 6);
        for (int cx = -chunkRadius; cx <= chunkRadius; cx++) {
            int chunkX = playerChunkX + cx;
            for (int cz = -chunkRadius; cz <= chunkRadius; cz++) {
                int chunkZ = playerChunkZ + cz;
                int dx = (cx << 4);
                int dz = (cz << 4);
                if (dx * dx + dz * dz > rSq) continue;
                Chunk chunk = mc.theWorld.getChunkFromChunkCoords(chunkX, chunkZ);
                if (!chunk.isLoaded()) continue;
                int baseX = chunkX << 4;
                int baseZ = chunkZ << 4;
                for (int y = minY; y <= maxY; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            if (chunk.getBlock(x, y, z) != Blocks.bed) continue;
                            int meta = chunk.getBlockMetadata(x, y, z);
                            if ((meta & 8) == 0) continue;
                            beds.add(new BlockPos(baseX + x, y, baseZ + z));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRender3D() {
    	EspUtils.drawBedPlates(beds);
    }
}