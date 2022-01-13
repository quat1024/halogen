package agency.highlysuspect.halogen;

import agency.highlysuspect.halogen.mechanics.aura.HaloAuraTypes;
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
		HaloPoiTypes.onInitialize();
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
