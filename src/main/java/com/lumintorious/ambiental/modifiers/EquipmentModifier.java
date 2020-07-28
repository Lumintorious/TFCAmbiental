package com.lumintorious.ambiental.modifiers;

import java.util.HashMap;
import java.util.Map;

import com.lumintorious.ambiental.capability.TemperatureSystem;

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
						if(envTemp > TemperatureSystem.AVERAGE + 3) {
							float diff = envTemp - TemperatureSystem.AVERAGE;
							modifiers.add(new EquipmentModifier("helmet", - envTemp / 3, -0.4f));
						}
					}
				}else {
					modifiers.add(new EquipmentModifier("armor", 2f, -0.1f));
				}
			}
		}
	}

}






