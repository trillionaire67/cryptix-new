package cryptix.module.player;

import org.lwjgl.input.Mouse;

import cryptix.module.Category;
import cryptix.module.Module;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

public class AutoTool extends Module {
	private int finalSlot;
	private boolean swapped;

    public AutoTool() {
        super("AutoTool", 0, Category.PLAYER);
    }
    
    @Override
    public void onEnable() {
    	finalSlot = -1;
    }

    @Override
    public void onPreUpdate() {
    	if(finalSlot != -1){
        	mc.thePlayer.inventory.currentItem = finalSlot;
        	finalSlot = -1;
        }
        if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || !Mouse.isButtonDown(0) || mc.currentScreen != null || mc.pointedEntity instanceof EntityLivingBase) return;

        BlockPos pos = mc.objectMouseOver.getBlockPos();
        Block block = mc.theWorld.getBlockState(pos).getBlock();

        int bestSlot = -1;
        float bestStrength = 1.0f;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null) continue;

            float strength = stack.getStrVsBlock(block);
            if (strength > bestStrength) {
                bestStrength = strength;
                bestSlot = i;
            }
        }

        if (bestSlot != -1) {
        	if(finalSlot == -1) {
        		finalSlot = mc.thePlayer.inventory.currentItem;
        	}
            mc.thePlayer.inventory.currentItem = bestSlot;
        }else if(finalSlot != -1){
        	mc.thePlayer.inventory.currentItem = finalSlot;
        	finalSlot = -1;
        }
    }
}