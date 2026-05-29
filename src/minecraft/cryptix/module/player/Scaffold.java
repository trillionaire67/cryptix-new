package cryptix.module.player;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.BlinkUtils;
import cryptix.utils.MovementUtils;
import cryptix.utils.RotationUtils;
import cryptix.utils.Utils;
import java.awt.Color;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class Scaffold
extends Module {
	private AxisAlignedBB box = new AxisAlignedBB(0,0,0,0,0,0);
    private MovingObjectPosition placeBlock;
    private BlockPos placePos;
    private Setting rotations = new Setting("Rotations", (Module)this, "Simple", Arrays.asList("None", "Simple", "Strict", "Lazy", "Offset"));
    private Setting rotationsFake = new Setting("Rotations (fake)", (Module)this, "None", Arrays.asList("None", "Simple", "Strict"));
    private Setting sprint;
    private Setting silentSwing;
    private Setting tower;
    private Setting multiPlace;
    private Setting blockOutline;
    private Setting movefix;
    private Setting rotationSpeed;
    public Setting count;
    public Setting spoof;
    private float strictYaw;
    private float strictPitch;
    private float preYaw;
    private float changeYaw;
    private int keepy_y;
    private int towerTick;
    private int blinkTick;
    private boolean sprinting;
    private boolean ground, blink;
    public BlockPos previousBlock, targetPos;
    private Block blockUnder;
    private boolean forceStrict;
    private boolean place;
    private float[] rotation;
    private float[] hypixelRots;
    private int floatTick;
    private int enable;
    public int setback;
    public int lastSlot;
    private int diag;
    private float prePitch;
    private int direction, wrongDirectionTick;
    private double jumpX, jumpZ;
    private BlockPos[] offsets = new BlockPos[]{new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, -1, 0)};
    private static final BlockPos[] SEARCH_OFFSETS = {new BlockPos(0, -1, 0),new BlockPos(0, 1, 0), new BlockPos(-1, 0, 0),new BlockPos(1, 0, 0),new BlockPos(0, 0, -1),new BlockPos(0, 0, 1),new BlockPos(1, 0, 1),new BlockPos(1, 0, -1),new BlockPos(-1, 0, 1),new BlockPos(-1, 0, -1),new BlockPos(1, -1, 0),new BlockPos(-1, -1, 0),new BlockPos(0, -1, 1),new BlockPos(0, -1, -1)};
    private EnumFacing[] facings = new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.UP};
    private float[] searchYaw = this.generateSearchSequence(180);
    private float[] searchPitch = new float[] {1,-1,2,-2,3,-3,4,-4,5,-5,6,-6,7,-7,8,-8,9,-9,10,-10,11,-11,12,-12,13,-13,14,-14,15,-15,16,-16,17,-17,18,-18,19,-19,20,-20,};
    private PlaceData extraPlaceData;
    public Scaffold() {
        super("Scaffold", 0, Category.PLAYER);
        this.addSetting(this.rotations, this.rotationsFake);
        this.sprint = new Setting("Sprint", (Module)this, "None", Arrays.asList("None", "Vanilla", "BlocksMC", "Hypixel", "Hypixel2", "BlocksD", "Vulcan"));
        Client.instance.settingsManager.addSetting(this.sprint);
        this.tower = new Setting("Tower", (Module)this, "None", Arrays.asList("None", "Old", "Hypixel"));
        Client.instance.settingsManager.addSetting(this.tower);
        this.multiPlace = new Setting("Multi Place", this, true);
        Client.instance.settingsManager.addSetting(this.multiPlace);
        this.silentSwing = new Setting("Silent Swing", this, false);
        Client.instance.settingsManager.addSetting(this.silentSwing);
        this.count = new Setting("Block Count", (Module)this, "None", Arrays.asList("None", "Simple", "Rise"));
        Client.instance.settingsManager.addSetting(this.count);
        this.blockOutline = new Setting("Block Outline", this, true);
        Client.instance.settingsManager.addSetting(this.blockOutline);
        this.spoof = new Setting("Spoof Item", this, false);
        Client.instance.settingsManager.addSetting(this.spoof);
        this.movefix = new Setting("Movefix", this, false);
        Client.instance.settingsManager.addSetting(this.movefix);
        this.rotationSpeed = new Setting("Rotation Speed", this, 3, 1, 10, true);
        Client.instance.settingsManager.addSetting(this.rotationSpeed);
        extraPlaceData = new PlaceData(EnumFacing.UP, new BlockPos(0,0,0));
    }

    @Override
    public void onEnable() {
        this.enable = 5;
        this.preYaw = this.mc.thePlayer.rotationYaw % 360.0f;
        this.keepy_y = (int)this.mc.thePlayer.posY;
        this.lastSlot = this.mc.thePlayer.inventory.currentItem;
        this.ground = true;
        strictPitch = 80;
        prePitch = mc.thePlayer.rotationPitch;
        this.changeYaw = RotationUtils.getMovementYaw() + 180;
        this.strictYaw = RotationUtils.getMovementYaw();
        this.floatTick = 0;
        this.placeBlock = null;
        this.blinkTick = 0;
    }

    @Override
    public void onDisable() {
        this.placePos = null;
        this.mc.thePlayer.inventory.currentItem = this.lastSlot;
        this.mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown((int)this.mc.gameSettings.keyBindJump.getKeyCode());
        this.placeBlock = null;
        BlinkUtils.stopBlink();
    }

    @Override
    public void onPreMotion() {
    	if(Client.instance.moduleManager.killAura.blocking || Client.instance.moduleManager.killAura.swapped || Client.instance.moduleManager.killAura.b3) return;
        this.sprint();
        if (this.mc.gameSettings.keyBindJump.isKeyDown()) {
            if (this.mc.thePlayer.onGround && this.mc.thePlayer.posY % 1.0 > (double)0.42f) {
                return;
            }
            this.tower();
        } else {
            this.towerTick = 1;
        }
        this.rotation = this.getRotations();
        if (this.rotation != null) {
            this.mc.thePlayer.rotationYawHead = (this.rotation[0] % 360.0f + 360.0f) % 360.0f;
            this.mc.thePlayer.renderYawOffset = this.rotation[2] == 45.0f ? this.mc.thePlayer.rotationYawHead + 45.0f : this.rotation[2];
            this.mc.thePlayer.rotationPitchHead = this.rotation[1];
        }
    }
    
    @Override
    public void onPostMotion() {
    	float yaw = mc.thePlayer.rotationYaw;
    	float pitch = mc.thePlayer.rotationPitch;
    	switch(rotationsFake.getString().toLowerCase()) {
	    	case "simple":
	    		yaw = RotationUtils.getMovementYaw();
	    		pitch = 80;
	    		break;
	    	case "strict":
	    		yaw = strictYaw;
	    		pitch = strictPitch;
	    		break;
	    }
    	if(!rotationsFake.getString().equalsIgnoreCase("None")) {
	    	mc.thePlayer.fakeYaw = yaw;
	    	mc.thePlayer.fakePitch = pitch;
	    	mc.thePlayer.renderYawOffset = yaw + 45;
    	}
    	if(blink) {
    		blink = false;
    		BlinkUtils.stopBlink();
    		BlinkUtils.startBlink();
    	}
    }

    @Override
    public void onRender3D() {
        if (this.placePos != null && this.blockOutline.getBoolean()) {
            double x = (double)this.placePos.getX() - this.mc.getRenderManager().viewerPosX;
            double y = (double)this.placePos.getY() - this.mc.getRenderManager().viewerPosY;
            double z = (double)this.placePos.getZ() - this.mc.getRenderManager().viewerPosZ;
            box.minX = x;
            box.minY = y;
            box.minZ = z;
            box.maxX = x + 1.0;
            box.maxY = y + 1.0;
            box.maxZ = z + 1.0;
            int colorInt = Client.instance.moduleManager.hud.getColorInt(0, 1f);
	        float red   = ((colorInt >> 16) & 0xFF) / 255f;
	        float green = ((colorInt >> 8) & 0xFF) / 255f;
	        float blue  = (colorInt & 0xFF) / 255f;
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GL11.glLineWidth((float)2.0f);
            GlStateManager.color(red, green, blue, 1.0f);
            RenderGlobal.drawSelectionBoundingBox(box);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    private void tower() {
        if (this.tower.getString().equalsIgnoreCase("Old")) {
            double yMod;
            boolean snapY = Math.round((yMod = this.mc.thePlayer.posY % 1.0) * 100.0) == 0L || yMod < 0.1 && !this.mc.thePlayer.onGround;
            double speed = 0.3 * (this.mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.332 : 1.0);
            if (this.towerTick > 5 && this.mc.thePlayer.motionY > 0.0 && this.setback < 0) {
                speed *= 1.081;
            }
            if (this.setback >= 0) {
                speed *= 0.9;
            }
            if (snapY) {
                this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, Math.floor(this.mc.thePlayer.posY), this.mc.thePlayer.posZ);
                this.mc.thePlayer.motionY = 0.42f;
            }
            if (MovementUtils.isMoving()) {
                ++this.towerTick;
                MovementUtils.strafe(speed);
            } else {
                this.towerTick = 0;
                MovementUtils.strafe(0.0);
            }
        }
        if (this.tower.getString().equalsIgnoreCase("Hypixel")) {
            if (this.mc.thePlayer.onGround && MovementUtils.getSpeed() <= 0.02) {
                this.mc.thePlayer.motionY = 0.42f;
            }else if (this.mc.thePlayer.motionY < 0 && MovementUtils.getSpeed() <= 0.02 && blockUnder != Blocks.air && mc.thePlayer.offGroundTicks > 4 && Keyboard.isKeyDown((int)this.mc.gameSettings.keyBindJump.getKeyCode())) {
                this.mc.thePlayer.motionY = -0.37;
            }
        }
        if (this.sprinting) {
            this.sprinting = false;
        }
    }

    private void sprint() {
        switch (this.sprint.getString().toLowerCase()) {
            case "none": {
                if (this.movefix.getBoolean()) break;
                this.mc.thePlayer.setSprinting(false);
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), false);
                break;
            }
            case "vanilla": {
                if (MovementUtils.isMoving() && !this.mc.gameSettings.keyBindJump.isKeyDown()) {
                    this.sprinting = true;
                    this.mc.thePlayer.setSprinting(true);
                    KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), true);
                    break;
                }
                this.mc.thePlayer.setSprinting(false);
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), false);
                break;
            }
            case "blocksd": {
                if (MovementUtils.isMoving()) {
                    this.sprinting = true;
                    this.mc.thePlayer.setSprinting(true);
                    KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), true);
                } else {
                    this.mc.thePlayer.setSprinting(false);
                    KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), false);
                }
                break;
            }
            case "keepy": {
                if (MovementUtils.isMoving() && !this.mc.gameSettings.keyBindJump.isKeyDown()) {
                    this.sprinting = true;
                    if (this.mc.thePlayer.onGround) {
                        this.mc.thePlayer.jump();
                    }
                    this.mc.thePlayer.setSprinting(true);
                    KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), true);
                    break;
                }
                this.keepy_y = (int)this.mc.thePlayer.posY;
                this.mc.thePlayer.setSprinting(false);
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), false);
                break;
            }
            case "blocksmc": {
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), true);
                break;
            }
            case "hypixel": {
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), true);
                if(floatTick >= 9) {
                	this.mc.gameSettings.keyBindJump.pressed = false;
                }else if(!MovementUtils.isMoving()){
                	this.mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown((int)this.mc.gameSettings.keyBindJump.getKeyCode());
                }
                if((mc.thePlayer.posY < keepy_y || Keyboard.isKeyDown((int)this.mc.gameSettings.keyBindJump.getKeyCode())) && enable <= -2) {
                	this.keepy_y = (int)this.mc.thePlayer.posY;
                }
                break;
            }
            case "hypixel2": {
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), true);
                break;
            }
            case "vulcan":
            	KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), false);
            	mc.thePlayer.setSprinting(false);
            	if(mc.thePlayer.onGround) {
            		MovementUtils.strafe(mc.thePlayer.onGroundTicks % 2 != 0 ? 0.16 : 0.14);
            	}else if(!mc.gameSettings.keyBindJump.isKeyDown()){
            		MovementUtils.strafe(0);
            	}
            	if(mc.gameSettings.keyBindJump.isKeyDown()) {
            		switch(mc.thePlayer.offGroundTicks) {
            		case 3:
	            		mc.thePlayer.motionY = -0.42F;
	            		MovementUtils.strafe();
	            		mc.thePlayer.offGroundTicks = 0;
            			break;
            		}
            		if(mc.thePlayer.onGround) {
            			mc.thePlayer.motionY = 0.42F;
            			MovementUtils.strafe(0.3);
            		}else {
            			if(MovementUtils.getSpeed() < 0.222) {
        					MovementUtils.strafe(0.222 + Math.random() * 0.001);
        					sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        				}
            		}
            	}
            	if(MovementUtils.getSpeed() < 0.2) {
        			sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        		}
            	break;
        }
    }

    private float[] getRotations() {
        float yaw = 0.0f;
        float pitch = 0.0f;
        float bodyYaw = 0.0f;
        
        float movementYaw = RotationUtils.getMovementYaw();
        yaw = movementYaw + 45.0f;
        bodyYaw = 45.0f;
        switch (this.rotations.getString().toLowerCase()) {
            case "simple":
            	yaw = movementYaw;
            	pitch = strictPitch;
            	break;
            case "offset":
            case "strict": 
            case "lazy": {
                yaw = this.strictYaw;
                pitch = this.strictPitch;
                break;
            }
            case "none": {
            	yaw = mc.thePlayer.rotationYaw;
                pitch = mc.thePlayer.rotationPitch;
            	break;
            }
        }
        if (this.sprint.getString().equalsIgnoreCase("Blocksmc") && (this.mc.thePlayer.onGround || this.mc.thePlayer.offGroundTicks < (Keyboard.isKeyDown((int)this.mc.gameSettings.keyBindJump.getKeyCode()) ? 2 : 4))) {
            this.preYaw = yaw = RotationUtils.getMovementYaw() + 180.0f;
            this.changeYaw = yaw;
            enable = 1;
            this.mc.gameSettings.keyBindJump.pressed = this.mc.thePlayer.onGround ? true : Keyboard.isKeyDown((int)this.mc.gameSettings.keyBindJump.getKeyCode());
        }else if(this.sprint.getString().equalsIgnoreCase("Blocksmc")){
        	this.mc.gameSettings.keyBindJump.pressed = false;
        }
        if (this.sprint.getString().equalsIgnoreCase("hypixel") && this.hypixelRots != null) {
	            if (this.mc.thePlayer.onGround) {
	            	if(Utils.holdingBlock() && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY+ 2, mc.thePlayer.posZ)).getBlock() == Blocks.air && MovementUtils.isMoving()) {
		                this.preYaw = yaw = RotationUtils.getMovementYaw() + 179;
		                this.strictYaw = RotationUtils.getMovementYaw();
		                blinkTick = 0;
		                enable = 1;
		                this.changeYaw = yaw;
		                this.mc.gameSettings.keyBindJump.pressed = this.mc.thePlayer.onGround ? true : Keyboard.isKeyDown((int)this.mc.gameSettings.keyBindJump.getKeyCode());
		                pitch = 20;
		                if(RotationUtils.getMovementYaw() % 90 > 20 && RotationUtils.getMovementYaw() % 90 < 70) {
		            		diag++;
		            	}else {
		            		diag = 0;
		            	}
		                BlinkUtils.startBlink();
	            	}else {
	            		BlinkUtils.stopBlink();
	            		yaw = this.hypixelRots[0];
	 	                pitch = this.hypixelRots[1];
	            	}
	            } else {
	            	this.mc.gameSettings.keyBindJump.pressed = this.mc.thePlayer.onGround ? true : Keyboard.isKeyDown((int)this.mc.gameSettings.keyBindJump.getKeyCode());
	                yaw = this.hypixelRots[0];
	                pitch = this.hypixelRots[1];
	            }
        }else if(!this.sprint.getString().equalsIgnoreCase("Blocksmc") && hypixelRots != null){
        	yaw = this.hypixelRots[0];
            pitch = this.hypixelRots[1];
        }
        return new float[]{yaw, pitch, bodyYaw};
    }

    private BlockPos getTargetBlockPos() {
        BlockPos bp;
        if (this.sprint.getString().equalsIgnoreCase("keepy a") || this.sprint.getString().equalsIgnoreCase("keepy b") || this.sprint.getString().equalsIgnoreCase("blocksmc") || this.sprint.getString().equalsIgnoreCase("hypixel") || this.sprint.getString().equalsIgnoreCase("hypixel2")) {
            bp = new BlockPos(this.mc.thePlayer.posX, this.keepy_y - 1.0, this.mc.thePlayer.posZ);
            if(this.sprint.getString().equalsIgnoreCase("hypixel") && !mc.thePlayer.onGround && mc.thePlayer.motionY < -0 && (mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 0.5, this.mc.thePlayer.posZ)).getBlock() instanceof BlockAir) && !(mc.thePlayer.posY > (double)(keepy_y + 2)) && mc.thePlayer.posY > (double)(keepy_y + 1)) {
            	//bp = new BlockPos(jumpX, this.mc.thePlayer.posY - 1.0, jumpZ);
            }
        }else {
        	bp = new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.posZ);
        }
        if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindJump.getKeyCode()) && enable <= -2) {
            this.keepy_y = (int)this.mc.thePlayer.posY;
        }
        return bp;
    }

    private boolean shouldPlaceBlock() {
        return blockUnder instanceof BlockAir || blockUnder instanceof BlockLiquid;
    }

    @Override
    public void onPreUpdate() {
    	if(Client.instance.moduleManager.killAura.blocking || Client.instance.moduleManager.killAura.swapped || Client.instance.moduleManager.killAura.b3) return;
        if (this.movefix.getBoolean()) {
            Client.movefix = true;
        }
        this.getBlocks();
        ItemStack heldItem = this.mc.thePlayer.getHeldItem();
        if (heldItem == null || !(heldItem.getItem() instanceof ItemBlock)) {
            return;
        }
        targetPos = getTargetBlockPos();
        blockUnder = mc.theWorld.getBlockState(targetPos).getBlock();
        --this.floatTick;
        this.placeBlock();
    }

    @Override
    public void onPreInput() {
    	boolean diagonal = RotationUtils.getMovementYaw() % 90.0f > 15.0f && RotationUtils.getMovementYaw() % 90.0f < 75.0f;
    	if(sprint.getString().equalsIgnoreCase("Hypixel") && mc.thePlayer.offGroundTicks > 3 && mc.thePlayer.offGroundTicks < 7) {
    		//mc.thePlayer.movementInput.sneak = true;
    		//mc.thePlayer.movementInput.moveStrafe = (float)((double)mc.thePlayer.movementInput.moveStrafe * 0.3D);
        	//mc.thePlayer.movementInput.moveForward = (float)((double)mc.thePlayer.movementInput.moveForward * 0.3D);
    	}
    }

    private void placeBlock() {
        ItemStack heldItem = this.mc.thePlayer.getHeldItem();
        MovingObjectPosition rayCasted = null;
        BlockPos baseTargetPos = targetPos;
        String rota = this.rotations.getString();
        rayCasted = this.tryPlaceAt(heldItem, baseTargetPos, rota);
        if (rayCasted == null) {
            BlockPos lowerPos = baseTargetPos.down();
            rayCasted = this.tryPlaceAt(heldItem, lowerPos, rota);
        }
        this.hypixelRots = this.getRotationsHypixel(rota);
        this.place = false;
        if(enable > 0) {
        	enable--;
        	return;
        }else {
        	enable--;
        }
        if (rayCasted == null) {
            return;
        }
        this.placeBlock = rayCasted;
        place(this.placeBlock, false);
        if(!multiPlace.getBoolean()) return;
        MovingObjectPosition multi = this.tryPlaceAt(heldItem, baseTargetPos, rota);
        if (multi == null) {
            BlockPos lowerPos = baseTargetPos.down();
            multi = this.tryPlaceAt(heldItem, lowerPos, rota);
        }
        if (multi == null) {
            return;
        }
        this.placeBlock = multi;
        place(this.placeBlock, false);
    }

    private MovingObjectPosition tryPlaceAt(ItemStack heldItem, BlockPos targetPos, String rota) {
        if (!shouldPlaceBlock() || !(heldItem.getItem() instanceof ItemBlock)) {
            return null;
        }
        PlaceData placeData = getBlockData(targetPos);
        if (placeData == null) return null;
        final BlockPos placePos = placeData.blockPos;
        final EnumFacing placeSide = placeData.enumFacing;
        if (placePos == null || placeSide == null) return null;
        final ItemBlock itemBlock = (ItemBlock) heldItem.getItem();
        final float[] targetRot = RotationUtils.getRotationsBlock(placePos);
        final float playerYaw = !mc.thePlayer.onGround ? (float)(Math.toDegrees(Math.atan2(mc.thePlayer.motionZ, mc.thePlayer.motionX)) % 360.0 + 360.0) + 90.0f : RotationUtils.getMovementYaw();
        final boolean offset = rota.equalsIgnoreCase("Offset");
        final boolean lazy = rota.equalsIgnoreCase("Lazy");
        float baseYaw = lazy ? playerYaw : strictYaw;
        if (offset && !lazy) {
            float offsetYaw = playerYaw + (45.0f);
            float diff = MathHelper.wrapAngleTo180_float(offsetYaw - strictYaw);
            diff = MathHelper.clamp_float(diff, -20.0f, 20.0f);
            baseYaw = strictYaw + diff;
        }
        final float fakeYaw = mc.thePlayer.fakeYaw;
        final float fakePitch = mc.thePlayer.fakePitch;
        for (float yawOffset : searchYaw) {
            float fixedYaw = baseYaw + yawOffset;
            for (float pitchOffset : searchPitch) {
                float fixedPitch = RotationUtils.clampTo90(targetRot[1] + pitchOffset);
                MovingObjectPosition raycast = RotationUtils.rayCast(4.5, fixedYaw, fixedPitch, fakeYaw, fakePitch);
                if (raycast == null || raycast.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
                    continue;
                }
                if (!raycast.getBlockPos().equals(placePos) || raycast.sideHit != placeSide) {
                    continue;
                }
                if (!itemBlock.canPlaceBlockOnSide(mc.theWorld,raycast.getBlockPos(),raycast.sideHit,mc.thePlayer,heldItem)) {
                    continue;
                }
                strictYaw = MathHelper.wrapAngleTo180_float(fixedYaw);
                strictPitch = fixedPitch;
                return raycast;
            }
        }
        return null;
    }

    
    private MovingObjectPosition[] tryPlaceMulti(ItemStack heldItem, BlockPos targetPos, String rota) {
        if (!shouldPlaceBlock()) return null;

        PlaceData placeData = getBlockData(targetPos);
        if (placeData == null) return null;

        BlockPos placePos = placeData.blockPos;
        EnumFacing placeSide = placeData.enumFacing;

        float[] targetRotation = RotationUtils.getRotationsBlock(placePos);
        float playerYaw = !mc.thePlayer.onGround
                ? (float)(Math.toDegrees(Math.atan2(mc.thePlayer.motionZ, mc.thePlayer.motionX)) % 360.0 + 360.0) + 90.0f
                : RotationUtils.getMovementYaw();

        float offsetYaw = playerYaw + 45.0f;

        boolean offset = rota.equalsIgnoreCase("Offset");
        boolean lazy = rota.equalsIgnoreCase("Lazy");

        MovingObjectPosition first = null;
        MovingObjectPosition second = null;

        for (float yawOffset : searchYaw) {
            float fixedYaw = (!lazy ? (offset ? offsetYaw : strictYaw) : playerYaw) + yawOffset;

            for (float pitchOffset : searchPitch) {
                float fixedPitch = RotationUtils.clampTo90(targetRotation[1] + pitchOffset);

                MovingObjectPosition raycast = RotationUtils.rayCast(
                        4.5, fixedYaw, fixedPitch,
                        mc.thePlayer.fakeYaw, mc.thePlayer.fakePitch
                );

                if (raycast == null
                        || raycast.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK
                        || !raycast.getBlockPos().equals(placePos)
                        || raycast.sideHit != placeSide
                        || !(heldItem.getItem() instanceof ItemBlock)) {
                    continue;
                }

                ItemBlock itemBlock = (ItemBlock) heldItem.getItem();
                if (!itemBlock.canPlaceBlockOnSide(mc.theWorld,
                        raycast.getBlockPos(),
                        raycast.sideHit,
                        mc.thePlayer,
                        heldItem)) {
                    continue;
                }

                if (first == null) {
                    first = raycast;
                    strictYaw = fixedYaw;
                    strictPitch = fixedPitch;
                } else if (second == null) {
                    second = raycast;
                    return new MovingObjectPosition[]{first, second};
                }
            }
        }

        return first != null ? new MovingObjectPosition[]{first} : null;
    }
    
    public float[] generateSearchSequence(float value) {
        int step = 2;
        int numSteps = (int)(value / step);
        float[] sequence = new float[numSteps * 2 + 1];
        int index = 0;
        sequence[index++] = 0.0f;
        for (int i = step; i <= value; i += step) {
            sequence[index++] = i;
            sequence[index++] = -i;
        }
        return sequence;
    }
    
    private void place(MovingObjectPosition block, boolean extra) {
        ItemStack heldItem = this.mc.thePlayer.getHeldItem();
        if (heldItem == null || !(heldItem.getItem() instanceof ItemBlock)) {
            return;
        }
        if (this.mc.playerController.onPlayerRightClick(this.mc.thePlayer, this.mc.theWorld, heldItem, block.getBlockPos(), block.sideHit, block.hitVec)) {
            this.place = true;
            if (this.silentSwing.getBoolean()) {
                this.sendPacket(new C0APacketAnimation());
            } else {
                this.mc.thePlayer.swingItem();
            }
            if(this.sprint.getString().equalsIgnoreCase("hypixel") && !mc.thePlayer.onGround && mc.thePlayer.motionY < 0 && (mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 0.5, this.mc.thePlayer.posZ)).getBlock() instanceof BlockAir) && !(mc.thePlayer.posY > (double)(keepy_y + 2)) && mc.thePlayer.posY > (double)(keepy_y + 1)) {
            	forceStrict = true;
            }
            if(this.sprint.getString().equalsIgnoreCase("hypixel") && !mc.thePlayer.onGround && mc.thePlayer.motionY < -0 && (mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 0.5, this.mc.thePlayer.posZ)).getBlock() instanceof BlockAir) && !(mc.thePlayer.posY > (double)(keepy_y + 2)) && mc.thePlayer.posY > (double)(keepy_y + 1)) {
            	blacklistedBlock = mc.theWorld.getBlockState(block.getBlockPos());
            }
            this.placePos = block.getBlockPos().offset(block.sideHit);
            this.floatTick = 10;
        }
    }
    
    private IBlockState blacklistedBlock;
    
    private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

    public PlaceData getBlockData(BlockPos pos) {
        EnumFacing bestFacing = null;
        BlockPos bestPos = null;
        double closestDistSq = Double.MAX_VALUE;
        final double px = mc.thePlayer.posX;
        final double py = mc.thePlayer.posY;
        final double pz = mc.thePlayer.posZ;
        final int baseX = pos.getX();
        final int baseY = pos.getY();
        final int baseZ = pos.getZ();
        final BlockPos prev = previousBlock;
        for (int s = 0; s < SEARCH_OFFSETS.length; s++) {
            BlockPos searchOffset = SEARCH_OFFSETS[s];
            int sx = baseX + searchOffset.getX();
            int sy = baseY + searchOffset.getY();
            int sz = baseZ + searchOffset.getZ();
            for (int i = 0; i < 5; i++) {
                int cx = sx + offsets[i].getX();
                int cy = sy + offsets[i].getY();
                int cz = sz + offsets[i].getZ();
                if (prev != null &&
                    cx == prev.getX() &&
                    cy == prev.getY() &&
                    cz == prev.getZ()) {
                    extraPlaceData.set(facings[i], prev);
                    return extraPlaceData;
                }
                mutablePos.set(cx, cy, cz);
                Block block = mc.theWorld.getBlockState(mutablePos).getBlock();
                if (block.getMaterial().isReplaceable()) continue;
                double dx = px - (cx + 0.5);
                double dy = py - (cy + 0.5);
                double dz = pz - (cz + 0.5);
                double distSq = dx * dx + dy * dy + dz * dz;
                if (distSq < closestDistSq) {
                    closestDistSq = distSq;
                    bestFacing = facings[i];
                    bestPos = new BlockPos(cx, cy, cz);
                }
            }
        }
        if (bestPos != null) {
            extraPlaceData.set(bestFacing, bestPos);
        }
        return extraPlaceData;
    }

    public int getHotbarBlockCount() {
        int blockCount = 0;
        int i = 0;
        while (i < 9) {
            Block block;
            ItemStack itemStack = this.mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() instanceof ItemBlock && (block = ((ItemBlock)itemStack.getItem()).getBlock()).isFullBlock()) {
                blockCount += itemStack.stackSize;
            }
            ++i;
        }
        return blockCount;
    }

    private void getBlocks() {
        int newitem = -1;
        int maxCount = 0;
        int slot = 0;
        while (slot < 9) {
            ItemStack stack = this.mc.thePlayer.inventory.mainInventory[slot];
            if (stack != null && stack.getItem() instanceof ItemBlock) {
                int count;
                Block block = ((ItemBlock)stack.getItem()).getBlock();
                if (block == Blocks.sand || block == Blocks.gravel) {
                    ++slot;
                    continue;
                }
                if (block.isFullBlock() && (count = stack.stackSize) > maxCount) {
                    maxCount = count;
                    newitem = slot;
                }
            }
            ++slot;
        }
        if (newitem != -1) {
            this.mc.thePlayer.inventory.currentItem = newitem;
        }
    }

    private float[] getRotationsHypixel(String rota) {
        String sprintMode = sprint.getString();
        boolean simple = rota.equalsIgnoreCase("Simple");
        float strictYaw = this.strictYaw;
        float changeYaw = this.changeYaw;
        if (sprintMode.equalsIgnoreCase("Hypixel")) {
            float moveYaw = RotationUtils.getMovementYaw();
            float yaw = simple ? moveYaw : strictYaw;
            float targetYaw = yaw;
            yaw = MathHelper.wrapAngleTo180_float(yaw);
            float speed = (float) MovementUtils.getSpeed();
            float pitch = speed < 0.05f ? 90f : RotationUtils.clampTo90(this.strictPitch);
            float limit = 39.9f;
            if (blinkTick == 0) {
            	forceStrict = false;
                pitch = 40f;
            } else if (blinkTick == 1) {
                pitch = 60f;
                strictPitch = 60f;
            } else if (blinkTick == 2) {
                limit = (float) (55 + Math.random() * 4);
            }else if (blinkTick >= 3) {
            	BlinkUtils.stopBlink();
            }
            blinkTick++;
            float diffToTarget = MathHelper.wrapAngleTo180_float(targetYaw - changeYaw);
            float diffToTarget2 = MathHelper.wrapAngleTo180_float(pitch - prePitch);
            if (Math.abs(diffToTarget) < 20f && Math.abs(diffToTarget2) < 15f && MovementUtils.isMoving()) {
            	return new float[]{changeYaw, prePitch};
            }
            float diff = MathHelper.wrapAngleTo180_float(yaw - changeYaw);
            diff = MathHelper.clamp_float(diff, -limit, limit);
            changeYaw = MathHelper.wrapAngleTo180_float(changeYaw + diff);
            this.changeYaw = changeYaw;
            this.prePitch = pitch;
            float yawError = MathHelper.wrapAngleTo180_float(changeYaw - targetYaw);
            if (Math.abs(yawError) > (blinkTick > 5 ? 40 : 0) && !mc.thePlayer.onGround) {
                enable = 2;
            }
            return new float[]{changeYaw, pitch};
        }else if (sprintMode.equalsIgnoreCase("blocksmc")) {
            float yaw = strictYaw;
            float diff = MathHelper.wrapAngleTo180_float(yaw - changeYaw);
            if (Math.abs(diff) > 90f) {
                enable = 1;
            }
            this.prePitch = this.strictPitch;
            this.changeYaw = yaw;
            return new float[]{yaw, this.strictPitch};
        }else if (sprintMode.equalsIgnoreCase("hypixel2")) {
            float baseYaw = !mc.thePlayer.onGround ? strictYaw : RotationUtils.getMovementYaw();
            baseYaw = MathHelper.wrapAngleTo180_float(baseYaw);
            float mod = Math.abs(!mc.thePlayer.onGround ? strictYaw : RotationUtils.getMovementYaw() % 90f);
            if (mod > 30f && mod < 70f && wrongDirectionTick > 10) {
                direction = (mod >= 45f) ? 1 : 0;
                wrongDirectionTick = 0;
            }
            wrongDirectionTick++;
            float yaw = baseYaw;
            if(mc.thePlayer.onGround) {
            	yaw = baseYaw + (direction == 0 ? 45f : -45f);
            }
            yaw = MathHelper.wrapAngleTo180_float(yaw + ((float) Math.random() - 0.5f) * 0.2f);
            float targetYaw = yaw;
            float diff = MathHelper.wrapAngleTo180_float(yaw - changeYaw);
            float maxJump = 90f;
            diff = MathHelper.clamp_float(diff, -maxJump, maxJump);
            float limit = (float) (rotationSpeed.getValue() * 10f);
            diff = MathHelper.clamp_float(diff, -limit, limit);
            changeYaw = MathHelper.wrapAngleTo180_float(changeYaw + diff);
            float err = MathHelper.wrapAngleTo180_float(changeYaw - targetYaw);
            if (Math.abs(err) > 45f) {
                enable = 2;
            }
            this.changeYaw = changeYaw;
            return new float[]{changeYaw, this.strictPitch};
        }else {
            float moveYaw = RotationUtils.getMovementYaw();
            float yaw = simple ? moveYaw : strictYaw;
            yaw = MathHelper.wrapAngleTo180_float(yaw);
            float diff = MathHelper.wrapAngleTo180_float(yaw - changeYaw);
            float limit = (float) (rotationSpeed.getValue() * 10f);
            diff = MathHelper.clamp_float(diff, -limit, limit);
            changeYaw = MathHelper.wrapAngleTo180_float(changeYaw + diff);
            float err = MathHelper.wrapAngleTo180_float(changeYaw - yaw);
            if (Math.abs(err) > 0.1f) {
                enable = 2;
            }
            this.changeYaw = changeYaw;
            return new float[]{(float) (changeYaw + Math.random()), this.strictPitch};
        }
    }
    
    static class PlaceData {
        public EnumFacing enumFacing;
        public BlockPos blockPos;

        PlaceData(EnumFacing enumFacing, BlockPos blockPos) {
            this.enumFacing = enumFacing;
            this.blockPos = blockPos;
        }

        public void set(EnumFacing facing, BlockPos pos) {
            this.enumFacing = facing;
            this.blockPos = pos;
        }
    }
}