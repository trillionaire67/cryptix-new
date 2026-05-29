package cryptix.utils;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.AxisAlignedBB;

public class FrustumUtils {
    public static final Frustum frustum = new Frustum();
    public static AxisAlignedBB box = new AxisAlignedBB(0,0,0,0,0,0);

    public static void update(double x, double y, double z) {
        frustum.setPosition(x, y, z);
    }

    public static boolean isVisible(AxisAlignedBB bb) {
        return frustum.isBoundingBoxInFrustum(bb);
    }
    
    public static boolean isVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    	box.minX = minX;
    	box.minY = minY;
    	box.minZ = minZ;
    	box.maxX = maxX;
    	box.maxY = maxY;
    	box.maxZ = maxZ;
        return frustum.isBoundingBoxInFrustum(box);
    }
}