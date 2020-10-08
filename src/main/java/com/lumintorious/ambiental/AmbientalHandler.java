package com.lumintorious.ambiental;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.lang.reflect.Field;

import com.lumintorious.ambiental.capability.TemperatureCapability;
import com.lumintorious.ambiental.capability.TimeExtensionCapability;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.plants.BlockPlantTFC;
import net.dries007.tfc.objects.blocks.stone.BlockFarmlandTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.minecraft.advancements.critereon.VillagerTradeTrigger;
import net.minecraft.block.Block;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.GameRuleChangeEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.terraingen.BiomeEvent.GetGrassColor;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AmbientalHandler {

//	@SubscribeEvent
//	public void onSleep(PlayerWakeUpEvent event) {
//		TimeExtensionCapability.onSleep(event);
//	}
//	
//	@SubscribeEvent
//	public void onGameRuleChange(GameRuleChangeEvent event) {
//		TimeExtensionCapability.onGameRuleChange(event);
//	}
	
	// Ignore this code!
	@SubscribeEvent
	public void onInteract(EntityInteractSpecific event) {
		
		Entity entity = event.getTarget();
		EntityPlayer player = event.getEntityPlayer();
		if(entity instanceof EntityVillager) {
			EntityVillager villager = (EntityVillager)entity;
			long time = villager.world.getWorldTime();
			try {
				MerchantRecipeList list = new MerchantRecipeList();
				for(Field f : EntityVillager.class.getDeclaredFields()) {
					f.setAccessible(true);
					if(f.get(villager) instanceof MerchantRecipeList) {
						list = (MerchantRecipeList)f.get(villager);
					
						MerchantRecipeList list2 = new MerchantRecipeList();
						
						for(MerchantRecipe recipe: list) {
							ItemStack itemToBuy = recipe.getItemToBuy().copy();
							ItemStack itemToBuy2 = recipe.getSecondItemToBuy().copy();
							ItemStack itemToSell = recipe.getItemToSell().copy();
							if(itemToSell.hasCapability(CapabilityFood.CAPABILITY, null)) {
								IFood cap = (IFood)itemToSell.getCapability(CapabilityFood.CAPABILITY, null);
								cap.setCreationDate(time);
							}
							list2.add(new MerchantRecipe(itemToBuy, itemToBuy2, itemToSell));
						}
						f.set(villager, list2);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
//	@SubscribeEvent
//	public void onWorldTick(WorldTickEvent event) {
//		TimeExtensionCapability.onWorldTick(event);
//	}
	
	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if(event.getEntityLiving().world.isRemote) {
			return;
		}
		if(!(event.getEntityLiving() instanceof EntityPlayer)) {
			return;
		}
		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		if(player.hasCapability(TemperatureCapability.CAPABILITY, null)) {
			TemperatureCapability cap = (TemperatureCapability)player.getCapability(TemperatureCapability.CAPABILITY, null);
			cap.bodyTemperature = TemperatureCapability.AVERAGE;
		}
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
	
//	@SubscribeEvent
//    public void onAttachWorldCapabilities(AttachCapabilitiesEvent<World> event)
//    {
//		if (event.getObject() instanceof World)
//        {
//            ResourceLocation loc = new ResourceLocation(TFCAmbiental.MODID, "time_extension");
//            World world = (World)event.getObject();
//            if (!event.getCapabilities().containsKey(loc))
//                event.addCapability(loc, new TimeExtensionCapability(world));
//        }
//    }
	
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
