package cryptix.script.api;

import org.luaj.vm2.lib.ZeroArgFunction;

import cryptix.Client;
import cryptix.utils.MovementUtils;
import cryptix.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

public class Player extends cryptix.script.api.Entity2 {
	private EntityPlayerSP getPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }
	
	public Player(EntityPlayerSP player) {
		super(player);
		this.entity = player;
        set("jump", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                EntityPlayerSP p = getPlayer();
                if (p != null) p.jump();
                return NIL;
            }
        });
        set("strafe", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue speed) {
                EntityPlayerSP p = getPlayer();
                if (p != null) {
                    MovementUtils.strafe((float) speed.todouble());
                }
                return NIL;
            }
        });
        set("swingItem", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                EntityPlayerSP p = getPlayer();
                if (p != null) p.swingItem();
                return NIL;
            }
        });
        set("attackEntity", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue value) {
            	 if (!(value instanceof cryptix.script.api.Entity)) {
                     return LuaValue.error("Expected an Entity");
                 }
                 cryptix.script.api.Entity luaEntity = (cryptix.script.api.Entity) value;
                 net.minecraft.entity.Entity targetEntity = luaEntity.getEntity();
                EntityPlayerSP p = getPlayer();
                if (p != null) Client.mc.playerController.attackEntity(p, targetEntity);;
                return NIL;
            }
        });
        set("getSpeed", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                EntityPlayerSP p = getPlayer();
                return p != null ? LuaValue.valueOf(MovementUtils.getSpeed()) : LuaValue.valueOf(0);
            }
        });
        // motion stuff
        set("getMotionX", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                EntityPlayerSP p = getPlayer();
                return p != null ? LuaValue.valueOf(p.motionX) : LuaValue.valueOf(0);
            }
        });
        set("setMotionX", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue value) {
                EntityPlayerSP p = getPlayer();
                if (p != null) p.motionX = value.todouble();
                return NIL;
            }
        });
        set("getMotionY", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                EntityPlayerSP p = getPlayer();
                return p != null ? LuaValue.valueOf(p.motionY) : LuaValue.valueOf(0);
            }
        });
        set("setMotionY", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue value) {
                EntityPlayerSP p = getPlayer();
                if (p != null) p.motionY = value.todouble();
                return NIL;
            }
        });
        set("getMotionZ", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                EntityPlayerSP p = getPlayer();
                return p != null ? LuaValue.valueOf(p.motionZ) : LuaValue.valueOf(0);
            }
        });
        set("setMotionZ", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue value) {
                EntityPlayerSP p = getPlayer();
                if (p != null) p.motionZ = value.todouble();
                return NIL;
            }
        });
        set("setMoveFix", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue value) {
                Client.movefix = value.checkboolean();
                return NIL;
            }
        });
        set("setRotations", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue yawValue, LuaValue pitchValue) {
                EntityPlayerSP player = getPlayer();
                if (player != null) {
                    player.rotationYawHead = yawValue.tofloat();
                    player.rotationPitchHead = pitchValue.tofloat();
                }
                return NIL;
            }
        });
        set("getHurtTime", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                EntityPlayerSP p = getPlayer();
                return p != null ? LuaValue.valueOf(p.hurtTime) : LuaValue.valueOf(0);
            }
        });
        set("getRotationsToEntity", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue entityValue) {
                if (!(entityValue instanceof cryptix.script.api.Entity)) {
                    return LuaValue.error("Expected Entity");
                }
                cryptix.script.api.Entity luaEntity = (cryptix.script.api.Entity) entityValue;
                net.minecraft.entity.Entity e = luaEntity.getEntity();
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
                if (player == null || e == null) return LuaValue.NIL;
                double dx = e.posX - player.posX;
                double dy = (e.posY + e.getEyeHeight()) - (player.posY + player.getEyeHeight());
                double dz = e.posZ - player.posZ;
                double distXZ = Math.sqrt(dx * dx + dz * dz);
                float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
                float pitch = (float) -Math.toDegrees(Math.atan2(dy, distXZ));
                LuaTable rot = new LuaTable();
                rot.set(1, LuaValue.valueOf(yaw));
                rot.set(2, LuaValue.valueOf(pitch));
                return rot;
            }
        });
        set("isUsingItem", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                EntityPlayerSP p = getPlayer();
                return p != null ? LuaValue.valueOf(p.isUsingItem()) : NIL;
            }
        });
        set("setOnGround", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue value) {
            	EntityPlayerSP p = getPlayer();
            	if(p != null) {
            		p.onGround = value.checkboolean();
            	}
                return NIL;
            }
        });
        set("getHeldItem", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                EntityPlayerSP p = getPlayer();
                if (p == null || p.getHeldItem() == null) {
                    return NIL;
                }
                return new cryptix.script.api.ItemStack(p.getHeldItem());
            }
        });
    }
}
