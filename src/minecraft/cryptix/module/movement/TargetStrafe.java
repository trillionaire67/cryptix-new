package cryptix.module.movement;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.BooleanSetting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.MovementUtils;
import cryptix.utils.RotationUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class TargetStrafe extends Module{
	public float angle;
	private BooleanSetting requireSpace = new BooleanSetting("Require Space", this, false);
	private BooleanSetting drawCircle = new BooleanSetting("Draw Circle", this, true);
	public DoubleSetting range = new DoubleSetting("Range", this, 2, 1, 5, 1);
	private int direction = 1;
	public TargetStrafe() {
		super("TargetStrafe", 0, Category.MOVEMENT);
		this.addSetting(range, drawCircle, requireSpace);
	}
	
	@Override
	public void onPreInput() {
		angle = 0;
	    EntityLivingBase target = Client.instance.moduleManager.killAura.target;
	    if (target == null || mc.thePlayer == null || !MovementUtils.isMoving()) return;
	    if(requireSpace.getBoolean() && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) return;
	    angle = 1;
	    float radius = (float) range.getValue() - 0.25f;
	    double dx = target.posX - mc.thePlayer.posX;
	    double dz = target.posZ - mc.thePlayer.posZ;
	    if (Math.sqrt(dx * dx + dz * dz) < 0.01) return;
	    double correction = Math.abs(Math.sqrt(dx * dx + dz * dz) - radius) > 0.5 ? (Math.sqrt(dx * dx + dz * dz)) : (Math.sqrt(dx * dx + dz * dz) - radius);
	    double strafeRad = Math.toRadians(Math.toDegrees(Math.atan2(dz, dx)) + direction * 90);
	    double toTargetRad = Math.toRadians(Math.toDegrees(Math.atan2(dz, dx)));
	    double motionX = Math.cos(strafeRad) + correction * Math.cos(toTargetRad);
	    double motionZ = Math.sin(strafeRad) + correction * Math.sin(toTargetRad);
	    float yawRad = (float) Math.toRadians(mc.thePlayer.rotationYaw);
	    float moveForward = (float) (motionZ * Math.cos(yawRad) - motionX * Math.sin(yawRad));
	    float moveStrafe = (float) (motionX * Math.cos(yawRad) + motionZ * Math.sin(yawRad));
	    mc.thePlayer.movementInput.moveForward = moveForward;
	    mc.thePlayer.movementInput.moveStrafe = moveStrafe;
	    if(mc.thePlayer.isCollidedHorizontally) direction = direction == 1 ? -1 : 1;
	}
	
	@Override
	public void onRender3D() {
	    if (!drawCircle.getBoolean()) return;

	    EntityLivingBase target = Client.instance.moduleManager.killAura.target;
	    if (target == null || !MovementUtils.isMoving()) return;
	    if(requireSpace.getBoolean() && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) return;
	    double radius = (double) range.getValue();
	    double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
	    double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY;
	    double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

	    int points = 40;
	    float dotSize = 10.0f;

	    GL11.glPushMatrix();
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glEnable(GL11.GL_POINT_SMOOTH);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    GL11.glPointSize(dotSize);
	    GL11.glBegin(GL11.GL_POINTS);

	    for (int i = 0; i < points; i++) {
	        double angle = 2 * Math.PI * i / points;
	        double xOffset = radius * Math.cos(angle);
	        double zOffset = radius * Math.sin(angle);
	        int colorInt = Client.instance.moduleManager.hud.getColorInt(0, 1f);
	        float red   = ((colorInt >> 16) & 0xFF) / 255f;
	        float green = ((colorInt >> 8) & 0xFF) / 255f;
	        float blue  = (colorInt & 0xFF) / 255f;
	        GL11.glColor4f(red, green, blue, 1f);
	        GL11.glVertex3d(x + xOffset, y + 0.01, z + zOffset);
	    }
	    GL11.glEnd();
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_POINT_SMOOTH);
	    GL11.glPopMatrix();
	}

}
