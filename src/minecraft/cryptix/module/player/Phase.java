package cryptix.module.player;

import java.util.ArrayList;
import java.util.List;

import cryptix.module.Category;
import cryptix.module.Module;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;

public class Phase extends Module{
    public boolean start, blinking, stopBlink, reblock;
    public final List<Packet<?>> blinkedPackets = new ArrayList();
    public Phase() {
        super("Phase", 0, Category.PLAYER);
    }

    @Override
    public void onPreMotion() {
        if(start) {
            blinking = true;
            start = false;
            reblock = true;
            return;
        }
        if(reblock) {
            BlockPos below = new BlockPos(mc.thePlayer.posX,  mc.thePlayer.posY - 1, mc.thePlayer.posZ);
            BlockPos bp = new BlockPos(mc.thePlayer.posX,  mc.thePlayer.posY - (mc.theWorld.getBlockState(below).getBlock() == Blocks.air ? 2 : 1), mc.thePlayer.posZ);
            mc.theWorld.setBlockState(bp, Blocks.air.getDefaultState(), 11); 
            reblock = false;
        }
        if(stopBlink && !blinkedPackets.isEmpty()) {
            blinking = false;
            synchronized (blinkedPackets) {
                for (Packet<?> packet : blinkedPackets) {
                    this.sendPacket(packet);
                }
            }
            blinkedPackets.clear();
            stopBlink = false;
        }
    }

    @Override
    public void onRender2D() {
        if(blinking) {
            mc.fontRendererObj.drawStringWithShadow("Blinking: " + blinkedPackets.size(), mc.displayWidth / 4 - (mc.fontRendererObj.getStringWidth("Blinking: " + blinkedPackets.size()) / 2), mc.displayHeight / 4, -1);
        }
    }

}