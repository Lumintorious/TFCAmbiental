package com.lumintorious.ambiental;

import com.lumintorious.ambiental.capability.TemperatureSystem;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = TFCAmbiental.MODID, category = "")
@Mod.EventBusSubscriber(modid = TFCAmbiental.MODID)
@Config.LangKey("config." + TFCAmbiental.MODID)
public class TFCAmbientalConfig {
		
		    @Config.Comment("Client settings")
		    @Config.LangKey("config." + TFCAmbiental.MODID + ".client")
		    public static final ClientCFG CLIENT = new ClientCFG();
		    
		    @Config.Comment("General settings")
		    @Config.LangKey("config." + TFCAmbiental.MODID + ".general")
		    public static final GeneralCFG GENERAL = new GeneralCFG();

		    @SubscribeEvent
		    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
		    {
		        if (event.getModID().equals(TFCAmbiental.MODID))
		        {
		            ConfigManager.sync(TFCAmbiental.MODID, Config.Type.INSTANCE);
		            
		        }
		    }

		    public static class ClientCFG{
		    	@Config.Comment("If true, temperature is displayed in Celsius instead of Farhenheit .")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".client.celsius")
	         	public boolean celsius = true;
		    	
	    	 	@Config.Comment("If true, you will get extra details about your temperature when sneaking, when false they are always visible.")
	    	 	@Config.LangKey("config." + TFCAmbiental.MODID + ".client.sneakyDetails")
	    	 	public boolean sneakyDetails = true;
		    }
		    
		    public static class GeneralCFG {
		    	@Config.Comment("How quickly temperature rises and decreases")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.temperatureMultiplier")
	         	public float temperatureMultiplier = 1.0f;
		    	
		    	@Config.Comment("How fast does temperature change when it's going towards the average.")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.positiveModifier")
	         	public float positiveModifier = 5f;
		    	
		    	@Config.Comment("How fast does temperature change when it's going away from the average. If you think you are giving yourself a challenge by increasing this number, think twice. It makes it so that you have to warm yourself up every so often.")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.negativeModifier")
	         	public float negativeModifier = 1f;
		    	
		    	@Config.Comment("How many ticks between modifier calculations. Too high values help performance but behave weirdly. 20 = 1 second means modifiers are checked every second. Also affects the packet sending interval.")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.tickInterval")
	         	public int tickInterval = 20;
		    	
		    	@Config.Comment("How potent are multipliers with more than one instance. (Eg. 2 fire pits nearby means they have 2 * this effectiveness)")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.diminishedModifierMultiplier")
	         	public float diminishedModifierMultiplier = 0.7f;
		    	
		    	@Config.Comment("How many modifiers of the same type until they stop adding together")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.modifierCap")
	         	public int modifierCap = 4;
		    	
		    	@Config.Comment("If true, temperate areas won't be as mild")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.harsherTemperateAreas")
	         	public boolean harsherTemperateAreas = true;
		    	
		    	@Config.Comment("If harsherTemperateAreas is true, environmental temperatures going away from the average are multiplied by this number. (The less temperate an area is, the less the modifier affects it) ")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.harsherMultiplier")
	         	public float harsherMultiplier = 1.35f;
		    }

}
