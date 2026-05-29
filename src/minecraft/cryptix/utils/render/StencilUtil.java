package cryptix.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL11;

public final class StencilUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void prepareStencilWrite() {
        Framebuffer fbo = mc.getFramebuffer();
        fbo.bindFramebuffer(false);

        if (fbo.depthBuffer > -1) {
            replaceDepthWithStencil(fbo);
            fbo.depthBuffer = -1;
        }

        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glEnable(GL11.GL_STENCIL_TEST);

        GL11.glStencilFunc(GL11.GL_ALWAYS, 0x1, 0xFF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glColorMask(false, false, false, false);
    }

    public static void restrictToStencil(int stencilRef) {
    	GL11.glColorMask(true, true, true, true);
    	GL11.glStencilFunc(GL11.GL_EQUAL, stencilRef, 0xFF);
    	GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
    }

    public static void clearStencil() {
    	GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    private static void replaceDepthWithStencil(Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);

        int bufferId = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, bufferId);
        EXTFramebufferObject.glRenderbufferStorageEXT(
                EXTFramebufferObject.GL_RENDERBUFFER_EXT,
                EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT,
                mc.displayWidth,
                mc.displayHeight
        );

        EXTFramebufferObject.glFramebufferRenderbufferEXT(
                EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT,
                EXTFramebufferObject.GL_RENDERBUFFER_EXT,
                bufferId
        );

        EXTFramebufferObject.glFramebufferRenderbufferEXT(
                EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
                EXTFramebufferObject.GL_RENDERBUFFER_EXT,
                bufferId
        );
    }
}
