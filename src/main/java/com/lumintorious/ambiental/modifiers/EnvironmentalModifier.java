package com.lumintorious.ambiental.modifiers;

import com.lumintorious.ambiental.TFCAmbientalConfig;
import com.lumintorious.ambiental.api.IEnvironmentalTemperatureProvider;
import com.lumintorious.ambiental.api.TemperatureRegistry;
import com.lumintorious.ambiental.capability.TemperatureCapability;

import com.lumintorious.ambiental.effects.TempEffect;
import net.dries007.tfc.api.capability.food.IFoodStatsTFC;
import net.dries007.tfc.api.capability.food.Nutrient;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.util.climate.ClimateData;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.biome.Biome;

public class EnvironmentalModifier extends BaseModifier {
	public EnvironmentalModifier(String name, float change, float potency) {
		super(name, change, potency);
	}
	
	public static float getEnvironmentTemperature(EntityPlayer player) {
		float avg = ClimateData.DEFAULT.getRegionalTemp() + 2f;
		float actual = ClimateTFC.getActualTemp(player.world, player.getPosition());
		if(TFCAmbientalConfig.GENERAL.harsherTemperateAreas) {
			float diff = actual - TemperatureCapability.AVERAGE;
			float sign = Math.signum(diff);
			float generalDiff = Math.abs(avg - TemperatureCapability.AVERAGE);
			float mult0 = Math.max(0f, TFCAmbientalConfig.GENERAL.harsherMultiplier - 1f);
			float multiplier = 1 + Math.max(0, 1 - generalDiff / 55) * mult0;
			actual = TemperatureCapability.AVERAGE + (diff + 1.5f * sign) * multiplier;
		}
		return actual;
	}
	
	public static float getEnvironmentHumidity(EntityPlayer player) {
		return ClimateTFC.getRainfall(player.world, player.getPosition()) / 3000;
	}
	
	public static EnvironmentalModifier handleFire(EntityPlayer player) {
		return player.isBurning() ? new EnvironmentalModifier("on_fire", 4f, 4f) : null;
	}
	
	public static EnvironmentalModifier handleWater(EntityPlayer player) {
		if(player.isInWater()) {
			BlockPos pos = player.getPosition();
			IBlockState state = player.world.getBlockState(pos);
			if(state.getBlock() == FluidsTFC.HOT_WATER.get().getBlock()) {
				return new EnvironmentalModifier("in_hot_water", 5f, 6f);
			}else if(state.getBlock() == Blocks.LAVA) {
				return new EnvironmentalModifier("in_lava", 10f, 5f);
			}else if(state.getBlock() == FluidsTFC.SALT_WATER.get().getBlock() && player.world.getBiome(pos).getTempCategory() == Biome.TempCategory.OCEAN ){
				return new EnvironmentalModifier("in_ocean_water", -8f, 6f);
			}else {
				return new EnvironmentalModifier("in_water", -5f, 6f);
			}
		}else {
			return null;
		}
	}
	
	public static EnvironmentalModifier handleRain(EntityPlayer player) {
		if(player.world.isRaining()) {
			if(getSkylight(player) < 15) {
				return new EnvironmentalModifier("rain", -2f, 0.1f);
			}else {
				return new EnvironmentalModifier("rain", -4f, 0.3f);
			}
		}else {
			return null;
		}
	}
	
	public static EnvironmentalModifier handleSprinting(EntityPlayer player) {
		if(player.isSprinting()) {
			return new EnvironmentalModifier("sprint", 2f, 0.3f);
		}else {
			return null;
		}
	}
	
	public static EnvironmentalModifier handleUnderground(EntityPlayer player) {
		if(player.world.getLight(player.getPosition()) < 3 && player.getPosition().getY() < 135) {
			return new EnvironmentalModifier("underground", -6f, 0.2f);
		}else{
			return null;
		}
	}
	
	public static EnvironmentalModifier handleShade(EntityPlayer player) {
		int light = getSkylight(player);
		light = Math.max(12, light);
		float temp = getEnvironmentTemperature(player);
		float avg = TemperatureCapability.AVERAGE;
		float coverage = (1f - (float)light/15f) + 0.5f;
		if(light < 15 && temp > avg) {
			return new EnvironmentalModifier("shade", -Math.abs(avg - temp) * coverage, 0f);
		}else{
			return null;
		}
	}
	
