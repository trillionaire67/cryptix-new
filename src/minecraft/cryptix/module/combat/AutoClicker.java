package cryptix.module.combat;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.BooleanSetting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.other.event.Event;
import cryptix.other.event.events.PacketReceiveEvent;
import cryptix.utils.Utils;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.Vec3;

public class AutoClicker extends Module{
	private DoubleSetting minCPS = new DoubleSetting("MinCPS", this, 8.0, 1.0, 20.0, false);
	private DoubleSetting maxCPS = new DoubleSetting("MaxCPS", this, 12.0, 1.0, 20.0, false);
	private DoubleSetting blockHitChance = new DoubleSetting("Block Hit Chance", this, 0.0, 0.0, 100.0, false);
	private BooleanSetting pred = new BooleanSetting("Predict Block Hit", this, false);
	private DoubleSetting jitter = new DoubleSetting("Jitter", this, 0, 0, 5, 1);
	private BooleanSetting breakBlocks = new BooleanSetting("Break Blocks", this, false);
	private BooleanSetting rightClicker = new BooleanSetting("Right Clicker", this, false);
	private BooleanSetting disableInv = new BooleanSetting("Disable in Inventory", this, true);
	private long lastClick;
    private long hold;
    private double speed;
    private double holdLength;
    private double min, max;
    private Random random = new Random();
    private boolean block;
    public AutoClicker() {
        super("AutoClicker", 0, Category.COMBAT);
        this.addSetting(this.minCPS, this.maxCPS, this.blockHitChance, this.pred, this.jitter, this.breakBlocks, this.rightClicker, this.disableInv);
    }

    @Override
    public void onPreMotion() {
    	if(disableInv.getBoolean() && mc.currentScreen != null) {
    		block = false;
    		return;
    	}
        if (Mouse.isButtonDown(0) && !mc.thePlayer.isUsingItem()) {
        	int key = mc.gameSettings.keyBindAttack.getKeyCode();
            if (Utils.isLookingAtBlock() && breakBlocks.getBoolean() && !(mc.pointedEntity instanceof EntityLivingBase)) {
                KeyBinding.setKeyBindState(key, true);
                this.update();
                return;
            }
            if ((double)(System.currentTimeMillis() - this.lastClick) > this.speed * 1000.0) {
                this.lastClick = System.currentTimeMillis();
                if (this.hold < this.lastClick) {
                    this.hold = this.lastClick;
                }
                KeyBinding.setKeyBindState(key, true);
                KeyBinding.onTick(key);
                if (mc.pointedEntity instanceof EntityLivingBase) {
                	if(block) {
                    	KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                    	return;
                    }
                    if (blockHitChance.getValue() != 100.0 && !(Math.random() <= blockHitChance.getValue() / 100.0)) {
                        return;
                    }
                    KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                }
                block = false;
                mc.thePlayer.rotationPitch += random.nextBoolean() ? random.nextFloat() * jitter.getValue() : -random.nextFloat() * jitter.getValue();
                mc.thePlayer.rotationYaw += random.nextBoolean() ? random.nextFloat() * jitter.getValue() : -random.nextFloat() * jitter.getValue();
                update();
            } else if ((double)(System.currentTimeMillis() - hold) > holdLength * 1000.0) {
            	KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
                update();
            }
        }
        if(!Mouse.isButtonDown(0)){
        	block = false;
        }
        if (Mouse.isButtonDown(1) && rightClicker.getBoolean() && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            if ((double)(System.currentTimeMillis() - lastClick) > speed * 1000.0) {
                this.lastClick = System.currentTimeMillis();
                if (hold < lastClick) {
                    hold = lastClick;
                }
                KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
            } else if ((double)(System.currentTimeMillis() - this.hold) > this.holdLength * 1000.0) {
                this.update();
            }
        }
    }
    
    @Override
	public void onEvent(Event event) {
		if(event instanceof PacketReceiveEvent) {
			PacketReceiveEvent e = (PacketReceiveEvent) event;
	    	if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				if(packet.getEntityID() == mc.thePlayer.getEntityId() && pred.getBoolean() && Utils.holdingSword()) {
					block = true;
				}
	    	}
		}
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.update();
    }
    private void update() {
        this.min = minCPS.getValue();
        this.max = maxCPS.getValue();
        if (this.min >= this.max) {
            this.max = this.min + 1.0;
        }
        this.speed = 1.0 / ThreadLocalRandom.current().nextDouble(this.min - 0.2, this.max);
        this.holdLength = this.speed / ThreadLocalRandom.current().nextDouble(this.min, this.max);
    }
}
