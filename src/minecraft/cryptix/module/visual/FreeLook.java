package cryptix.module.visual;

import org.lwjgl.input.Keyboard;

import cryptix.module.Category;
import cryptix.module.Module;
import net.minecraft.util.MathHelper;

public class FreeLook extends Module{
	public float yaw, pitch;
	private int prevPerspective;
	public FreeLook() {
		super("FreeLook", 0, Category.VISUAL);
	}
	
	@Override
	public void onDisable() {
		mc.gameSettings.thirdPersonView = prevPerspective;
		yaw = 0;
		pitch = 0;
	}
	
	@Override
	public void onEnable() {
		prevPerspective = mc.gameSettings.thirdPersonView;
	}
	
	@Override
	public void onRender2D() {
		if(pitch == 0) {
			pitch = mc.thePlayer.rotationPitch;
		}
		if(yaw == 0) {
			yaw = mc.thePlayer.rotationYaw;
		}
		if(!Keyboard.isKeyDown(this.getKey())) {
			this.toggle();
			return;
		}
		this.mc.mouseHelper.mouseXYChange();
        final float mouseSensitivity = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float multiplier = (float) (mouseSensitivity * mouseSensitivity * mouseSensitivity * 1.5);
        yaw += this.mc.mouseHelper.deltaX * multiplier;
        pitch -= this.mc.mouseHelper.deltaY * multiplier;

        pitch = MathHelper.clamp_float(pitch, -90, 90);
        mc.gameSettings.thirdPersonView = 1;
	}

}
