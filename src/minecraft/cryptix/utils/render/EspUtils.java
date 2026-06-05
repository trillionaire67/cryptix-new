package cryptix.utils.render;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cryptix.Client;
import cryptix.module.combat.AntiBot;
import cryptix.utils.FrustumUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.BlockPosM;

public class EspUtils {
	private static final ResourceLocation ALAN_WOOD_IMAGE = new ResourceLocation("cryptix/alan.png");
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	public static void drawPlayersESP(List<EntityPlayer> entities, int mode, int color) {
	    if (entities == null || entities.isEmpty()) return;
	    RenderManager rm = mc.getRenderManager();
	    float partialTicks = mc.timer.renderPartialTicks;
	    final float red   = ((color >> 16) & 255) / 255f;
	    final float green = ((color >> 8) & 255) / 255f;
	    final float blue  = (color & 255) / 255f;
	    if (mode == 0 || mode == 3) {
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        GL11.glDisable(GL11.GL_TEXTURE_2D);
	        GL11.glDisable(GL11.GL_DEPTH_TEST);
	        GL11.glDepthMask(false);
	    }
	    Tessellator tess = null;
	    WorldRenderer wr = null;
	    if (mode == 3 || mode == 0) {
	        tess = Tessellator.getInstance();
	        wr = tess.getWorldRenderer();
	        GL11.glColor4f(red, green, blue, mode == 0 ? 1f : 0.3f);
	        wr.begin(mode == 0 ? 1 : GL11.GL_QUADS, DefaultVertexFormats.POSITION);
	    }
	    for (int i = 0; i < entities.size(); i++) {
	        EntityLivingBase e = entities.get(i);
	        AxisAlignedBB bb = e.getEntityBoundingBox();
	        if (e == null || e.isDead || e == mc.thePlayer || AntiBot.isBot(e) || !FrustumUtils.isVisible(bb)) continue;
	        double interpX = e.lastTickPosX + (e.posX - e.lastTickPosX) * partialTicks;
	        double interpY = e.lastTickPosY + (e.posY - e.lastTickPosY) * partialTicks;
	        double interpZ = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * partialTicks;
	        double x = interpX - rm.viewerPosX;
	        double y = interpY - rm.viewerPosY;
	        double z = interpZ - rm.viewerPosZ;
	        switch (mode) {
		        case 0: {
		        	double minX = bb.minX - e.posX + x;
		        	double minY = bb.minY - e.posY + y;
		        	double minZ = bb.minZ - e.posZ + z;
		        	double maxX = bb.maxX - e.posX + x;
		        	double maxY = bb.maxY - e.posY + y;
		        	double maxZ = bb.maxZ - e.posZ + z;
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
		            break;
		        }
	            case 1: {
	                float dist = mc.thePlayer.getDistanceToEntity(e) - 1;
	                float off = 0.1F + dist * 0.1f;
	                GL11.glPushMatrix();
	                GL11.glTranslated(x, y - 0.2D, z);
	                GL11.glRotatef(-rm.playerViewY, 0, 1, 0);
	                GL11.glRotatef(rm.playerViewX, 1, 0, 0);
	                GL11.glScalef(0.03F, 0.03F, 0.03F);
	                GlStateManager.disableDepth();
	                Gui.drawRect(-16, 0, -16 + off, 71 + off, color);
	                Gui.drawRect(16, 0, 16 + off, 71 + off, color);
	                Gui.drawRect(-16, 0, 16 + off, off, color);
	                Gui.drawRect(-16, 71, 16 + off, 71 + off, color);
	                GlStateManager.enableDepth();
	                GL11.glPopMatrix();
	                break;
	            }
	            case 2: {
	            	float barHeight = 70f;
	            	float filled = (e.getHealth() / e.getMaxHealth()) * barHeight;
	            	GL11.glPushMatrix();
	            	GL11.glTranslated(x, y - 0.2D, z);
	            	GL11.glRotatef(-rm.playerViewY, 0, 1, 0);
	            	GL11.glScalef(0.03F, 0.03F, 0.03F);
	            	GlStateManager.disableDepth();
	            	drawBar(20f, 0f,23f, barHeight,filled,0xFF323232,color | 0xFF000000,1.0f);
	            	GlStateManager.enableDepth();
	            	GL11.glPopMatrix();
	                break;
	            }
	            case 3: {
	            	double minX = bb.minX - e.posX + x;
	            	double minY = bb.minY - e.posY + y;
	            	double minZ = bb.minZ - e.posZ + z;
	            	double maxX = bb.maxX - e.posX + x;
	            	double maxY = bb.maxY - e.posY + y;
	            	double maxZ = bb.maxZ - e.posZ + z;
	                wr.pos(minX, minY, minZ).endVertex();
	                wr.pos(maxX, minY, minZ).endVertex();
	                wr.pos(maxX, minY, maxZ).endVertex();
	                wr.pos(minX, minY, maxZ).endVertex();
	                wr.pos(minX, maxY, minZ).endVertex();
	                wr.pos(minX, maxY, maxZ).endVertex();
	                wr.pos(maxX, maxY, maxZ).endVertex();
	                wr.pos(maxX, maxY, minZ).endVertex();
	                wr.pos(minX, minY, minZ).endVertex();
	                wr.pos(minX, maxY, minZ).endVertex();
	                wr.pos(maxX, maxY, minZ).endVertex();
	                wr.pos(maxX, minY, minZ).endVertex();
	                wr.pos(minX, minY, maxZ).endVertex();
	                wr.pos(maxX, minY, maxZ).endVertex();
	                wr.pos(maxX, maxY, maxZ).endVertex();
	                wr.pos(minX, maxY, maxZ).endVertex();
	                wr.pos(minX, minY, minZ).endVertex();
	                wr.pos(minX, minY, maxZ).endVertex();
	                wr.pos(minX, maxY, maxZ).endVertex();
	                wr.pos(minX, maxY, minZ).endVertex();
	                wr.pos(maxX, minY, minZ).endVertex();
	                wr.pos(maxX, maxY, minZ).endVertex();
	                wr.pos(maxX, maxY, maxZ).endVertex();
	                wr.pos(maxX, minY, maxZ).endVertex();
	                break;
	            }
	            case 4: {
	                GL11.glPushMatrix();
	                GL11.glTranslated(x, y - 0.2D, z);
	                GL11.glRotatef(-rm.playerViewY, 0, 1, 0);
	                GL11.glRotatef(rm.playerViewX, 1, 0, 0);
	                GL11.glRotatef(180, 0, 0, 1);
	                GL11.glScalef(0.03F, 0.03F, 0.03F);
	                GlStateManager.enableBlend();
	                GlStateManager.disableDepth();
	                mc.getTextureManager().bindTexture(ALAN_WOOD_IMAGE);
	                Gui.drawModalRectWithCustomSizedTexture(-15, -70, 0, 0, 30, 69, 30, 69);
	                GlStateManager.enableDepth();
	                GlStateManager.disableBlend();
	                GL11.glPopMatrix();
	                break;
	            }
	            case 5: {
	            	float barHeight = 70f;
	            	float filled = (e.getHealth() / e.getMaxHealth()) * barHeight;
	            	GL11.glPushMatrix();
	            	GL11.glTranslated(x, y - 0.15D, z);
	            	GL11.glRotatef(-rm.playerViewY, 0, 1, 0);
	            	GL11.glScalef(0.03F, 0.03F, 0.03F);
	            	GlStateManager.disableDepth();
	            	float hp = e.getHealth();
	            	int clr;
	            	if (hp < 5f) {
	            	    clr = 0xFFFF0000;
	            	} else if (hp < 12.5f) {
	            	    clr = 0xFFFFFF00;
	            	} else {
	            	    clr = 0xFF00FF00;
	            	}
	            	final float outline = 1.0f;
	            	final float top = 0f;
	            	final float bottom = barHeight;
	            	final float left = 20f;
	            	final float right = 23f;
	            	drawBar(20f, 0f, 23f, barHeight,filled,0xFF323232, clr,1.0f);
	            	GlStateManager.enableDepth();
	            	GL11.glPopMatrix();
	            	break;
	            }
	        }
	    }
	    if (mode == 0 || mode == 3) {
	        tess.draw();
	    }
	    if (mode == 0 || mode == 3) {
	        GL11.glColor4f(1,1,1,1);
	        GL11.glDisable(GL11.GL_BLEND);
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        GL11.glEnable(GL11.GL_DEPTH_TEST);
	        GL11.glDepthMask(true);
	    }
	}

