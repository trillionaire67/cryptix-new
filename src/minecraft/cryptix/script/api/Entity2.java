package cryptix.script.api;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import net.minecraft.entity.EntityLivingBase;

public class Entity2 extends Entity {
	protected EntityLivingBase livingEntity;
	
	public Entity2(EntityLivingBase entity) {
		super(entity);
		set("getHealth", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.getHealth());
            }
        });
		set("getMaxHealth", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(entity.getMaxHealth());
            }
        });
	}
}
