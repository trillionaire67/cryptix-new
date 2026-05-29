package cryptix.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDropper;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class Utils {
	private static final Minecraft mc = Minecraft.getMinecraft();
	public static Random random = new Random();
	
	public static void setMotion(double motion) {
		mc.thePlayer.motionX *= motion;
		mc.thePlayer.motionZ *= motion;
	}
	
	public static List<Class<?>> getAllClassesInPackage(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        URL packageUrl = classLoader.getResource(path);
        if (packageUrl == null) {
            throw new ClassNotFoundException("Package not found: " + packageName);
        }
        File directory = new File(packageUrl.getFile());
        if (!directory.exists()) {
            throw new IOException("Directory does not exist: " + directory.getPath());
        }
        List<Class<?>> classes = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }
	
	public static boolean overVoid() {
		double playerPosY = mc.thePlayer.posY;
		BlockPos currentPos = new BlockPos(mc.thePlayer.posX, 0, mc.thePlayer.posZ);
	    for (int y = (int) playerPosY; y >= 0; y--) {
	        currentPos.setPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ);
	        Block blockAtPos = mc.theWorld.getBlockState(currentPos).getBlock();
	        if (!(blockAtPos instanceof BlockAir)) {
	            return false;
	        }
	    }
	    return true;
    }
	
	public static int getBlocks() {
    	int slot = -1;
    	for(int i = 0; i < 9; i++) {
    		ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
    		if(stack != null && stack.getItem() instanceof ItemBlock) {
    			slot = i;
    		}
    	}
    	return slot;
    }
	
	public static int getTool(Block block) {
        float n = 1.0f;
        int n2 = -1;
        for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
            final ItemStack getStackInSlot = mc.thePlayer.inventory.getStackInSlot(i);
            if (getStackInSlot != null) {
                final float a = getEfficiency(getStackInSlot, block);
                if (a > n) {
                    n = a;
                    n2 = i;
                }
            }
        }
        return n2;
    }
	
	public static float getEfficiency(final ItemStack itemStack, final Block block) {
        float getStrVsBlock = itemStack.getStrVsBlock(block);
        if (getStrVsBlock > 1.0f) {
            final int getEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
            if (getEnchantmentLevel > 0) {
                getStrVsBlock += getEnchantmentLevel * getEnchantmentLevel + 1;
            }
        }
        return getStrVsBlock;
    }
	
	public static boolean isInteractable(Block block) {
        return block instanceof BlockFurnace || block instanceof BlockFenceGate || block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockEnchantmentTable || block instanceof BlockBrewingStand || block instanceof BlockBed || block instanceof BlockDropper || block instanceof BlockDispenser || block instanceof BlockHopper || block instanceof BlockAnvil || block == Blocks.crafting_table;
    }
	
	public static int randomInt(int min, int max) {
	    return random.nextInt(max - min) + min;
	}
	
	public static boolean isLookingAtBlock() {
        Vec3 start = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        Vec3 look = mc.thePlayer.getLook(1.0f);
        Vec3 end = start.addVector(look.xCoord * 5.0, look.yCoord * 5.0, look.zCoord * 5.0);
        MovingObjectPosition rayTraceResult = mc.theWorld.rayTraceBlocks(start, end, false, true, false);
        return rayTraceResult != null && rayTraceResult.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK;
    }
	
	public static boolean holdingSword() {
		return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
	}
	
	public static boolean holdingBlock() {
		return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock;
	}
	
	public static void sendClientChatMessage(String msg) {
		mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§f["+"§aCryptix"+"§f] "+msg));
	}
	
	public static void sendServerChatMessage(String msg) {
		mc.getNetHandler().addToSendQueue(new C01PacketChatMessage(msg));
	}
	
	public static float lerp(float start, float end, float alpha) {
	    return start + alpha * (end - start);
	}
	
	public static float lerp(float previous, float target, float partialTicks, float smoothingFactorPerTick) {
	    float alpha = 1.0F - (float) Math.pow(1.0F - smoothingFactorPerTick, partialTicks);
	    return previous + alpha * (target - previous);
	}
	
	public static boolean teamMate(EntityLivingBase entity) {
	    if (entity == null || mc.thePlayer == null) {
	        return false;
	    }
	    
	    try {
	    	NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
	        String entityName = entity.getDisplayName().getUnformattedText();
	        String playerName = mc.thePlayer.getDisplayName().getUnformattedText();
	        String networkName = ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName());

	        boolean sameTeam = mc.thePlayer.isOnSameTeam((EntityLivingBase) entity);
	        boolean namePrefixMatch = playerName.startsWith(entityName.substring(0, 2)) || 
	                                  networkName.startsWith(entityName.substring(0, 2));

	        return sameTeam || namePrefixMatch;
	    } catch (Exception ignored) {
	        return false;
	    }
	}
	
	public static int getPlayerHelmet(EntityPlayer player) {
        ItemStack helmetStack = player.inventory.armorItemInSlot(3);
        if (helmetStack != null && helmetStack.getItem() instanceof ItemArmor) {
            if (helmetStack.getItem() == Items.diamond_helmet) {
                return 4;
            }
            if (helmetStack.getItem() == Items.iron_helmet) {
                return 3;
            }
            if (helmetStack.getItem() == Items.golden_helmet) {
                return 2;
            }
            if (helmetStack.getItem() == Items.leather_helmet) {
                return 0;
            }
            if (helmetStack.getItem() == Items.chainmail_helmet) {
                return 1;
            }
        }
        return -1;
    }

    public static int getPlayerChestPlate(EntityPlayer player) {
        ItemStack chestplateStack = player.inventory.armorItemInSlot(2);
        if (chestplateStack != null && chestplateStack.getItem() instanceof ItemArmor) {
            if (chestplateStack.getItem() == Items.diamond_chestplate) {
                return 4;
            }
            if (chestplateStack.getItem() == Items.iron_chestplate) {
                return 3;
            }
            if (chestplateStack.getItem() == Items.golden_chestplate) {
                return 2;
            }
            if (chestplateStack.getItem() == Items.leather_chestplate) {
                return 0;
            }
            if (chestplateStack.getItem() == Items.chainmail_chestplate) {
                return 1;
            }
        }
        return -1;
    }

    public static int getPlayerLeggings(EntityPlayer player) {
        ItemStack leggingsStack = player.inventory.armorItemInSlot(1);
        if (leggingsStack != null && leggingsStack.getItem() instanceof ItemArmor) {
            if (leggingsStack.getItem() == Items.diamond_leggings) {
                return 4;
            }
            if (leggingsStack.getItem() == Items.iron_leggings) {
                return 3;
            }
            if (leggingsStack.getItem() == Items.golden_leggings) {
                return 2;
            }
            if (leggingsStack.getItem() == Items.leather_leggings) {
                return 0;
            }
            if (leggingsStack.getItem() == Items.chainmail_leggings) {
                return 1;
            }
        }
        return -1;
    }

    public static int getPlayerBoots(EntityPlayer player) {
        ItemStack bootsStack = player.inventory.armorItemInSlot(0);
        if (bootsStack != null && bootsStack.getItem() instanceof ItemArmor) {
            if (bootsStack.getItem() == Items.diamond_boots) {
                return 4;
            }
            if (bootsStack.getItem() == Items.iron_boots) {
                return 3;
            }
            if (bootsStack.getItem() == Items.golden_boots) {
                return 2;
            }
            if (bootsStack.getItem() == Items.leather_boots) {
                return 0;
            }
            if (bootsStack.getItem() == Items.chainmail_boots) {
                return 1;
            }
        }
        return -1;
    }
    
    public static void drawArmor(EntityPlayer e, int x, int y, int alpha) {
        GlStateManager.enableBlend();
        GlStateManager.color(1F, 1F, 1F, alpha / 255F);
        int size = 16;
        int spacing = 2;
        int totalWidth = (size * 4) + (spacing * 3);
        int startX = x - totalWidth / 2;
        drawArmorPiece(getPlayerHelmet(e),     startX + (size + spacing) * 0, y, ArmorType.HELMET);
        drawArmorPiece(getPlayerChestPlate(e), startX + (size + spacing) * 1, y, ArmorType.CHESTPLATE);
        drawArmorPiece(getPlayerLeggings(e),   startX + (size + spacing) * 2, y, ArmorType.LEGGINGS);
        drawArmorPiece(getPlayerBoots(e),      startX + (size + spacing) * 3, y, ArmorType.BOOTS);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableBlend();
    }

    enum ArmorType {
        HELMET, CHESTPLATE, LEGGINGS, BOOTS
    }

    private static void drawArmorPiece(int type, int x, int y, ArmorType armorType) {
        ItemStack stack = null;
        switch (armorType) {
            case HELMET:
                switch (type) {
                    case 0: stack = new ItemStack(Items.leather_helmet); break;
                    case 1: stack = new ItemStack(Items.chainmail_helmet); break;
                    case 2: stack = new ItemStack(Items.golden_helmet); break;
                    case 3: stack = new ItemStack(Items.iron_helmet); break;
                    case 4: stack = new ItemStack(Items.diamond_helmet); break;
                }
                break;
            case CHESTPLATE:
                switch (type) {
                    case 0: stack = new ItemStack(Items.leather_chestplate); break;
                    case 1: stack = new ItemStack(Items.chainmail_chestplate); break;
                    case 2: stack = new ItemStack(Items.golden_chestplate); break;
                    case 3: stack = new ItemStack(Items.iron_chestplate); break;
                    case 4: stack = new ItemStack(Items.diamond_chestplate); break;
                }
                break;
            case LEGGINGS:
                switch (type) {
                    case 0: stack = new ItemStack(Items.leather_leggings); break;
                    case 1: stack = new ItemStack(Items.chainmail_leggings); break;
                    case 2: stack = new ItemStack(Items.golden_leggings); break;
                    case 3: stack = new ItemStack(Items.iron_leggings); break;
                    case 4: stack = new ItemStack(Items.diamond_leggings); break;
                }
                break;
            case BOOTS:
                switch (type) {
                    case 0: stack = new ItemStack(Items.leather_boots); break;
                    case 1: stack = new ItemStack(Items.chainmail_boots); break;
                    case 2: stack = new ItemStack(Items.golden_boots); break;
                    case 3: stack = new ItemStack(Items.iron_boots); break;
                    case 4: stack = new ItemStack(Items.diamond_boots); break;
                }
                break;
        }

        if (stack != null) {
            mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        }
    }
    public static String decrypt(String encryptedUrl) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec("u9xCq3jVb7eFwKz1".getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedUrl));
        return new String(decrypted);
    }

}