	public static EnvironmentalModifier handleCozy(EntityPlayer player) {
		int skyLight = getSkylight(player);
		skyLight = Math.max(11, skyLight);
		int blockLight = getBlockLight(player);
		float temp = getEnvironmentTemperature(player);
		float avg = TemperatureCapability.AVERAGE;
		float coverage = (1f - (float)skyLight/15f) + 0.4f;
		if(skyLight < 14 && blockLight > 4 && temp < avg - 2 && player.getPosition().getY() > 130) {
			return new EnvironmentalModifier("cozy", Math.abs(avg - 2 - temp) *  coverage, 0f);
		}else{
			return null;
		}
	}
	
	public static EnvironmentalModifier handleThirst(EntityPlayer player) {
		if(player.getFoodStats() instanceof IFoodStatsTFC) {
			IFoodStatsTFC stats = (IFoodStatsTFC) player.getFoodStats();
			if(getEnvironmentTemperature(player) > TemperatureCapability.AVERAGE + 3 && stats.getThirst() > 80f) {
				return new EnvironmentalModifier("well_hidrated", -2f, 0f);
			}
		}
		return null;
	}
	
	public static EnvironmentalModifier handleFood(EntityPlayer player) {
		if(getEnvironmentTemperature(player) < TemperatureCapability.AVERAGE - 3 && player.getFoodStats().getFoodLevel() > 16) {
			return new EnvironmentalModifier("well_fed", 2f, 0f);
		}
		return null;
	}
	
	public static EnvironmentalModifier handleDiet(EntityPlayer player) {
		if(player.getFoodStats() instanceof IFoodStatsTFC) {
			IFoodStatsTFC stats = (IFoodStatsTFC) player.getFoodStats();
			if(getEnvironmentTemperature(player) < TemperatureCapability.COOL_THRESHOLD) {
				float grainLevel = stats.getNutrition().getNutrient(Nutrient.GRAIN);
				float meatLevel = stats.getNutrition().getNutrient(Nutrient.PROTEIN);
				return new EnvironmentalModifier("nutrients", 4f * grainLevel * meatLevel, 0f);
			}
			if(getEnvironmentTemperature(player) > TemperatureCapability.HOT_THRESHOLD) {
				float fruitLevel = stats.getNutrition().getNutrient(Nutrient.FRUIT);
				float veggieLevel = stats.getNutrition().getNutrient(Nutrient.VEGETABLES);
				return new EnvironmentalModifier("nutrients", -4f  * fruitLevel * veggieLevel, 0f);
			}
		}
		return null;
	}
	
	public static int getSkylight(EntityPlayer player) {
		BlockPos pos = new BlockPos(player.getPosition());
		BlockPos pos2 = pos.add(0, 1.8, 0);
		return player.world.getLightFor(EnumSkyBlock.SKY, pos2);
	}
	
	public static int getBlockLight(EntityPlayer player) {
		BlockPos pos = new BlockPos(player.getPosition());
		pos.add(0, 1, 0);
		return player.world.getLightFor(EnumSkyBlock.BLOCK, pos);
	}
	
	public static EnvironmentalModifier handleGeneralTemperature(EntityPlayer player) {
		int dayTicks = (int) (player.world.getWorldTime() % 24000);
		float dayPart;
		if(dayTicks < 6000) dayPart = 2f;
		else if(dayTicks < 12000) dayPart = 4f;
		else if(dayTicks < 18000) dayPart = 1f;
		else dayPart = -4;
		return new EnvironmentalModifier("environment", getEnvironmentTemperature(player) + dayPart, getEnvironmentHumidity(player));
	}

	public static EnvironmentalModifier handlePotionEffects(EntityPlayer player) {
		if(player.isPotionActive(TempEffect.COOL)){
			return new EnvironmentalModifier("cooling_effect", -10F, 0);
		}
		if(player.isPotionActive(TempEffect.WARM)){
			return new EnvironmentalModifier("heating_effect", 10F, 0);
		}
		return null;
	}
	
	public static void computeModifiers(EntityPlayer player, ModifierStorage modifiers){
		for(IEnvironmentalTemperatureProvider provider : TemperatureRegistry.ENVIRONMENT) {
			modifiers.add(provider.getModifier(player));
		}
	}

}
