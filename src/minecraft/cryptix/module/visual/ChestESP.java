package cryptix.module.visual;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.FrustumUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class ChestESP extends Module {
	private BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
	private final EnumFacing[] FACINGS = EnumFacing.values();
	private Setting mode = new Setting("Mode", this, "Outline", Arrays.asList("Outline", "Box"));
	private Setting enderchest = new Setting("Ender Chest", this, false);
	private Setting r = new Setting("Red", this, 255, 0, 255, true);
	private Setting g = new Setting("Green", this, 255, 0, 255, true);
	private Setting b = new Setting("Blue", this, 255, 0, 255, true);
	private Block[] blockArray = new Block[6];
    public ChestESP() {
        super("ChestESP", 0, Category.VISUAL);
        Client.instance.settingsManager.addSettings(mode, enderchest, r, g, b);
    }

    @Override
    public void onRender3D() {
    	boolean outline = mode.getString().equalsIgnoreCase("outline");
    	GlStateManager.enableBlend();
    	GlStateManager.disableTexture2D();
    	GlStateManager.disableDepth();
    	if (outline) {
    	    GL11.glLineWidth(2.0F);
    	}
    	GlStateManager.color((float) (r.getValue() / 255.0F), (float) (g.getValue() / 255.0F), (float) (b.getValue() / 255.0F), outline ? 1.0f : 0.25f);
    	Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(outline ? GL11.GL_LINES : GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        double viewerX = mc.getRenderManager().viewerPosX;
        double viewerY = mc.getRenderManager().viewerPosY;
        double viewerZ = mc.getRenderManager().viewerPosZ;
        for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
            if (tileEntity instanceof TileEntityChest || (tileEntity instanceof TileEntityEnderChest && enderchest.getBoolean())) {
            	BlockPos pos = tileEntity.getPos();
                double x = pos.getX() - viewerX;
                double y = pos.getY() - viewerY;
                double z = pos.getZ() - viewerZ;
                if (outline) {
                    drawSelectionBoundingBox(x, y, z, x + 1, y + 1, z + 1, renderer);
                } else {
                    drawFilledBox(x, y, z, x + 1, y + 1, z + 1, renderer, pos, tileEntity);
                }
            }
        }
        tessellator.draw();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    private void drawFilledBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, WorldRenderer renderer, BlockPos chest, TileEntity tileEntity) {
    	for (int i = 0; i < 2; i++) {
            EnumFacing facing = FACINGS[i];
            mutablePos.set(chest.getX() + facing.getFrontOffsetX(),chest.getY() + facing.getFrontOffsetY(),chest.getZ() + facing.getFrontOffsetZ());
            blockArray[i] = mc.theWorld.getBlockState(mutablePos).getBlock();
        }
    	TileEntityChest te = (TileEntityChest) tileEntity;
    	blockArray[2] = te.adjacentChestZNeg != null ? Blocks.chest : null;
    	blockArray[3] = te.adjacentChestZPos != null ? Blocks.chest : null;
    	blockArray[4] = te.adjacentChestXNeg != null ? Blocks.chest : null;
    	blockArray[5] = te.adjacentChestXPos != null ? Blocks.chest : null;
        if(blockArray[0] != Blocks.chest && blockArray[0] != Blocks.ender_chest) {
	        renderer.pos(minX, minY, minZ).endVertex();
	        renderer.pos(maxX, minY, minZ).endVertex();
	        renderer.pos(maxX, minY, maxZ).endVertex();
	        renderer.pos(minX, minY, maxZ).endVertex();
        }
        if(blockArray[1] != Blocks.chest && blockArray[1] != Blocks.ender_chest) {
	        renderer.pos(minX, maxY, minZ).endVertex();
	        renderer.pos(minX, maxY, maxZ).endVertex();
	        renderer.pos(maxX, maxY, maxZ).endVertex();
	        renderer.pos(maxX, maxY, minZ).endVertex();
        }
        if(blockArray[2] != Blocks.chest && blockArray[2] != Blocks.ender_chest) {
	        renderer.pos(minX, minY, minZ).endVertex();
	        renderer.pos(minX, maxY, minZ).endVertex();
	        renderer.pos(maxX, maxY, minZ).endVertex();
	        renderer.pos(maxX, minY, minZ).endVertex();
        }
        if(blockArray[3] != Blocks.chest && blockArray[3] != Blocks.ender_chest) {
	        renderer.pos(minX, minY, maxZ).endVertex();
	        renderer.pos(maxX, minY, maxZ).endVertex();
	        renderer.pos(maxX, maxY, maxZ).endVertex();
	        renderer.pos(minX, maxY, maxZ).endVertex();
        }
        if(blockArray[4] != Blocks.chest && blockArray[4] != Blocks.ender_chest) {
	        renderer.pos(minX, minY, minZ).endVertex();
	        renderer.pos(minX, minY, maxZ).endVertex();
	        renderer.pos(minX, maxY, maxZ).endVertex();
	        renderer.pos(minX, maxY, minZ).endVertex();
        }
        if(blockArray[5] != Blocks.chest && blockArray[5] != Blocks.ender_chest) {
	        renderer.pos(maxX, minY, minZ).endVertex();
	        renderer.pos(maxX, maxY, minZ).endVertex();
	        renderer.pos(maxX, maxY, maxZ).endVertex();
	        renderer.pos(maxX, minY, maxZ).endVertex();
        }
    }
    
    private void drawSelectionBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, WorldRenderer wr)
    {
    	wr.pos(minX, minY, minZ).endVertex();
    	wr.pos(maxX, minY, minZ).endVertex();
    	wr.pos(maxX, minY, minZ).endVertex();
    	wr.pos(maxX, minY, maxZ).endVertex();
    	wr.pos(maxX, minY, maxZ).endVertex();
    	wr.pos(minX, minY, maxZ).endVertex();
    	wr.pos(minX, minY, maxZ).endVertex();
    	wr.pos(minX, minY, minZ).endVertex();
    	wr.pos(minX, maxY, minZ).endVertex();
    	wr.pos(maxX, maxY, minZ).endVertex();
    	wr.pos(maxX, maxY, minZ).endVertex();
    	wr.pos(maxX, maxY, maxZ).endVertex();
    	wr.pos(maxX, maxY, maxZ).endVertex();
    	wr.pos(minX, maxY, maxZ).endVertex();
    	wr.pos(minX, maxY, maxZ).endVertex();
    	wr.pos(minX, maxY, minZ).endVertex();
    	wr.pos(minX, minY, minZ).endVertex();
    	wr.pos(minX, maxY, minZ).endVertex();
    	wr.pos(maxX, minY, minZ).endVertex();
    	wr.pos(maxX, maxY, minZ).endVertex();
    	wr.pos(maxX, minY, maxZ).endVertex();
    	wr.pos(maxX, maxY, maxZ).endVertex();
    	wr.pos(minX, minY, maxZ).endVertex();
    	wr.pos(minX, maxY, maxZ).endVertex();
    }
}
