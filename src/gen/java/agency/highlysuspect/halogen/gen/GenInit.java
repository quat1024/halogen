package agency.highlysuspect.halogen.gen;

import agency.highlysuspect.halogen.gen.asset.BlockStateGen;
import agency.highlysuspect.halogen.gen.asset.ItemModelGen;
import agency.highlysuspect.halogen.gen.data.BlockDropGen;
import agency.highlysuspect.halogen.gen.data.RecipeGen;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.minecraft.data.DataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class GenInit implements ModInitializer {
	public static final Logger LOG = LogManager.getLogger("halogen-gen");
	public static final Path OUT_ROOT = Paths.get(System.getProperty("datagen.out"));
	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
	
	@Override
	public void onInitialize() {
		try {
			Files.createDirectories(OUT_ROOT);
			
			DataGenerator datagen = new DataGenerator(OUT_ROOT, Collections.emptyList());
			
			datagen.install(new BlockStateGen());
			datagen.install(new ItemModelGen());
			
			datagen.install(new BlockDropGen());
			datagen.install(new RecipeGen());
			
			datagen.run();
		} catch (Exception e) {
			LOG.fatal("Oh fuck!!!!", e);
			System.exit(69);
		}
		
		LOG.warn("Kthx bye!!!");
		System.exit(0);
	}
}
