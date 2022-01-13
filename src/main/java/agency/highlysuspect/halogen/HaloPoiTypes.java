package agency.highlysuspect.halogen;

import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Blocks;

public class HaloPoiTypes {
	public static PoiType BUDDING_AMETHYST;
	
	public static void onInitialize() {
		//Trying to play nice if some mod is doing the same thing. you can't register multiple poi types for the same blockstate.
		BUDDING_AMETHYST = PoiType.forState(Blocks.BUDDING_AMETHYST.defaultBlockState())
			.orElseGet(() -> PointOfInterestHelper.register(Init.id("budding_amethyst_poi"), 0, 0, Blocks.BUDDING_AMETHYST));
	}
}
