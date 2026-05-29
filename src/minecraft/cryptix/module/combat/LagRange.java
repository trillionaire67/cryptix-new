package cryptix.module.combat;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.other.event.Event;
import cryptix.other.event.events.PacketReceiveEvent;
import cryptix.other.event.events.PacketSendEvent;
import cryptix.utils.BlinkUtils;
import cryptix.utils.BlinkUtils.DelayedPacket;
import cryptix.utils.Utils;
import cryptix.utils.render.RenderUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class LagRange
extends Module {
	private Setting delay = new Setting("Delay", this, true);
    private Setting blinkLimit = new Setting("Delay (far)", (Module)this, 200.0, 50.0, 1000.0, true);
    private Setting blinkLimit2 = new Setting("Delay (close)", (Module)this, 200.0, 50.0, 1000.0, true);
    private Setting buffer = new Setting("Buffer Abuse", this, true);
    private Setting show = new Setting("Show Position", this, true);
    private Setting blink = new Setting("Blink", this, true);
    private Setting blinkDelay = new Setting("Blink Delay", (Module)this, 10.0, 1.0, 20.0, true);
    private Setting attackDelay = new Setting("Blink Attack Delay", (Module)this, 10.0, 5.0, 20.0, true);
    private Setting kaonly = new Setting("KillAura Only", this, true);
    private Setting wponly = new Setting("Weapon Only", this, true);
    public List<DelayedPacket> packets = new CopyOnWriteArrayList<>();
    private Vec3 pos = new Vec3(0,0,0), pos2 = new Vec3(0,0,0);
    public boolean blinking, blinking2;
    public boolean attack;
    private boolean reset;
    private int blinkTime,blinkTime2,attackTime;
    private double prevDist;

    public LagRange() {
        super("LagRange", 0, Category.COMBAT);
        this.addSetting(this.delay, this.blinkLimit, this.blinkLimit2, this.buffer, this.show, this.blink, this.blinkDelay, this.attackDelay, this.kaonly, this.wponly);
    }

    @Override
    public void onDisable() {
        this.release();
    }
    @Override
    public void onPreMotion() {
    	if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.ticksExisted < 20) {
    		release2();
            return;
        }
    	EntityLivingBase target = findTarget(6);
        if (target != null && blink.getBoolean()) {
        	double distance = this.mc.thePlayer.getDistanceToEntity(target);
        	if (this.blinkTime2 == 0) {
                this.pos2.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            }
        	++this.blinkTime2;
        	attackTime++;
        	if(this.pos2.distanceTo(target.getPositionVector()) < this.mc.thePlayer.getPositionVector().distanceTo(target.getPositionVector()) && attackTime >= attackDelay.getValue() || attack || blinkTime2 > this.blinkDelay.getValue()) {
        		release2();
        	}else {
        		if(distance <= prevDist) {
        			blinking2 = true;
        			BlinkUtils.startBlink();
        		}
        	}
        	this.prevDist = distance;
        }else {
        	release2();
        }
    }
    @Override
    public void onPostMotion() {
    	if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.ticksExisted < 20) {
    		reset();
            return;
        }
    	this.setDisplayName(String.valueOf(String.valueOf(this.getName())) + " §7" + this.blinkLimit.getValue());
        EntityLivingBase target = findTarget(6);
        if (target != null && delay.getBoolean()) {
            double distance = this.mc.thePlayer.getDistanceToEntity(target);
            this.blinking = true;
            if (this.blinkTime == 0) {
                this.pos.setPosition(target.posX, target.posY, target.posZ);
            }
            ++this.blinkTime;
            if(pos != null && mc.thePlayer.getDistance(pos.xCoord, pos.yCoord, pos.zCoord) < mc.thePlayer.getDistance(target.posX, target.posY, target.posZ)) {
            	reset();
            }
            if(reset) {
            	reset();
            	reset = false;
            }
            List<DelayedPacket> list2 = this.packets;
            synchronized (list2) {
            	ArrayList<DelayedPacket> snapshot = new ArrayList<>(this.packets);
                for (DelayedPacket packet : snapshot) {
                	if(packet == null) continue;
                    if (System.currentTimeMillis() - packet.timestamp > (attack ? blinkLimit.getValue() : blinkLimit2.getValue())){;
	                    this.blinking = false;
	                    mc.addScheduledTask(() -> packet.packet.processPacket(mc.getNetHandler()));
	                    this.packets.remove(packet);
	                    this.blinking = true;
                    }
                }
            }
        } else {
            this.reset();
        }
        attack = false;
    }

    @Override
    public void onRender3D() {
        if (this.blinking && this.pos != null && this.show.getBoolean()) {
            this.drawBox(this.pos);
        }
        if (this.blinking2 && this.pos2 != null && this.show.getBoolean()) {
            this.drawBox(this.pos2);
        }
    }

    @Override
	public void onEvent(Event event) {
		if(event instanceof PacketReceiveEvent) {
			PacketReceiveEvent e = (PacketReceiveEvent) event;
		    	try {
		            if (this.blinking && !e.isCancelled()) {
		                S18PacketEntityTeleport wrapper;
		                if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.ticksExisted < 20) {
		                    reset();
		                    return;
		                }
		                if(e.getPacket() instanceof C02PacketUseEntity) {
		                	this.attackTime = 0;
		                }
		                if (e.getPacket() instanceof S19PacketEntityStatus
		                        || e.getPacket() instanceof S02PacketChat
		                        || e.getPacket() instanceof S0BPacketAnimation
		                        || e.getPacket() instanceof S06PacketUpdateHealth
		                        || e.getPacket() instanceof S0CPacketSpawnPlayer
		                )
		                    return;
		                if(buffer.getBoolean() && !(e.getPacket() instanceof S32PacketConfirmTransaction || e.getPacket() instanceof S12PacketEntityVelocity || e.getPacket() instanceof S14PacketEntity || e.getPacket() instanceof S18PacketEntityTeleport)) return;
		
		                if (e.getPacket() instanceof S08PacketPlayerPosLook || e.getPacket() instanceof S40PacketDisconnect) {
		                    reset();
		                    return;
		                }
		                EntityLivingBase target = findTarget(6);
		                if (e.getPacket() instanceof S13PacketDestroyEntities) {
		                    S13PacketDestroyEntities wrapper2 = (S13PacketDestroyEntities) e.getPacket();
		                    for (int id : wrapper2.getEntityIDs()) {
		                        if (id == target.getEntityId()) {
		                        	reset();
		                            return;
		                        }
		                    }
		                } 
		                if (target == null) {
		                    return;
		                }
		                Packet packet = e.getPacket();
		                if (packet instanceof S14PacketEntity && this.pos != null) {
		                    S14PacketEntity wrapper2 = (S14PacketEntity)packet;
		                    if (wrapper2.entityId == target.getEntityId()) {
		                        this.pos = this.pos.addVector((double)wrapper2.func_149062_c() / 32.0, (double)wrapper2.func_149061_d() / 32.0, (double)wrapper2.func_149064_e() / 32.0);
		                    }
		                } else if (packet instanceof S18PacketEntityTeleport && (wrapper = (S18PacketEntityTeleport)packet).getEntityId() == target.getEntityId()) {
		                    this.pos.setPosition((double)wrapper.getX() / 32.0, (double)wrapper.getY() / 32.0, (double)wrapper.getZ() / 32.0);
		                }
		                if(!e.isCancelled()) {
			                this.packets.add(new DelayedPacket(e.getPacket(),System.currentTimeMillis()));
			                e.setCancelled(true);
		                }
		            }
		    	} catch (NullPointerException ignored) {
		
		        }
		}
		if(event instanceof PacketSendEvent) {
			PacketSendEvent e = (PacketSendEvent) event;
			if (e.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity) e.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
				attack = true;
			}
		}
    }

    private void reset() {
        this.blinkTime = 0;
        if (this.blinking) {
            this.release();
        }
        this.attack = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void release() {
        this.blinking = false;
        ArrayList<DelayedPacket> copy = new ArrayList<>(packets);
        for (DelayedPacket packet : copy) {
        	if(packet == null) continue;
            if(packet.packet == null) continue;
            if(this.mc.getNetHandler() == null) continue;
            if(Client.instance.moduleManager.velo.delaying) {
            	Client.instance.moduleManager.velo.packets.add(packet.packet);
            	continue;
            }
            mc.addScheduledTask(() -> packet.packet.processPacket(mc.getNetHandler()));
        }
        this.packets.clear();
        this.blinkTime = 0;
    }
    
    void release2() {
    	if(blinking2) {
    		BlinkUtils.stopBlink();
    	}
    	this.blinking2 = false;
    	this.blinkTime2 = 0;
    }

    private void drawBox(Vec3 pos) {
        GlStateManager.pushMatrix();
        double x = pos.xCoord;
        double y = pos.yCoord;
        double z = pos.zCoord;
        AxisAlignedBB bbox = this.mc.thePlayer.getEntityBoundingBox().expand(0.1, 0.1, 0.1);
        AxisAlignedBB axis = new AxisAlignedBB(bbox.minX - this.mc.thePlayer.posX + x, bbox.minY - this.mc.thePlayer.posY + y, bbox.minZ - this.mc.thePlayer.posZ + z, bbox.maxX - this.mc.thePlayer.posX + x, bbox.maxY - this.mc.thePlayer.posY + y, bbox.maxZ - this.mc.thePlayer.posZ + z);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glLineWidth((float)2.0f);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)0.3f);
        RenderUtils.drawBoundingBox(axis);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1f);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GlStateManager.popMatrix();
    }
    
    private EntityLivingBase findTarget(double range) {
        EntityPlayer nearest = null;
        double closestDistance = range;
        EntityLivingBase target = Client.instance.moduleManager.killAura.target;
        for (Object obj : mc.theWorld.playerEntities) {
            if (obj instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) obj;
                if(player == mc.thePlayer || AntiBot.isBot(player) || !Utils.holdingSword() && this.wponly.getBoolean()) continue;
                if (mc.thePlayer.getDistanceToEntity(player) <= closestDistance) {
                    closestDistance = mc.thePlayer.getDistanceToEntity(player);
                    nearest = player;
                }
            }
        }
        
        return target != null || this.kaonly.getBoolean() ? target : nearest;
    }
}
