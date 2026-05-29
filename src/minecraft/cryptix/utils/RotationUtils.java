package cryptix.utils;

import java.util.List;
import java.util.Random;

import cryptix.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class RotationUtils {
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static Random random = new Random();
	public static float currentYaw = 0.0f;
    public static float currentPitch = 0.0f;
    private static int rotTick;
    public static float[] getRotations(EntityLivingBase e, boolean smoothing) {
    	if (Client.instance.moduleManager.killAura.rotation.getString().equalsIgnoreCase("Hypixel")) {
    		    double x = e.posX + (e.posX - e.lastTickPosX) - mc.thePlayer.posX;
    		    double y = e.posY - 3.25 + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
    		    double z = e.posZ + (e.posZ - e.lastTickPosZ) - mc.thePlayer.posZ;
    		    double dist = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
    		    float targetYaw = (float) Math.toDegrees(-Math.atan(x / z));
    		    float targetPitch = (float) -Math.toDegrees(Math.atan(y / dist));
    		    if (mc.thePlayer.posY < e.posY) targetPitch = 0.05F;
    		    if (e.posY + 0.5 > mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) {
    		        targetPitch = (float) -Math.toDegrees(Math.atan((e.posY - 3.3 + e.getEyeHeight() - mc.thePlayer.posY + 0.5) / dist));
    		    }
    		    if (x < 0 && z < 0) {
    		        targetYaw = (float) (90 + Math.toDegrees(Math.atan(z / x)));
    		    } else if (x > 0 && z < 0) {
    		        targetYaw = (float) (-90 + Math.toDegrees(Math.atan(z / x)));
    		    }
    		    float[] gcd = applyGCD(new float[] {wrapAngleTo180(targetYaw), clampTo90(targetPitch)});
    		    return gcd;
    	}else if (Client.instance.moduleManager.killAura.rotation.getString().equalsIgnoreCase("Vulcan") && smoothing) {
    		double x = e.posX + (e.posX - e.lastTickPosX) - mc.thePlayer.posX;
		    double y = e.posY - 3.25 + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
		    double z = e.posZ + (e.posZ - e.lastTickPosZ) - mc.thePlayer.posZ;
		    double dist = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
		    float targetYaw = (float) Math.toDegrees(-Math.atan(x / z));
		    float targetPitch = (float) -Math.toDegrees(Math.atan(y / dist));
		    if (mc.thePlayer.posY < e.posY) targetPitch = 0.05F;
		    if (e.posY + 0.5 > mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) {
		        targetPitch = (float) -Math.toDegrees(Math.atan((e.posY - 3.3 + e.getEyeHeight() - mc.thePlayer.posY + 0.5) / dist));
		    }
		    if (x < 0 && z < 0) {
		        targetYaw = (float) (90 + Math.toDegrees(Math.atan(z / x)));
		    } else if (x > 0 && z < 0) {
		        targetYaw = (float) (-90 + Math.toDegrees(Math.atan(z / x)));
		    }
		    float playerYaw = mc.thePlayer.rotationYaw;
		    while (targetYaw - playerYaw > 360) {
		    	targetYaw -= 360;
		    }
		    while (targetYaw - playerYaw < -360) {
		    	targetYaw += 360;
		    }
		    if (currentYaw == 0.0f) {
                currentYaw = RotationUtils.mc.thePlayer.rotationYawHead;
            }
		    float yawDiff = targetYaw - currentYaw;
		    float maxChange = 90.0f + random.nextFloat();
		    yawDiff = MathHelper.clamp_float(yawDiff, -maxChange, maxChange);
		    float newYaw = currentYaw + yawDiff;
		    float[] gcd = applyGCD(new float[] {newYaw,clampTo90(targetPitch)});
		    return gcd;
    	}else if (Client.instance.moduleManager.killAura.rotation.getString().equalsIgnoreCase("Grim")) {

    	    float[] rots = getRotationFromPosition(e.posX, e.posY + e.getEyeHeight(), e.posZ);
    	    float[] gcd = applyGCD(rots);

    	    float newYaw = gcd[0];
    	    float newPitch = gcd[1];

    	    float yawDiff = Math.abs(wrapAngleTo180(newYaw - currentYaw));
    	    float pitchDiff = Math.abs(newPitch - currentPitch);

    	    // only update if significant change
    	    if (yawDiff < 20f && pitchDiff < 20f) {
    	    	newYaw = currentYaw;
    	    	newPitch = currentPitch;
    	    }

    	    return new float[]{newYaw, newPitch};
    	}else {
    		float[] rots = getRotationFromPosition(e.posX, e.posY + e.getEyeHeight(), e.posZ);
    		float[] gcd = applyGCD(rots);
    		rots[0] = gcd[0];
    		rots[1] = gcd[1];
            return rots;
        }
    }
    
    public static float[] applyGCD(float[] rotations) {
        float yaw = currentYaw;
        float pitch = currentPitch;
        float sensitivity = mc.gameSettings.mouseSensitivity;
        float f = sensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 8.0F;
        float yawDelta = (rotations[0] - yaw) / 0.15F;
        float pitchDelta = (pitch - rotations[1]) / 0.15F;
        yawDelta = (int)yawDelta;
        pitchDelta = (int)pitchDelta;
        float finalYaw = yaw + yawDelta * 0.15F;
        float finalPitch = pitch - pitchDelta * 0.15F;
        finalPitch = MathHelper.clamp_float(finalPitch, -90.0F, 90.0F);
        return new float[]{finalYaw, finalPitch};
    }

    
    public static float getGCD() {
        float sensitivity = mc.gameSettings.mouseSensitivity;
        float f = sensitivity * 0.6F + 0.2F;
        float gcd = f * f * f * 1.2F;
        return gcd;
    }
    public static float[] getRotationsBlock(final BlockPos blockPos) {
        final double n = blockPos.getX() + 0.45 - mc.thePlayer.posX;
        final double n2 = blockPos.getY() + 0.25 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        final double n3 = blockPos.getZ() + 0.45 - mc.thePlayer.posZ;
        return new float[] { mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float((float)(Math.atan2(n3, n) * 57.295780181884766) - 90.0f - mc.thePlayer.rotationYaw), clampTo90(mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float((float)(-(Math.atan2(n2, MathHelper.sqrt_double(n * n + n3 * n3)) * 57.295780181884766)) - mc.thePlayer.rotationPitch)) };
    }
    
    public static float[] getRotationFromPosition(double x, double y, double z) {
        double diffX = x - mc.thePlayer.posX;
        double diffY = y - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double diffZ = z - mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
        yaw = mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw);
        pitch = mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch);
        return new float[]{yaw, pitch};
    }
    
    public static float applyContinuousRotation(float current, float target) {
        float delta = MathHelper.wrapAngleTo180_float(target - current);
        return current + delta;
    }

    public static float smoothYaw(float targetYaw) {
        float playerYaw = RotationUtils.mc.thePlayer.rotationYaw;
        if (currentYaw == 0.0f) {
            currentYaw = playerYaw;
        }
        float deltaYaw = RotationUtils.wrapAngleTo180(RotationUtils.rotDistance(currentYaw, targetYaw));
        currentYaw = wrapAngleTo180((float)(currentYaw + deltaYaw));
        return currentYaw;
    }

    public static float smoothPitch(float targetPitch) {
        float playerPitch = RotationUtils.mc.thePlayer.rotationPitch;
        if (currentPitch == 0.0f) {
            currentPitch = playerPitch;
        }
        float deltaPitch = RotationUtils.rotDistance(currentPitch, targetPitch);
        float threshold = (float)(8.0);
        if ((Math.abs(deltaPitch) < threshold)) {
            return (float) (clampTo90(currentPitch));
        }
        float smoothing = (float) Math.min(Math.max(Math.abs(deltaPitch) / 50f + Math.random(), 0.05f), 1.0f);

        currentPitch += deltaPitch;
        return currentPitch;
    }

    public static float rotDistance(float src, float target) {
        float difference = wrapAngleTo180(target - src);
        return difference;
    }

    public static float wrapAngleTo180(float angle) {
        angle %= 360f;
        if (angle >= 180f) angle -= 360f;
        if (angle < -180f) angle += 360f;
        return angle;
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
	
    public static float[] rotateToVec3(Vec3 targetVec) {
        Vec3 eyePos = new Vec3(
                mc.thePlayer.posX,
                mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
                mc.thePlayer.posZ
        );
        double deltaX = targetVec.xCoord - eyePos.xCoord;
        double deltaY = targetVec.yCoord - eyePos.yCoord;
        double deltaZ = targetVec.zCoord - eyePos.zCoord;
        double distXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float yaw = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90F);
        float pitch = (float) (-Math.toDegrees(Math.atan2(deltaY, distXZ)));
        yaw = MathHelper.wrapAngleTo180_float(yaw);
        pitch = MathHelper.wrapAngleTo180_float(pitch);
        return new float[] { yaw, pitch };
    }
	
	public static Vec3 getVectorForRotation(float p, float y) {
        return new Vec3(MathHelper.sin(-y * 0.017453292F - 3.1415927F) * -MathHelper.cos(-p * 0.017453292F), MathHelper.sin(-p * 0.017453292F), MathHelper.cos(-y * 0.017453292F - 3.1415927F) * -MathHelper.cos(-p * 0.017453292F));
    }
	
	public static float getMovementYaw() {
		float yaw = 180;
	    KeyBinding forward = mc.gameSettings.keyBindForward;
	    KeyBinding back = mc.gameSettings.keyBindBack;
	    KeyBinding right = mc.gameSettings.keyBindRight;
	    KeyBinding left = mc.gameSettings.keyBindLeft;
	    if(back.isKeyDown()) {
	    	yaw -= 180;
	    	if(right.isKeyDown()) {
	    		yaw -= 45;
	    	}
	    	if(left.isKeyDown()) {
	    		yaw += 45;
	    	}
	    }else if(forward.isKeyDown()) {
	    	if(right.isKeyDown()) {
	    		yaw += 45;
	    	}
	    	if(left.isKeyDown()) {
	    		yaw -= 45;
	    	}
	    }else {
	    	if(right.isKeyDown()) {
	    		yaw += 90;
	    	}
	    	if(left.isKeyDown()) {
	    		yaw -= 90;
	    	}
	    }
	    return (MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) + yaw % 360 + 360) % 360;
	}
	
	private static Vec3 getLook(float partialTicks, float yaw, float pitch, float preYaw, float prePitch)
    {
        //if (partialTicks == 1.0F)
        //{
            return mc.thePlayer.getVectorForRotation(pitch, yaw);
        //}
        //else
        //{
            //float f = prePitch + (pitch - prePitch) * partialTicks;
            //float f1 = preYaw + (yaw - preYaw) * partialTicks;
            //return mc.thePlayer.getVectorForRotation(f, f1);
        //}
    }
	
	public static MovingObjectPosition rayCast(double distance, float yaw, float pitch, float preYaw, float prePitch) {
		Vec3 lookVec = mc.thePlayer.getVectorForRotation(pitch, yaw);
		Vec3 eyes = mc.thePlayer.getPositionEyes(1.0f);
		Vec3 end = eyes.addVector(lookVec.xCoord * distance,lookVec.yCoord * distance,lookVec.zCoord * distance);
		return mc.theWorld.rayTraceBlocks(eyes, end, true, true, true);
    }
	
	public static MovingObjectPosition rayCastEntity(double distance, float yaw, float pitch) {
	    Vec3 eyes = mc.thePlayer.getPositionEyes(1.0f);
	    Vec3 lookVec = mc.thePlayer.getVectorForRotation(pitch, yaw);
	    Vec3 end = eyes.addVector(
	        lookVec.xCoord * distance,
	        lookVec.yCoord * distance,
	        lookVec.zCoord * distance
	    );
	    MovingObjectPosition blockHit = mc.theWorld.rayTraceBlocks(eyes, end, false, true, false);
	    double maxDistance = distance;
	    if (blockHit != null) {
	        maxDistance = eyes.distanceTo(blockHit.hitVec);
	    }
	    Entity closestEntity = null;
	    Vec3 closestHit = null;
	    List<Entity> entities = mc.theWorld.getEntitiesWithinAABBExcludingEntity(
	        mc.thePlayer,
	        mc.thePlayer.getEntityBoundingBox()
	            .addCoord(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance)
	            .expand(1.0, 1.0, 1.0)
	    );
	    for (Entity entity : entities) {
	        if (!(entity instanceof EntityLivingBase) || entity == mc.thePlayer)
	            continue;
	        AxisAlignedBB bb = entity.getEntityBoundingBox().expand(0.1, 0.1, 0.1);
	        MovingObjectPosition intercept = bb.calculateIntercept(eyes, end);
	        if (intercept != null) {
	            double dist = eyes.distanceTo(intercept.hitVec);
	            if (dist < maxDistance) {
	                maxDistance = dist;
	                closestEntity = entity;
	                closestHit = intercept.hitVec;
	            }
	        }
	    }
	    if (closestEntity != null) {
	        return new MovingObjectPosition(closestEntity, closestHit);
	    }
	    return blockHit;
	}
	
	public static MovingObjectPosition rayCast(Entity entity, double distance) {
	    Vec3 eyes = entity.getPositionEyes(1.0F);
	    Vec3 look = entity.getLookVec();
	    Vec3 end = eyes.addVector(look.xCoord * distance,look.yCoord * distance,look.zCoord * distance);
	    return entity.worldObj.rayTraceBlocks(eyes,end);
	}
	
	public static MovingObjectPosition rayTrace(AxisAlignedBB boundingBox, float yaw, float pitch, double distance) {
        Vec3 eyePos = mc.thePlayer.getPositionEyes(1.0f);
        Vec3 lookVec = mc.thePlayer.getVectorForRotation(pitch, yaw);
        Vec3 targetPos = eyePos.addVector(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance);
        return boundingBox.calculateIntercept(eyePos, targetPos);
    }
	
	public static float[] getRotationsToVec(Vec3 from, Vec3 to) {
	    double diffX = to.xCoord - from.xCoord;
	    double diffY = to.yCoord - from.yCoord;
	    double diffZ = to.zCoord - from.zCoord;
	    double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

	    float yaw = (float)(Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F);
	    float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, dist)));

	    yaw = MathHelper.wrapAngleTo180_float(yaw);
	    pitch = MathHelper.clamp_float(pitch, -90.0f, 90.0f);
	    return new float[]{yaw, pitch};
	}
	
	public static float clampTo90(float n) {
        return MathHelper.clamp_float(n, -90.0f, 90.0f);
    }
}