	public static void drawBar(float left, float top, float right, float bottom,float filled,int bgColor,int fillColor,float outline) {
	    Tessellator tess = Tessellator.getInstance();
	    WorldRenderer wr = tess.getWorldRenderer();
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    float bgA = ((bgColor >> 24) & 255) / 255f;
	    float bgR = ((bgColor >> 16) & 255) / 255f;
	    float bgG = ((bgColor >> 8) & 255) / 255f;
	    float bgB = (bgColor & 255) / 255f;
	    float fA = ((fillColor >> 24) & 255) / 255f;
	    float fR = ((fillColor >> 16) & 255) / 255f;
	    float fG = ((fillColor >> 8) & 255) / 255f;
	    float fB = (fillColor & 255) / 255f;
	    wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
	    addQuad(wr,left - outline, top - outline,right + outline, bottom + outline,0, 0, 0, 1);
	    addQuad(wr,left, top,right, bottom,bgR, bgG, bgB, bgA);
	    addQuad(wr,left, top,right, top + filled,fR, fG, fB, fA);
	    tess.draw();
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	private static void addQuad(WorldRenderer wr,float x1, float y1,float x2, float y2,float r, float g, float b, float a) {
	    wr.pos(x1, y2, 0).color(r, g, b, a).endVertex();
	    wr.pos(x2, y2, 0).color(r, g, b, a).endVertex();
	    wr.pos(x2, y1, 0).color(r, g, b, a).endVertex();
	    wr.pos(x1, y1, 0).color(r, g, b, a).endVertex();
	}
	
	public static void drawBedPlates(List<BlockPos> beds) {
	    if (beds.isEmpty()) return;
	    final RenderManager rm = mc.getRenderManager();
	    final double viewerX = rm.viewerPosX;
	    final double viewerY = rm.viewerPosY;
	    final double viewerZ = rm.viewerPosZ;
	    final float yaw = -rm.playerViewY;
	    final float pitch = rm.playerViewX;
	    final int color = (100 << 24) | (0 << 16) | (0 << 8) | 0;
	    GlStateManager.pushMatrix();
	    GlStateManager.disableDepth();
	    for (BlockPos bedPos : beds) {
	        BlockPos pos = bedPos.up();
	        Block block = mc.theWorld.getBlockState(pos).getBlock();
	        if (block == Blocks.air) continue;
	        double x = bedPos.getX() + 0.5 - viewerX;
	        double y = bedPos.getY() + 1 - viewerY;
	        double z = bedPos.getZ() + 0.5 - viewerZ;
	        GlStateManager.pushMatrix();
	        GL11.glTranslated(x, y, z);
	        GL11.glRotatef(yaw, 0, 1, 0);
	        GL11.glRotatef(pitch, 1, 0, 0);
	        GL11.glScalef(-0.03F, -0.03F, 0.03F);
	        RenderUtils.drawRoundedRectangle(0, 0, 16, 16, 10, color);
	        GlStateManager.popMatrix();
	    }
	    GlStateManager.enableDepth();
	    GlStateManager.popMatrix();
	    GlStateManager.pushMatrix();
	    GlStateManager.disableDepth();
	    for (BlockPos bedPos : beds) {
	        BlockPos pos = bedPos.up();
	        Block block = mc.theWorld.getBlockState(pos).getBlock();
	        if (block == Blocks.air) continue;
	        ItemStack stack = new ItemStack(block);
	        if (stack.getItem() == null) continue;
	        double x = bedPos.getX() + 0.5 - viewerX;
	        double y = bedPos.getY() + 1 - viewerY;
	        double z = bedPos.getZ() + 0.5 - viewerZ;
	        GlStateManager.pushMatrix();
	        GL11.glTranslated(x, y, z);
	        GL11.glRotatef(yaw, 0, 1, 0);
	        GL11.glRotatef(pitch, 1, 0, 0);
	        GlStateManager.scale(0.33f, 0.33f, 0.33f);
	        GL11.glTranslated(-0.7, -0.7, 0);
	        mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
	        GlStateManager.popMatrix();
	    }
	    GlStateManager.enableDepth();
	    GlStateManager.popMatrix();
	}

	public static void drawBedESP(BlockPos bedPos, float red, float green, float blue, boolean box) {
	    final float partialTicks = mc.timer.renderPartialTicks;
	    final double px = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTicks;
	    final double py = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTicks;
	    final double pz = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTicks;
	    final double x = bedPos.getX() - px;
	    final double y = bedPos.getY() - py;
	    final double z = bedPos.getZ() - pz;
	    final double x2 = x + 1.0;
	    final double y2 = y + 1.0;
	    final double z2 = z + 1.0;
	    final float r = red * 0.003921569F;
	    final float g = green * 0.003921569F;
	    final float b = blue * 0.003921569F;
	    GlStateManager.pushMatrix();
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	    GlStateManager.disableTexture2D();
	    GlStateManager.disableDepth();
	    GlStateManager.depthMask(false);
	    GlStateManager.color(r, g, b, box ? 0.25F : 1.0F);
	    Tessellator tess = Tessellator.getInstance();
	    WorldRenderer wr = tess.getWorldRenderer();
	    wr.begin(box ? GL11.GL_QUADS : GL11.GL_LINES, DefaultVertexFormats.POSITION);
	    if (!box) {
		    addLine(wr, x,  y,  z,  x2, y,  z);
		    addLine(wr, x2, y,  z,  x2, y,  z2);
		    addLine(wr, x2, y,  z2, x,  y,  z2);
		    addLine(wr, x,  y,  z2, x,  y,  z);
		    addLine(wr, x,  y2, z,  x2, y2, z);
		    addLine(wr, x2, y2, z,  x2, y2, z2);
		    addLine(wr, x2, y2, z2, x,  y2, z2);
		    addLine(wr, x,  y2, z2, x,  y2, z);
		    addLine(wr, x,  y,  z,  x,  y2, z);
		    addLine(wr, x2, y,  z,  x2, y2, z);
		    addLine(wr, x2, y,  z2, x2, y2, z2);
		    addLine(wr, x,  y,  z2, x,  y2, z2);
	    }else {
            wr.pos(x, y, z).endVertex();
            wr.pos(x2, y, z).endVertex();
            wr.pos(x2, y, z2).endVertex();
            wr.pos(x, y, z2).endVertex();
            wr.pos(x, y2, z).endVertex();
            wr.pos(x, y2, z2).endVertex();
            wr.pos(x2, y2, z2).endVertex();
            wr.pos(x2, y2, z).endVertex();
            wr.pos(x, y, z).endVertex();
            wr.pos(x, y2, z).endVertex();
            wr.pos(x2, y2, z).endVertex();
            wr.pos(x2, y, z).endVertex();
            wr.pos(x, y, z2).endVertex();
            wr.pos(x2, y, z2).endVertex();
            wr.pos(x2, y2, z2).endVertex();
            wr.pos(x, y2, z2).endVertex();
            wr.pos(x, y, z).endVertex();
            wr.pos(x, y, z2).endVertex();
            wr.pos(x, y2, z2).endVertex();
            wr.pos(x, y2, z).endVertex();
            wr.pos(x2, y, z).endVertex();
            wr.pos(x2, y2, z).endVertex();
            wr.pos(x2, y2, z2).endVertex();
            wr.pos(x2, y, z2).endVertex();
	    }
	    tess.draw();
	    GlStateManager.depthMask(true);
	    GlStateManager.enableDepth();
	    GlStateManager.enableTexture2D();
	    GlStateManager.disableBlend();
	    GlStateManager.popMatrix();
	}
	
	public static void drawBedsESP(List<BlockPos> beds, float red, float green, float blue, boolean box) {
	    if (beds.isEmpty()) return;

	    final float partialTicks = mc.timer.renderPartialTicks;

	    final double px = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTicks;
	    final double py = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTicks;
	    final double pz = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTicks;

	    final float r = red * 0.003921569F;
	    final float g = green * 0.003921569F;
	    final float b = blue * 0.003921569F;

	    GlStateManager.pushMatrix();
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	    GlStateManager.disableTexture2D();
	    GlStateManager.disableDepth();
	    GlStateManager.depthMask(false);

	    Tessellator tess = Tessellator.getInstance();
	    WorldRenderer wr = tess.getWorldRenderer();

	    if (box) {
	        GlStateManager.color(r, g, b, 0.25F);
	        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
	        BlockPosM mutable = new BlockPosM(0,0,0);
	        for (BlockPos bedPos : beds) {
	            double x = bedPos.getX() - px;
	            double y = bedPos.getY() - py;
	            double z = bedPos.getZ() - pz;
	            double x2 = x + 1.0;
	            double y2 = y + 1.0;
	            double z2 = z + 1.0;
	            boolean down  = isBed(bedPos, EnumFacing.DOWN, mutable);
	            boolean up    = isBed(bedPos, EnumFacing.UP, mutable);
	            boolean north = isBed(bedPos, EnumFacing.NORTH, mutable);
	            boolean south = isBed(bedPos, EnumFacing.SOUTH, mutable);
	            boolean west  = isBed(bedPos, EnumFacing.WEST, mutable);
	            boolean east  = isBed(bedPos, EnumFacing.EAST, mutable);
	            if (!down) {
	                wr.pos(x, y, z).endVertex();
	                wr.pos(x2, y, z).endVertex();
	                wr.pos(x2, y, z2).endVertex();
	                wr.pos(x, y, z2).endVertex();
	            }
	            if (!up) {
	                wr.pos(x, y2, z).endVertex();
	                wr.pos(x, y2, z2).endVertex();
	                wr.pos(x2, y2, z2).endVertex();
	                wr.pos(x2, y2, z).endVertex();
	            }
	            if (!north) {
	                wr.pos(x, y, z).endVertex();
	                wr.pos(x, y2, z).endVertex();
	                wr.pos(x2, y2, z).endVertex();
	                wr.pos(x2, y, z).endVertex();
	            }
	            if (!south) {
	                wr.pos(x, y, z2).endVertex();
	                wr.pos(x2, y, z2).endVertex();
	                wr.pos(x2, y2, z2).endVertex();
	                wr.pos(x, y2, z2).endVertex();
	            }
	            if (!west) {
	                wr.pos(x, y, z).endVertex();
	                wr.pos(x, y, z2).endVertex();
	                wr.pos(x, y2, z2).endVertex();
	                wr.pos(x, y2, z).endVertex();
	            }
	            if (!east) {
	                wr.pos(x2, y, z).endVertex();
	                wr.pos(x2, y2, z).endVertex();
	                wr.pos(x2, y2, z2).endVertex();
	                wr.pos(x2, y, z2).endVertex();
	            }
	        }
	        tess.draw();
	        GlStateManager.color(r, g, b, 1.0F);
	    } else {
	        GlStateManager.color(r, g, b, 1.0F);
	        wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
	        for (BlockPos bedPos : beds) {
		        double x = bedPos.getX() - px;
		        double y = bedPos.getY() - py;
		        double z = bedPos.getZ() - pz;
		        double x2 = x + 1.0;
		        double y2 = y + 1.0;
		        double z2 = z + 1.0;
		        addLine(wr, x,  y,  z,  x2, y,  z);
		        addLine(wr, x2, y,  z,  x2, y,  z2);
		        addLine(wr, x2, y,  z2, x,  y,  z2);
		        addLine(wr, x,  y,  z2, x,  y,  z);
		        addLine(wr, x,  y2, z,  x2, y2, z);
		        addLine(wr, x2, y2, z,  x2, y2, z2);
		        addLine(wr, x2, y2, z2, x,  y2, z2);
		        addLine(wr, x,  y2, z2, x,  y2, z);
		        addLine(wr, x,  y,  z,  x,  y2, z);
		        addLine(wr, x2, y,  z,  x2, y2, z);
		        addLine(wr, x2, y,  z2, x2, y2, z2);
		        addLine(wr, x,  y,  z2, x,  y2, z2);
		    }
	        tess.draw();
	    }
	    GlStateManager.depthMask(true);
	    GlStateManager.enableDepth();
	    GlStateManager.enableTexture2D();
	    GlStateManager.disableBlend();
	    GlStateManager.popMatrix();
	}
	
	private static boolean isBed(BlockPos pos, EnumFacing facing, BlockPosM mutable) {
		mutable.set(pos.getX() + facing.getFrontOffsetX(),pos.getY() + facing.getFrontOffsetY(),pos.getZ() + facing.getFrontOffsetZ());
	    return mc.theWorld.getBlockState(mutable).getBlock() == Blocks.bed;
	}

	private static void addLine(WorldRenderer wr, double x1, double y1, double z1, double x2, double y2, double z2) {
	    wr.pos(x1, y1, z1).endVertex();
	    wr.pos(x2, y2, z2).endVertex();
	}
	
	public static void drawObby(List<BlockPos> positions, float r, float g, float b) {
		if(positions.isEmpty()) return;
		float pt = mc.timer.renderPartialTicks;
	    double px = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * pt;
	    double py = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * pt;
	    double pz = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * pt;
	    GlStateManager.pushMatrix();
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	    GlStateManager.disableTexture2D();
	    GlStateManager.disableDepth();
	    GlStateManager.depthMask(false);
	    GlStateManager.color(r, g, b, 1.0f);
	    Tessellator tess = Tessellator.getInstance();
	    WorldRenderer wr = tess.getWorldRenderer();
	    wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
	    for(BlockPos pos : positions) {
		    double x = pos.getX() - px;
		    double y = pos.getY() - py;
		    double z = pos.getZ() - pz;
		    double x1 = x;
		    double y1 = y;
		    double z1 = z;
		    double x2 = x + 1.0;
		    double y2 = y + 1.0;
		    double z2 = z + 1.0;
		    wr.pos(x1, y1, z1).endVertex();
		    wr.pos(x2, y1, z1).endVertex();
		    wr.pos(x2, y1, z1).endVertex();
		    wr.pos(x2, y1, z2).endVertex();
		    wr.pos(x2, y1, z2).endVertex();
		    wr.pos(x1, y1, z2).endVertex();
		    wr.pos(x1, y1, z2).endVertex();
		    wr.pos(x1, y1, z1).endVertex();
		    wr.pos(x1, y2, z1).endVertex();
		    wr.pos(x2, y2, z1).endVertex();
		    wr.pos(x2, y2, z1).endVertex();
		    wr.pos(x2, y2, z2).endVertex();
		    wr.pos(x2, y2, z2).endVertex();
		    wr.pos(x1, y2, z2).endVertex();
		    wr.pos(x1, y2, z2).endVertex();
		    wr.pos(x1, y2, z1).endVertex();
		    wr.pos(x1, y1, z1).endVertex();
		    wr.pos(x1, y2, z1).endVertex();
		    wr.pos(x2, y1, z1).endVertex();
		    wr.pos(x2, y2, z1).endVertex();
		    wr.pos(x2, y1, z2).endVertex();
		    wr.pos(x2, y2, z2).endVertex();
		    wr.pos(x1, y1, z2).endVertex();
		    wr.pos(x1, y2, z2).endVertex();
	    }
	    tess.draw();
	    GlStateManager.depthMask(true);
	    GlStateManager.enableDepth();
	    GlStateManager.enableTexture2D();
	    GlStateManager.disableBlend();
	    GlStateManager.popMatrix();
	}
	
	public static void drawKillAuraRing(Entity e, float radius, int alpha) {
		double partial = mc.timer.renderPartialTicks;
	    double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * partial - mc.getRenderManager().viewerPosX;
	    double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * partial - mc.getRenderManager().viewerPosY;
	    double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * partial - mc.getRenderManager().viewerPosZ;
	    double maxHeight = e.getEyeHeight() + 0.20;
	    double time = (System.currentTimeMillis() % 2000L) * 0.001;
	    double yOffset = (MathHelper.sin((float) (time * Math.PI)) * 0.5 + 0.5) * maxHeight;
	    double bounce = (time > 1.5 || time < 0.5) ? 0.2 : -0.2;
	    int colorInt = Client.instance.moduleManager.hud.getColorInt(0, 1f);
	    float red   = ((colorInt >> 16) & 0xFF) * (1f / 255f);
	    float green = ((colorInt >> 8) & 0xFF) * (1f / 255f);
	    float blue  = (colorInt & 0xFF) * (1f / 255f);
	    final int points = 64;
	    final float angleStep = (float) (Math.PI * 2.0 / points);
	    float sinStep = (float) Math.sin(angleStep);
	    float cosStep = (float) Math.cos(angleStep);
	    float sin = 0f;
	    float cos = 1f;
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_DEPTH_TEST);
	    GL11.glDisable(GL11.GL_CULL_FACE);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    GL11.glShadeModel(GL11.GL_SMOOTH);
	    Tessellator tess = Tessellator.getInstance();
	    WorldRenderer wr = tess.getWorldRenderer();
	    wr.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
	    double yBottom = y + yOffset - bounce;
	    double yTop = y + yOffset;
	    for (int i = 0; i <= points; i++) {
	        double angle = (Math.PI * 2.0 * i) / points;
	        double px = x - Math.sin(angle) * radius;
	        double pz = z + Math.cos(angle) * radius;
	        wr.pos(px, yBottom, pz).color(red, green, blue, 0f).endVertex();
	        wr.pos(px, yTop, pz).color(red, green, blue, 1f).endVertex();
	    }
	    tess.draw();
	    GL11.glShadeModel(GL11.GL_FLAT);
	    GL11.glEnable(GL11.GL_CULL_FACE);
	    GL11.glEnable(GL11.GL_DEPTH_TEST);
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glColor4f(1f, 1f, 1f, 1f);
	}
	
	private static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2) {
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x2, y2, z2);
    }
}
