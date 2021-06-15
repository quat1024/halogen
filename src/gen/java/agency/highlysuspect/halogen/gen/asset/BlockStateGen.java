package agency.highlysuspect.halogen.gen.asset;

import agency.highlysuspect.halogen.block.HaloBlocks;
import agency.highlysuspect.halogen.gen.GenInit;
import net.minecraft.block.Block;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.data.client.model.BlockStateVariant;
import net.minecraft.data.client.model.ModelIds;
import net.minecraft.data.client.model.VariantSettings;
import net.minecraft.data.client.model.VariantsBlockStateSupplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.nio.file.Path;

public class BlockStateGen implements DataProvider {
	@Override
	public void run(DataCache cache) throws IOException {
		write(cache, model(HaloBlocks.BASIC_NODE));
	}
	
	//All blockstates map to this block model.
	private VariantsBlockStateSupplier model(Block b, Identifier modelPath) {
		return VariantsBlockStateSupplier.create(b, BlockStateVariant.create().put(VariantSettings.MODEL, modelPath));
	}
	
	//All blockstates map to the model named after the block.
	private VariantsBlockStateSupplier model(Block b) {
		return model(b, ModelIds.getBlockModelId(b));
	}
	
	private Path outPath(Identifier id) {
		return GenInit.OUT_ROOT.resolve("assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json");
	}
	
	private void write(DataCache cache, VariantsBlockStateSupplier v) throws IOException {
		Identifier id = Registry.BLOCK.getId(v.getBlock());
		DataProvider.writeToPath(GenInit.GSON, cache, v.get(), outPath(id));
	}
	
	@Override
	public String getName() {
		return "Halogen blockstates";
	}
}
