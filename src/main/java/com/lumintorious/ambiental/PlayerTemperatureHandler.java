package com.lumintorious.ambiental;

import com.lumintorious.ambiental.capability.TemperatureCapability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerTemperatureHandler {
	
	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if(event.getEntityLiving().world.isRemote) {
			return;
		}
		if(!(event.getEntityLiving() instanceof EntityPlayer)) {
			return;
		}
		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
	}
	
	@SubscribeEvent
	public void onPlayerSpawn(LivingSpawnEvent event) {
		if(event.getEntityLiving().world.isRemote) {
			return;
		}
		if(!(event.getEntityLiving() instanceof EntityPlayer)) {
			return;
		}
		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		player.sendMessage(new TextComponentString("respawned"));
	}
	
	@SubscribeEvent
    public void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)event.getObject();

                ResourceLocation loc = new ResourceLocation(TFCAmbiental.MODID, "temperature");

                //Each player should have their own instance for each stat, as associated values may vary
                if (!event.getCapabilities().containsKey(loc))
                    event.addCapability(loc, new TemperatureCapability(player));
        }
    }

	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent  event) {
		if(!(event.getEntityLiving() instanceof EntityPlayer)) {
			return;
		}

		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		if(player.isCreative()) {
			return;
		}
		TemperatureCapability temp = (TemperatureCapability)player.getCapability(TemperatureCapability.CAPABILITY, null);
		ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
		if(stack != null && !player.world.isRemote) {
			if(stack.getItem().getRegistryName().toString().equals("tfc:wand")) {
				temp.say(temp);
			}
		}
		temp.update();
	}
}
