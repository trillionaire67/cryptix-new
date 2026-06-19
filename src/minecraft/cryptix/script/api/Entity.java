package cryptix.script.api;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import cryptix.Client;
import net.minecraft.client.entity.EntityPlayerSP;

public class Entity extends LuaTable {
    protected net.minecraft.entity.Entity entity;

    public Entity(net.minecraft.entity.Entity entity) {
        this.entity = entity;
        set("getName", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.getName());
            }
        });
        set("getPosX", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.posX);
            }
        });
        set("getPosY", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.posY);
            }
        });
        set("getPosZ", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.posZ);
            }
        });
        set("getLastTickPosX", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.lastTickPosX);
            }
        });
        set("getLastTickPosY", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.lastTickPosY);
            }
        });
        set("getLastTickPosZ", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.lastTickPosZ);
            }
        });
        set("getYaw", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return entity != null ? LuaValue.valueOf(entity.rotationYaw) : LuaValue.valueOf(0);
            }
        });
        set("getPitch", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return entity != null ? LuaValue.valueOf(entity.rotationPitch) : LuaValue.valueOf(0);
            }
        });
        set("distanceTo", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue x, LuaValue y, LuaValue z) {
                return LuaValue.valueOf(entity.getDistance(x.checkdouble(), y.checkdouble(), z.checkdouble()));
            }
        });
        set("isSprinting", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.isSprinting());
            }
        });
        set("isSneaking", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.isSneaking());
            }
        });
        set("onGround", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.onGround);
            }
        });
        set("getEntityId", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.getEntityId());
            }
        });
        set("isAirBorne", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.isAirBorne);
            }
        });
        set("isDead", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.isDead);
            }
        });
        set("isCollidedHorizontally", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.isCollidedHorizontally);
            }
        });
        set("isCollidedVertically", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.isCollidedVertically);
            }
        });
        set("getTicksExisted", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.ticksExisted);
            }
        });
        set("getFallDistance", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.fallDistance);
            }
        });
        set("getNoClip", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.noClip);
            }
        });
    }
    
    public net.minecraft.entity.Entity getEntity() {
    	return this.entity;
    }
}