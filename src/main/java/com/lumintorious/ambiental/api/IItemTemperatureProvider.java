package com.lumintorious.ambiental.api;

import com.lumintorious.ambiental.modifiers.ItemModifier;
import com.lumintorious.ambiental.modifiers.ModifierStorage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface IItemTemperatureProvider extends ITemperatureProvider{
	public ItemModifier getModifier(ItemStack stack, EntityPlayer player);
}
