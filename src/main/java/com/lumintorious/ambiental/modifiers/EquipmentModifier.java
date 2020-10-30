package com.lumintorious.ambiental.modifiers;

import com.lumintorious.ambiental.capability.TemperatureCapability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class EquipmentModifier extends BaseModifier{
	
	public EquipmentModifier(String name) {
		super(name);
	}
	
	public EquipmentModifier(String unlocalizedName, float change, float potency) {
		super(unlocalizedName, change, potency);
	}
	
	public static void getModifiers(EntityPlayer player, ModifierStorage modifiers) {
		Iterable<ItemStack> armor = player.getArmorInventoryList();
		for(ItemStack stack : armor) {
			if(stack.getItem() instanceof ItemArmor) {
				ItemArmor thing = (ItemArmor)stack.getItem();
				if(thing.armorType == EntityEquipmentSlot.HEAD) {
					if(player.world.getLight(player.getPosition()) > 14) {
						float envTemp = EnvironmentalModifier.getEnvironmentTemperature(player);
						if(envTemp > TemperatureCapability.AVERAGE + 3) {
							float diff = envTemp - TemperatureCapability.AVERAGE;
							modifiers.add(new EquipmentModifier("helmet", - diff / 3f, -0.5f));
						}else{
							modifiers.add(new EquipmentModifier("armor", 3f, -0.25f));
						}
					}
				}else {
					float envTemp = EnvironmentalModifier.getEnvironmentTemperature(player);
					if(envTemp > TemperatureCapability.AVERAGE + 3){
						modifiers.add(new EquipmentModifier("armor", 3f, -0.25f));
					}
				}
			}
		}
	}

}






