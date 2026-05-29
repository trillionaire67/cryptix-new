package cryptix.script.api;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.*;

import cryptix.Client;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.Vec3;

public class Packets extends LuaTable {
	public Packets() {
		set("sendC01", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue message) {
                return LuaValue.userdataOf(
                    new C01PacketChatMessage(message.checkjstring())
                );
            }
        });
		set("sendC02", new TwoArgFunction() {
		    @Override
		    public LuaValue call(LuaValue entityValue, LuaValue actionValue) {
		        if (!(entityValue instanceof cryptix.script.api.Entity)) {
		            return LuaValue.error("Expected Entity");
		        }
		        cryptix.script.api.Entity luaEntity = (cryptix.script.api.Entity) entityValue;
		        net.minecraft.entity.Entity mcEntity = luaEntity.getEntity();
		        C02PacketUseEntity.Action action = C02PacketUseEntity.Action.valueOf(actionValue.checkjstring().toUpperCase());
		        Client.mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(mcEntity, action));
		        return NIL;
		    }
		});
	}
}
