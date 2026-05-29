package cryptix.module.combat;
import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.BlinkUtils;
import cryptix.utils.RotationUtils;
import cryptix.utils.Utils;
import cryptix.utils.render.EspUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class KillAura
extends Module {
	private ArrayList<EntityLivingBase> validTargets = new ArrayList<EntityLivingBase>();
    public EntityLivingBase target;
    public EntityLivingBase oldTarget;
    private EntityLivingBase lastTarget = null;
    private long lastSwitchTime = 0L;
    private long lastAttackTime;
    public boolean blocking;
	public boolean blinking;
    public boolean swapped;
    public boolean b2, unblock;
    public boolean b3, postBlock;
    private int blockTick;
    public int rotTick;
    public int asw;
    public int attack;
    public int nextTick;
    private final Setting switchDelay;
    private final Setting rotationRange;
    private final Setting blockRange;
    private final Setting attackRange;
    private final Setting minCPS;
    private final Setting maxCPS;
    public final Setting autoblock;
    private final Setting team;
    private final Setting movefix;
    private final Setting rotateBody;
    private final Setting targetESP;
    private final Setting swordOnly;
    private final Setting delay;
    private final Setting raycast;
    private final Setting reach, reach2;
    public Setting rotation;
    private int reached;
    public KillAura() {
        super("KillAura", 0, Category.COMBAT);
        ArrayList<String> autoblocks = new ArrayList<String>(Arrays.asList("None", "Fake", "Vanilla", "BlocksMC", "Hypixel", "Hypixel2", "Hypixel3", "NCP", "Vulcan", "Legit"));
        ArrayList<String> rotations = new ArrayList<String>(Arrays.asList("None", "Normal", "Hypixel", "Grim", "Vulcan"));
        this.minCPS = new Setting("Min CPS", (Module)this, 10.0, 1.0, 20.0, true);
        Client.instance.settingsManager.addSetting(this.minCPS);
        this.maxCPS = new Setting("Max CPS", (Module)this, 10.0, 1.0, 20.0, true);
        Client.instance.settingsManager.addSetting(this.maxCPS);
        this.autoblock = new Setting("Autoblock", (Module)this, "None", autoblocks);
        Client.instance.settingsManager.addSetting(this.autoblock);
        this.rotation = new Setting("Rotations", (Module)this, "Normal", rotations);
        Client.instance.settingsManager.addSetting(this.rotation);
        this.attackRange = new Setting("Attack Range", (Module)this, 3.0, 3.0, 10.0, false);
        Client.instance.settingsManager.addSetting(this.attackRange);
        this.blockRange = new Setting("Block Range", (Module)this, 3.0, 3.0, 10.0, false);
        Client.instance.settingsManager.addSetting(this.blockRange);
        this.rotationRange = new Setting("Rotation Range", (Module)this, 3.0, 3.0, 10.0, false);
        Client.instance.settingsManager.addSetting(this.rotationRange);
        this.switchDelay = new Setting("Switch Delay", (Module)this, 150.0, 0.0, 1000.0, true);
        Client.instance.settingsManager.addSetting(this.switchDelay);
        this.rotateBody = new Setting("Rotate Body", this, false);
        Client.instance.settingsManager.addSetting(this.rotateBody);
        this.targetESP = new Setting("TargetESP", this, false);
        Client.instance.settingsManager.addSetting(this.targetESP);
        this.swordOnly = new Setting("Sword Only", this, false);
        Client.instance.settingsManager.addSetting(this.swordOnly);
        this.team = new Setting("Teams", this, true);
        Client.instance.settingsManager.addSetting(this.team);
        this.movefix = new Setting("Movefix", this, false);
        Client.instance.settingsManager.addSetting(this.movefix);
        this.delay = new Setting("Delay While Attacking", this, false);
        Client.instance.settingsManager.addSetting(this.delay);
        this.raycast = new Setting("Raycast", this, false);
        Client.instance.settingsManager.addSetting(this.raycast);
        this.reach = new Setting("Hypixel Reach Bypass", this, false);
        Client.instance.settingsManager.addSetting(this.reach);
        this.reach2 = new Setting("Hypixel Reach", this, 3.1, 3.1, 3.5, 1);
        Client.instance.settingsManager.addSetting(this.reach2);
    }

    @Override
    public void onDisable() {
        RotationUtils.currentYaw = 0.0f;
        this.target = null;
        this.lastTarget = null;
        Client.movefix = false;
        attack = 10;
    }

    @Override
    public void onPreMotion() {
        if (this.target != null && this.isTargetInRange(this.target, this.rotationRange.getValue()) && !Client.instance.moduleManager.bedNuker.rotating) {
            ++this.rotTick;
            if(rotation.getString().equalsIgnoreCase("None")) return;
            float[] rotations = RotationUtils.getRotations(target, true);
            this.mc.thePlayer.rotationYawHead = rotations[0];
            if (this.rotateBody.getBoolean()) {
                this.mc.thePlayer.renderYawOffset = this.mc.thePlayer.rotationYawHead - MathHelper.clamp_float(MathHelper.wrapAngleTo180_float(this.mc.thePlayer.rotationYawHead - this.mc.thePlayer.renderYawOffset), -75.0f, 75.0f);
                this.mc.thePlayer.renderYawOffset += MathHelper.wrapAngleTo180_float(this.mc.thePlayer.rotationYawHead - this.mc.thePlayer.renderYawOffset) * 0.3f;
            }
            this.mc.thePlayer.rotationPitchHead = rotations[1];
            RotationUtils.currentYaw = rotations[0];
            RotationUtils.currentPitch = rotations[1];
        }
    }

    @Override
    public void onPostMotion() {
        if (this.target != null && this.isTargetInRange(this.target, this.blockRange.getValue()) && Utils.holdingSword() && this.autoblock.getString().equalsIgnoreCase("NCP")) {
            this.block();
        }
    }
    
    @Override
    public void onSprint() {
    	if(b2) {
    		BlinkUtils.stopBlink();
    		BlinkUtils.startBlink();
    		b2 = false;
    	}
    }
    
    public void sprint() {
    	
    }

    @Override
    public void onPreUpdate() {
        this.setDisplayName(String.valueOf(this.getName()) + this.getUppercaseSuffix(this.autoblock.getString()));
        if (Client.instance.moduleManager.getModuleByName("Scaffold").isToggled() || mc.currentScreen != null) {
            this.target = null;
            this.reset();
            RotationUtils.currentYaw = 0.0f;
            return;
        }
        float a = (float) this.attackRange.getValue();
        float b = (float) this.blockRange.getValue();
        float c = (float) this.rotationRange.getValue();
        float targetRange = Math.max(a, Math.max(b, c));
        this.target = this.getTarget(targetRange);
        if (this.target == null || Client.instance.moduleManager.bedNuker.rotating) {
            this.reset();
            RotationUtils.currentYaw = 0.0f;
            return;
        }
        if (!this.isTargetInRange(this.target, this.blockRange.getValue()) || !Utils.holdingSword()) {
            this.reset();
        }
        if (this.target != null) {
        	if (this.movefix.getBoolean()) {
                Client.movefix = true;
            }
            int maxCPSi;
            int minCPSi;
            ++this.nextTick;
            if (this.movefix.getBoolean()) {
                Client.movefix = true;
            }
            if (this.target != this.oldTarget) {
                this.rotTick = -1;
            }
            if ((minCPSi = (int)this.minCPS.getValue()) > (maxCPSi = (int)this.maxCPS.getValue())) {
                int temp = minCPSi;
                minCPSi = maxCPSi;
                maxCPSi = temp;
            }
            long currentTime = System.currentTimeMillis();
            int cps = minCPSi + Utils.random.nextInt(maxCPSi - minCPSi + 8);
            int delay = 1000 / cps;
            if (this.isTargetInRange(this.target, this.blockRange.getValue()) && Utils.holdingSword()) {
                if (this.autoblock.getString().equalsIgnoreCase("Vanilla")) {
                    this.sendPacket(new C08PacketPlayerBlockPlacement(this.mc.thePlayer.getHeldItem()));
                }
                if (this.autoblock.getString().equalsIgnoreCase("Vulcan")) {
                	b3 = true;
                    if (this.blocking) {
                        ++this.blockTick;
                        BlinkUtils.startBlink();
                        this.unblock();
                        ++this.attack;
                    } else {
                        if (this.isTargetInRange(this.target, this.attackRange.getValue()) && this.attack < 4) {
                            this.attack(this.target, true);
                            Utils.setMotion(0.97);
                            this.lastAttackTime = currentTime;
                        } else {
                            this.attack = 0;
                        }
                        this.block();
                        BlinkUtils.stopBlink();
                    }
                }
                if (this.autoblock.getString().equalsIgnoreCase("Legit")) {
                	b3 = true;
                    if (this.blocking) {
                        ++this.blockTick;
                        BlinkUtils.startBlink();
                        if(blocking) {
                        	this.unblock();
                        }
                    } else {
                    	cps = minCPSi + maxCPSi / 2;
                        delay = 1000 / cps;
                        if(!(currentTime - this.lastAttackTime < (long)delay || !this.isTargetInRange(this.target, this.attackRange.getValue()))) {
                            if (this.isTargetInRange(this.target, this.attackRange.getValue()) ) {
                                this.attack(this.target, true);
                                this.lastAttackTime = currentTime;
                            }
                        }else {
                        	if(BlinkUtils.isBlinking()) {
                            	return;
                            }
                        }
	                    if(!Client.instance.moduleManager.noslow.isToggled()) {
	                        nextTick = -1;
	                    }
	                    this.block();
                        if(!Client.instance.moduleManager.lagrange.blinking2) {
                        	BlinkUtils.stopBlink();
                        }
                    }
                }
                if (this.autoblock.getString().equalsIgnoreCase("BlocksMC")) {
                    ++this.asw;
                    KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), false);
                    b3 = true;
                    switch (this.asw) {
                        case 1: {
                        	attack++;
                            if (this.isTargetInRange(this.target, this.attackRange.getValue()) && !Client.instance.moduleManager.bedNuker.rotating && rotTick > 0) {
                            	MovingObjectPosition mop = RotationUtils.rayTrace(target.boundingBox, mc.thePlayer.rotationYawHead, mc.thePlayer.rotationPitchHead, 8.0);
                            	if(mop != null && attack < 8) {
	                                this.attack(this.target, false);
	                                this.sendPacket(new C02PacketUseEntity(target,  new Vec3(mop.hitVec.xCoord-target.posX, mop.hitVec.yCoord-target.posY, mop.hitVec.zCoord-target.posZ)));
	                            	this.sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
                            	}else {
                            		attack = 0;
                            	}
                            }
                            block();
                            BlinkUtils.stopBlink();
                            break;
                        }
                        case 2: {
	                    	BlinkUtils.startBlink();
	                        this.sendPacket(new C09PacketHeldItemChange((this.mc.thePlayer.inventory.currentItem + 1) % 9));
	                        this.swapped = true;
	                        break;
	                    }
	                    case 3: {
	                    	BlinkUtils.startBlink();
	                    	this.sendPacket(new C09PacketHeldItemChange(this.mc.thePlayer.inventory.currentItem));
	                    	if(blocking) {
	                    		unblock();
	                    	}
	                        this.swapped = false;
	                        this.asw = 0;
	                        break;
	                    }
                    }
                }
                if (this.autoblock.getString().equalsIgnoreCase("Hypixel")) {
                    switch (this.asw) {
                        case 0:
                        	b3 = true;
                        	BlinkUtils.startBlink();
                        	attack++;
                        	int slot = Utils.random.nextInt(9);
                        	while(slot == this.mc.thePlayer.inventory.currentItem) {
                        		slot = Utils.random.nextInt(9);
                        	}
                        	if(blocking) {
                        		unblock();
                        	}
                        	++this.asw;
                        	break;
                        case 1:
                            if (this.isTargetInRange(this.target, reach.getBoolean() && reached < 3 ? reach2.getValue() : this.attackRange.getValue()) && !Client.instance.moduleManager.bedNuker.rotating) {
                            	this.attack(this.target, true);
                            }else if(this.isTargetInRange(this.target, this.rotationRange.getValue()) && !Client.instance.moduleManager.bedNuker.rotating) {
                            	mc.thePlayer.swingItem();
                            }
                            reached++;
                            if(reached >= 4) {
                        		reached = 0;
                        	}
                            nextTick = -1;
                            block();
                            BlinkUtils.stopBlink();
                    		BlinkUtils.startBlink();
                            this.asw = 0;
                            break;
                    }
                }
                if (this.autoblock.getString().equalsIgnoreCase("Hypixel2")) {
                    switch (this.asw) {
                        case 0:
                        	b3 = true;
                        	BlinkUtils.startBlink();
                        	attack++;
                        	int slot = Utils.random.nextInt(9);
                        	while(slot == this.mc.thePlayer.inventory.currentItem) {
                        		slot = Utils.random.nextInt(9);
                        	}
                        	if(blocking) {
                        		unblock();
                        	}
                        	sendPacket(new C09PacketHeldItemChange(slot));
                    		swapped = true;
                        	++this.asw;
                        	break;
                        case 1:
                        	if(swapped) {
                        		sendPacket(new C09PacketHeldItemChange(this.mc.thePlayer.inventory.currentItem));
                        		swapped = false;
                        	}
                        	++this.asw;
                        	break;
                        case 2:
                        	++this.asw;
                        	if(!postBlock)break;
                        case 3:
                        	++this.asw;
                        	if(!postBlock)break;
                        case 4:
                            if (this.isTargetInRange(this.target, reach.getBoolean() && reached < 2 ? reach2.getValue() : this.attackRange.getValue()) && Client.instance.moduleManager.bedNuker.bedPos == null) {
                            	this.attack(this.target, true);
                            }else if(this.isTargetInRange(this.target, this.rotationRange.getValue()) && Client.instance.moduleManager.bedNuker.bedPos == null) {
                            	mc.thePlayer.swingItem();
                            }
                            reached++;
                            if(this.isTargetInRange(this.target, this.attackRange.getValue())) {
                        		reached = 0;
                        	}
    	                    this.block();
    	                    b2 = true;
    	                    postBlock = attack % 2 == 0;
                            this.asw = 0;
                            break;
                    }
                }
                if (this.autoblock.getString().equalsIgnoreCase("Hypixel3")) {
                	switch (this.asw) {
                    case 0:
                    	b3 = true;
                    	BlinkUtils.startBlink();
                    	attack++;
                    	int slot = Utils.random.nextInt(9);
                    	while(slot == this.mc.thePlayer.inventory.currentItem) {
                    		slot = Utils.random.nextInt(9);
                    	}
                    	if(blocking) {
                    		unblock();
                    		if(postBlock) {
	                    		sendPacket(new C09PacketHeldItemChange(slot));
	                			swapped = true;
	                			postBlock = false;
                    		}
                    	}
                    	++this.asw;
                    	break;
                    case 1:
                    	++this.asw;
                    	if(swapped) {
                    		sendPacket(new C09PacketHeldItemChange(this.mc.thePlayer.inventory.currentItem));
                    		swapped = false;
                    		break;
                    	}
                    case 2:
                        if (this.isTargetInRange(this.target, reach.getBoolean() && reached < 3 ? reach2.getValue() : this.attackRange.getValue()) && !Client.instance.moduleManager.bedNuker.rotating) {
                            this.attack(this.target, true);
                        }else if(this.isTargetInRange(this.target, this.rotationRange.getValue()) && !Client.instance.moduleManager.bedNuker.rotating) {
                        	mc.thePlayer.swingItem();
                        }
                        reached++;
                        if(reached >= 4) {
                    		reached = 0;
                    	}
                		if(attack % 2 != 0) {
                			nextTick = -1;
                		}else {
                			postBlock = true;
                		}
                        block();
                        b2 = true;
                        this.asw = 0;
                        break;
                	}
                }
                if (this.autoblock.getString().equalsIgnoreCase("NCP")) {
                    this.unblock();
                }
            } else {
                this.reset();
            }
            if (!(!this.isTargetInRange(this.target, reach.getBoolean() && reached < 3 ? reach2.getValue(): this.attackRange.getValue()) || currentTime - this.lastAttackTime < (long)delay && this.minCPS.getValue() + this.maxCPS.getValue() != 40.0 || this.autoblock.getString().equalsIgnoreCase("BlocksMC") || this.autoblock.getString().equalsIgnoreCase("BlocksMC2") || this.autoblock.getString().equalsIgnoreCase("Legit") || this.autoblock.getString().equalsIgnoreCase("Vulcan") || this.autoblock.getString().equalsIgnoreCase("Hypixel") || this.autoblock.getString().equalsIgnoreCase("Hypixel2") || this.autoblock.getString().equalsIgnoreCase("Hypixel3"))) {
                this.attack(this.target, false);
                this.lastAttackTime = currentTime;
            }
        }
        this.oldTarget = this.target;
    }

    @Override
    public void onRender3D() {
        if (this.target != null && this.targetESP.getBoolean()) {
            EspUtils.drawKillAuraRing(this.target, 0.6f, 255);
        }
    }

    @Override
    public void onPreInput() {
        if (this.target != null && this.autoblock.getString().equalsIgnoreCase("BlocksMC") && this.isTargetInRange(this.target, this.blockRange.getValue()) && Utils.holdingSword() && this.nextTick < 0) {
            this.mc.thePlayer.movementInput.moveStrafe *= 0.2f;
            this.mc.thePlayer.movementInput.moveForward *= 0.2f;
            this.mc.thePlayer.sprintToggleTimer = 0;
        }
        if (this.target != null && this.autoblock.getString().equalsIgnoreCase("Legit") && this.isTargetInRange(this.target, this.blockRange.getValue()) && Utils.holdingSword() && this.nextTick < 0) {
            this.mc.thePlayer.movementInput.moveStrafe *= 0.2f;
            this.mc.thePlayer.movementInput.moveForward *= 0.2f;
            this.mc.thePlayer.sprintToggleTimer = 0;
        }
        if (this.target != null && this.autoblock.getString().equalsIgnoreCase("Hypixel") && this.isTargetInRange(this.target, this.blockRange.getValue()) && Utils.holdingSword() && this.nextTick < 0) {
            this.mc.thePlayer.movementInput.moveStrafe *= 0.2f;
            this.mc.thePlayer.movementInput.moveForward *= 0.2f;
            this.mc.thePlayer.sprintToggleTimer = 0;
        }
        if (this.target != null && this.autoblock.getString().equalsIgnoreCase("Hypixel3") && this.isTargetInRange(this.target, this.blockRange.getValue()) && Utils.holdingSword() && this.nextTick < 0) {
            this.mc.thePlayer.movementInput.moveStrafe *= 0.2f;
            this.mc.thePlayer.movementInput.moveForward *= 0.2f;
            this.mc.thePlayer.sprintToggleTimer = 0;
        }
    }

    public void attack(EntityLivingBase e, boolean interact) {
    	if(Client.instance.moduleManager.velo.delaying && !delay.getBoolean()) return;
    	if(Client.instance.moduleManager.bedNuker.bedPos != null) return;
    	MovingObjectPosition mop = RotationUtils.rayCastEntity(8, mc.thePlayer.rotationYawHead, mc.thePlayer.rotationPitchHead);
    	if (!raycast.getBoolean() || (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)) {
    	    mc.thePlayer.swingItem();
    	    mc.playerController.attackEntity(mc.thePlayer, e);
    	    if (interact) {
    	    	sendPacket(new C02PacketUseEntity(e, new Vec3(0,0,0)));
    	        sendPacket(new C02PacketUseEntity(e, C02PacketUseEntity.Action.INTERACT));
    	    }
    	}
    }

    private boolean isTargetInRange(EntityLivingBase target, double range) {
        float[] rotations = RotationUtils.getRotations(target, false);
        return this.calculateRange(target, rotations, range);
    }

    private boolean calculateRange(Entity target, float[] rotations, double range) {
        Vec3 vec3 = this.mc.thePlayer.getPositionEyes(1.0f);
        Vec3 vec31 = RotationUtils.getVectorForRotation(rotations[1], rotations[0]);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
        AxisAlignedBB axisalignedbb = target.getEntityBoundingBox();
        MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
        return movingobjectposition != null && vec3 != null && vec3.distanceTo(movingobjectposition.hitVec) <= range;
    }

    private EntityLivingBase getTarget(double range) {
        double currentDist;
        EntityLivingBase entity;
        EntityLivingBase extendedRangeTarget = null;
        validTargets.clear();
        for (Entity object : this.mc.theWorld.loadedEntityList) {
            if (!(object instanceof EntityLivingBase) || (entity = (EntityLivingBase)object) == this.mc.thePlayer || AntiBot.isBot(entity) || Utils.teamMate(entity) && this.team.getBoolean() || this.mc.currentScreen instanceof GuiInventory || this.swordOnly.getBoolean() && !Utils.holdingSword() || Client.instance.commandManager.friend.isFriend(entity.getName()) || !(entity instanceof EntityPlayer) && !(entity instanceof EntityMob) && !(entity instanceof EntityAnimal)) continue;
            
            currentDist = (double)this.mc.thePlayer.getDistanceToEntity(entity);
            
            if (currentDist <= this.attackRange.getValue()) {
                validTargets.add(entity);
            } else if (currentDist <= range && extendedRangeTarget == null) {
                extendedRangeTarget = entity;
            }
        }
        if (validTargets.isEmpty()) {
            return extendedRangeTarget;
        }
        long currentTime = System.currentTimeMillis();
        if (this.lastTarget == null || (double)(currentTime - this.lastSwitchTime) > this.switchDelay.getValue() || !validTargets.contains(this.lastTarget) || this.lastTarget.isDead) {
            EntityLivingBase newTarget;
            if (validTargets.size() > 1 && this.lastTarget != null) {
                validTargets.remove(this.lastTarget);
            }
            int index = Utils.random.nextInt(validTargets.size());
            this.lastTarget = newTarget = (EntityLivingBase)validTargets.get(index);
            this.lastSwitchTime = currentTime;
        }
        return this.lastTarget;
    }

    public void reset() {
        this.blockTick = 0;
        this.attack = 0;
        boolean releasing = false;
        if (this.swapped) {
            this.swapped = false;
            this.sendPacket(new C09PacketHeldItemChange(this.mc.thePlayer.inventory.currentItem));
            releasing = true;
        }
        if ((this.autoblock.getString().equalsIgnoreCase("Hypixel") || this.autoblock.getString().equalsIgnoreCase("Hypixel2") || this.autoblock.getString().equalsIgnoreCase("Hypixel3")) && this.asw == 0 && this.blocking) {
            BlinkUtils.startBlink();
            int slot = Utils.random.nextInt(9);
            while (slot == this.mc.thePlayer.inventory.currentItem) {
                slot = Utils.random.nextInt(9);
            }
            this.sendPacket(new C09PacketHeldItemChange(slot));
            swapped = true;
            this.unblock();
            ++this.asw;
        } else {
        	if (this.b3) {
        		BlinkUtils.stopBlink();
                this.b3 = false;
            }
            this.asw = 0;
        }
        if(blocking) {
        	unblock();
        }
        if(swapped) {
        	target = lastTarget;
        }
        this.rotTick = 0;
    }

    public void block() {
        this.sendPacket(new C08PacketPlayerBlockPlacement(this.mc.thePlayer.getHeldItem()));
        this.blocking = true;
    }

    private void unblock() {
    	this.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
    	blocking = false;
    }
}