package com.lumintorious.ambiental;

import com.lumintorious.ambiental.capability.ITemperatureSystem;
import com.lumintorious.ambiental.capability.TemperaturePacket;
import com.lumintorious.ambiental.capability.TemperatureSystem;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.DumbStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = TFCAmbiental.MODID, name = TFCAmbiental.NAME, version = TFCAmbiental.VERSION)
public class TFCAmbiental
{
    public static final String MODID = "tfcambiental";
    public static final String NAME = "TFC Ambiental";
    public static final String VERSION = "1.0";
    
    @Mod.Instance
    public static TFCAmbiental INSTANCE;
    
    public final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	CapabilityManager.INSTANCE.register(ITemperatureSystem.class, new DumbStorage(), () -> null);
    	
    	
    	TerraFirmaCraft.getNetwork().registerMessage(new TemperaturePacket.Handler(), TemperaturePacket.class, 0, Side.CLIENT);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(new PlayerTemperatureHandler());
    	
    	
    	if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
    		MinecraftForge.EVENT_BUS.register(new GuiRenderer());
        }
    }
    
}
