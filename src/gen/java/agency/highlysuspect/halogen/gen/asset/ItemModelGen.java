package agency.highlysuspect.halogen.gen.asset;

import agency.highlysuspect.halogen.block.HaloBlocks;
import agency.highlysuspect.halogen.gen.GenInit;
import agency.highlysuspect.halogen.item.HaloItems;
import com.google.gson.JsonElement;
import net.minecraft.block.Block;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.data.client.model.ModelIds;
import net.minecraft.data.client.model.Models;
import net.minecraft.data.client.model.SimpleModelSupplier;
import net.minecraft.data.client.model.Texture;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ItemModelGen implements DataProvider {
	private DataCache yeetCache;
	
	@Override
	public void run(DataCache cache) throws IOException {
		yeetCache = cache;
		
		itemWithBlockModelParent(HaloBlocks.NODE, cache);
		itemWithBlockModelParent(HaloBlocks.LARGE_MOONLIGHT_PRISM, cache);
		itemWithBlockModelParent(HaloBlocks.SMALL_MOONLIGHT_PRISM, cache);
		
		layer0(HaloItems.LINKING_WAND, this::write);
		layer0(HaloItems.WHITE_AURA_CRYSTAL, this::write);
		layer0(HaloItems.RED_AURA_CRYSTAL, this::write);
		layer0(HaloItems.BLUE_AURA_CRYSTAL, this::write);
	}
	
	private void itemWithBlockModelParent(Block b, DataCache cache) throws IOException {
		itemWithBlockModelParent(b, ModelIds.getBlockModelId(b), cache);
	}
	
	private void itemWithBlockModelParent(Block b, Identifier modelId, DataCache cache) throws IOException {
		Identifier id = Registry.BLOCK.getId(b);
		JsonElement modelJson = new SimpleModelSupplier(modelId).get();
		DataProvider.writeToPath(GenInit.GSON, cache, modelJson, itemModelOutPath(id));
	}
	
	private static void layer0(ItemConvertible item, BiConsumer<Identifier, Supplier<JsonElement>> consumer) {
		Item i = item.asItem();
		Models.GENERATED.upload(ModelIds.getItemModelId(i), Texture.layer0(i), consumer);
	}
	
	private static void layer0(ItemConvertible item, Identifier tex, BiConsumer<Identifier, Supplier<JsonElement>> consumer) {
		Item i = item.asItem();
		Models.GENERATED.upload(ModelIds.getItemModelId(i), Texture.layer0(tex), consumer);
	}
	
	private Path outPath(Identifier id) {
		return GenInit.OUT_ROOT.resolve("assets/" + id.getNamespace() + "/models/" + id.getPath() + ".json");
	}
	
	private Path itemModelOutPath(Identifier id) {
		return GenInit.OUT_ROOT.resolve("assets/" + id.getNamespace() + "/models/item/" + id.getPath() + ".json");
	}
	
	//Demanded by some wacky stuff in vanilla model generators. Not great imo
	private void write(Identifier id, Supplier<JsonElement> jsonSupplier) {
		try {
			DataProvider.writeToPath(GenInit.GSON, yeetCache, jsonSupplier.get(), outPath(id));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String getName() {
		return "halogen item models";
	}
}
