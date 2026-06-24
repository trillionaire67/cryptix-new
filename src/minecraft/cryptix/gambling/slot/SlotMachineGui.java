package cryptix.gambling.slot;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;

import cryptix.utils.render.RenderUtils;

public class SlotMachineGui extends GuiScreen {
	private long lastTime;
    private final SlotMachine machine = new SlotMachine();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        long curr = System.currentTimeMillis();
        if(curr - lastTime > 20) {
        	machine.update();
        	lastTime = curr;
        }
        drawCenteredString(this.fontRendererObj, "Slot Machine", this.width / 2, 40, 0xFFFFFF);
        SlotReel[] reels = machine.getReels();
        for (int i = 0; i < reels.length; i++) {
            float x = this.width / 2 - 60 + (i * 40);
            float y = this.height / 2;

            renderReel(reels[i], x, y);
        }

        if (machine.isFinished()) {
            drawCenteredString(this.fontRendererObj,
                "Won: " + machine.getPayout(),
                this.width / 2,
                this.height / 2 + 120,
                0x00FF00
            );
        } else {
            drawCenteredString(this.fontRendererObj,
                "Spinning...",
                this.width / 2,
                this.height / 2 + 120,
                0xAAAAAA
            );
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void renderReel(SlotReel reel, float x, float y) {

        float offset = reel.getPosition() % 32;

        int baseIndex = reel.getBaseIndex();

        for (int i = -2; i <= 3; i++) {
            SlotSymbol symbol = reel.getSymbolAt(baseIndex + i);

            float drawY = y + (i * 32) - offset;

            drawSymbol(symbol, x, drawY);
        }
    }

    private void drawSymbol(SlotSymbol symbol, float x, float y) {
        ItemStack stack;

        switch (symbol) {
            case DIAMOND:
                stack = new ItemStack(Items.diamond);
                break;
            case EMERALD:
                stack = new ItemStack(Items.emerald);
                break;
            case GOLD:
                stack = new ItemStack(Items.gold_ingot);
                break;
            case IRON:
                stack = new ItemStack(Items.iron_ingot);
                break;
            case COAL:
            default:
                stack = new ItemStack(Items.coal);
                break;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, (int)x, (int)y);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        if (machine.isFinished()) {
            machine.spin();
            mc.thePlayer.playSound("random.click", 1.0F, 1.0F);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}