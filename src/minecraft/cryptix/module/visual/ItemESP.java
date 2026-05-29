package cryptix.module.visual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class ItemESP extends Module{
	private Setting mode = new Setting("Mode", this, "Outline", Arrays.asList("Outline", "Box"));
	private Setting expand = new Setting("Size", this, 0.2, 0.1, 0.5, 1);
	private Setting r = new Setting("Red", this, 255, 0, 255, true);
	private Setting g = new Setting("Green", this, 255, 0, 255, true);
	private Setting b = new Setting("Blue", this, 255, 0, 255, true);
	public ItemESP() {
		super("ItemESP", 0, Category.VISUAL);
		this.addSetting(mode,expand,r,g,b);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public void onEnable() {
		
	}
	
	@Override
	public void onRender3D() {
	    boolean outline = mode.getString().equalsIgnoreCase("outline");
	    float redF = (float) (r.getValue() / 255.0F);
	    float greenF = (float) (g.getValue() / 255.0F);
	    float blueF = (float) (b.getValue() / 255.0F);
	    float alpha = outline ? 1.0f : 0.2f;
	    GlStateManager.enableBlend();
	    GlStateManager.disableTexture2D();
	    GlStateManager.disableDepth();
	    GlStateManager.color(redF, greenF, blueF, alpha);
	    float pt = mc.timer.renderPartialTicks;
	    double viewerX = mc.getRenderManager().viewerPosX;
	    double viewerY = mc.getRenderManager().viewerPosY;
	    double viewerZ = mc.getRenderManager().viewerPosZ;
	    double expandSize = expand.getValue() * 0.5;
	    Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(outline ? GL11.GL_LINES : GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        for (Entity entity : mc.theWorld.loadedEntityList) {
        	if (!(entity instanceof EntityItem))
        	    continue;
        	EntityItem item = (EntityItem) entity;
	        double x = item.lastTickPosX + (item.posX - item.lastTickPosX) * pt - viewerX;
	        double y = item.lastTickPosY + (item.posY - item.lastTickPosY) * pt - viewerY;
	        double z = item.lastTickPosZ + (item.posZ - item.lastTickPosZ) * pt - viewerZ;
	        AxisAlignedBB bb = item.getEntityBoundingBox();
	        double minX = x + (bb.minX - item.posX) - expandSize;
	        double minY = y + (bb.minY - item.posY) - expandSize;
	        double minZ = z + (bb.minZ - item.posZ) - expandSize;
	        double maxX = x + (bb.maxX - item.posX) + expandSize;
	        double maxY = y + (bb.maxY - item.posY) + expandSize;
	        double maxZ = z + (bb.maxZ - item.posZ) + expandSize;
	        if (outline) {
	            drawOutlineBox(minX, minY, minZ, maxX, maxY, maxZ, renderer);
	        } else {
	            drawFilledBox(minX, minY, minZ, maxX, maxY, maxZ, renderer);
	        }
	    }
	    tessellator.draw();
	    GlStateManager.enableDepth();
	    GlStateManager.enableTexture2D();
	    GlStateManager.disableBlend();
	}
	
	private void drawFilledBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, WorldRenderer renderer) {
	    renderer.pos(minX, minY, minZ).endVertex();
	    renderer.pos(maxX, minY, minZ).endVertex();
	    renderer.pos(maxX, minY, maxZ).endVertex();
	    renderer.pos(minX, minY, maxZ).endVertex();
	    renderer.pos(minX, maxY, minZ).endVertex();
	    renderer.pos(minX, maxY, maxZ).endVertex();
	    renderer.pos(maxX, maxY, maxZ).endVertex();
	    renderer.pos(maxX, maxY, minZ).endVertex();
	    renderer.pos(minX, minY, minZ).endVertex();
	    renderer.pos(minX, maxY, minZ).endVertex();
	    renderer.pos(maxX, maxY, minZ).endVertex();
	    renderer.pos(maxX, minY, minZ).endVertex();
	    renderer.pos(minX, minY, maxZ).endVertex();
	    renderer.pos(maxX, minY, maxZ).endVertex();
	    renderer.pos(maxX, maxY, maxZ).endVertex();
	    renderer.pos(minX, maxY, maxZ).endVertex();
	    renderer.pos(minX, minY, minZ).endVertex();
	    renderer.pos(minX, minY, maxZ).endVertex();
	    renderer.pos(minX, maxY, maxZ).endVertex();
	    renderer.pos(minX, maxY, minZ).endVertex();
	    renderer.pos(maxX, minY, minZ).endVertex();
	    renderer.pos(maxX, maxY, minZ).endVertex();
	    renderer.pos(maxX, maxY, maxZ).endVertex();
	    renderer.pos(maxX, minY, maxZ).endVertex();
    }
	
	private void drawOutlineBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, WorldRenderer wr)
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
