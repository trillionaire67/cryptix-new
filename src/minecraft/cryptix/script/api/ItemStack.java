package cryptix.script.api;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.*;

public class ItemStack extends LuaTable {

	private final net.minecraft.item.ItemStack stack;

    public ItemStack(net.minecraft.item.ItemStack stack) {
        this.stack = stack;
        set("getStackSize", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(stack.stackSize);
            }
        });
        set("getDisplayName", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(stack.getDisplayName());
            }
        });
        set("getItemDamage", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(stack.getItemDamage());
            }
        });
        set("getItemID", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(net.minecraft.item.Item.getIdFromItem(stack.getItem()));
            }
        });
    }

    public net.minecraft.item.ItemStack getItemStack() {
        return stack;
    }
}