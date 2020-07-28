package com.lumintorious.ambiental.api;

import com.lumintorious.ambiental.modifiers.EnvironmentalModifier;
import com.lumintorious.ambiental.modifiers.ModifierStorage;

import net.minecraft.entity.player.EntityPlayer;

@FunctionalInterface
public interface IEnvironmentalTemperatureProvider extends ITemperatureProvider{
	public EnvironmentalModifier getModifier(EntityPlayer player);
}
