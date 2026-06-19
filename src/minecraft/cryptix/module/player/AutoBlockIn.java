package cryptix.module.player;

import cryptix.Client;
import cryptix.module.Category;
import cryptix.module.Module;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class AutoBlockIn extends Module {
	private float[] rotations = new float[2];
	private int rotationTick;
    public AutoBlockIn() {
        super("AutoBlockIn", 0, Category.PLAYER);
    }

    @Override
    public void onPreUpdate() {
    	mc.thePlayer.rotationYawHead = rotations[0];
    	mc.thePlayer.rotationPitchHead = rotations[1];
        BlockPos base = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        Client.movefix = true;
        // 1
        if(place(base.add(1, 0, 0))) return;
        if(place(base.add(-1, 0, 0))) return;
        if(place(base.add(0, 0, 1))) return;
        if(place(base.add(0, 0, -1))) return;

        // 2
        if(place(base.add(1, 1, 0))) return;
        if(place(base.add(-1, 1, 0))) return;
        if(place(base.add(0, 1, 1))) return;
        if(place(base.add(0, 1, -1))) return;
        
        //head
        if(place(base.add(0, 2, 0))) return;
    }
    
    @Override
    public void onPreMotion() {
    	mc.thePlayer.rotationYawHead = rotations[0];
    	mc.thePlayer.rotationPitchHead = rotations[1];
    }

    private boolean place(BlockPos pos) {
        if (mc.theWorld == null || mc.thePlayer == null) return false;
        if (!mc.theWorld.isAirBlock(pos)) return false;
        rotationTick++;
        int slot = findBlockInHotbar();
        if (slot == -1) return false;
        BlockPos[] offsets = {pos.add(1, 0, 0),pos.add(-1, 0, 0),pos.add(0, 0, 1),pos.add(0, 0, -1),pos.add(0, -1, 0),pos.add(0, 1, 0)};
        for (BlockPos neighbor : offsets) {
            if (!isValidSupport(neighbor, EnumFacing.UP)) continue;
            EnumFacing side = getFacingFrom(pos, neighbor);
            if (side == null) continue;
            float oldYaw = rotations[0], oldPitch = rotations[1];
            faceBlock(neighbor, side);
            //if(!shouldPlace(oldYaw, oldPitch)) return false;
            Vec3 hitVec = new Vec3(neighbor.getX() + 0.5 + side.getFrontOffsetX() * 0.5,neighbor.getY() + 0.5 + side.getFrontOffsetY() * 0.5,neighbor.getZ() + 0.5 + side.getFrontOffsetZ() * 0.5);
            int old = mc.thePlayer.inventory.currentItem;
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(slot);
            mc.thePlayer.inventory.currentItem = slot;
            mc.playerController.onPlayerRightClick(mc.thePlayer,mc.theWorld,stack,neighbor,side,hitVec);
            mc.thePlayer.inventory.currentItem = old;
            rotationTick = 0;
            return true;
        }
        return false;
    }
    
    private boolean isValidSupport(BlockPos pos, EnumFacing side) {
        if (mc.theWorld == null) return false;
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        if (block == null) return false;
        return block.getMaterial().isSolid() && block.isFullCube();
    }
    
    private EnumFacing getFacingFrom(BlockPos target, BlockPos neighbor) {
        if (neighbor.getX() + 1 == target.getX()) return EnumFacing.EAST;
        if (neighbor.getX() - 1 == target.getX()) return EnumFacing.WEST;
        if (neighbor.getY() + 1 == target.getY()) return EnumFacing.UP;
        if (neighbor.getY() - 1 == target.getY()) return EnumFacing.DOWN;
        if (neighbor.getZ() + 1 == target.getZ()) return EnumFacing.SOUTH;
        if (neighbor.getZ() - 1 == target.getZ()) return EnumFacing.NORTH;
        return null;
    }

    private int findBlockInHotbar() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemBlock) {
                return i;
            }
        }
        return -1;
    }
    
    private boolean shouldPlace(float oldYaw, float oldPitch) {
    	if(Math.abs(MathHelper.wrapAngleTo180_double(oldYaw - rotations[0])) < 5 && Math.abs(MathHelper.wrapAngleTo180_double(oldPitch - rotations[1])) < 5) return true;
    	return false;
    }
    
    private void faceBlock(BlockPos pos, EnumFacing side) {
        double x = pos.getX() + 0.5 + side.getFrontOffsetX() * 0.5;
        double y = pos.getY() + 0.25 + side.getFrontOffsetY() * 0.5;
        double z = pos.getZ() + 0.5 + side.getFrontOffsetZ() * 0.5;
        double dx = x - mc.thePlayer.posX;
        double dy = y - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dz = z - mc.thePlayer.posZ;
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90F);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
	    rotations[0] = yaw;
	    rotations[1] = pitch;
    }
}