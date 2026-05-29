package cryptix.other;

import cryptix.Client;
import cryptix.module.player.Scaffold;
import cryptix.utils.Utils;
import cryptix.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class ScaffoldBlockCount {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Scaffold scaffold;
    private long alphaTimer;
    private int alpha;
    private final cryptix.module.visual.HUD hud;

    public ScaffoldBlockCount(Scaffold scaffold) {
        this.scaffold = scaffold;
        this.hud = Client.instance.moduleManager.hud;
    }
    
    public void onUpdate() {
    	updateAlpha();
    }

    public void onRender() {
        if (alpha > 15) {
            drawBlockCount();
        }
    }

    private void updateAlpha() {
        boolean toggled = Client.instance.moduleManager.scaffold.isToggled();
        float targetAlpha = toggled ? 180f : 0f;
        alpha = (int) Utils.lerp(alpha, targetAlpha, 0.5f);
        alpha = clamp255(alpha);
    }

    private void drawBlockCount() {
        GlStateManager.enableBlend();
        int hotbarCount = scaffold.getHotbarBlockCount();
        int hudColor = hud.getColorInt(0, 1.0f);
        int alpha255 = alpha;
        int colorWhite = (alpha255 << 24) | 0x00FFFFFF;
        int colorHud = (alpha255 << 24) | (hudColor & 0x00FFFFFF);
        int x = mc.displayWidth / 4 + 10;
        int y = mc.displayHeight / 4 + 10;
        String prefix = "Blocks: ";
        if (scaffold.count.getString().equalsIgnoreCase("Simple")) {
            if (hotbarCount < 10) {
                mc.fontRendererObj.drawStringWithShadow(prefix + "§c" + hotbarCount,x, y,alpha > 150 ? -1 : colorWhite);
            } else {
                mc.fontRendererObj.drawStringWithShadow(prefix + hotbarCount,x, y,alpha > 150 ? -1 : colorWhite);
            }
        } else if (scaffold.count.getString().equalsIgnoreCase("Rise")) {
            ItemStack heldItem = mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem);
            int rx = mc.displayWidth / 4 - 18;
            int ry = mc.displayHeight / 2 - 80;
            if (heldItem != null) {
                GlStateManager.pushMatrix();
                GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);
                RenderHelper.enableStandardItemLighting();
                GlStateManager.popMatrix();
                mc.getRenderItem().renderItemIntoGUI(heldItem, rx - 20, ry - 3);
                RenderHelper.disableStandardItemLighting();
            }
            int bgAlpha = alpha / 2;
            int bgColor = (bgAlpha << 24);
            RenderUtils.drawRoundedRectangle(rx - 22,ry - 6,rx + (10 + Client.instance.sans.getStringWidth("Amount: ") + Client.instance.sans.getStringWidth(String.valueOf(hotbarCount)) - 6),ry + 15,5,bgColor);
            GlStateManager.disableBlend();
            Client.instance.sans.drawString("Amount: ",rx - 2,ry + 1,alpha > 150 ? -1 : colorWhite);
            Client.instance.sans.drawString(String.valueOf(hotbarCount),rx + Client.instance.sans.getStringWidth("Amount: ") - 2,ry + 1,colorHud);
        }
    }

    private int clamp255(int v) {
        return v < 0 ? 0 : (v > 255 ? 255 : v);
    }
}