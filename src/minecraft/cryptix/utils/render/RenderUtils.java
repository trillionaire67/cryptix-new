package cryptix.utils.render;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cryptix.utils.RenderCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderUtils {
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation("cryptix/glow.png");
	private static final ResourceLocation GLOW_TEXTURE2 = new ResourceLocation("cryptix/targethud.png");
	private static final FloatBuffer SCREEN_COORDS = GLAllocation.createDirectFloatBuffer(3);
	private static final double[] RESULT = new double[2];
	private static final int step = 3;
	private static int pointsCount = 180 / step + 1;
	private static double[] sinValues = new double[pointsCount];
	private static double[] cosValues = new double[pointsCount];
	private static double[] sinValues2 = new double[33];
	private static double[] cosValues2 = new double[33];
	static {
	    for (int i = 0; i < pointsCount; i++) {
	        double angle = i * step * Math.PI / 180.0;
	        sinValues[i] = Math.sin(angle);
	        cosValues[i] = Math.cos(angle);
	    }
	    for(int i = 0; i <= 32; i++) {
            double angle = Math.PI * 2 * i / 32;
            sinValues2[i] = Math.sin(angle);
            cosValues2[i] = Math.cos(angle);
        }
	}

	public static void drawRoundedRectangle(double x1, double y1, double x2, double y2, double radius, int color) {
	    if (x2 <= x1) return;
	    double width = x2 - x1;
	    if (width < 5.0) {
	        radius = Math.min(radius, width * 0.5);
	    }
	    final float r = ((color >> 16) & 0xFF) * 0.0039215686f;
	    final float g = ((color >> 8) & 0xFF) * 0.0039215686f;
	    final float b = (color & 0xFF) * 0.0039215686f;
	    final float a = ((color >> 24) & 0xFF) * 0.0039215686f;
	    GL11.glPushMatrix();
	    GL11.glScaled(0.5, 0.5, 1.0);
	    x1 *= 2.0;
	    y1 *= 2.0;
	    x2 *= 2.0;
	    y2 *= 2.0;
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    GL11.glColor4f(r, g, b, a);
	    GL11.glBegin(GL11.GL_POLYGON);
	    for (int i = 0, idx = 0; i <= 90; i += step, idx++) {
	        GL11.glVertex2d(x1 + radius - sinValues[idx] * radius, y1 + radius - cosValues[idx] * radius);
	    }
	    for (int i = 90, idx = 90 / step; i <= 180; i += step, idx++) {
	        GL11.glVertex2d(x1 + radius - sinValues[idx] * radius, y2 - radius - cosValues[idx] * radius);
	    }

	    if (width >= 4.5) {
	        for (int i = 0, idx = 0; i <= 90; i += step, idx++) {
	            GL11.glVertex2d(x2 - radius + sinValues[idx] * radius, y2 - radius + cosValues[idx] * radius);
	        }
	        for (int i = 90, idx = 90 / step; i <= 180; i += step, idx++) {
	            GL11.glVertex2d(x2 - radius + sinValues[idx] * radius, y1 + radius + cosValues[idx] * radius);
	        }
	    }
	    GL11.glEnd();
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glColor4f(1, 1, 1, 1);
	    GL11.glPopMatrix();
	}
	
	public static void drawRoundedRectangleNoRender(double x1, double y1, double x2, double y2, double radius, int color) {
	    if (x2 <= x1) return;
	    double width = x2 - x1;
	    if (width < 5.0) {
	        radius = Math.min(radius, width * 0.5);
	    }
	    final float r = ((color >> 16) & 0xFF) * 0.0039215686f;
	    final float g = ((color >> 8) & 0xFF) * 0.0039215686f;
	    final float b = (color & 0xFF) * 0.0039215686f;
	    final float a = ((color >> 24) & 0xFF) * 0.0039215686f;
	    x1 *= 2.0;
	    y1 *= 2.0;
	    x2 *= 2.0;
	    y2 *= 2.0;
	    GL11.glColor4f(r, g, b, a);
	    GL11.glBegin(GL11.GL_POLYGON);
	    for (int i = 0, idx = 0; i <= 90; i += step, idx++) {
	        GL11.glVertex2d(x1 + radius - sinValues[idx] * radius, y1 + radius - cosValues[idx] * radius);
	    }
	    for (int i = 90, idx = 90 / step; i <= 180; i += step, idx++) {
	        GL11.glVertex2d(x1 + radius - sinValues[idx] * radius, y2 - radius - cosValues[idx] * radius);
	    }

	    if (width >= 4.5) {
	        for (int i = 0, idx = 0; i <= 90; i += step, idx++) {
	            GL11.glVertex2d(x2 - radius + sinValues[idx] * radius, y2 - radius + cosValues[idx] * radius);
	        }
	        for (int i = 90, idx = 90 / step; i <= 180; i += step, idx++) {
	            GL11.glVertex2d(x2 - radius + sinValues[idx] * radius, y1 + radius + cosValues[idx] * radius);
	        }
	    }
	    GL11.glEnd();
	}
	
	public static void startRoundedRectangle() {
		GL11.glPushMatrix();
	    GL11.glScaled(0.5, 0.5, 1.0);
		GL11.glEnable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public static void stopRoundedRectangle() {
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glColor4f(1, 1, 1, 1);
	    GL11.glPopMatrix();
	}
	
	public static void glVertex3D(Vec3 vector3d) {
        GL11.glVertex3d(vector3d.xCoord, vector3d.yCoord, vector3d.zCoord);
    }

	public static void drawBoundingBox(AxisAlignedBB aa) {
	    double x = mc.getRenderManager().renderPosX;
	    double y = mc.getRenderManager().renderPosY;
	    double z = mc.getRenderManager().renderPosZ;
	    Tessellator tessellator = Tessellator.getInstance();
	    WorldRenderer wr = tessellator.getWorldRenderer();
	    wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
	    wr.pos(aa.minX - x, aa.minY - y, aa.minZ - z).endVertex();
	    wr.pos(aa.maxX - x, aa.minY - y, aa.minZ - z).endVertex();
	    wr.pos(aa.maxX - x, aa.minY - y, aa.maxZ - z).endVertex();
	    wr.pos(aa.minX - x, aa.minY - y, aa.maxZ - z).endVertex();
	    wr.pos(aa.minX - x, aa.maxY - y, aa.minZ - z).endVertex();
	    wr.pos(aa.minX - x, aa.maxY - y, aa.maxZ - z).endVertex();
	    wr.pos(aa.maxX - x, aa.maxY - y, aa.maxZ - z).endVertex();
	    wr.pos(aa.maxX - x, aa.maxY - y, aa.minZ - z).endVertex();
	    wr.pos(aa.minX - x, aa.minY - y, aa.minZ - z).endVertex();
	    wr.pos(aa.minX - x, aa.maxY - y, aa.minZ - z).endVertex();
	    wr.pos(aa.maxX - x, aa.maxY - y, aa.minZ - z).endVertex();
	    wr.pos(aa.maxX - x, aa.minY - y, aa.minZ - z).endVertex();
	    wr.pos(aa.minX - x, aa.minY - y, aa.maxZ - z).endVertex();
	    wr.pos(aa.maxX - x, aa.minY - y, aa.maxZ - z).endVertex();
	    wr.pos(aa.maxX - x, aa.maxY - y, aa.maxZ - z).endVertex();
	    wr.pos(aa.minX - x, aa.maxY - y, aa.maxZ - z).endVertex();
	    wr.pos(aa.minX - x, aa.minY - y, aa.minZ - z).endVertex();
	    wr.pos(aa.minX - x, aa.minY - y, aa.maxZ - z).endVertex();
	    wr.pos(aa.minX - x, aa.maxY - y, aa.maxZ - z).endVertex();
	    wr.pos(aa.minX - x, aa.maxY - y, aa.minZ - z).endVertex();
	    wr.pos(aa.maxX - x, aa.minY - y, aa.minZ - z).endVertex();
	    wr.pos(aa.maxX - x, aa.maxY - y, aa.minZ - z).endVertex();
	    wr.pos(aa.maxX - x, aa.maxY - y, aa.maxZ - z).endVertex();
	    wr.pos(aa.maxX - x, aa.minY - y, aa.maxZ - z).endVertex();
	    tessellator.draw();
	}
	
    public static void drawRoundedGradientRect(float x, float y, float x2, float y2,
    	final float radius,
        final int colorTopLeft, final int colorBottomLeft,
        final int colorBottomRight, final int colorTopRight) {
		GL11.glPushAttrib(0);
		GL11.glScaled(0.5, 0.5, 0.5);
		x *= 2.0;
		y *= 2.0;
		x2 *= 2.0;
		y2 *= 2.0;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glShadeModel(7425);
		GL11.glBegin(GL11.GL_POLYGON);
		GL11.glColor4f((colorTopLeft >> 16 & 0xFF) / 255f,
		(colorTopLeft >> 8 & 0xFF) / 255f,
		(colorTopLeft & 0xFF) / 255f,
		(colorTopLeft >> 24 & 0xFF) / 255f);
		for (int i = 0; i <= 90; i += step) {
		int idx = i / step;
		GL11.glVertex2d(x + radius + sinValues[idx] * -radius, y + radius + cosValues[idx] * -radius);
		}
		GL11.glColor4f((colorBottomLeft >> 16 & 0xFF) / 255f,
		(colorBottomLeft >> 8 & 0xFF) / 255f,
		(colorBottomLeft & 0xFF) / 255f,
		(colorBottomLeft >> 24 & 0xFF) / 255f);
		for (int j = 90; j <= 180; j += step) {
		int idx = j / step;
		GL11.glVertex2d(x + radius + sinValues[idx] * -radius, y2 - radius + cosValues[idx] * -radius);
		}
		GL11.glColor4f((colorBottomRight >> 16 & 0xFF) / 255f,
		(colorBottomRight >> 8 & 0xFF) / 255f,
		(colorBottomRight & 0xFF) / 255f,
		(colorBottomRight >> 24 & 0xFF) / 255f);
		for (int k = 0; k <= 90; k += step) {
		int idx = k / step;
		GL11.glVertex2d(x2 - radius + sinValues[idx] * radius, y2 - radius + cosValues[idx] * radius);
		}
		GL11.glColor4f((colorTopRight >> 16 & 0xFF) / 255f,
		(colorTopRight >> 8 & 0xFF) / 255f,
		(colorTopRight & 0xFF) / 255f,
		(colorTopRight >> 24 & 0xFF) / 255f);
		for (int l = 90; l <= 180; l += step) {
		int idx = l / step;
		GL11.glVertex2d(x2 - radius + sinValues[idx] * radius, y + radius + cosValues[idx] * radius);
		}
		GL11.glEnd();
		GL11.glShadeModel(7424);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glScaled(2.0, 2.0, 2.0);
		GL11.glColor4f(1,1,1,1);
		GL11.glPopAttrib();
	}
	
	public static void drawGradientRect(double left, double top, double right, double bottom, int color1, int color2) {
		float alpha1 = (color1 >> 24 & 255) / 255.0F;
	    float red1 = (color1 >> 16 & 255) / 255.0F;
	    float green1 = (color1 >> 8 & 255) / 255.0F;
	    float blue1 = (color1 & 255) / 255.0F;
	    float alpha2 = (color2 >> 24 & 255) / 255.0F;
	    float red2 = (color2 >> 16 & 255) / 255.0F;
	    float green2 = (color2 >> 8 & 255) / 255.0F;
	    float blue2 = (color2 & 255) / 255.0F;
	    GlStateManager.pushMatrix();
	    GlStateManager.enableBlend();
	    GlStateManager.disableTexture2D();
	    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	    GlStateManager.shadeModel(GL11.GL_SMOOTH);
	    Tessellator tessellator = Tessellator.getInstance();
	    WorldRenderer wr = tessellator.getWorldRenderer();
	    wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
	    wr.pos(left, top, 0.0D).color(red1, green1, blue1, alpha1).endVertex();
	    wr.pos(left, bottom, 0.0D).color(red1, green1, blue1, alpha1).endVertex();
	    wr.pos(right, bottom, 0.0D).color(red2, green2, blue2, alpha2).endVertex();
	    wr.pos(right, top, 0.0D).color(red2, green2, blue2, alpha2).endVertex();
	    tessellator.draw();
	    GlStateManager.shadeModel(GL11.GL_FLAT);
	    GlStateManager.enableTexture2D();
	    GlStateManager.disableBlend();
	    GlStateManager.popMatrix();
	}
	
	public static void drawGradientRectNoRender(double left, double top, double right, double bottom, int color1, int color2, WorldRenderer wr) {
		float alpha1 = (color1 >> 24 & 255) / 255.0F;
	    float red1 = (color1 >> 16 & 255) / 255.0F;
	    float green1 = (color1 >> 8 & 255) / 255.0F;
	    float blue1 = (color1 & 255) / 255.0F;
	    float alpha2 = (color2 >> 24 & 255) / 255.0F;
	    float red2 = (color2 >> 16 & 255) / 255.0F;
	    float green2 = (color2 >> 8 & 255) / 255.0F;
	    float blue2 = (color2 & 255) / 255.0F;
	    wr.pos(left, top, 0.0D).color(red1, green1, blue1, alpha1).endVertex();
	    wr.pos(left, bottom, 0.0D).color(red1, green1, blue1, alpha1).endVertex();
	    wr.pos(right, bottom, 0.0D).color(red2, green2, blue2, alpha2).endVertex();
	    wr.pos(right, top, 0.0D).color(red2, green2, blue2, alpha2).endVertex();
	}
	
	public static void startGradientRect(Tessellator tessellator) {
		GlStateManager.pushMatrix();
	    GlStateManager.enableBlend();
	    GlStateManager.disableTexture2D();
	    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	    GlStateManager.shadeModel(GL11.GL_SMOOTH);
	    WorldRenderer wr = tessellator.getWorldRenderer();
	    wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
	}
	
	public static void stopGradientRect(Tessellator tessellator) {
		tessellator.draw();
	    GlStateManager.shadeModel(GL11.GL_FLAT);
	    GlStateManager.enableTexture2D();
	    GlStateManager.disableBlend();
	    GlStateManager.popMatrix();
	}
	
	public static void drawFilledBox(AxisAlignedBB box) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        renderer.pos(box.minX, box.minY, box.minZ).endVertex();
        renderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        renderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        renderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        renderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        renderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        renderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        renderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        renderer.pos(box.minX, box.minY, box.minZ).endVertex();
        renderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        renderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        renderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        renderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        renderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        renderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        renderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        renderer.pos(box.minX, box.minY, box.minZ).endVertex();
        renderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        renderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        renderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        renderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        renderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        renderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        renderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        tessellator.draw();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
	
    public static void drawCircle(float x, float y, float radius, float lineWidth, int color) {
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(red, green, blue, alpha);
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for(int i = 0; i <= 32; i++) {
            GL11.glVertex2d(x + sinValues2[i] * radius, y + cosValues2[i] * radius);
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawFilledCircle(float x, float y, float radius, int color) {
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(red, green, blue, alpha);
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2d(x, y);
        for(int i = 0; i <= 32; i++) {
            GL11.glVertex2d(x + sinValues2[i] * radius, y + cosValues2[i] * radius);
        }
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawGlow(int x, int y, int width, int height) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(GLOW_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(770, 771);
        GL11.glColor4f(0.2F, 0.2F, 0.2F, 1F);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
    }
    
    public static void drawProgressBar(double progress, int color1, int color2, int background) {
    	drawRoundedRectangle(mc.displayWidth / 4 - 80, mc.displayHeight / 4 + 20, mc.displayWidth / 4 + 80, mc.displayHeight / 4 + 33, 10, background);
		drawRoundedGradientRect(mc.displayWidth / 4 - 80, mc.displayHeight / 4 + 20, (float) (mc.displayWidth / 4 - 70 + (progress * 150)), mc.displayHeight / 4 + 33, 10, color1, color1, color2, color2);
    }
    
    public static void drawCheckmark(int x, int y, int size, int color) {
        float scale = size / 20.0F;
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(2.0F);

        GL11.glColor4f(
            ((color >> 16) & 0xFF) / 255.0F,
            ((color >> 8) & 0xFF) / 255.0F,
            (color & 0xFF) / 255.0F,
            ((color >> 24) & 0xFF) / 255.0F
        );
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2f(x, y + 10 * scale);
        GL11.glVertex2f(x + 5 * scale, y + 15 * scale);
        GL11.glVertex2f(x + 15 * scale, y + 5 * scale);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
    
    public static double[] worldToScreen(double x, double y, double z, int scale) {
        boolean result = org.lwjgl.util.glu.Project.gluProject((float) x,(float) y,(float) z,ActiveRenderInfo.MODELVIEW,ActiveRenderInfo.PROJECTION,ActiveRenderInfo.VIEWPORT,SCREEN_COORDS);
        if (!result)
            return null;
        float screenZ = SCREEN_COORDS.get(2);
        if (screenZ < 0.0F || screenZ > 1.0F)
            return null;
        float screenX = SCREEN_COORDS.get(0);
        float screenY = SCREEN_COORDS.get(1);
        RESULT[0] = screenX / scale;
        RESULT[1] = (mc.displayHeight / (double) scale) - (screenY / scale);
        return RESULT;
    }

}
