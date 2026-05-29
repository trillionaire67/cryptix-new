package cryptix.module.player;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.module.Category;
import cryptix.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MathHelper;

public class AntiFireBall extends Module {
	private Setting movefix = new Setting("Movefix", this, false);
	private Setting scaffold = new Setting("Disable while Scaffolding", this, false);
	private Setting rotationRange = new Setting("Rotation Range", this, 10, 3, 10, 1);
	private Setting attackRange = new Setting("Attack Range", this, 4.5, 3, 10, 1);
    private final Minecraft mc = Minecraft.getMinecraft();
    private EntityFireball targetFireball = null;
    private EntityFireball nextTickTarget = null;
    private boolean rotating = false;
    public AntiFireBall() {
        super("AntiFireBall", 0, Category.PLAYER);
        this.addSetting(movefix, scaffold, rotationRange, attackRange);
    }

    @Override
    public void onPreMotion() {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        rotating = false;
        if(scaffold.getBoolean() && Client.instance.moduleManager.scaffold.isToggled() || Client.instance.moduleManager.killAura.target != null) {
        	return;
        }
        EntityFireball closest = null;
        double range = rotationRange.getValue();

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityFireball) {
            	EntityFireball fireball = (EntityFireball) entity;
                if (fireball.shootingEntity == mc.thePlayer) {
                    continue;
                }
                double dist = mc.thePlayer.getDistanceToEntity(entity);
                if (dist <= range) {
                    range = dist;
                    closest = (EntityFireball) entity;
                }
            }
        }

        if (closest != null) {
            float[] rotations = getRotations(closest);
            mc.thePlayer.rotationYawHead = rotations[0];
            mc.thePlayer.rotationPitchHead = rotations[1];
            rotating = true;
            nextTickTarget = closest;
        }
    }

    @Override
    public void onPreUpdate() {
        if (movefix.getBoolean() && rotating) {
            Client.movefix = true;
        }
        if(scaffold.getBoolean() && Client.instance.moduleManager.scaffold.isToggled() || Client.instance.moduleManager.killAura.target != null) {
        	return;
        }
        if (nextTickTarget != null && !nextTickTarget.isDead && mc.theWorld.loadedEntityList.contains(nextTickTarget)) {
            double dist = mc.thePlayer.getDistanceToEntity(nextTickTarget);
            if (dist <= attackRange.getValue()) {
                targetFireball = nextTickTarget;
                nextTickTarget = null;
                mc.thePlayer.swingItem();
                mc.playerController.attackEntity(mc.thePlayer, targetFireball);
                targetFireball = null;
            } else if (dist <= 6.0) {
                mc.thePlayer.swingItem();
            } else {
                nextTickTarget = null;
            }
        } else {
            nextTickTarget = null;
        }
    }

    private float[] getRotations(Entity entity) {
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffY = (entity.posY + entity.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double diffZ = entity.posZ - mc.thePlayer.posZ;

        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float)(MathHelper.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90F;
        float pitch = (float)(-(MathHelper.atan2(diffY, dist) * 180.0D / Math.PI));

        return new float[]{yaw, pitch};
    }
}