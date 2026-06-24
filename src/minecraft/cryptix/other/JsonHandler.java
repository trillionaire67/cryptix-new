package cryptix.other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import cryptix.Client;
import cryptix.altmanager.Alt;
import cryptix.altmanager.AltManagerGui;
import cryptix.altmanager.SessionChanger;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.BooleanSetting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.gui.clickgui.settings.ModeSetting;
import cryptix.module.Module;
import cryptix.module.visual.ClickGUI;
import cryptix.script.Script;
import cryptix.utils.Utils;

public class JsonHandler {
	public static String NICEALTS_KEY = "";
	public static File ROOT_DIR = new File("cryptix");
    public static File config = new File(ROOT_DIR, "config");
    public static File script = new File(ROOT_DIR, "script");
    public static File friends = new File(ROOT_DIR, "friends.json");
    public static File keybinds = new File(ROOT_DIR, "keybinds.json");
    public static File alts = new File(ROOT_DIR, "alts.json");
    public static File apiKey = new File(ROOT_DIR, "key.json");
    private static HashSet<String> modBlackList = Sets.newHashSet(ClickGUI.class.getName());
    public static Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
    public static JsonParser jsonParser = new JsonParser();
    public static Set<String> friendList = new HashSet<>();
    public static void start() {
        if(!ROOT_DIR.exists()) {ROOT_DIR.mkdirs();}
        if(!config.exists()) {config.mkdirs();}
        if(!script.exists()) {script.mkdirs();}
        Client.instance.scriptManager.loadScripts();
        if(!friends.exists()) {saveFriends();}
        if(!keybinds.exists()) {saveKeybinds();}
        loadKeybinds();
        loadFriends();
    }
    
    public static void stop() {
    	saveKeybinds();
    	saveFriends();
    }
    
