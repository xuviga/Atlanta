package ru.will0376.cases;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class Rolling {
	public static final SoundEvent test = reg("rolling");


	private static SoundEvent reg(String name) {
		ResourceLocation rl = new ResourceLocation(Cases.MOD_ID, "rolling");
		return new SoundEvent(rl).setRegistryName(rl);
	}
}
