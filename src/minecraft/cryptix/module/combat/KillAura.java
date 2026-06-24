// Decompiled with: CFR 0.152
// Class Version: 8
package cryptix.module.combat;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.BooleanSetting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.gui.clickgui.settings.ModeSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.module.combat.AntiBot;
import cryptix.other.event.Event;
import cryptix.other.event.EventManager;
import cryptix.other.event.events.PacketReceiveEvent;
import cryptix.other.event.events.RotationEvent;
import cryptix.utils.BlinkUtils;
import cryptix.utils.RotationUtils;
import cryptix.utils.Utils;
import cryptix.utils.render.EspUtils;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class KillAura
extends Module {
    private ArrayList<EntityLivingBase> validTargets = new ArrayList();
    public EntityLivingBase target;
    public EntityLivingBase oldTarget;
    private EntityLivingBase lastTarget = null;
    private long lastSwitchTime = 0L;
    private long lastAttackTime;
    public boolean blocking;
    public boolean blinking;
    public boolean swapped;
    public boolean b2;
    public boolean unblock;
    public boolean b3;
    public boolean postBlock;
    private int blockTick;
    public int rotTick;
    public int asw;
    public int attack;
    public int nextTick;
    private final DoubleSetting switchDelay = new DoubleSetting("Switch Delay", (Module)this, 150.0, 0.0, 1000.0, true);
    private final DoubleSetting rotationRange = new DoubleSetting("Rotation Range", (Module)this, 3.0, 3.0, 10.0, false);;
    private final DoubleSetting blockRange = new DoubleSetting("Block Range", (Module)this, 3.0, 3.0, 10.0, false);
    private final DoubleSetting attackRange = new DoubleSetting("Attack Range", (Module)this, 3.0, 3.0, 10.0, false);
    private final DoubleSetting minCPS = new DoubleSetting("Min CPS", (Module)this, 10.0, 1.0, 20.0, true);
    private final DoubleSetting maxCPS = new DoubleSetting("Max CPS", (Module)this, 10.0, 1.0, 20.0, true);
    public final ModeSetting autoblock = new ModeSetting("Autoblock", (Module)this, "None", new ArrayList<String>(Arrays.asList("None", "Fake", "Vanilla", "BlocksMC", "Hypixel", "Hypixel2", "Hypixel3", "NCP", "Vulcan", "Legit")));
    private final BooleanSetting team = new BooleanSetting("Teams", this, true);
    private final BooleanSetting movefix = new BooleanSetting("Movefix", this, false);
    private final BooleanSetting targetESP = new BooleanSetting("TargetESP", this, false);
    private final BooleanSetting swordOnly = new BooleanSetting("Sword Only", this, false);
    private final BooleanSetting delay = new BooleanSetting("Delay While Attacking", this, false);
    private final BooleanSetting raycast = new BooleanSetting("Raycast", this, false);
    private final BooleanSetting reach = new BooleanSetting("Hypixel Reach Bypass", this, false);
    private final BooleanSetting switchAttack = new BooleanSetting("Switch On Attack", this, false);
    private final DoubleSetting reach2 = new DoubleSetting("Hypixel Reach", (Module)this, 3.1, 3.1, 3.5, 1);
    public ModeSetting rotation = new ModeSetting("Rotations", (Module)this, "Normal", new ArrayList<String>(Arrays.asList("None", "Normal", "Hypixel", "Grim", "Vulcan")));
    private int reached, reduce;

    public KillAura() {
        super("KillAura", 0, Category.COMBAT);
        this.addSetting(this.minCPS, this.maxCPS, this.autoblock, this.rotation, this.attackRange, this.blockRange, this.rotationRange, this.switchDelay,
        		this.targetESP, this.swordOnly, this.team, this.movefix, this.delay, this.raycast, this.switchAttack, this.reach, this.reach2);
    }

    @Override
    public void onDisable() {
        RotationUtils.currentYaw = 0.0f;
        this.target = null;
        this.lastTarget = null;
        Client.movefix = false;
        this.attack = 10;
    }

    @Override
    public void onPreMotion() {
    	
    }

    @Override
    public void onPostMotion() {
        if (this.target != null && this.isTargetInRange(this.target, this.blockRange.getValue()) && Utils.holdingSword() && this.autoblock.getString().equalsIgnoreCase("NCP")) {
            this.block();
        }
    }

    @Override
    public void onSprint() {
        if (this.b2) {
            if(this.autoblock.getString().equalsIgnoreCase("Hypixel3")) {
            	block();
            }
            //if(!Client.instance.moduleManager.lagrange.blinking2) {
	            BlinkUtils.stopBlink();
	            BlinkUtils.startBlink();
            //}
            this.b2 = false;
        }
    }

    @Override
    public void onEvent(Event e) {
    	if(e instanceof PacketReceiveEvent) {
    		PacketReceiveEvent ev = (PacketReceiveEvent) e;
    		if(ev.getPacket() instanceof S12PacketEntityVelocity) {
    			S12PacketEntityVelocity packet = (S12PacketEntityVelocity) ev.getPacket();
    			if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
    				reduce = 3;
    			}
        	}
    	}
    	if(e instanceof RotationEvent) {
    		if (this.target != null && this.isTargetInRange(this.target, this.rotationRange.getValue()) && !Client.instance.moduleManager.bedNuker.rotating) {
                ++this.rotTick;
                if (this.rotation.getString().equalsIgnoreCase("None")) {
                    return;
                }
                float[] rotations = RotationUtils.getRotations(this.target, true);
                ((RotationEvent) e).setYaw(rotations[0]);
                ((RotationEvent) e).setPitch(rotations[1]);
                RotationUtils.currentYaw = rotations[0];
                RotationUtils.currentPitch = rotations[1];
            }
    	}
    }

    @Override
    public void onPreUpdate() {
        this.setDisplayName(String.valueOf(String.valueOf(this.getName())) + this.getUppercaseSuffix(this.autoblock.getString()));
        reduce--;
        if (Client.instance.moduleManager.getModuleByName("Scaffold").isToggled() || this.mc.currentScreen != null) {
            this.target = null;
            this.reset();
            RotationUtils.currentYaw = 0.0f;
            return;
        }
        float a = (float)this.attackRange.getValue();
        float b = (float)this.blockRange.getValue();
        float c = (float)this.rotationRange.getValue();
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
            int maxCPSi;
            int minCPSi;
            if (this.movefix.getBoolean()) {
                Client.movefix = true;
            }
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
                    this.b3 = true;
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
                    this.b3 = true;
                    if (this.blocking) {
                        ++this.blockTick;
                        BlinkUtils.startBlink();
                        if (this.blocking) {
                            this.unblock();
                        }
                    } else {
                        cps = minCPSi + maxCPSi / 2;
                        delay = 1000 / cps;
                        if (currentTime - this.lastAttackTime < (long)delay) {
                        	return;
                        }
                        if (this.isTargetInRange(this.target, this.attackRange.getValue())) {
                            this.attack(this.target, true);
                        }
                        this.lastAttackTime = currentTime;
                        if (!Client.instance.moduleManager.noslow.isToggled()) {
                            this.nextTick = -1;
                        }
                        this.block();
                        if (!Client.instance.moduleManager.lagrange.blinking2) {
                            BlinkUtils.stopBlink();
                        }
                    }
                }
                if (this.autoblock.getString().equalsIgnoreCase("BlocksMC")) {
                    ++this.asw;
                    KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), false);
                    this.b3 = true;
                    switch (this.asw) {
                        case 1: {
                            ++this.attack;
                            if (this.isTargetInRange(this.target, this.attackRange.getValue()) && !Client.instance.moduleManager.bedNuker.rotating && this.rotTick > 0) {
                                MovingObjectPosition mop = RotationUtils.rayTrace(this.target.boundingBox, this.mc.thePlayer.rotationYawHead, this.mc.thePlayer.rotationPitchHead, 8.0);
                                if (mop != null && this.attack < 8) {
                                    this.attack(this.target, false);
                                    this.sendPacket(new C02PacketUseEntity((Entity)this.target, new Vec3(mop.hitVec.xCoord - this.target.posX, mop.hitVec.yCoord - this.target.posY, mop.hitVec.zCoord - this.target.posZ)));
                                    this.sendPacket(new C02PacketUseEntity((Entity)this.target, C02PacketUseEntity.Action.INTERACT));
                                } else {
                                    this.attack = 0;
                                }
                            }
                            this.block();
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
                            if (this.blocking) {
                                this.unblock();
                            }
                            this.swapped = false;
                            this.asw = 0;
                        }
                    }
                }
                if (this.autoblock.getString().equalsIgnoreCase("Hypixel")) {
                    switch (this.asw) {
                        case 0: {
                            this.b3 = true;
                            BlinkUtils.startBlink();
                            ++this.attack;
                            int slot = Utils.random.nextInt(9);
                            while (slot == this.mc.thePlayer.inventory.currentItem) {
                                slot = Utils.random.nextInt(9);
                            }
                            if (this.blocking) {
                                this.unblock();
                            }
                            ++this.asw;
                            break;
                        }
                        case 1: {
                            if (this.isTargetInRange(this.target, this.reach.getBoolean() && this.reached < 3 ? this.reach2.getValue() : this.attackRange.getValue()) && !Client.instance.moduleManager.bedNuker.rotating) {
                                this.attack(this.target, true);
                            } else if (this.isTargetInRange(this.target, this.rotationRange.getValue()) && !Client.instance.moduleManager.bedNuker.rotating) {
                                this.mc.thePlayer.swingItem();
                            }
                            ++this.reached;
                            if (this.reached >= 4) {
                                this.reached = 0;
                            }
                            this.nextTick = -1;
                            this.block();
                            BlinkUtils.stopBlink();
                            BlinkUtils.startBlink();
                            this.asw = 0;
                        }
                    }
                }
                if (this.autoblock.getString().equalsIgnoreCase("Hypixel2")) {
                    switch (this.asw) {
                        case 0: {
                            this.b3 = true;
                            BlinkUtils.startBlink();
                            ++this.attack;
                            int slot = Utils.random.nextInt(9);
                            while (slot == this.mc.thePlayer.inventory.currentItem) {
                                slot = Utils.random.nextInt(9);
                            }
                            this.sendPacket(new C09PacketHeldItemChange(slot));
                            if (this.blocking) {
                                this.unblock();
                            }
                            this.swapped = true;
                            ++this.asw;
                            break;
                        }
                        case 1: {
                            if (this.swapped) {
                                this.sendPacket(new C09PacketHeldItemChange(this.mc.thePlayer.inventory.currentItem));
                                this.swapped = false;
                            }
                            ++this.asw;
                            break;
                        }
                        case 2: {
                            ++this.asw;
                            if (!this.postBlock) break;
                        }
                        case 3: {
                            ++this.asw;
                            if (!this.postBlock) break;
                        }
                        case 4: {
                            if (this.isTargetInRange(this.target, this.reach.getBoolean() && this.reached < 2 ? this.reach2.getValue() : this.attackRange.getValue()) && Client.instance.moduleManager.bedNuker.bedPos == null) {
                                this.attack(this.target, true);
                            } else if (this.isTargetInRange(this.target, this.rotationRange.getValue()) && Client.instance.moduleManager.bedNuker.bedPos == null) {
                                this.mc.thePlayer.swingItem();
                            }
                            ++this.reached;
                            if (this.isTargetInRange(this.target, this.attackRange.getValue())) {
                                this.reached = 0;
                            }
                            this.block();
                            this.b2 = true;
                            this.postBlock = this.attack % 2 == 0;
                            this.asw = 0;
                        }
                    }
                }
                if (this.autoblock.getString().equalsIgnoreCase("Hypixel3")) {
                	switch (this.asw) {
	                    case 0: {
	                    	b3 = true;
	                    	if(postBlock) {
	                    		block();
		                        BlinkUtils.stopBlink();
	                    	}
	                    	BlinkUtils.startBlink();
	                        ++this.attack;
	                        if (this.blocking && !postBlock) {
	                            this.unblock();
	                        }
	                        ++this.asw;
	                        break;
	                    }
	                    case 1: {
	                    	if (this.blocking) {
	                            this.unblock();
	                        }
	                        ++this.asw;
	                        break;
	                    }
	                    case 2: {
	                        if (this.isTargetInRange(this.target, this.reach.getBoolean() && this.reached < 2 ? this.reach2.getValue() : this.attackRange.getValue()) && Client.instance.moduleManager.bedNuker.bedPos == null) {
	                            this.attack(this.target, true);
	                        } else if (this.isTargetInRange(this.target, this.rotationRange.getValue()) && Client.instance.moduleManager.bedNuker.bedPos == null) {
	                            this.mc.thePlayer.swingItem();
	                        }
	                        ++this.reached;
	                        if (this.isTargetInRange(this.target, this.attackRange.getValue())) {
	                            this.reached = 0;
	                        }
	                        postBlock = false;
	                        if(!postBlock) {
	                        	nextTick = -1;
		                        block();
		                        if(!Client.instance.moduleManager.lagrange.blinking2) {
			                        b2 = true;
		                        }
	                        }
	                        this.asw = 0;
	                    }
	                }
                }
                if (this.autoblock.getString().equalsIgnoreCase("NCP")) {
                    this.unblock();
                }
            } else {
                this.reset();
            }
            if (!(!this.isTargetInRange(this.target, this.reach.getBoolean() && this.reached < 3 ? this.reach2.getValue() : this.attackRange.getValue()) || currentTime - this.lastAttackTime < (long)delay && this.minCPS.getValue() + this.maxCPS.getValue() != 40.0 || this.autoblock.getString().equalsIgnoreCase("BlocksMC") || this.autoblock.getString().equalsIgnoreCase("BlocksMC2") || this.autoblock.getString().equalsIgnoreCase("Legit") || this.autoblock.getString().equalsIgnoreCase("Vulcan") || this.autoblock.getString().equalsIgnoreCase("Hypixel") || this.autoblock.getString().equalsIgnoreCase("Hypixel2") || this.autoblock.getString().equalsIgnoreCase("Hypixel3"))) {
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
        if (Client.instance.moduleManager.velo.delaying && !this.delay.getBoolean()) {
            return;
        }
        if (Client.instance.moduleManager.bedNuker.bedPos != null) {
            return;
        }
        MovingObjectPosition mop = RotationUtils.rayCastEntity(8.0, EventManager.ROTATION_EVENT.getYaw(), EventManager.ROTATION_EVENT.getPitch());
        if (!this.raycast.getBoolean() || mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            this.mc.thePlayer.swingItem();
            this.mc.playerController.attackEntity(this.mc.thePlayer, e);
            if (interact) {
                this.sendPacket(new C02PacketUseEntity((Entity)e, new Vec3(0.0, 0.0, 0.0)));
                this.sendPacket(new C02PacketUseEntity((Entity)e, C02PacketUseEntity.Action.INTERACT));
            }
        }
        long currentTime = System.currentTimeMillis();
        if(this.validTargets.isEmpty()) {
        	return;
        }
        if (this.lastTarget == null || (double)(currentTime - this.lastSwitchTime) > this.switchDelay.getValue() && this.switchAttack.getBoolean() || !this.validTargets.contains(this.lastTarget) || this.lastTarget.isDead) {
        	EntityLivingBase newTarget;
            if (this.validTargets.size() > 1 && this.lastTarget != null) {
                this.validTargets.remove(this.lastTarget);
            }
            int index = Utils.random.nextInt(this.validTargets.size());
            this.lastTarget = newTarget = this.validTargets.get(index);
            this.lastSwitchTime = currentTime;
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
        EntityLivingBase extendedRangeTarget = null;
        this.validTargets.clear();
        for (Entity object : this.mc.theWorld.loadedEntityList) {
            EntityLivingBase entity;
            if (!(object instanceof EntityLivingBase) || (entity = (EntityLivingBase)object) == this.mc.thePlayer || AntiBot.isBot(entity) || Utils.teamMate(entity) && this.team.getBoolean() || this.mc.currentScreen instanceof GuiInventory || this.swordOnly.getBoolean() && !Utils.holdingSword() || Client.instance.commandManager.friend.isFriend(entity.getName()) || !(entity instanceof EntityPlayer) && !(entity instanceof EntityMob) && !(entity instanceof EntityAnimal)) continue;
            double currentDist = this.mc.thePlayer.getDistanceToEntity(entity);
            if (currentDist <= this.attackRange.getValue()) {
                this.validTargets.add(entity);
                continue;
            }
            if (!(currentDist <= range) || extendedRangeTarget != null) continue;
            extendedRangeTarget = entity;
        }
        if (this.validTargets.isEmpty()) {
            return extendedRangeTarget;
        }
        long currentTime = System.currentTimeMillis();
        if (this.lastTarget == null || (double)(currentTime - this.lastSwitchTime) > this.switchDelay.getValue() && !this.switchAttack.getBoolean() || !this.validTargets.contains(this.lastTarget) || this.lastTarget.isDead) {
            EntityLivingBase newTarget;
            if (this.validTargets.size() > 1 && this.lastTarget != null) {
                this.validTargets.remove(this.lastTarget);
            }
            int index = Utils.random.nextInt(this.validTargets.size());
            this.lastTarget = newTarget = this.validTargets.get(index);
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
        if ((this.autoblock.getString().equalsIgnoreCase("Hypixel") || this.autoblock.getString().equalsIgnoreCase("Hypixel2")) && this.blocking) {
            BlinkUtils.startBlink();
            int slot = Utils.random.nextInt(9);
            while (slot == this.mc.thePlayer.inventory.currentItem) {
                slot = Utils.random.nextInt(9);
            }
            this.sendPacket(new C09PacketHeldItemChange(slot));
            this.swapped = true;
            this.unblock();
            ++this.asw;
        } else {
            if (this.b3) {
                BlinkUtils.stopBlink();
                this.b3 = false;
            }
            this.asw = 0;
        }
        if (this.blocking) {
            this.unblock();
        }
        if (this.swapped) {
            this.target = this.lastTarget;
        }
        this.rotTick = 0;
    }

    public void block() {
        this.sendPacket(new C08PacketPlayerBlockPlacement(this.mc.thePlayer.getHeldItem()));
        this.blocking = true;
    }

    private void unblock() {
        this.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        this.blocking = false;
    }
}