    public static void saveFriends() {
        try {
            JsonObject json = new JsonObject();
            JsonArray array = new JsonArray();
            for (String friend : friendList) {
            	array.add(new JsonPrimitive(friend));
            }
            json.add("friends", array);
            try (PrintWriter save = new PrintWriter(new FileWriter(friends))) {
                save.println(prettyGson.toJson(json));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFriends() {
        try {
            BufferedReader load = new BufferedReader(new FileReader(friends));
            JsonObject json = (JsonObject) jsonParser.parse(load);
            load.close();
            friendList.clear();
            JsonArray array = json.getAsJsonArray("friends");
            for (JsonElement element : array) {
                friendList.add(element.getAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addFriend(String name) {
        if (friendList.add(name)) {
            saveFriends();
            Utils.sendClientChatMessage("Added " + name + " to friends. ");
        } else {
            Utils.sendClientChatMessage(name + " is already in your friends list.");
        }
    }

    public static void removeFriend(String name) {
    	if(name.equalsIgnoreCase("the_trilliona1re")) {
    		Utils.sendClientChatMessage("You cant remove this friend");
    		return;
    	}
    	if (friendList.remove(name)) {
            saveFriends();
            Utils.sendClientChatMessage("Removed " + name + " from friends. ");
        } else {
            Utils.sendClientChatMessage(name + " is not in your friends list.");
        }
    }
    
    public static void saveAlts() {
        try {
            JsonObject json = new JsonObject();
            JsonArray altArray = new JsonArray();
            for (Alt alt : AltManagerGui.alts) {
                JsonObject altJson = new JsonObject();
                altJson.addProperty("name", alt.getName());
                altJson.addProperty("cracked", alt.isCracked());
                altJson.addProperty("has-refresh-token", alt.hasRefreshToken());
                altJson.addProperty("has-token", alt.hasToken());

                if (!alt.isCracked()) {
                    altJson.addProperty("email", alt.getEmail());
                    altJson.addProperty("password", alt.getPassword());
                }
                if(alt.hasRefreshToken()) {
                	altJson.addProperty("refresh-token", alt.getRefreshToken());
                }
                if(alt.hasToken()) {
                	altJson.addProperty("token", alt.getToken());
                }
                altArray.add(altJson);
            }
            json.add("alts", altArray);
            PrintWriter save = new PrintWriter(new FileWriter(alts));
            save.println(prettyGson.toJson(json));
            save.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void loadAlts() {
        try {
            BufferedReader load = new BufferedReader(new FileReader(alts));
            JsonObject json = (JsonObject) jsonParser.parse(load);
            load.close();
            AltManagerGui.alts.clear();
            JsonArray altArray = json.getAsJsonArray("alts");
            for (JsonElement element : altArray) {
                JsonObject altJson = element.getAsJsonObject();
                String name = altJson.get("name").getAsString();
                boolean cracked = altJson.get("cracked").getAsBoolean();
                boolean refresh = altJson.get("has-refresh-token").getAsBoolean();
                boolean token = altJson.get("has-token").getAsBoolean();
                String email = cracked || refresh || token ? null : altJson.get("email").getAsString();
                String password = cracked || refresh || token ? null : altJson.get("password").getAsString();
                Alt alt = new Alt(email, password, name, cracked);
                if(refresh) {
                	alt.setRefreshToken(altJson.get("refresh-token").getAsString());
                }
                if(token) {
                	alt.setToken(altJson.get("token").getAsString());
                }
                AltManagerGui.alts.add(alt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveMods(String configName) {
        File configs = new File(config, configName + ".json");
        try {
            JsonObject json = new JsonObject();
            for (Module mod : Client.instance.moduleManager.getModules()) {
                JsonObject jsonMod = new JsonObject();
                jsonMod.addProperty("enabled", mod.isToggled());
                if(Client.instance.settingsManager.getSettingsByMod(mod) != null) {
                    JsonObject jsonSettings = new JsonObject();
                    for (Setting setting : Client.instance.settingsManager.getSettingsByMod(mod)) {
                        if(setting == null) continue;
                        if (setting instanceof ModeSetting) {
                            jsonSettings.addProperty(setting.getName(), ((ModeSetting)Client.instance.settingsManager.getSettingByName(mod, setting.getName())).getString());
                        } else if (setting instanceof BooleanSetting) {
                            jsonSettings.addProperty(setting.getName(), ((BooleanSetting)Client.instance.settingsManager.getSettingByName(mod, setting.getName())).getBoolean());
                        } else if (setting instanceof DoubleSetting) {
                            jsonSettings.addProperty(setting.getName(), ((DoubleSetting)Client.instance.settingsManager.getSettingByName(mod, setting.getName())).getValue());
                        }
                    }
                    jsonMod.add("settings", jsonSettings);
                }
                json.add(mod.getName(), jsonMod);
            }
            PrintWriter save = new PrintWriter(new FileWriter(configs));
            save.println(prettyGson.toJson(json));
            save.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void loadMods(String configName) {
    	File configs = new File(config, configName + ".json");
    	try {
             BufferedReader load = new BufferedReader(new FileReader(configs));
             JsonObject json = (JsonObject) jsonParser.parse(load);
             load.close();
             for (Entry<String, JsonElement> entry : json.entrySet()) {
                 Module mod = Client.instance.moduleManager.getModuleByName(entry.getKey());
                 JsonObject jsonModule = (JsonObject) entry.getValue();
                 if (mod != null ) {
                     boolean enabled = jsonModule.get("enabled").getAsBoolean();

                     if (enabled && !mod.isToggled()) {
                         mod.toggle();
                     }
                     if (!enabled && mod.isToggled()) {
                         mod.toggle();
                     }
                 }else {
                	 continue;
                 }
                 JsonObject jsonSettings = jsonModule.getAsJsonObject("settings");
                 if (jsonSettings != null && Client.instance.settingsManager.getSettings() != null) {
                     for (Setting setting : Client.instance.settingsManager.getSettingsByMod(mod)) {
                         if (jsonSettings.has(setting.getName())) {
                        	 if (setting instanceof ModeSetting) {
                                 String value = jsonSettings.get(setting.getName()).getAsString();
                                 ((ModeSetting)Client.instance.settingsManager.getSettingByName(mod, setting.getName())).setString(value);
                             } else if (setting instanceof BooleanSetting) {
                                 boolean value = jsonSettings.get(setting.getName()).getAsBoolean();
                                 ((BooleanSetting)Client.instance.settingsManager.getSettingByName(mod, setting.getName())).setBoolean(value);
                             } else if (setting instanceof DoubleSetting) {
                                 double value = jsonSettings.get(setting.getName()).getAsDouble();
                                 ((DoubleSetting)Client.instance.settingsManager.getSettingByName(mod, setting.getName())).setValue(value);
                             }
                         }
                     }
                 }
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
    }

    public static void saveKeybinds() {
        try {
            JsonObject root = new JsonObject();
            JsonObject moduleKeybinds = new JsonObject();
            for (Module mod : Client.instance.moduleManager.getModules()) {
                moduleKeybinds.addProperty(mod.getName(), mod.getKey());
            }
            root.add("modules", moduleKeybinds);
            JsonObject scriptKeybinds = new JsonObject();
            for (Script script : Client.instance.scriptManager.getScripts()) {
                scriptKeybinds.addProperty(script.getName(), script.getKey());
            }
            root.add("scripts", scriptKeybinds);
            PrintWriter save = new PrintWriter(new FileWriter(keybinds));
            save.println(prettyGson.toJson(root));
            save.close();
        } catch (Exception e) {
            System.err.println("Error while saving keybinds:");
            e.printStackTrace();
        }
    }


    public static void loadKeybinds() {
        try {
            BufferedReader load = new BufferedReader(new FileReader(keybinds));
            JsonObject root = (JsonObject) jsonParser.parse(load);
            load.close();
            if (root.has("modules") || root.has("scripts")) {
                if (root.has("modules")) {
                    JsonObject modules = root.getAsJsonObject("modules");
                    for (Entry<String, JsonElement> entry : modules.entrySet()) {
                        Module mod = Client.instance.moduleManager.getModuleByName(entry.getKey());
                        if (mod != null && entry.getValue().isJsonPrimitive()) {
                            mod.setKey(entry.getValue().getAsInt());
                        }
                    }
                }
                if (root.has("scripts")) {
                    JsonObject scripts = root.getAsJsonObject("scripts");
                    for (Entry<String, JsonElement> entry : scripts.entrySet()) {
                        Script script = Client.instance.scriptManager.getScript(entry.getKey());
                        if (script != null && entry.getValue().isJsonPrimitive()) {
                            script.setKey(entry.getValue().getAsInt());
                        }
                    }
                }

            } 
            else {
                for (Entry<String, JsonElement> entry : root.entrySet()) {
                    Module mod = Client.instance.moduleManager.getModuleByName(entry.getKey());
                    if (mod != null && entry.getValue().isJsonPrimitive()) {
                        mod.setKey(entry.getValue().getAsInt());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error while loading keybinds:");
            e.printStackTrace();
        }
    }
    
    public static void saveKey() {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("nicealts_key", NICEALTS_KEY);

            PrintWriter save = new PrintWriter(new FileWriter(apiKey));
            save.println(prettyGson.toJson(json));
            save.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void loadKey() {
        try {
            if (!apiKey.exists()) {
                saveKey();
                return;
            }

            BufferedReader load = new BufferedReader(new FileReader(apiKey));
            JsonObject json = (JsonObject) jsonParser.parse(load);
            load.close();

            if (json.has("nicealts_key")) {
                NICEALTS_KEY = json.get("nicealts_key").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
