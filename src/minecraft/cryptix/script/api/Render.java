package cryptix.script.api;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.lwjgl.opengl.GL11;

import cryptix.Client;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class Render extends LuaTable {

    public Render() {
        set("drawRect", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                int x = args.checkint(1);
                int y = args.checkint(2);
                int width = args.checkint(3);
                int height = args.checkint(4);
                int color = args.checkint(5);
                Gui.drawRect(x,y,x + width,y + height,color);
                return LuaValue.NIL;
            }
        });
        set("drawString", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
            	String text = args.checkjstring(1);
                int x = args.checkint(2);
                int y = args.checkint(3);
                int color = args.checkint(4);
                Client.mc.fontRendererObj.drawString(text, x, y, color);
                return LuaValue.NIL;
            }
        });
        set("getStringWidth", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue text) {
                return LuaValue.valueOf(Client.mc.fontRendererObj.getStringWidth(text.checkjstring()));
            }
        });
        set("enableBlend", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                GlStateManager.enableBlend();
                return NIL;
            }
        });
        set("disableBlend", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                GlStateManager.disableBlend();
                return NIL;
            }
        });
        set("enableDepth", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                GlStateManager.enableDepth();
                return NIL;
            }
        });
        set("disableDepth", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                GlStateManager.disableDepth();
                return NIL;
            }
        });
        set("enableTexture2D", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                GlStateManager.enableTexture2D();
                return NIL;
            }
        });
        set("disableTexture2D", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                GlStateManager.disableTexture2D();
                return NIL;
            }
        });
        set("enableCull", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                GlStateManager.enableCull();
                return NIL;
            }
        });
        set("disableCull", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                GlStateManager.disableCull();
                return NIL;
            }
        });
        set("pushMatrix", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                GlStateManager.pushMatrix();
                return NIL;
            }
        });
        set("popMatrix", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                GlStateManager.popMatrix();
                return NIL;
            }
        });
        set("translate", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                float x = (float) args.checkdouble(1);
                float y = (float) args.checkdouble(2);
                float z = (float) args.optdouble(3, 0);
                GlStateManager.translate(x, y, z);
                return NIL;
            }
        });
        set("rotate", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                float angle = (float) args.checkdouble(1);
                float x = (float) args.checkdouble(2);
                float y = (float) args.checkdouble(3);
                float z = (float) args.checkdouble(4);
                GlStateManager.rotate(angle, x, y, z);
                return NIL;
            }
        });
        set("scale", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                float x = (float) args.checkdouble(1);
                float y = (float) args.checkdouble(2);
                float z = (float) args.optdouble(3, 1);
                GlStateManager.scale(x, y, z);
                return NIL;
            }
        });
        set("color", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                float r = 1f, g = 1f, b = 1f, a = 1f;
                LuaValue first = args.arg1();
                if (first.istable()) {
                    LuaTable t = (LuaTable) first;
                    r = (float) t.get(1).optdouble(1.0);
                    g = (float) t.get(2).optdouble(1.0);
                    b = (float) t.get(3).optdouble(1.0);
                    a = (float) t.get(4).optdouble(1.0);
                } else {
                    r = (float) args.optdouble(1, 1.0);
                    g = (float) args.optdouble(2, 1.0);
                    b = (float) args.optdouble(3, 1.0);
                    a = (float) args.optdouble(4, 1.0);
                }
                GlStateManager.color(r, g, b, a);
                return LuaValue.NIL;
            }
        });
        set("lineWidth", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue width) {
            	GL11.glLineWidth((float) width.todouble());
                return NIL;
            }
        });
        set("vertex", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                float x, y, z;
                LuaValue first = args.arg1();
                if (first.istable()) {
                    LuaTable t = (LuaTable) first;
                    x = (float) t.get(1).optdouble(0);
                    y = (float) t.get(2).optdouble(0);
                    z = (float) t.get(3).optdouble(0);
                } else {
                    x = (float) args.checkdouble(1);
                    y = (float) args.checkdouble(2);
                    z = (float) args.checkdouble(3);
                }
                GL11.glVertex3f(x, y, z);
                return LuaValue.NIL;
            }
        });
        set("getRenderPos", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                double rx = Client.mc.getRenderManager().renderPosX;
                double ry = Client.mc.getRenderManager().renderPosY;
                double rz = Client.mc.getRenderManager().renderPosZ;
                LuaTable t = new LuaTable();
                t.set(1, LuaValue.valueOf(rx));
                t.set(2, LuaValue.valueOf(ry));
                t.set(3, LuaValue.valueOf(rz));
                return t;
            }
        });
        set("glBegin", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                int index;
                if (args.narg() >= 2 && args.arg1().istable()) {
                    index = 2;
                } else {
                    index = 1;
                }
                int glMode = args.checkint(index);
                GL11.glBegin(glMode);
                return NIL;
            }
        });
        set("glEnd", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                GL11.glEnd();
                return LuaValue.NIL;
            }
        });
        set("enableAlpha", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                GlStateManager.enableAlpha();
                return NIL;
            }
        });
        set("disableAlpha", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                GlStateManager.disableAlpha();
                return NIL;
            }
        });
    }
}
