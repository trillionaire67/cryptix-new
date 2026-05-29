package cryptix.font;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.texture.DynamicTexture;

public class FontUtil {

    private static final int CHAR_COUNT = 256;
    private static final float IMAGE_SIZE = 512.0f;

    protected final CharData[] charData = new CharData[CHAR_COUNT];
    protected Font font;
    protected boolean antiAlias;
    protected boolean fractionalMetrics;
    protected int fontHeight = -1;
	protected int charOffset = 0;

    protected DynamicTexture texture;

    public FontUtil(Font font, boolean antiAlias, boolean fractionalMetrics) {
        this.font = font;
        this.antiAlias = antiAlias;
        this.fractionalMetrics = fractionalMetrics;
        this.texture = this.setupTexture(font, antiAlias, fractionalMetrics, this.charData);
    }

    protected DynamicTexture setupTexture(Font font, boolean antiAlias, boolean fractionalMetrics, CharData[] chars) {
        BufferedImage img = this.generateFontImage(font, antiAlias, fractionalMetrics, chars);

        try {
            return new DynamicTexture(img);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected BufferedImage generateFontImage(Font font, boolean antiAlias, boolean fractionalMetrics, CharData[] chars) {
        int imgSize = (int) this.IMAGE_SIZE;
        BufferedImage bufferedImage = new BufferedImage(imgSize, imgSize, 2);
        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
        graphics.setFont(font);
        graphics.setColor(new Color(255, 255, 255, 0));
        graphics.fillRect(0, 0, imgSize, imgSize);
        graphics.setColor(Color.WHITE);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int charHeight = 0;
        int positionX = 0;
        int positionY = 1;
        int index = 0;

        while (index < chars.length) {
            char c = (char) index;
            CharData charData = new CharData();
            Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(c), graphics);
            charData.width = dimensions.getBounds().width + 8;
            charData.height = dimensions.getBounds().height;

            if (positionX + charData.width >= imgSize) {
                positionX = 0;
                positionY += charHeight;
                charHeight = 0;
            }

            if (charData.height > charHeight)
                charHeight = charData.height;

            charData.storedX = positionX;
            charData.storedY = positionY;

            if (charData.height > this.fontHeight)
                this.fontHeight = charData.height;

            chars[index] = charData;
            graphics.drawString(String.valueOf(c), positionX + 2, positionY + fontMetrics.getAscent());
            positionX += charData.width;
            ++index;
        }

        return bufferedImage;
    }

    public void drawChar(CharData[] chars, char c, float x, float y) throws ArrayIndexOutOfBoundsException {
        try {
            this.drawQuad(x, y, chars[c].width, chars[c].height, chars[c].storedX, chars[c].storedY, chars[c].width, chars[c].height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawQuad(float x, float y, float width, float height,
                          float srcX, float srcY, float srcWidth, float srcHeight) {
    	float inv = 1.0f / IMAGE_SIZE;
        float u = srcX * inv;
        float v = srcY * inv;
        float u2 = (srcX + srcWidth) * inv;
        float v2 = (srcY + srcHeight) * inv;
        float x2 = x + width;
        float y2 = y + height;
        GL11.glTexCoord2f(u2, v);
        GL11.glVertex2f(x2, y);
        GL11.glTexCoord2f(u, v);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(u, v2);
        GL11.glVertex2f(x, y2);
        GL11.glTexCoord2f(u, v2);
        GL11.glVertex2f(x, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v);
        GL11.glVertex2f(x2, y);
    }

    public void setAntiAlias(boolean antiAlias) {
        if (this.antiAlias != antiAlias) {
            this.antiAlias = antiAlias;
            this.texture = this.setupTexture(this.font, antiAlias, this.fractionalMetrics, this.charData);
        }
    }

    public void setFractionalMetrics(boolean fractionalMetrics) {
        if (this.fractionalMetrics != fractionalMetrics) {
            this.fractionalMetrics = fractionalMetrics;
            this.texture = this.setupTexture(this.font, this.antiAlias, fractionalMetrics, this.charData);
        }
    }

    public void setFont(Font font) {
        this.font = font;
        this.texture = this.setupTexture(font, this.antiAlias, this.fractionalMetrics, this.charData);
    }

    public boolean isFractionalMetrics() {
        return fractionalMetrics;
    }

    public boolean isAntiAlias() {
        return antiAlias;
    }

    public int getFontHeight() {
        return fontHeight;
    }

    static class CharData {
        public int width;
        public int height;
        public int storedX;
        public int storedY;
    }
}