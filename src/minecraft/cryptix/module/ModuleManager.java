package cryptix.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import cryptix.Client;
import cryptix.module.combat.*;
import cryptix.module.exploit.*;
import cryptix.module.movement.*;
import cryptix.module.player.*;
import cryptix.module.visual.*;
import cryptix.other.event.Event;
import net.minecraft.client.settings.KeyBinding;

public class ModuleManager {
	public final Map<Integer, List<Module>> keyMap = new HashMap<>();
	private final Map<String, Module> moduleMap = new HashMap<>();
	private final Map<Category, List<Module>> categoryMap = new HashMap<>();
	private List<Module> modules = new ArrayList<>();
	public AimAssist aimAssist;
	public Criticals criticals;
	public KillAura killAura;
	public Killsults killsults;
	public LagRange lagrange;
	public Velocity velo;
	public AutoLogin autologin;
	public Disabler disabler;
	public PlayerCrasher playerCrasher;
	public Fly fly;
	public LongJump longjump;
	public NoSlow noslow;
	public Sprint sprint;
	public TargetStrafe targetStrafe;
	public AntiVoid antiVoid;
	public AutoPlay autoPlay;
	public BedNuker bedNuker;
	public NoFall noFall;
	public NoRotate noRotate;
	public Phase phase;
	public SafeWalk safeWalk;
	public Scaffold scaffold;
	public Animations aminations;
	public BedESP bedesp;
	public ClickGUI clickGUI;
	public FreeLook freeLook;
	public HUD hud;
	public KillEffects killEffects;
	public SessionInfo sessionInfo;
	public AntiBot antibot;
	public AutoRod autorod;
	public Speed speed;
	public NoHurtCam noHurtCam;
	public Scoreboard scoreboard;
	public ModuleManager() {
		//Modules goes here
		//Combat
		modules.add(new AimAssist());
		modules.add(antibot = new AntiBot());
		modules.add(new AutoClicker());
		modules.add(autorod = new AutoRod());
		modules.add(criticals = new Criticals());
		modules.add(killAura = new KillAura());
		modules.add(killsults = new Killsults());
		modules.add(lagrange = new LagRange());
		//modules.add(new Reach());
		modules.add(new SprintReset());
		modules.add(velo = new Velocity());
		//Exploit
		modules.add(new AntiCheat());
		modules.add(autologin = new AutoLogin());
		modules.add(disabler = new Disabler());
		modules.add(new StaffDetector());
		modules.add(playerCrasher = new PlayerCrasher());
		//Movement
		modules.add(new AirStuck());
		modules.add(new GroundSpeed());
		modules.add(fly = new Fly());
		modules.add(new InvMove());
		modules.add(new KeepSprint());
		modules.add(longjump = new LongJump());
		modules.add(noslow = new NoSlow());
		modules.add(speed = new Speed());
		modules.add(sprint = new Sprint());
		modules.add(new Step());
		modules.add(targetStrafe = new TargetStrafe());
		modules.add(new Timer());
		//Player
		modules.add(new AntiFireBall());
		modules.add(antiVoid = new AntiVoid());
		modules.add(new AutoBlockIn());
		modules.add(new AutoFish());
		modules.add(autoPlay = new AutoPlay());
		modules.add(new AutoTool());
		modules.add(bedNuker = new BedNuker());
		modules.add(new Blink());
		modules.add(new ChestStealer());
		modules.add(new FastPlace());
		modules.add(new InvManager());
		modules.add(new Jesus());
		modules.add(new NoClickDelay());
		modules.add(noFall = new NoFall());
		modules.add(new NoJumpDelay());
		modules.add(noRotate = new NoRotate());
		modules.add(phase = new Phase());
		modules.add(safeWalk = new SafeWalk());
		modules.add(scaffold = new Scaffold());
		//Visual
		modules.add(aminations = new Animations());
		modules.add(bedesp = new BedESP());
		modules.add(new BedPlates());
		modules.add(new BPSCounter());
		modules.add(new Chams());
		modules.add(new ChestESP());
		modules.add(clickGUI = new ClickGUI());
		modules.add(freeLook = new FreeLook());
		modules.add(new FullBright());
		modules.add(new Gamble());
		modules.add(hud = new HUD());
		modules.add(new ItemESP());
		modules.add(killEffects = new KillEffects());
		modules.add(new NameTags());
		modules.add(noHurtCam = new NoHurtCam());
		modules.add(new PlayerESP());
		modules.add(scoreboard = new Scoreboard());
		modules.add(sessionInfo = new SessionInfo());
		modules.add(new TargetHUD());
		modules.add(new Weather());
		for (Module module : modules) {
			keyMap.computeIfAbsent(module.getKey(), k -> new ArrayList<>()).add(module);
		    moduleMap.put(module.getName().toLowerCase(), module);
		}
	}
	
	public List<Module> getModules() {
        return modules;
    }
    
	public Module getModuleByName(String name) {
	    return moduleMap.get(name.toLowerCase());
	}
    
	public void onKey(int key) {
		List<Module> mods = keyMap.get(key);
	    if (mods != null) {
	        for (Module mod : mods) {
	            mod.toggle();
	        }
	    }
	}
	
	public void onEvent(Event e) {
		for(Module mod : modules) {
			if(mod.isToggled()) mod.onEvent(e);
		}
	}
}
