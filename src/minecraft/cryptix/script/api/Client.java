package cryptix.script.api;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;

public class Client extends LuaTable {
	public Client() {
		set("print", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue message) {
                cryptix.Client.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message.checkjstring()));
                return NIL;
            }
        });
        set("getEntities", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable table = new LuaTable();
                int index = 1;

                if (Minecraft.getMinecraft().theWorld == null) return table;

                for (Object obj : Minecraft.getMinecraft().theWorld.loadedEntityList) {
                    if (obj instanceof net.minecraft.entity.Entity) {
                        net.minecraft.entity.Entity e = (net.minecraft.entity.Entity) obj;
                        if (e != null) {
                            table.set(index++, new cryptix.script.api.Entity(e));
                        }
                    }
                }

                return table;
            }
        });
        set("getHudColor", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                int c = cryptix.Client.instance.moduleManager.hud.getColorInt(0, 1f);
                return LuaValue.valueOf(c);
            }
        });
        set("getTime", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(System.currentTimeMillis());
            }
        });
        set("getNanoTime", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(System.nanoTime());
            }
        });
        set("getScreenWidth", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(cryptix.Client.mc.displayWidth);
            }
        });
        set("getScreenHeight", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(cryptix.Client.mc.displayHeight);
            }
        });
        set("getKillAuraTarget", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
            	EntityLivingBase target = cryptix.Client.instance.moduleManager.killAura.target;
                if (target == null) return LuaValue.NIL;
                return CoerceJavaToLua.coerce(new cryptix.script.api.Entity2(target));
            }
        });
	}
}
