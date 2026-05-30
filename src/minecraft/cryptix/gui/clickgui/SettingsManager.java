package cryptix.gui.clickgui;

import java.util.ArrayList;
import java.util.List;

import cryptix.Client;
import cryptix.module.Module;


public class SettingsManager {
	
	private ArrayList<Setting> settings;
	
	public SettingsManager(){
		this.settings = new ArrayList<>();
	}
	
	public void addSetting(Setting in){
		this.settings.add(in);
	}
	
	public void addSettings(Setting... in) {
	    for (Setting setting : in) {
	        this.settings.add(setting);
	    }
	}
	
	public ArrayList<Setting> getSettings(){
		return this.settings;
	}
	
	public ArrayList<Setting> getSettingsByMod(Module mod){
		ArrayList<Setting> out = new ArrayList<>();
		for(Setting s : getSettings()){
			if(s.getParentMod().equals(mod)){
				out.add(s);
			}
		}
		if(out.isEmpty()){
			return null;
		}
		return out;
	}
	
	public Setting getSettingByName(String name){
		for(Setting set : getSettings()){
			if(set.getName().equalsIgnoreCase(name)){
				return set;
			}
		}
		System.err.println("["+ Client.name + "] Error Setting NOT found: '" + name +"'!");
		return null;
	}
	
	public Setting getSettingByName(Module m, String name) {
		for(Setting set : getSettings()){
			if(set.getName().equalsIgnoreCase(name) && set.getParentMod() == m){
				return set;
			}
		}
		System.err.println("["+ Client.name + "] Error Setting NOT found: '" + name +"'!");
		return null;
	}

}