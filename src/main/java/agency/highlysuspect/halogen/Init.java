package agency.highlysuspect.halogen;

import agency.highlysuspect.halogen.aura.HaloAuraTypes;
import agency.highlysuspect.halogen.block.HaloBlocks;
import agency.highlysuspect.halogen.block.entity.HaloBlockEntityTypes;
import agency.highlysuspect.halogen.item.HaloItems;
import agency.highlysuspect.halogen.worldgen.HaloFeatures;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Init implements ModInitializer {
	public static final String MODID = "halogen";
	public static final Logger LOG = LogManager.getLogger(MODID);
	
	@Override
	public void onInitialize() {
		HaloAuraTypes.onInitialize();
		
		HaloBlocks.onInitialize();
		HaloBlockEntityTypes.onInitialize();
		
		HaloItems.onInitialize();
		
		HaloFeatures.onInitialize();
	}
	
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	public static void log(String msg, Object... args) {
		LOG.info(msg, args);
	}
	
	public static void warn(String msg, Object... args) {
		LOG.warn(msg, args);
	}
}
