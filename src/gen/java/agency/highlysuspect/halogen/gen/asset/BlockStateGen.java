package agency.highlysuspect.halogen.gen.asset;

import agency.highlysuspect.halogen.block.HaloBlocks;
import agency.highlysuspect.halogen.gen.GenInit;
import net.minecraft.core.Registry;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import java.io.IOException;
import java.nio.file.Path;

public class BlockStateGen implements DataProvider {
	@Override
	public void run(HashCache cache) throws IOException {
		write(cache, model(HaloBlocks.NODE));
		write(cache, model(HaloBlocks.LARGE_MOONLIGHT_PRISM));
		write(cache, model(HaloBlocks.SMALL_MOONLIGHT_PRISM));
	}
	
	//All blockstates map to this block model.
	private MultiVariantGenerator model(Block b, ResourceLocation modelPath) {
		return MultiVariantGenerator.multiVariant(b, Variant.variant().with(VariantProperties.MODEL, modelPath));
	}
	
	//All blockstates map to the model named after the block.
	private MultiVariantGenerator model(Block b) {
		return model(b, ModelLocationUtils.getModelLocation(b));
	}
	
	private Path outPath(ResourceLocation id) {
		return GenInit.OUT_ROOT.resolve("assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json");
	}
	
	private void write(HashCache cache, MultiVariantGenerator v) throws IOException {
		ResourceLocation id = Registry.BLOCK.getKey(v.getBlock());
		DataProvider.save(GenInit.GSON, cache, v.get(), outPath(id));
	}
	
	@Override
	public String getName() {
		return "Halogen blockstates";
	}
}
