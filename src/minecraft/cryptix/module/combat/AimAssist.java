package cryptix.module.combat;

import java.util.List;

import org.lwjgl.input.Mouse;

import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.BooleanSetting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.RotationUtils;
import cryptix.utils.Utils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class AimAssist extends Module{
	private DoubleSetting range = new DoubleSetting("Range", this, 5,3,10,1);
	private DoubleSetting speed = new DoubleSetting("Speed", this, 5,1,10,1);
	private BooleanSetting weaponOnly = new BooleanSetting("Weapon only", this, false);
	private BooleanSetting teams = new BooleanSetting("Teams", this, false);
	private BooleanSetting vertical = new BooleanSetting("Aim Vertically", this, false);
	private BooleanSetting gcd = new BooleanSetting("GCD", this, false);
	public AimAssist() {
		super("AimAssist", 0, Category.COMBAT);
		this.addSetting(this.range, this.speed, this.weaponOnly, this.teams, this.vertical, this.gcd);
	}
	
	@Override
	public void onPreMotion() {
		if (mc.currentScreen != null || !mc.inGameHasFocus) {
            return;
        }
		if(weaponOnly.getBoolean() && !Utils.holdingSword()) {
			return;
		}

        Entity target = getClosestTarget(range.getValue());
        if (target != null && Mouse.isButtonDown(0)) {
            faceEntitySmooth(target, (int) speed.getValue());
        }
    }

    private Entity getClosestTarget(double range) {
        Entity closest = null;
        double closestDistance = range;

        List<Entity> entities = mc.theWorld.loadedEntityList;
        for (Entity entity : entities) {
            if (!(entity instanceof EntityPlayer) || entity == mc.thePlayer || entity.isInvisible()) continue;
            if(teams.getBoolean() && !Utils.teamMate((EntityLivingBase) entity)) continue;
            if(AntiBot.isBot(entity)) continue;
            double distance = mc.thePlayer.getDistanceToEntity(entity);
            if (distance < closestDistance) {
                closest = entity;
                closestDistance = distance;
            }
        }

        return closest;
    }

    private void faceEntitySmooth(Entity entity, int speed) {
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffZ = entity.posZ - mc.thePlayer.posZ;
        double diffY = (entity.posY + entity.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());

        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float targetYaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90);
        float targetPitch = (float) (-Math.toDegrees(Math.atan2(diffY, dist)));
        float[] rotations = new float[] {updateRotation(mc.thePlayer.rotationYaw, targetYaw, (float) (speed - Math.random() * 0.5)), updateRotation(mc.thePlayer.rotationPitch, targetPitch, (float) (speed / 2 - Math.random() * 0.5))};
        if(gcd.getBoolean()) {
        	rotations = RotationUtils.applyGCD(rotations);
        }
        mc.thePlayer.rotationYaw = rotations[0];
        if(vertical.getBoolean()) {
        	mc.thePlayer.rotationPitch = rotations[1];
        }
    }

    private float updateRotation(float current, float target, float speed) {
        float delta = MathHelper.wrapAngleTo180_float(target - current);
        if (delta > speed) delta = speed;
        if (delta < -speed) delta = -speed;
        return current + delta;
    }
}