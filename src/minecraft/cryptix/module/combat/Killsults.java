package cryptix.module.combat;

import java.util.Arrays;

import cryptix.Client;
import cryptix.gui.clickgui.Setting;
import cryptix.gui.clickgui.settings.DoubleSetting;
import cryptix.gui.clickgui.settings.ModeSetting;
import cryptix.module.Category;
import cryptix.module.Module;
import cryptix.utils.Utils;

public class Killsults extends Module{
	private ModeSetting mode = new ModeSetting("Mode", this, "Normal", Arrays.asList("Normal", "Islam"));
	private DoubleSetting delay = new DoubleSetting("Delay", this, 5, 1, 10, true);
	private final String[] insultsIslam = {"muhammad was a caravan robber", "muhammad married a 9 year old",
            "muhammad is a fucking pædo", "fun fact: aisha used to scrape sæmen stains off the prophets clothes!",
            "fuck your moon god!",
            "muhammad is a rtarded false prophet", "allah cant save you from me mwahahaha",
            "islam is a crime against humanity", "not even allah can move as fast as me",
            "go drink some fucking camel urine you rtard", "fuck muhammad", "muhammad piss be upon him",
            "leave fucking islam", "the quran isnt preserved you rtard", "u need Jesus not some pædophile prophet",
            "astaghfirullah! theres sæmen on my clothes!!!!",
            "stop idolizing fucking zakir naik and grow a brain rtard", "let me get this straight, muhammad was a pædo and hes supposed to be the best example for humanity? XD",
            "bro was doing wudu afk", "i am fighting for the cause of allah for my 72 virgins in paradise!!!!",
            "fuck your religion and fuck your prophet", "muslims kill people in a block game to prepare for jihad",
            "islam was spread by the sword",
            "your dad prays to a black rock", "the quran is good quality toilet paper - 5 stars", "islam is cancær",
            "your fucking desert culture is a joke, go back to riding camels", "you fucking muslim teerrorist",
            "ur mom wears a burqa and still cant hide her shame", "the quran says allah sends people to hell for not fasting during ramadan you fatass"};
	private final String[] insultsNormal = {"your dad should've worn a condom that time", "keep clicking your 6cps d!ck head",
			"keep smashing your touchpad", "The only thing lower than your k/d ratio is your I.Q.", "your aim is so poor people held a fundraiser for it",
			"even vape v4 can't save your shit fatass", "you are more productive sucking my d!ck then playing this game", "your grandma couldn't withstand my almighty d!ck",
			"calling you a rtard is a complement", "ding ding ding oh what's this the elevator your not on my level", "did you know aiming actually improves the chances of hitting your target",
			"ooowee seems like your can't handle my d!ck", "my d!ck goes all the way up your d!ck and out of your mouth", "your mom's balloons are pressing against my pencil",
			"you can't last a day of Ramadan you fatass Muslim", "maybe it's time to get a job you useless unemployed faggot",
			"shower your dirty b!tch instead of wasting your time on this block game", "useless fatass doing nothing just suicide bro", "your blowjobs never disappoint baby girl <3",
			"your mom's balloons are on my pencil", "your skull has 3 layers and your still rtarded", "your so trash at pvp", "your pvp is like a bot", "report = gay",
			"stop running you fuckin rtard!", "i really dont get it, mfs get shit on by hackers everyday on blocksmc and they STILL play again. can someone explain?"};
	private long lastInsultTime;
	public Killsults() {
		super("Killsults", 0, Category.COMBAT);
		this.addSetting(mode, delay);
	}
	
	public void insult() {
		long cooldown = (long) delay.getValue() * 1000L;
        if (System.currentTimeMillis() - lastInsultTime < cooldown) {
            return;
        }
        lastInsultTime = System.currentTimeMillis();
		if(mode.getString().equalsIgnoreCase("Islam")) {
			Utils.sendServerChatMessage(insultsIslam[Utils.randomInt(0, insultsIslam.length)]);
		}
		if(mode.getString().equalsIgnoreCase("Normal")) {
			Utils.sendServerChatMessage(insultsNormal[Utils.randomInt(0, insultsNormal.length)]);
		}
	}
	
	@Override
	public void onPreUpdate() {
		this.setDisplayName(this.getName() + this.getUppercaseSuffix(mode.getString()));
	}

}