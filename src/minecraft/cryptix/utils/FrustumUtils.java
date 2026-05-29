package cryptix.utils;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.AxisAlignedBB;

public class FrustumUtils {
    public static final Frustum frustum = new Frustum();

    public static void update(double x, double y, double z) {
        frustum.setPosition(x, y, z);
    }

    public static boolean isVisible(AxisAlignedBB bb) {
        return frustum.isBoundingBoxInFrustum(bb);
    }
}