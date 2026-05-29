package cryptix.module.player;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class InvManager extends Module{
	private Setting swordSlot, blockSlot, delay, startDelay, gappleSlot, dropTrash, dropTools, randomDelay, swor;
	private int ticks, startTicks;
	public InvManager() {
		super("InvManager", 0, Category.PLAYER);
		Client.instance.settingsManager.addSetting(startDelay = new Setting("Start Delay", this, 1, 0, 10, true)); 
		Client.instance.settingsManager.addSetting(delay = new Setting("Delay", this, 1, 0, 10, true)); 
		Client.instance.settingsManager.addSetting(randomDelay = new Setting("Random Delay", this, true)); 
		Client.instance.settingsManager.addSetting(swordSlot = new Setting("Sword Slot", this, 1, 1, 9, true)); 
		Client.instance.settingsManager.addSetting(blockSlot = new Setting("Block Slot", this, 2, 1, 9, true)); 
		Client.instance.settingsManager.addSetting(gappleSlot = new Setting("Gapple Slot", this, 3, 1, 9, true)); 
		Client.instance.settingsManager.addSetting(dropTrash = new Setting("Drop Trash", this, true));
		Client.instance.settingsManager.addSetting(dropTools = new Setting("Drop Tools", this, false));
		Client.instance.settingsManager.addSetting(swor = new Setting("Ignore Low Durability Sword", this, false));
	}
	
	
	@Override
    public void onPreMotion() {
		if (mc.currentScreen instanceof GuiInventory) {
			startTicks++;
			if(startTicks < startDelay.getValue()) return;
			ticks++;
			if(ticks < delay.getValue()) return;
			if(Math.random() > 0.8 && randomDelay.getBoolean()) return;
			if (!mc.thePlayer.inventoryContainer.getSlot(36).getHasStack()) {
	            this.getSword((int) swordSlot.getValue());
	        } else if (!this.isBestSword(mc.thePlayer.inventoryContainer.getSlot(36).getStack())) {
	            this.getSword((int) swordSlot.getValue());
	        }
			this.getBestArmor();
			dropLeftSword();
			dropTrash();
			dropTools();
			this.getBestGap();
			this.getBestBlock((int) blockSlot.getValue());
		}else {
			startTicks = 0;
		}
    }

	public void swap(int slot1, int slot2) {
		if(ticks >= delay.getValue()) {
			mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, slot2, 2, mc.thePlayer);
			ticks = 0;
		}
    }

    private int getSwordDamage(ItemStack sword) {
        return (int) ((ItemSword) sword.getItem()).getDamageVsEntity();
    }
    
    private void shiftClick(int slot) {
    	if(ticks >= delay.getValue()) {
    		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
    		ticks = 0;
    	}
    }

    private boolean isBlock(ItemStack item) {
    	if(item != null && item.getItem() instanceof ItemBlock) {
    		Block block = ((ItemBlock)item.getItem()).getBlock();
    		if(block.isFullBlock() && block != Blocks.sand && block != Blocks.gravel) {
    			return true;
    		}
    	}
        return false;
    }
    
    private void dropTrash() {
    	if(!dropTrash.getBoolean()) return;
    	ItemStack is;
    	int i = 9;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && this.isTrash(is = mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
                this.drop(i);
            }
            ++i;
        }
    }
    
    private void dropTools() {
    	if(!dropTools.getBoolean()) return;
    	ItemStack is;
    	int i = 9;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && this.isTool(is = mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
                this.drop(i);
            }
            ++i;
        }
    }
    
    private boolean isTrash(ItemStack stack) {
    	return stack.getUnlocalizedName().contains("diamond") || stack.getUnlocalizedName().contains("web") || stack.getUnlocalizedName().contains("work") || stack.getUnlocalizedName().contains("blaze") || stack.getUnlocalizedName().contains("chest") || stack.getUnlocalizedName().contains("seeds") || stack.getUnlocalizedName().contains("tnt") || stack.getUnlocalizedName().contains("cobblestone") || stack.getUnlocalizedName().contains("spawn") || stack.getUnlocalizedName().contains("cobweb") || stack.getUnlocalizedName().contains("piston") || stack.getUnlocalizedName().contains("redstone") || stack.getUnlocalizedName().contains("egg") || stack.getUnlocalizedName().contains("flint") || stack.getUnlocalizedName().contains("snow") || stack.getUnlocalizedName().contains("fish") || stack.getUnlocalizedName().contains("string") || stack.getUnlocalizedName().contains("stick") || stack.getUnlocalizedName().contains("bucket") || stack.getItem() instanceof ItemExpBottle;
    }
    
    private boolean isTool(ItemStack stack) {
    	return stack.getUnlocalizedName().contains("shovel") || stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemPickaxe;
    }
    
    private void getBestGap() {
        int selectedSlot = (int)gappleSlot.getValue();
        int i = 9;
        while (i < 45) {
            ItemStack is;
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && (is = mc.thePlayer.inventoryContainer.getSlot(i).getStack()).getItem() instanceof ItemAppleGold && !mc.thePlayer.inventoryContainer.getSlot(selectedSlot - 1).getHasStack()) {
                this.swap(i, selectedSlot - 1);
                break;
            }
            ++i;
        }
    }
    
    private int getArmorProtection(ItemArmor armor) {
        return armor.getArmorMaterial().getDamageReductionAmount(armor.armorType);
    }
    
    private boolean isBestSword(ItemStack stack) {
        float damage = this.getDamage(stack);
        int i = 9;
        while (i < 45) {
            ItemStack is;
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && this.getDamage(is = mc.thePlayer.inventoryContainer.getSlot(i).getStack()) > damage && is.getItem() instanceof ItemSword) {
                return false;
            }
            ++i;
        }
        return stack.getItem() instanceof ItemSword;
    }
    
    private void getSword(int slot) {
        ItemStack bestSword = null;
        int bestSwordSlot = -1;

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSword && isBestSword(is) && this.hasEnoughDurability(is)) {
                    bestSword = is;
                    bestSwordSlot = i;
                    break;
                }
            }
        }

        if (bestSword != null && bestSwordSlot != -1) {
            this.swap(bestSwordSlot, slot - 1);
        }

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSword && !isBestSword(is)) {
                    drop(i);
                }
            }
        }
    }
    
    private boolean hasEnoughDurability(ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage() > 20 || !swor.getBoolean();
    }
    
    private void dropLeftSword() {
        boolean bestSword = false;

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSword) {
                    if (isBestSword(is) && !bestSword) {
                        bestSword = true;
                    }else {
                        drop(i);
                    }
                }
                if (is.getUnlocalizedName().contains("helmet") && !isBestArmor(is, 1)) {
                    drop(i);
                } else if (is.getUnlocalizedName().contains("chestplate") && !isBestArmor(is, 2)) {
                    drop(i);
                } else if (is.getUnlocalizedName().contains("leggings") && !isBestArmor(is, 3)) {
                    drop(i);
                } else if (is.getUnlocalizedName().contains("boots") && !isBestArmor(is, 4)) {
                    drop(i);
                }
            }
        }
    }
    
    private void drop(int slot) {
    	if(ticks >= delay.getValue()) {
    		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
    		ticks = 0;
    	}
    }
    
    private void getBestBlock(int slot) {
        int selectedSlot = slot;
        int bestSlot = -1;
        int maxStackSize = -1;

        for (int i = 9; i < 45; i++) {
            Slot inventorySlot = mc.thePlayer.inventoryContainer.getSlot(i);
            if (inventorySlot.getHasStack()) {
                ItemStack stack = inventorySlot.getStack();
                if (stack.getItem() instanceof ItemBlock && stack.stackSize > maxStackSize) {
                    bestSlot = i;
                    maxStackSize = stack.stackSize;
                }
            }
        }

        if (bestSlot != -1 && !mc.thePlayer.inventoryContainer.getSlot(selectedSlot - 1).getHasStack()) {
            this.swap(bestSlot, selectedSlot - 1);
        }
    }
    
    private float getDamage(ItemStack stack) {
        float damage = 0.0f;
        Item item = stack.getItem();
        if (item instanceof ItemSword) {
            ItemSword sword = (ItemSword)item;
            damage += sword.attackDamage;
        }
        damage += (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 2.0f;
        if (stack.getItemDamage() * 3 > stack.getMaxItemUseDuration()) {
            damage -= 3.0f;
        }
        return damage;
    }
    
    private void getBestArmor() {
    	for (int type = 1; type < 5; type++) {
            if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                if (isBestArmor(is, type)) {
                    continue;
                } else {
                    drop(4 + type);
                }
            }
            for (int i = 9; i < 45; i++) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if (isBestArmor(is, type)) {
                        shiftClick(i);
                    }
                }
            }
        }
    }
    
    private boolean isBestArmor(ItemStack stack, int type) {
        double prot = getProtection(stack);
        String strType = "";
        if (type == 1) {
            strType = "helmet";
        } else if (type == 2) {
            strType = "chestplate";
        } else if (type == 3) {
            strType = "leggings";
        } else if (type == 4) {
            strType = "boots";
        }
        if (!stack.getUnlocalizedName().contains(strType)) {
            return false;
        }
        int i = 5;
        while (i < 45) {
            ItemStack is;
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && getProtection(is = mc.thePlayer.inventoryContainer.getSlot(i).getStack()) > prot && is.getUnlocalizedName().contains(strType)) {
                return false;
            }
            ++i;
        }
        return true;
    }
    
    private double getProtection(ItemStack stack) {
        double prot = 0.0;
        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor)stack.getItem();
            prot += (double)armor.damageReduceAmount + (double)((100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)) * 0.0075;
            prot += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100.0;
            prot += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100.0;
            prot += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100.0;
            prot += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50.0;
        }
        return prot;
    }

}