package cryptix.module.visual;

import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.other.event.Event;
import cryptix.other.event.events.PacketSendEvent;
import net.minecraft.block.Block;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

public class KillEffects extends Module{
	public EntityLivingBase entity;
	public KillEffects() {
		super("KillEffects", 0, Category.VISUAL);
	}
	
	@Override
	public void onPreMotion() {
		if(entity != null && !mc.theWorld.loadedEntityList.contains(entity)) {
            double startY = entity.posY;
            double endY = entity.posY + entity.height+.4;
            double step = 0.4;
            for (int i = 0; i < 100; i++) {
                for (double y = startY; y <= endY; y += step) {
                	mc.theWorld.spawnParticle(EnumParticleTypes.BLOCK_CRACK, entity.posX, y, entity.posZ, 0, 0, 0, Block.getStateId(Blocks.redstone_block.getDefaultState()));
                }
            }
            for (double y = startY; y <= endY; y += step) {
            	mc.getSoundHandler().playSound( new PositionedSoundRecord(new ResourceLocation("dig.stone"),1f,1.0f,(float) mc.thePlayer.posX,(float) mc.thePlayer.posY,(float) mc.thePlayer.posZ));
            }
            entity = null;
		}
	}
	
	public void onAttack(Entity entity) {
		if(!(entity instanceof EntityPlayer)) return;
		this.entity = (EntityLivingBase) entity;
	}
	
	@Override
	public void onEvent(Event event) {
		if(event instanceof PacketSendEvent) {
			PacketSendEvent e = (PacketSendEvent) event;
			if(e.getPacket() instanceof C02PacketUseEntity) {
				if (((C02PacketUseEntity) e.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
					onAttack(((C02PacketUseEntity) e.getPacket()).getEntityFromWorld(mc.theWorld));
				}
			}
		}
	}

}
