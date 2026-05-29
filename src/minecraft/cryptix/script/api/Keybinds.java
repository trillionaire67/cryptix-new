package cryptix.script.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;
import org.lwjgl.input.Keyboard;

public class Keybinds extends LuaTable {
    private final Minecraft mc = Minecraft.getMinecraft();

    public Keybinds() {

        set("setPressed", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue keyValue, LuaValue pressedValue) {
                int key = keyValue.checkint();
                boolean pressed = pressedValue.checkboolean();
                for (KeyBinding kb : mc.gameSettings.keyBindings) {
                    if (kb.getKeyCode() == key) {
                        kb.pressed = pressed;
                        break;
                    }
                }

                return NIL;
            }
        });
        set("isPressed", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue keyValue) {
                int key = keyValue.checkint();
                for (KeyBinding kb : mc.gameSettings.keyBindings) {
                    if (kb.getKeyCode() == key) {
                        return LuaValue.valueOf(Keyboard.isKeyDown(kb.getKeyCode()));
                    }
                }
                return LuaValue.FALSE;
            }
        });
        set("getKeyCode", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue keyNameValue) {
                String keyName = keyNameValue.checkjstring().toUpperCase();
                int keyCode = Keyboard.getKeyIndex(keyName);
                return LuaValue.valueOf(keyCode);
            }
        });
    }
}