package cryptix.module.visual;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import cryptix.Client;
import cryptix.font.CustomFontRenderer;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.BooleanSetting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.gui.clickgui.settings.ModeSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.other.Compare;
import cryptix.script.Script;
import cryptix.utils.RenderCache;
import cryptix.utils.Utils;
import cryptix.utils.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class HUD extends Module {
	public boolean needsSort;
    private Comparator<Module> comparator;
    public ArrayList<Module> mods2 = new ArrayList<>();
    private ArrayList<Script> scripts = new ArrayList<>();
    private long lastServerTime, lastUpdateTime;
    private ResourceLocation cryptixLogo;
    private String currentServer;
    private FontRenderer fr = mc.fontRendererObj;
    private CustomFontRenderer cachedFontRenderer;
    private String cachedFontName;
    public DoubleSetting color1red = new DoubleSetting("Color1 red", this, 255, 0, 255, false);
    public DoubleSetting color1green = new DoubleSetting("Color1 green", this, 255, 0, 255, false);
    public DoubleSetting color1blue = new DoubleSetting("Color1 blue", this, 255, 0, 255, false);
    public DoubleSetting color2red = new DoubleSetting("Color2 red", this, 255, 0, 255, false);
    public DoubleSetting color2green = new DoubleSetting("Color2 green", this, 255, 0, 255, false);
    public DoubleSetting color2blue = new DoubleSetting("Color2 blue", this, 255, 0, 255, false);
    private BooleanSetting dynamicIsland = new BooleanSetting("Dynamic Island", this, false);
    private ModeSetting outline = new ModeSetting("Outline", this, "None", Arrays.asList("None", "Right", "Left"));
    public ModeSetting font = new ModeSetting("Font", this, "Minecraft", Arrays.asList("Minecraft", "Apple", "Arial", "Product Sans"));
    private BooleanSetting watermark = new BooleanSetting("Watermark", this, false);
    private BooleanSetting animation = new BooleanSetting("Animation", this, true);
    private BooleanSetting background = new BooleanSetting("Background", this, false);
    public BooleanSetting lowercase = new BooleanSetting("Lowercase", this, false);
    private BooleanSetting removeVisuals = new BooleanSetting("Remove Visuals", this, false);
    private BooleanSetting removeScripts = new BooleanSetting("Remove Scripts", this, false);
    private BooleanSetting removeIP = new BooleanSetting("Hide IP", this, false);
    public BooleanSetting hideBoss = new BooleanSetting("Hide Boss", this, false);
    private DoubleSetting xOffset = new DoubleSetting("X Offset", this, 10, 0, 10, 1);
    private DoubleSetting offset = new DoubleSetting("Y Offset", this, 10, 0, 50, 1);
    private float islandWidth, islandHeight;
    private int cachedBlockCount = -1;
    private long blockCountCacheTime = 0;
    private int color1 = 0x6E151515;
    private int color2 = 0xC8282828;
    private int color3 = 0xC800FF00;
    private int color4 = 0xC8FFFF00;
    private int color5 = 0xC8FF0000;
    private int color6 = 0x3C000000;
    public HUD() {
        super("HUD", 0, Category.VISUAL);
        this.addSetting(color1red,color1green,color1blue,color2red,color2green,color2blue,dynamicIsland,outline,font,watermark,animation,background,lowercase,removeVisuals,removeScripts,removeIP,hideBoss,xOffset,offset);
        try {
            InputStream inputStream = getClass().getResourceAsStream("/assets/minecraft/cryptix/cryptixlogo.png");
            if (inputStream != null) {
                BufferedImage image = ImageIO.read(inputStream);
                DynamicTexture dynamicTexture = new DynamicTexture(image);
                cryptixLogo = mc.getTextureManager().getDynamicTextureLocation("cryptixlogo", dynamicTexture);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        comparator = new Compare().reversed();
        cachedFontName = font.getString().toLowerCase();
    }
    
    @Override
    public void onRender2D() {
        final ScaledResolution sr = RenderCache.getScaledResolution();
        final int screenWidth = sr.getScaledWidth();
        final long time = System.currentTimeMillis();

        int yOffset = 0;
        final boolean lower = lowercase.getBoolean();
        final boolean drawBg = background.getBoolean();
        final boolean animate = animation.getBoolean();
        final boolean removeVis = removeVisuals.getBoolean();
        final boolean removeScr = removeScripts.getBoolean();
        final double xOffsetVal = xOffset.getValue() * 10;
        final double yOffsetBase = offset.getValue() * 10;
        final int boxHeight = 11;
        final String outlineMode = outline.getString();

        final String fontName = font.getString().toLowerCase();
        final boolean isMinecraft = fontName.equals("minecraft");
        
        if (!fontName.equals(cachedFontName)) {
            cachedFontName = fontName;
            cachedFontRenderer =
                fontName.equals("apple") ? Client.instance.apple :
                fontName.equals("arial") ? Client.instance.arial :
                fontName.equals("product sans") ? Client.instance.sans :
                null;
        }
        final CustomFontRenderer fontRenderer = cachedFontRenderer;
        if (watermark.getBoolean()) {
            if (isMinecraft) {
                fr.drawStringWithShadow("C", 5, 5, getColorInt(0, 1.0f));
                fr.drawStringWithShadow("ryptix", 5 + fr.getStringWidth("C"), 5, -1);
            } else {
                fontRenderer.drawStringWithShadow("C", 5, 5, getColorInt(0, 1.0f));
                fontRenderer.drawStringWithShadow("ryptix", 5 + fontRenderer.getStringWidth("C"), 5, -1);
            }
        }
        if (dynamicIsland.getBoolean()) {
            if (time - lastServerTime > 5000) {
                updateServerInfo();
                lastServerTime = time;
            }
            if (Client.instance.moduleManager.scaffold.isToggled()) {
                renderScaffoldInfo(sr, time, fontRenderer);
            } else if (Client.instance.moduleManager.bedNuker.bedPos != null) {
                renderBreakingInfo(sr, time, fontRenderer);
            } else {
                renderDynamicIsland(sr, time, fontRenderer);
            }
        }
        if(needsSort) {
        	Collections.sort(mods2, comparator);
        	needsSort = false;
        }
        
        final boolean useCustomFont = fontRenderer != null;
        final boolean drawOutline = !"None".equalsIgnoreCase(outlineMode);
        final boolean outlineLeft = "Left".equalsIgnoreCase(outlineMode);
        
        for (Module m : mods2) {
            if (removeVis) {
                String cat = m.getCategory().toString();
                if (cat.equalsIgnoreCase("Visual") || cat.equalsIgnoreCase("Config")) continue;
            }
            String name = lower ? m.getDisplayNameLower() : m.getDisplayName();
            float textWidth = (float) (useCustomFont ? fontRenderer.getStringWidth(name) + 2f : fr.getStringWidth(name) + 2f);
            int boxWidth = (int) textWidth + 8;
            int yPos = (int) (yOffset * boxHeight + 1 + yOffsetBase);
            int xPos = (int) (screenWidth - boxWidth - xOffsetVal);
            float alpha = animate ? (float) Math.min(textWidth, (time - m.getToggleTimestamp()) * 0.5f) : 0f;
            int color = getColorInt(yOffset * 200_000_000L, 1.0f);
            int animatedTextX = animate ? (int) (xPos + 7 - alpha + textWidth) : xPos + 7;
            int animatedBgX = animate ? (int) (xPos + 5 - alpha + textWidth) : xPos + 5;
            if (drawBg) Gui.drawRect(animatedBgX, yPos - 1, xPos + boxWidth, yPos + boxHeight - 1, 0x80000000);
            if (drawOutline) {
                if (outlineLeft) {
                    int leftX = animate ? (int) (xPos + 4 - alpha + textWidth - 1) : xPos + 3;
                    Gui.drawRect(leftX, yPos - 1, leftX + 2, yPos + boxHeight - 1, color);
                } else {
                    int rightX = (int) (screenWidth - xOffsetVal);
                    Gui.drawRect(rightX - 1, yPos - 1, rightX, yPos + boxHeight - 1, color);
                }
            }
            if (isMinecraft) {
                fr.drawStringWithShadow(name, animatedTextX, yPos + 1, color);
            } else {
                fontRenderer.drawString(name, animatedTextX, yPos + 1, color, false, 8.3f);
            }
            yOffset++;
        }
        for (Script s : scripts) {
            if (!s.isEnabled() || removeScr) continue;
            String displayName = s.getName();
            String name = lower ? displayName.toLowerCase() : displayName;
            float textWidth = (float) (useCustomFont ? fontRenderer.getStringWidth(name) + 2f : fr.getStringWidth(name) + 2f);
            int boxWidth = (int) textWidth + 8;
            int yPos = (int) (yOffset * boxHeight + 1 + yOffsetBase);
            int xPos = (int) (screenWidth - boxWidth - xOffsetVal);
            float alpha = animate ? (float) Math.min(textWidth, (time - 1) * 0.5f) : 0f;
            int color = getColorInt(yOffset * 200_000_000L, 1.0f);
            int animatedTextX = animate ? (int) (xPos + 7 - alpha + textWidth) : xPos + 7;
            int animatedBgX = animate ? (int) (xPos + 5 - alpha + textWidth) : xPos + 5;
            if (drawBg) Gui.drawRect(animatedBgX, yPos - 1, xPos + boxWidth, yPos + boxHeight - 1, 0x80000000);
            if (isMinecraft) {
                fr.drawStringWithShadow(name, animatedTextX, yPos + 1, color);
            } else {
                fontRenderer.drawString(name, animatedTextX, yPos + 1, color, false, 8.3f);
            }
            yOffset++;
        }
        GlStateManager.disableBlend();
    }
    
    private void updateServerInfo() {
        currentServer = mc.getCurrentServerData() != null ? mc.getCurrentServerData().serverIP : "Singleplayer";
        if (mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP.toLowerCase().contains("liquidproxy.net")) {
            currentServer = "hidden.liquidproxy.net";
        }
        if (removeIP.getBoolean()) {
            currentServer = "----------";
        }
    }
    
    public int getColorInt(long offset, float fade) {
        float time = (Client.frameTime + offset) / 2.0E9f;
        float speed = 5.1f;
        float t = 0.5f * (1.0f + MathHelper.sin(speed * time));
        int red = (int)((1.0f - t) * color1red.getValue() + t * color2red.getValue());
        int green = (int)((1.0f - t) * color1green.getValue() + t * color2green.getValue());
        int blue = (int)((1.0f - t) * color1blue.getValue() + t * color2blue.getValue());
        red = Math.min(255, Math.max(0, (int)(red * fade)));
        green = Math.min(255, Math.max(0, (int)(green * fade)));
        blue = Math.min(255, Math.max(0, (int)(blue * fade)));
        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }
    
    private void renderDynamicIsland(ScaledResolution sr, long currentTime, CustomFontRenderer renderer) {
        // credit: chatgpt
        int centerX = sr.getScaledWidth() / 2;
        int yPos = 5;
        String cryptixText = Client.name;
        String serverText = currentServer;
        String fullServerText = " | " + serverText + " | " + Client.version;
        
        final boolean useCustomFont = renderer != null;
        int cryptixWidth = useCustomFont ? (int) renderer.getStringWidth(cryptixText) : fr.getStringWidth(cryptixText);
        int smallTextWidth = useCustomFont ? (int) renderer.getStringWidth(fullServerText) : fr.getStringWidth(fullServerText);
        
        int totalWidth = smallTextWidth + 65;
        int cornerRadius = 21;
        int height = 24;
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;
        float smoothTime = 0.15f;
        float alpha = 1.0f - (float)Math.exp(-deltaTime / smoothTime);
        islandHeight = Utils.lerp(islandHeight, height, alpha);
        islandWidth = Utils.lerp(islandWidth, totalWidth, alpha);
        int startX = centerX - totalWidth / 2;
        int endX = startX + totalWidth;
        RenderUtils.drawRoundedRectangle(centerX - islandWidth / 2,yPos,startX + islandWidth,yPos + islandHeight,cornerRadius,color1);
        GL11.glColor3f(1, 1, 1);
        int logoSize = 28;
        int logoX = startX + 5;
        int logoY = yPos + (height - logoSize)/2;
        if (cryptixLogo != null) {
            mc.getTextureManager().bindTexture(cryptixLogo);
            GlStateManager.enableBlend();
            float newLogoX = (float) (logoX - 6);
            float newLogoY = (float) (logoY);
            Gui.drawModalRectWithCustomSizedTexture((int) newLogoX,(int) newLogoY,0, 0,logoSize, logoSize,logoSize, logoSize);
            GlStateManager.disableBlend();
        }
        int textX = logoX + 20;
        int textY = yPos + (height - 8)/2 + 1;
        int cryptixColor = getColorInt(0, 1.0f);
        if(useCustomFont) {
        	CustomFontRenderer.startString();
            renderer.drawStringNoGL(cryptixText, textX, textY, cryptixColor,false,8.3f);
            renderer.drawStringNoGL(fullServerText,
                textX + cryptixWidth,
                textY,
                0xAAAAAA,false,8.3f
            );
            CustomFontRenderer.stopString();
        } else {
        	fr.drawString(cryptixText, textX, textY, cryptixColor);
            fr.drawString(fullServerText,
                textX + cryptixWidth,
                textY,
                0xAAAAAA
            );
        }
    }
    
    private void renderBreakingInfo(ScaledResolution sr, long currentTime, CustomFontRenderer renderer) {
        int centerX = sr.getScaledWidth() / 2;
        int yPos = 5;
        int blockCount = getHotbarBlockCount();
        int maxTextWidth = 50;
        int totalWidth = maxTextWidth + 41;
        int cornerRadius = 15;
        int height = 32;
        if (currentTime - lastUpdateTime > 5) {
            islandHeight = Utils.lerp(islandHeight, 32F, 0.08F);
            islandWidth = Utils.lerp(islandWidth, totalWidth, 0.08F);
            lastUpdateTime = currentTime;
        }
        int startX = centerX - totalWidth / 2;
        int endX = startX + totalWidth;
        RenderUtils.startRoundedRectangle();
        RenderUtils.drawRoundedRectangleNoRender(
            centerX - islandWidth / 2,
            yPos,
            startX + islandWidth,
            yPos + islandHeight,
            cornerRadius,
            color1
        );
        int theme = (255 << 24) | ((int) color1red.getValue() << 16) | ((int) color1green.getValue() << 8) | ((int) color1blue.getValue());
        int theme1 = (255 << 24) | ((int) color2red.getValue() << 16) | ((int) color2green.getValue() << 8) | ((int) color2blue.getValue());
        RenderUtils.drawRoundedRectangleNoRender(centerX - islandWidth / 2 + 4,yPos + 20,(float) (centerX - islandWidth / 2 + 87),yPos + 28,9 - 1,color6);
        RenderUtils.stopRoundedRectangle();
        RenderUtils.drawRoundedGradientRect(centerX - islandWidth / 2 + 4,yPos + 20,(float) (centerX - islandWidth / 2 + 15 + Client.instance.moduleManager.bedNuker.smoothProgress * 74),yPos + 28,9 - 1,theme,theme,theme1,theme1);
        Block block = mc.theWorld.getBlockState(Client.instance.moduleManager.bedNuker.surroundingPos != null ? Client.instance.moduleManager.bedNuker.surroundingPos : Client.instance.moduleManager.bedNuker.bedPos).getBlock();
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glColor4d(color1red.getValue() / 255.1f, color1green.getValue() / 255.1f, color1blue.getValue() / 255.1f, 1f);
        
        final boolean useCustomFont = renderer != null;
        String breakText = "Breaking: " + (int) (Client.instance.moduleManager.bedNuker.breakProgress * 100) + "%";
        if(useCustomFont) {
        	GlStateManager.disableBlend();
            renderer.drawString(breakText, centerX - islandWidth / 2 + 5, yPos + 5, getColorInt(0, 1f));
        } else {
        	fr.drawString(breakText, centerX - islandWidth / 2 + 5, yPos + 5, -1);
        }
        GL11.glColor4f(1f, 1f, 1f, 1f);
        RenderHelper.disableStandardItemLighting();
    }
    
    private void renderScaffoldInfo(ScaledResolution sr, long currentTime, CustomFontRenderer renderer) {
        int centerX = sr.getScaledWidth() / 2;
        int yPos = 5;
        String scaffoldText = "Scaffolding";
        String blockName = "None";
        int blockCount = getHotbarBlockCount();
        if (blockCount > 0 && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
            blockName = mc.thePlayer.getHeldItem().getDisplayName();
        }
        int maxStackSize = 64;
        String countText = blockCount + "/" + maxStackSize;
        
        final boolean useCustomFont = renderer != null;
        int scaffoldWidth = useCustomFont ? (int) renderer.getStringWidth(scaffoldText) : fr.getStringWidth(scaffoldText);
        int blockNameWidth = useCustomFont ? (int) renderer.getStringWidth(blockName) : fr.getStringWidth(blockName);
        int blockCountWidth = useCustomFont ? (int) renderer.getStringWidth(countText) : fr.getStringWidth(countText);
        
        int maxTextWidth = Math.max(scaffoldWidth, Math.max(blockNameWidth, blockCountWidth));
        int totalWidth = maxTextWidth + 41;
        int cornerRadius = 25;
        int height = 32;
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;
        float smoothTime = 0.15f;
        float alpha = 1.0f - (float)Math.exp(-deltaTime / smoothTime);
        islandHeight = Utils.lerp(islandHeight, 32F, alpha);
        islandWidth = Utils.lerp(islandWidth, totalWidth, alpha);
        int startX = centerX - totalWidth / 2;
        int endX = startX + totalWidth;
        RenderUtils.drawRoundedRectangle(centerX - islandWidth / 2,yPos,startX + islandWidth,yPos + islandHeight,cornerRadius,color1);
        int circleSize = 26;
        int circleX = startX + 4;
        int circleY = yPos + (height - circleSize)/2;
        RenderUtils.drawFilledCircle(circleX + circleSize/2,circleY + circleSize/2,circleSize/2,color2);
        float fillPercentage = (float) blockCount / maxStackSize;
        int borderColor;
        if (fillPercentage > 0.5f) {
            borderColor = color3;
        } else if (fillPercentage > 0.25f) {
            borderColor = color4;
        } else {
            borderColor = color5;
        }
        RenderUtils.drawCircle(circleX + circleSize/2,circleY + circleSize/2,circleSize/2,2,borderColor);
        String countOnlyText = blockCount + "";
        int countTextWidth = useCustomFont ? (int) renderer.getStringWidth(countOnlyText) : fr.getStringWidth(countOnlyText);
        int color = getColorInt(0, 1.0f);
        int textX = circleX + circleSize + 5;
        int textY = yPos + (height - 25)/1;
        if(useCustomFont) {
            CustomFontRenderer.startString();
        	renderer.drawStringNoGL(countOnlyText,circleX + (circleSize - countTextWidth)/2,circleY + (circleSize - 16)/1,color,false,8.3f);
        	renderer.drawStringNoGL(scaffoldText, textX, textY, color,false,8.3f);
        	renderer.drawStringNoGL(blockName, textX, textY + 12, 0xAAAAAA,false,8.3f);
            CustomFontRenderer.stopString();
        } else {
        	fr.drawStringWithShadow(countOnlyText,circleX + (circleSize - countTextWidth)/2,circleY + (circleSize - 16)/1,color);
            fr.drawString(scaffoldText, textX, textY, color);
            fr.drawString(blockName, textX, textY + 12, 0xAAAAAA);
        }
    }
    
    public void updateMods() {
        scripts.clear();
        for (Script script : Client.instance.scriptManager.getScripts()) {
            if (!script.isEnabled()) continue;
            scripts.add(script);
        }
    }
    
    private int getHotbarBlockCount() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - blockCountCacheTime < 100 && cachedBlockCount >= 0) {
            return cachedBlockCount;
        }
        
        int blockCount = 0;
        int i = 0;
        while (i < 9) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) itemStack.getItem()).getBlock();
                if (block.isFullBlock()) {
                    blockCount += itemStack.stackSize;
                }
            }
            i++;
        }
        
        cachedBlockCount = blockCount;
        blockCountCacheTime = currentTime;
        return blockCount;
    }
}